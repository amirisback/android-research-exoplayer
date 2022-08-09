package com.frogobox.research

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Created by KoinWorks (M. Faisal Amir) on 09/08/22.


class MainViewModel : ViewModel() {

    private var _seekExoPlayer = MutableLiveData<SeekExoPlayer>()
    var seekExoPlayer: LiveData<SeekExoPlayer> = _seekExoPlayer

    private var _playWhenReady = MutableLiveData<Boolean>().apply {
        postValue(true)
    }
    var playWhenReady: LiveData<Boolean> = _playWhenReady

    fun setPlayWhenReady(playWhenReady: Boolean) {
        _playWhenReady.value = playWhenReady
    }

    fun setSeekExoPlayer(seekExoPlayer: SeekExoPlayer) {
        _seekExoPlayer.postValue(seekExoPlayer)
    }

}

data class SeekExoPlayer(val currentItem: Int = 0, var playBackPosition: Long = 0L)