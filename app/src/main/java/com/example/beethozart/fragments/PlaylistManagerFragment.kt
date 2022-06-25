package com.example.beethozart.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.beethozart.R
import com.example.beethozart.databinding.FragmentPlaylistManagerBinding
import com.example.beethozart.entities.SongList
import com.example.beethozart.fragments.adapters.PlaylistAdapter
import com.example.beethozart.fragments.adapters.PlaylistListener
import com.example.beethozart.viewmodels.PlaylistViewModel
import kotlinx.android.synthetic.main.fragment_song_manager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class PlaylistManagerFragment : Fragment() {
    private val fragmentJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + fragmentJob)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentPlaylistManagerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_playlist_manager, container, false
        )

        val viewModel = ViewModelProvider(this).get(PlaylistViewModel::class.java)
        val adapter = PlaylistAdapter(PlaylistListener { viewModel.onPlaylistClicked(it) })

        binding.lifecycleOwner = this
        binding.playlistList.adapter = adapter
        binding.playlistViewModel = viewModel

        viewModel.playlistList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        binding.addPlaylistButton.setOnClickListener {
            viewModel.onAddPlaylist(requireActivity())
        }

        viewModel.clickedPlaylist.observe(viewLifecycleOwner, {
            it?.let {
                uiScope.launch {
                    val songs = viewModel.getSongOf(it)
                    findNavController().navigate(
                        PlaylistManagerFragmentDirections.actionPlaylistManagerFragmentToNestedPlaylistSongList(
                            SongList(songs)
                        )
                    )
                }

                binding.invalidateAll()
                viewModel.onPlaylistSongListNavigated()
            }
        })

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentJob.cancel()
    }
}