package com.example.beethozart.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.beethozart.R
import com.example.beethozart.databinding.FragmentPlaylistSongListBinding
import com.example.beethozart.fragments.adapters.SongAdapter
import com.example.beethozart.fragments.adapters.SongListener
import com.example.beethozart.viewmodels.PlaylistSongListViewModel
import com.example.beethozart.viewmodels.factories.PlaylistSongListViewModelFactory
import timber.log.Timber


class PlaylistSongListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentPlaylistSongListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_playlist_song_list, container, false
        )

        val args = ArtistSongListFragmentArgs.fromBundle(requireArguments())
        val application = requireActivity().application

        val viewModel: PlaylistSongListViewModel by navGraphViewModels(R.id.nestedPlaylistSongList) {
            PlaylistSongListViewModelFactory(application, args.songList)
        }
        val adapter = SongAdapter(
            SongListener {
                viewModel.onSongClicked(it)
            },
            SongListener {
                Timber.i("more clicked")
                Toast.makeText(context, "more clicked", Toast.LENGTH_LONG).show()
            }
        )

        val songList = binding.playlistSongList

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
                    PlaylistSongListFragmentDirections.actionPlaylistSongListFragmentToPlayerManagerFragment(viewModel.getSongList())
                )

                binding.invalidateAll()
                viewModel.onPlayerNavigated()
            }
        })

        return binding.root
    }
}