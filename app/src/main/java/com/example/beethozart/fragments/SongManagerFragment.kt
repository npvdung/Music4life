package com.example.beethozart.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.beethozart.R
import com.example.beethozart.databinding.FragmentSongManagerBinding
import com.example.beethozart.fragments.adapters.SongAdapter
import com.example.beethozart.fragments.adapters.SongListener
import com.example.beethozart.viewmodels.SongManagerViewModel
import kotlinx.android.synthetic.main.fragment_player.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SongManagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SongManagerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding: FragmentSongManagerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_song_manager, container, false
        )

        val viewModel = ViewModelProvider(requireActivity()).get(SongManagerViewModel::class.java)
        val adapter = SongAdapter(
            SongListener {
                viewModel.onSongClicked(it)
            },
            SongListener {
                viewModel.onAddToPlaylist(it, requireActivity())
            }
        )
        val songList = binding.songList

        songList.adapter = adapter
        binding.lifecycleOwner = this

        songList.addItemDecoration(DividerItemDecoration(songList.context, DividerItemDecoration.HORIZONTAL))
        binding.invalidateAll()

        viewModel.songList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.currentSong.observe(viewLifecycleOwner, {
            it?.let {
                this.findNavController().navigate(
                    SongManagerFragmentDirections.actionSongManagerFragmentToPlayerFragment(viewModel.getSongList())
                )

                binding.invalidateAll()
                viewModel.onPlayerNavigated()
            }
        })

        // binding.miniPlayer.setOnClickListener {
        //     this.findNavController().navigate(
        //         SongManagerFragmentDirections.actionSongManagerFragmentToPlayerFragment(viewModel.getSongList())
        //     )
        // }

        return binding.root
    }
}