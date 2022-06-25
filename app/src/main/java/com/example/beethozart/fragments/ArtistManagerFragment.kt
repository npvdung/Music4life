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
import com.example.beethozart.databinding.FragmentArtistManagerBinding
import com.example.beethozart.entities.SongList
import com.example.beethozart.fragments.adapters.ArtistAdapter
import com.example.beethozart.fragments.adapters.ArtistListener
import com.example.beethozart.viewmodels.ArtistManagerViewModel
import kotlinx.android.synthetic.main.fragment_artist_manager.*
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 * Use the [ArtistManagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArtistManagerFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentArtistManagerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_artist_manager, container, false
        )
        val viewModel = ViewModelProvider(this).get(ArtistManagerViewModel::class.java)
        val adapter = ArtistAdapter(ArtistListener { viewModel.onArtistClicked(it) })

        binding.lifecycleOwner = this
        binding.artistList.adapter = adapter

        viewModel.songList.observe(viewLifecycleOwner, {
            viewModel.createArtistList(it)
        })

        viewModel.artistList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.clickedArtist.observe(viewLifecycleOwner, {
            it?.let {
                this.findNavController().navigate(
                    ArtistManagerFragmentDirections.actionArtistManagerFragmentToNestedArtistSongList(
                        SongList(it.songs)
                    )
                )

                binding.invalidateAll()
                viewModel.onArtistSongListNavigated()
            }
        })

        return binding.root
    }
}