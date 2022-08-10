package com.frogobox.research

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer

// Created by KoinWorks (M. Faisal Amir) on 10/08/22.


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