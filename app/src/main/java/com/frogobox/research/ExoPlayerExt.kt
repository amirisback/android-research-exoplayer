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

fun ExoPlayer.setMediaItemExt(uriMedia: List<String>) {
    val mediaItemFirst = MediaItem.fromUri(uriMedia[0])
    setMediaItem(mediaItemFirst)
    for (i in uriMedia.indices) {
        Log.d("MediaItem", "MediaItem $i : ${uriMedia[i]}")
        val mediaItem = MediaItem.fromUri(uriMedia[i + 1])
        addMediaItem(mediaItem)
    }
}