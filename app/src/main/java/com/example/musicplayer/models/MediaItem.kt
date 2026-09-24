package com.example.musicplayer.models

import java.io.Serializable

data class MediaItem(
    val name: String, // The name of the Artist or Title of Album
    val subName: String? = null, // sub text (should only be used for albums)
    val type: Int, // 0 = Artist, 1 = Album, 2 = Playlist
    val coverImage: Int? = null, // This will store the ID of a drawable (e.g., R.drawable.album_art), null if not provided
    val songs: List<Song>? = null,
    val albums: List<MediaItem>? = null
) : Serializable