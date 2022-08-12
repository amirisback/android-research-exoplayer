package com.frogobox.research.util

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource


/**
 * Created by Faisal Amir on 10/08/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

fun ExoPlayer.setMediaItemExtYT(urlYoutube: String) {
    val mediaItem = MediaItem.Builder()
        .setUri(urlYoutube)
        .setMimeType(MimeTypes.APPLICATION_MPD)
        .build()
    setMediaItem(mediaItem)
}

fun ExoPlayer.setMediaItemExt(uriMedia: String) {
    val mediaItem = MediaItem.fromUri(uriMedia)
    setMediaItem(mediaItem)
}

fun ExoPlayer.setMediaItemsExt(uriMedia: List<String>) {
    val mediaItems = mutableListOf<MediaItem>()
    uriMedia.forEach {
        mediaItems.add(MediaItem.fromUri(it))
    }
    setMediaItems(mediaItems)

}

fun ExoPlayer.setMediaItemsExt(uriMedia: List<String>, position: Int) {
    Log.d("ExoPlayer", "uri Media Size: ${uriMedia.size}")
    Log.d("ExoPlayer", "uri Media Position: $position")
    Log.d("ExoPlayer", "uri Media: ${uriMedia[position]}")


    // Initiate MediaItems
    val mediaItems = mutableListOf<MediaItem>()
    uriMedia.forEach {
        mediaItems.add(MediaItem.fromUri(it))
    }

    setMediaItems(mediaItems, position, 0L)

    Log.d("ExoPlayer", "MediaItem: $mediaItemCount")

}

fun ExoPlayer.setJwp(uriMedia: String) {

    // Create a data source factory.
    val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

    // Create a HLS media source pointing to a playlist uri.
    val hlsMediaSource =
        HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uriMedia))

    // Create a player instance.
    setMediaSource(hlsMediaSource)

}