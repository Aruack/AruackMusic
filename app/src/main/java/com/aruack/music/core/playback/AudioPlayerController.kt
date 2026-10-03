package com.aruack.music.core.playback

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.aruack.music.core.model.PlaybackState
import com.aruack.music.core.model.RepeatMode
import com.aruack.music.core.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

class AudioPlayerController(
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)
        .setUserAgent("AruackMusic/1.0.1 (Linux; Android; ExoPlayer)")
        .setConnectTimeoutMs(15000)
        .setReadTimeoutMs(20000)

    private val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
    private val mediaSourceFactory = DefaultMediaSourceFactory(context).setDataSourceFactory(dataSourceFactory)

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(mediaSourceFactory)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            true // handle audio focus automatically
        )
        .setHandleAudioBecomingNoisy(true)
        .setWakeMode(C.WAKE_MODE_NETWORK)
        .build()

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var currentPlaylist: List<Song> = emptyList()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                val isEnded = playbackState == Player.STATE_ENDED
                val duration = if (exoPlayer.duration > 0) exoPlayer.duration else 0L

                updatePlaybackState {
                    it.copy(
                        isBuffering = isBuffering,
                        durationMs = duration
                    )
                }

                if (isEnded) {
                    playNext()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val currentIdx = exoPlayer.currentMediaItemIndex
                val song = currentPlaylist.getOrNull(currentIdx)
                updatePlaybackState {
                    it.copy(
                        currentSong = song,
                        currentQueueIndex = currentIdx,
                        durationMs = if (exoPlayer.duration > 0) exoPlayer.duration else (song?.durationMs ?: 0L),
                        currentPositionMs = 0L
                    )
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                updatePlaybackState {
                    it.copy(
                        isBuffering = false,
                        isPlaying = false,
                        errorMessage = error.localizedMessage ?: "Playback error"
                    )
                }
            }
        })
    }

    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        val playableSongs = songs.filter { it.playbackType.isDirectPlayable && it.mediaUri.isNotBlank() }
        if (playableSongs.isEmpty()) return

        val targetIndex = if (startIndex in playableSongs.indices) startIndex else 0
        currentPlaylist = playableSongs
        val mediaItems = playableSongs.map { it.toMediaItem() }

        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.setMediaItems(mediaItems, targetIndex, 0L)
        exoPlayer.prepare()
        exoPlayer.play()

        updatePlaybackState {
            it.copy(
                queue = playableSongs,
                currentQueueIndex = targetIndex,
                currentSong = playableSongs.getOrNull(targetIndex),
                isPlaying = true,
                errorMessage = null
            )
        }
    }

    fun playSong(song: Song) {
        if (!song.playbackType.isDirectPlayable) return
        playQueue(listOf(song), 0)
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (exoPlayer.playbackState == Player.STATE_ENDED) {
                exoPlayer.seekTo(0, 0L)
            }
            exoPlayer.play()
        }
    }

    fun playNext() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        } else if (_playbackState.value.repeatMode == RepeatMode.ALL && currentPlaylist.isNotEmpty()) {
            exoPlayer.seekTo(0, 0L)
            exoPlayer.play()
        }
    }

    fun playPrevious() {
        if (exoPlayer.currentPosition > 3000) {
            exoPlayer.seekTo(0L)
        } else if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        } else {
            exoPlayer.seekTo(0L)
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        updatePlaybackState { it.copy(currentPositionMs = positionMs) }
    }

    fun toggleShuffle() {
        val currentShuffle = exoPlayer.shuffleModeEnabled
        val newShuffle = !currentShuffle
        exoPlayer.shuffleModeEnabled = newShuffle
        updatePlaybackState { it.copy(isShuffleEnabled = newShuffle) }
    }

    fun cycleRepeatMode() {
        val nextMode = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }

        when (nextMode) {
            RepeatMode.OFF -> {
                exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
            }
            RepeatMode.ALL -> {
                exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
            }
            RepeatMode.ONE -> {
                exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
            }
        }

        updatePlaybackState { it.copy(repeatMode = nextMode) }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val currentPos = exoPlayer.currentPosition
                val bufferedPos = exoPlayer.bufferedPosition
                val duration = if (exoPlayer.duration > 0) exoPlayer.duration else _playbackState.value.durationMs

                updatePlaybackState {
                    it.copy(
                        currentPositionMs = currentPos,
                        bufferedPositionMs = bufferedPos,
                        durationMs = duration
                    )
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    private inline fun updatePlaybackState(updater: (PlaybackState) -> PlaybackState) {
        _playbackState.value = updater(_playbackState.value)
    }

    private fun Song.toMediaItem(): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(album)
            .setArtworkUri(albumArtUri?.let { Uri.parse(it) })
            .build()

        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(mediaUri)
            .setMediaMetadata(metadata)
            .build()
    }

    fun release() {
        stopProgressTracker()
        exoPlayer.release()
    }
}
