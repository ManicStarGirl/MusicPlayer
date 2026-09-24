package com.example.musicplayer.adapters

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.models.Song

class SongAdapter(private val songList: List<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    // This "finds" the views inside your song_recycler_item.xml
    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songTitle: TextView = view.findViewById(R.id.song_title)
        val songArtist: TextView = view.findViewById(R.id.song_artist)
        val songDuration: TextView = view.findViewById(R.id.song_duration)
        val songCover: ImageView = view.findViewById(R.id.song_cover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_recycler_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        holder.songTitle.text = song.title
        holder.songArtist.text = song.artist
        holder.songDuration.text = song.duration
        val onBackgroundColor = TypedValue()
        val appBackgroundColor = TypedValue()
        holder.itemView.context.theme.resolveAttribute(R.attr.onBackground, onBackgroundColor, true)
        holder.itemView.context.theme.resolveAttribute(R.attr.appBackground, appBackgroundColor, true)
        if (song.isPlaying) {
            holder.itemView.setBackgroundColor(onBackgroundColor.data)
            holder.songCover.setColorFilter(onBackgroundColor.data)
            holder.songCover.setBackgroundColor(appBackgroundColor.data)
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            holder.songCover.setColorFilter(appBackgroundColor.data)
            holder.songCover.setBackgroundColor(onBackgroundColor.data)
        }

        // SMARTER LOADING:
        // If coverImage is not null, use it. Otherwise, use our default icon.
        val imageRes = song.coverImage ?: R.drawable.ic_playlist
        holder.songCover.setImageResource(imageRes)
    }

    override fun getItemCount() = songList.size
}