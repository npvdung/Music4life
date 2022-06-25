package com.example.beethozart.fragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beethozart.R
import com.example.beethozart.entities.Playlist
import com.example.beethozart.databinding.ListItemPlaylistBinding

class PlaylistAdapter(
    private val playlistListener: PlaylistListener
    ): ListAdapter<Playlist, PlaylistAdapter.ViewHolder>(PlaylistDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(playlistListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: ListItemPlaylistBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(playlistListener: PlaylistListener, item: Playlist) {
            binding.playlist = item
            binding.clickListener = playlistListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: ListItemPlaylistBinding = DataBindingUtil.inflate(
                    layoutInflater, R.layout.list_item_playlist, parent, false
                )

                return ViewHolder(binding)
            }
        }

    }
}


class PlaylistDiffCallback: DiffUtil.ItemCallback<Playlist>() {
    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.playlistName == newItem.playlistName
    }
}

class PlaylistListener(val playlistListener: (playlist: Playlist) -> Unit) {
    fun onClick(playlist: Playlist) {
        playlistListener(playlist)
    }
}