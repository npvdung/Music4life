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
import com.example.beethozart.databinding.FragmentArtistSongListBinding
import com.example.beethozart.fragments.adapters.SongAdapter
import com.example.beethozart.fragments.adapters.SongListener
import com.example.beethozart.viewmodels.ArtistSongListViewModel
import com.example.beethozart.viewmodels.factories.ArtistSongListViewModelFactory
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 * Use the [ArtistSongListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArtistSongListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentArtistSongListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_artist_song_list, container, false
        )

        val args = ArtistSongListFragmentArgs.fromBundle(requireArguments())

        val application = requireActivity().application
        // val viewModel = ViewModelProvider(activity!!, ArtistSongListViewModelFactory(application,
        //     args.songList)).get(ArtistSongListViewModel::class.java)
        val viewModel: ArtistSongListViewModel by navGraphViewModels(R.id.nestedArtistSongList) {
            ArtistSongListViewModelFactory(application, args.songList)
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
                    ArtistSongListFragmentDirections.actionArtistSongListFragmentToPlayerManagerFragment(viewModel.getSongList())
                )

                binding.invalidateAll()
                viewModel.onPlayerNavigated()
            }
        })

        return binding.root
    }
}