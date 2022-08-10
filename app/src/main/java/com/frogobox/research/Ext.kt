package com.frogobox.research

import android.content.Context
import android.content.pm.ActivityInfo
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

// Created by (M. Faisal Amir) on 09/08/22.

fun Context.showToast(message: String) {
    android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
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
            playerView.resizeMode =
                androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
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