package com.example.musicplayer.ui.mainfragmentcontainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.adapters.ItemAdapter
import com.example.musicplayer.models.MediaItem

class MusicCompilationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Inflate the layout
        val view = inflater.inflate(R.layout.fragment_album, container, false)

        // 2. Find the RecyclerView inside the inflated view
        val recyclerView = view.findViewById<RecyclerView>(R.id.album_recycler_view)

        // NEW: Check if we were told to show Playlists or Artists
        val isPlaylistMode = arguments?.getBoolean("isPlaylist", false) ?: false
        val isArtistMode = arguments?.getBoolean("isArtist", false) ?: false
        val isAlbumMode = arguments?.getBoolean("isAlbum", false) ?: false

        // 3. Create your Queue list (Reusing the Song data class!)
        val playlistList = if (isPlaylistMode) {
            // NEW: Look for the 'itemList' we passed in the bundle (The Artist's albums)
            val receivedItems = arguments?.getSerializable("itemList") as? ArrayList<MediaItem>
            receivedItems ?: listOf(
                // Default dummy albums if the list is empty
                MediaItem("Add some music to get started!", null, 2, R.drawable.ic_plus_sign)
            )
        } else if (isArtistMode) {
            // NEW: Look for the 'itemList' we passed in the bundle (The Artist's albums)
            val receivedItems = arguments?.getSerializable("itemList") as? ArrayList<MediaItem>
            receivedItems ?: listOf()
        } else if (isAlbumMode) {
            // NEW: Look for the 'itemList' we passed in the bundle (The Artist's albums)
            val receivedItems = arguments?.getSerializable("itemList") as? ArrayList<MediaItem>
            receivedItems ?: listOf()

        } else listOf()

        val adapter = ItemAdapter(playlistList) { clickedItem ->
            val bundle = Bundle()
            // Handle item click here
            val fragment = if (clickedItem.type == 0) { // It's an ARTIST
                MusicCompilationFragment().apply {
                    // Pass the artist's albums to the next grid
                    bundle.putSerializable("itemList", ArrayList(clickedItem.albums ?: listOf()))
                    bundle.putInt("compilationMode", 1) // Set next screen to Album mode
                    bundle.putBoolean("isAlbum", true)   // Tell next screen: Show Albums
                    bundle.putBoolean("isArtist", false) // Tell next screen: Not Artists
                    bundle.putBoolean("isPlaylist", false) // Tell next screen: Not Playlists
                }
            } else { // It's an ALBUM or PLAYLIST
                SongListFragment().apply {
                    bundle.putSerializable("songList", ArrayList(clickedItem.songs ?: listOf()))
                }
            }

            // Common data for both fragment types
            bundle.putString("playlistName", clickedItem.name)
            fragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Allows the user to go "Back" to the list
                .commit()
        }

        // 5. Ensure LayoutManager is set (Grid)
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false

        return view
    }
}