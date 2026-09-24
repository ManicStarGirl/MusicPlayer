package com.example.musicplayer.ui.mainfragmentcontainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.adapters.SongAdapter
import com.example.musicplayer.models.Song

class SongListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Inflate the layout
        val view = inflater.inflate(R.layout.fragment_song_list, container, false)

        // 2. RETRIEVE THE NAME:
        // We check for "playlistName" OR "artistName" OR "albumName"
        // If none are found, we use a generic default like "Songs"
        val screenTitle = arguments?.getString("playlistName")
            ?: arguments?.getString("artistName")
            ?: ""

        // 3. UPDATE THE TITLE
        val titleTextView = view.findViewById<android.widget.TextView>(R.id.playlist_title)
        titleTextView.text = screenTitle
        if (screenTitle.isEmpty()) {
            // This removes the view and "flattens" the layout so the RecyclerView moves up
            titleTextView.visibility = View.GONE
        } else {
            titleTextView.visibility = View.VISIBLE
        }

        // 4. Find the RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.playlist_screen_recycler_view)

        // 5. Create your playlist screen list (Reusing the Song data class!)
        val receivedSongs = arguments?.getSerializable("songList") as? ArrayList<Song>
        val songList = receivedSongs ?: listOf() // Fallback to empty if null

        // 6. Reuse the SongAdapter!
        val adapter = SongAdapter(songList)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }
}