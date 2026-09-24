package com.example.musicplayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.models.MediaItem
import com.example.musicplayer.R

class ItemAdapter(private val artistList: List<MediaItem>, private val onItemClick: (MediaItem) -> Unit) :
    RecyclerView.Adapter<ItemAdapter.ArtistViewHolder>() {

    // This "finds" the views inside your media_item_recycler_item.xml
    class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.cover)
        val name: TextView = view.findViewById(R.id.name)
        val artist: TextView = view.findViewById(R.id.artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.media_item_recycler_item, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val item = artistList[position]
        holder.name.text = item.name

        if (item.type == 1) {
            holder.artist.text = item.subName
            holder.artist.visibility = View.VISIBLE
        } else {
            // Flatten or hide the text for Artists and Playlists
            holder.artist.text = ""
            holder.artist.visibility = View.GONE
        }
        // SMARTER LOADING:
        // If coverImage is not null, use it. Otherwise, use our default icon.
        val iconRes = when (item.type) {
            0 -> R.drawable.ic_artist
            1 -> R.drawable.ic_album
            else -> R.drawable.ic_playlist
        }
        val imageRes = item.coverImage ?: iconRes
        holder.cover.setImageResource(imageRes)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = artistList.size
}