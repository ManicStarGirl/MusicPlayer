package com.example.musicplayer.models

import java.io.Serializable

// This just holds the info for one song
data class Song (
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: String,
    var isPlaying: Boolean = false,
    val coverImage: Int? = null, // This will store the ID of a drawable (e.g., R.drawable.album_art), null if not provided
    val musicResId: Long?
) : Serializable