import logging
from flask import Flask, request, jsonify
from flask_cors import CORS
import yt_dlp

app = Flask(__name__)
CORS(app)

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("AruackYouTubeSearch")

MAX_QUERY_LENGTH = 100
MAX_RESULTS = 20

# yt-dlp configuration: Strictly metadata extraction, flat playlist/search extract, NO downloading, NO stream URL deciphering
YTDL_SEARCH_OPTIONS = {
    'extract_flat': True,
    'skip_download': True,
    'quiet': True,
    'no_warnings': True,
    'default_search': 'ytsearch',
    'noplaylist': True,
}

@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        "status": "ok",
        "service": "Aruack Music YouTube Search API"
    }), 200

@app.route('/search', methods=['GET'])
def search():
    query = request.args.get('q', '').strip()
    
    if not query:
        return jsonify({
            "success": False,
            "error": "Query parameter 'q' is required and cannot be empty",
            "results": []
        }), 400

    if len(query) > MAX_QUERY_LENGTH:
        return jsonify({
            "success": False,
            "error": f"Query exceeds maximum allowed length of {MAX_QUERY_LENGTH} characters",
            "results": []
        }), 400

    try:
        search_query = f"ytsearch{MAX_RESULTS}:{query}"
        with yt_dlp.YoutubeDL(YTDL_SEARCH_OPTIONS) as ydl:
            search_data = ydl.extract_info(search_query, download=False)
            
        results = []
        entries = search_data.get('entries', []) if search_data else []

        for entry in entries:
            if not entry:
                continue
            video_id = entry.get('id') or entry.get('url')
            if not video_id:
                continue
            
            # Extract standard metadata only
            title = entry.get('title', 'Unknown Title')
            channel = entry.get('channel') or entry.get('uploader') or 'Unknown Channel'
            duration = entry.get('duration') or 0
            thumbnails = entry.get('thumbnails', [])
            thumbnail_url = thumbnails[-1].get('url') if thumbnails else f"https://i.ytimg.com/vi/{video_id}/hqdefault.jpg"
            
            results.append({
                "title": title,
                "video_id": video_id,
                "thumbnail": thumbnail_url,
                "youtube_url": f"https://www.youtube.com/watch?v={video_id}",
                "channel": channel,
                "duration": duration
            })

        return jsonify({
            "success": True,
            "query": query,
            "results": results
        }), 200

    except Exception as e:
        logger.error(f"Search failed for query '{query}': {str(e)}")
        return jsonify({
            "success": False,
            "error": "Failed to retrieve search metadata",
            "results": []
        }), 500

@app.route('/stream', methods=['GET'])
def stream():
    video_id = request.args.get('id', '').strip()
    if not video_id:
        return jsonify({
            "success": False,
            "error": "Query parameter 'id' is required"
        }), 400

    try:
        ydl_opts = {
            'format': 'bestaudio/best',
            'quiet': True,
            'no_warnings': True,
            'skip_download': True
        }
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            info = ydl.extract_info(f"https://www.youtube.com/watch?v={video_id}", download=False)
            stream_url = info.get('url')
            
            if not stream_url:
                # check formats
                formats = info.get('formats', [])
                for f in reversed(formats):
                    if f.get('acodec') != 'none' and f.get('vcodec') == 'none':
                        stream_url = f.get('url')
                        break
                        
            return jsonify({
                "success": True,
                "video_id": video_id,
                "stream_url": stream_url or f"https://www.youtube.com/watch?v={video_id}",
                "title": info.get('title'),
                "duration": info.get('duration')
            }), 200

    except Exception as e:
        logger.error(f"Stream extraction failed for id '{video_id}': {str(e)}")
        return jsonify({
            "success": False,
            "error": "Failed to extract audio stream",
            "stream_url": f"https://www.youtube.com/watch?v={video_id}"
        }), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
