package com.frogobox.research.media3

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.frogobox.research.R
import com.frogobox.research.databinding.ActivityWatchBinding
import com.frogobox.research.setMediaItemsExt

class WatchActivity : AppCompatActivity() {

    companion object {
        val TAG = WatchActivity::class.java.simpleName
    }

    private var isFullScreen = false

    private val mainViewModel: MainViewModel by viewModels()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityWatchBinding.inflate(layoutInflater)
    }

    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val fullscreenButton = binding.videoView.findViewById<ImageView>(R.id.exo_fullscreen_icon)
        fullscreenButton.setOnClickListener {
            if (isFullScreen) {
                supportActionBar?.show()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                isFullScreen = false
                fullscreenButton.setImageResource(R.drawable.ic_baseline_fullscreen)
            } else {
                supportActionBar?.hide()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                isFullScreen = true
                fullscreenButton.setImageResource(R.drawable.ic_baseline_fullscreen_exit)
            }
        }
    }

    private fun mediaVideo(): MutableList<String> {
        val data = mutableListOf<String>()
        data.add(getString(R.string.media_url_mp4))
        data.add(getString(R.string.media_url_mp3))
        data.add(getString(R.string.media_url_mp4_from_youtube_1))
        data.add(getString(R.string.media_url_mp4_from_youtube_2))
        data.add(getString(R.string.media_url_mp4_from_youtube_3))
        return data
    }

    private fun setupExoPlayerByViewModel(exoPlayer: ExoPlayer, listener: Player.Listener) {
        mainViewModel.apply {
            playWhenReady.observe(this@WatchActivity) {
                exoPlayer.playWhenReady = it
            }
            seekExoPlayer.observe(this@WatchActivity) {
                exoPlayer.seekTo(it.currentItem, it.playBackPosition)
            }
        }
        exoPlayer.addListener(listener)
        exoPlayer.addAnalyticsListener(object : AnalyticsListener {
            override fun onPlaybackStateChanged(
                eventTime: AnalyticsListener.EventTime,
                state: Int
            ) {
                super.onPlaybackStateChanged(eventTime, state)
                Log.d(TAG, "onPlaybackStateChanged: $state")
            }
        })
        exoPlayer.prepare()
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer

                // Setup Media Single Video
                // exoPlayer.setMediaItemExt(getString(R.string.media_url_mp4))

                // Setup Media Multiple Video
                exoPlayer.setMediaItemsExt(mediaVideo(), 3)

                // Setup Media Single Video Youtube Url
                // exoPlayer.setMediaItemExtYT(getString(R.string.media_url_dash))

                // Default setup
                setupExoPlayerByViewModel(exoPlayer, playbackStateListener)
            }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            mainViewModel.setSeekExoPlayer(
                SeekExoPlayer(
                    exoPlayer.currentMediaItemIndex,
                    exoPlayer.currentPosition
                )
            )
            mainViewModel.setPlayWhenReady(exoPlayer.playWhenReady)
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.release()
        }
        player = null
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onBackPressed() {
        val position = (player?.currentPosition ?: 0)
        Log.d(TAG, "Position : $position")
        super.onBackPressed()
    }

    private fun playbackStateListener() = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {

            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {
                    Log.d(TAG, "ExoPlayer.STATE_IDLE")
                    Log.d(TAG, "Duration : ${player?.duration}")
                    val position = (player?.currentPosition ?: 0)
                    Log.d(TAG, "Position : $position")
                }
                ExoPlayer.STATE_BUFFERING -> {
                    Log.d(TAG, "ExoPlayer.STATE_BUFFERING")
                    Log.d(TAG, "Duration : ${player?.duration}")
                    val position = (player?.currentPosition ?: 0)
                    Log.d(TAG, "Position : $position")
                }
                ExoPlayer.STATE_READY -> {
                    Log.d(TAG, "ExoPlayer.STATE_READY")
                    Log.d(TAG, "Duration : ${player?.duration}")
                    val position = (player?.currentPosition ?: 0)
                    Log.d(TAG, "Position : $position")
                }
                ExoPlayer.STATE_ENDED -> {
                    Log.d(TAG, "ExoPlayer.STATE_ENDED")
                    Log.d(TAG, "Duration : ${player?.duration}")
                    val position = (player?.currentPosition ?: 0)
                    Log.d(TAG, "Position : $position")
                }
                else -> {
                    Log.d(TAG, "Unknown state")
                }

            }

        }

    }

}