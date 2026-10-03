# Aruack Music — YouTube Search Metadata Service

An optional, standalone REST API service that performs YouTube search and metadata discovery using `yt-dlp`.

> **Note**: This service only discovers public video metadata (title, channel, duration, thumbnail, official YouTube URL). It does **NOT** extract audio streams, download media, or bypass DRM. The Aruack Music Android application operates completely independently without requiring this service.

## Endpoints

### 1. Health Check
`GET /health`

**Response:**
```json
{
  "status": "ok",
  "service": "Aruack Music YouTube Search API"
}
```

### 2. Search Metadata
`GET /search?q=<query>`

**Parameters:**
- `q`: Search term (required, max 100 characters)

**Response:**
```json
{
  "success": true,
  "query": "A.R. Rahman",
  "results": [
    {
      "title": "Song Title",
      "video_id": "dQw4w9WgXcQ",
      "thumbnail": "https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
      "youtube_url": "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
      "channel": "Artist Channel",
      "duration": 240
    }
  ]
}
```

## Running Locally

```bash
pip install -r requirements.txt
python app.py
```
Service will start at `http://localhost:5000`.
