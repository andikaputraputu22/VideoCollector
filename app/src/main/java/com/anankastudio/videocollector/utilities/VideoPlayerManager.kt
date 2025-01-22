package com.anankastudio.videocollector.utilities

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

class VideoPlayerManager(
    private val context: Context,
    private var progressBar: ProgressBar? = null
) {

    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    fun initializePlayer(url: String, playerCallback: Player.Listener?): ExoPlayer? {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaSourceFactory = DefaultMediaSourceFactory(DefaultDataSource.Factory(
            context,
            dataSourceFactory
        ))
        player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                prepare()
                playerCallback?.let { addListener(it) }
            }
        return player
    }

    fun play() {
        player?.playWhenReady = true
    }

    fun pause() {
        player?.playWhenReady = false
    }

    fun release() {
        player?.apply {
            playWhenReady = false
            release()
        }
        player = null
    }

    fun restart() {
        player?.seekTo(0)
        player?.playWhenReady = true
    }

    fun setAutoPlay(isAutoPlay: Boolean) {
        player?.playWhenReady = isAutoPlay
    }

    fun setProgressBar(progressBar: ProgressBar) {
        this.progressBar = progressBar
    }

    val playerCallback: Player.Listener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            progressBar?.visibility = View.GONE
            Toast.makeText(context, "Cannot play this video", Toast.LENGTH_SHORT).show()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when(playbackState) {
                ExoPlayer.STATE_BUFFERING -> progressBar?.visibility = View.VISIBLE
                ExoPlayer.STATE_READY -> progressBar?.visibility = View.GONE
                ExoPlayer.STATE_ENDED -> restart()
                else -> progressBar?.visibility = View.GONE
            }
        }
    }
}