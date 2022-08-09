package com.frogobox.research

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.frogobox.research.databinding.ActivityWatchBinding

class WatchActivity : AppCompatActivity() {

    companion object {
        val TAG = WatchActivity::class.java.simpleName
    }

    private val mainViewModel: MainViewModel by viewModels()

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityWatchBinding.inflate(layoutInflater)
    }

    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

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
                viewBinding.videoView.player = exoPlayer

                // Setup Media
                exoPlayer.setMediaItem(MediaItem.fromUri(getString(R.string.media_url_mp4)))

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
                viewBinding.videoView.player = exoPlayer


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
                viewBinding.videoView.player = exoPlayer

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
        WindowInsetsControllerCompat(window, viewBinding.videoView).let { controller ->
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