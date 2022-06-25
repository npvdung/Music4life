
package com.example.beethozart.fragments.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beethozart.R
import com.example.beethozart.databinding.ListItemSongBinding
import com.example.beethozart.entities.Song
import timber.log.Timber


class SongAdapter(
    private val clickListener: SongListener,
    private val moreClickListener: SongListener
    ): ListAdapter<Song, SongAdapter.ViewHolder>(SongDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, moreClickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: ListItemSongBinding, private val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: SongListener, moreClickListener: SongListener, item: Song) {
            binding.song = item
            binding.clickListener = clickListener
            binding.moreClickListener = moreClickListener

            binding.moreMenuButton.setOnClickListener {
                val popupMenu = PopupMenu(context, binding.moreButton)

                popupMenu.inflate(R.menu.song_popup_menu)

                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.addToPlaylist -> {
                            moreClickListener.onClick(item)
                            true
                        }
                        else -> true
                    }
                }

                popupMenu.show()
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                    val inflater = LayoutInflater.from(parent.context)
                    val binding = ListItemSongBinding.inflate(inflater, parent, false)
                    return ViewHolder(binding, parent.context)
            }
        }
    }
}


class SongDiffCallback: DiffUtil.ItemCallback<Song>() {
    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.songId == newItem.songId
    }
}


class SongListener(val clickListener: (song: Song) -> Unit) {
    fun onClick(song: Song) {
        clickListener(song)
    }
}
