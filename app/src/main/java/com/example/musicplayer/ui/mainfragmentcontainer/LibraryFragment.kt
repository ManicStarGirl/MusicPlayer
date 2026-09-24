package com.example.musicplayer.ui.mainfragmentcontainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.musicplayer.R
import com.example.musicplayer.models.MediaItem
import com.example.musicplayer.models.Song
import java.util.Locale

class LibraryFragment : Fragment() {

    companion object {
        private var currentView = "Album"
    }

    private var songList = arrayListOf<Song>()
    private var albumList = arrayListOf<MediaItem>()
    private var artistList = arrayListOf<MediaItem>()

    private val requestPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            refreshMusicLibrary()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSongs = view.findViewById<Button>(R.id.btn_songs)
        val btnAlbums = view.findViewById<Button>(R.id.btn_albums)
        val btnArtists = view.findViewById<Button>(R.id.btn_artists)

        // Permission check
        val permission =
            android.Manifest.permission.READ_MEDIA_AUDIO

        if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(), permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            refreshMusicLibrary()
        } else {
            requestPermissionLauncher.launch(permission)
        }

        // Button listeners
        btnSongs.setOnClickListener {
            currentView = "Song"
            updateCurrentDisplay()
        }

        btnAlbums.setOnClickListener {
            currentView = "Album"
            updateCurrentDisplay()
        }

        btnArtists.setOnClickListener {
            currentView = "Artist"
            updateCurrentDisplay()
        }
    }

    private fun updateCurrentDisplay() {
        val fragment: Fragment
        val bundle = Bundle()

        when (currentView) {
            "Song" -> {
                fragment = SongListFragment()
                bundle.putSerializable("songList", ArrayList(songList))
            }
            "Album" -> {
                fragment = MusicCompilationFragment()
                bundle.putSerializable("itemList", ArrayList(albumList))
                bundle.putInt("compilationMode", 1) // 1 = Album Mode
                bundle.putBoolean("isAlbum", true)
            }
            "Artist" -> {
                fragment = MusicCompilationFragment()
                bundle.putSerializable("itemList", ArrayList(artistList))
                bundle.putInt("compilationMode", 0) // 0 = Artist Mode
                bundle.putBoolean("isArtist", true)
            }
            else -> return
        }

        fragment.arguments = bundle
        replaceFragment(fragment)
    }

    private fun refreshMusicLibrary() {val songsFromPhone = loadSongsFromStorage()
        songList.clear()
        songList.addAll(songsFromPhone)

        artistList.clear()

        // 1. Group all songs by Artist Name
        val songsByArtist = songsFromPhone.groupBy { it.artist ?: "Unknown Artist" }

        for ((artistName, artistSongs) in songsByArtist) {

            // 2. For this specific artist, find all their unique albums
            val albumsByThisArtist = artistSongs.groupBy { it.album ?: "Unknown Album" }
                .map { (albumName, albumSongs) ->
                    // Create a MediaItem for each Album, attaching its specific songs
                    MediaItem(
                        name = albumName,
                        subName = artistName,
                        type = 1, // Album Type
                        songs = albumSongs // <--- Attach the list of songs to the album
                    )
                }

            // 3. Create the Artist MediaItem and attach the list of albums we just made
            artistList.add(MediaItem(
                name = artistName,
                type = 0, // Artist Type
                albums = albumsByThisArtist // <--- Attach the list of albums to the artist
            ))
        }

        // 4. Also update the global albumList for the "Albums" tab
        albumList.clear()
        albumList.addAll(artistList.flatMap { it.albums ?: listOf() }.distinctBy { it.name to it.subName })

        updateCurrentDisplay()
    }

    private fun loadSongsFromStorage(): List<Song> {
        val songs = mutableListOf<Song>()
        val contentResolver = requireContext().contentResolver
        val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${android.provider.MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${android.provider.MediaStore.Audio.Media.TITLE} ASC"

        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)

        cursor?.use {
            val titleColumn = it.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
            val albumColumn = it.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM)
            val durationColumn = it.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION)
            val idColumn = it.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)

            while (it.moveToNext()) {
                // 1. Get the raw album string from MediaStore
                val rawAlbum = if (albumColumn != -1) it.getString(albumColumn) else ""

                // 2. Get the file path to see the folder name
                val dataColumn = it.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA)
                val filePath = if (dataColumn != -1) it.getString(dataColumn) else ""
                val folderName = java.io.File(filePath).parentFile?.name ?: ""

                // 3. Logic: If the album name is exactly the same as the folder name,
                // it's likely a fallback. Mark it as a "Single" instead.
                val album = if (rawAlbum.isNotEmpty() && rawAlbum != folderName) {
                    rawAlbum
                } else {
                    "Singles"
                }

                val title = if (titleColumn != -1) it.getString(titleColumn) else "Unknown Title"
                val artist = if (artistColumn != -1) it.getString(artistColumn) else "Unknown Artist"
                val durationMs = if (durationColumn != -1) it.getLong(durationColumn) else 0L
                val id = if (idColumn != -1) it.getLong(idColumn) else -1L

                val minutes = (durationMs / 1000) / 60
                val seconds = (durationMs / 1000) % 60
                val durationString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                songs.add(Song(title, artist, album, durationString, musicResId = id))
            }
        }
        return songs
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.library_fragment_container, fragment)
            .commit()
    }
}
