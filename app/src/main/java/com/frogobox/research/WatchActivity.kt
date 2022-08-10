package com.frogobox.research

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import com.frogobox.research.databinding.ActivityWatchBinding

class WatchActivity : AppCompatActivity() {

    companion object {
        val TAG = WatchActivity::class.java.simpleName
    }

    private val mainViewModel: MainViewModel by viewModels()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityWatchBinding.inflate(layoutInflater)
    }

    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
                binding.

                // Setup Media
                exoPlayer.setMediaItem(MediaItem.fromUri(getString(R.string.media_url_mp4)))
                exoPlayer.preparePlayer(binding.videoView, binding.videoViewFullscreen)
                setupExoPlayerByViewModel(exoPlayer, playbackStateListener)
            }
    }

    private fun mediaVideo(): MutableList<String> {
        val data = mutableListOf<String>()
        data.add(getString(R.string.media_url_mp4))
        data.add(getString(R.string.media_url_mp3))
        return data
    }

    private fun initializePlayer(uriMedia: List<String>) {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer


                exoPlayer.setMediaItem(MediaItem.fromUri(uriMedia[0]))
                for (i in uriMedia.indices) {
                    exoPlayer.addMediaItem(MediaItem.fromUri(uriMedia[i]))
                }

                // Default setup
                setupExoPlayerByViewModel(exoPlayer, playbackStateListener)
            }
    }

    private fun initializePlayer(uriMedia: String) {
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer

                val mediaItem = MediaItem.Builder()
                    .setUri(getString(R.string.media_url_dash))
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build()
                exoPlayer.setMediaItem(mediaItem)

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


    private fun playbackStateListener() = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED"
                else -> "UNKNOWN_STATE"
            }
            Log.d(TAG, "changed state to $stateString")
        }


    }


}

fun ExoPlayer.preparePlayer(
    playerView: PlayerView,
    playerViewFullscreen: PlayerView
) {
    (playerView.context as AppCompatActivity).apply {
        val fullScreenButton: ImageView = playerView.findViewById(R.id.exo_fullscreen_icon)
        fullScreenButton.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_baseline_fullscreen)
        )

        fullScreenButton.setOnClickListener {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            supportActionBar?.hide()
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            playerView.visibility = View.GONE
            playerViewFullscreen.visibility = View.VISIBLE
            playerView.resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
            playerView.player?.let { it1 ->
                PlayerView.switchTargetView(
                    it1,
                    playerView,
                    playerViewFullscreen
                )
            }
        }
    }

    (playerViewFullscreen.context as AppCompatActivity).apply {

        val normalScreenButton: ImageView =
            playerViewFullscreen.findViewById(R.id.exo_fullscreen_icon)

        normalScreenButton.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_baseline_fullscreen_exit)
        )

        normalScreenButton.setOnClickListener {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            supportActionBar?.show()
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            normalScreenButton.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_fullscreen_exit
                )
            )
            playerView.visibility = View.VISIBLE
            playerViewFullscreen.visibility = View.GONE
            playerView.player?.let { it1 ->
                PlayerView.switchTargetView(
                    it1,
                    playerViewFullscreen,
                    playerView
                )
            }
        }

    }

}
