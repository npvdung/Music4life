package com.example.beethozart.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.beethozart.R
import com.example.beethozart.databases.SongDatabase
import com.example.beethozart.databinding.OnlineHomeBinding
import com.example.beethozart.fragments.adapters.SongAdapter
import com.example.beethozart.fragments.adapters.SongListener
import com.example.beethozart.network.HistoryProperty
import com.example.beethozart.viewmodels.OnlineHomeViewModel
import com.example.beethozart.viewmodels.factories.OnlineHomeViewModelFactory


class OnlineHomeFragment : Fragment() {
    private var isSignIn = true
    private lateinit var viewModel: OnlineHomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: OnlineHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.online_home, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = SongDatabase.getInstance(application).songDatabaseDao

        val viewModelFactory = OnlineHomeViewModelFactory(dataSource, application)

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(OnlineHomeViewModel::class.java)

        val adapter = SongAdapter(
            SongListener {
                viewModel.onSongClicked(it)
            },
            SongListener {
            }
        )
        val onlineSongList = binding.searchSongList
        onlineSongList.adapter = adapter
        viewModel.listSong.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })


        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                isSignIn = false
                activity?.invalidateOptionsMenu()
            }
            if (it.size == 1) {
                viewModel.getHistory(it[0].username)
                isSignIn = true
                activity?.invalidateOptionsMenu()
            }
        })

        viewModel.historySongList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.listSong.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Log.d("aaa", it[0].title)
            }
        })

        viewModel.currentSong.observe(viewLifecycleOwner, {
            it?.let {
                this.findNavController().navigate(
                    OnlineHomeFragmentDirections.actionOnlineHomeFragmentToPlayerManagerFragment(
                        viewModel.getSongList()
                    )
                )
                if (isSignIn) {
                    val username = viewModel.currentUser.value?.get(0)?.username
                    val title = it.title
                    if (username != null) {
                        viewModel.postHistory(HistoryProperty(username, title))
                    }
                }
                binding.invalidateAll()
                viewModel.onPlayerNavigated()
            }
        })

        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.online_music_menu, menu)

        // Sign in - Sign up - Log out
        val itemLogOut = menu.findItem(R.id.logOut)
        val itemSignUp = menu.findItem(R.id.signUpFragment)
        val itemSignIn = menu.findItem(R.id.signInFragment)
        if (!isSignIn) {
            itemSignIn.isVisible = true
            itemSignUp.isVisible = true
            itemLogOut.isVisible = false
        } else {
            itemSignIn.isVisible = false
            itemSignUp.isVisible = false
            itemLogOut.isVisible = true
        }


        // Search bar
        val searchItem = menu.findItem(R.id.online_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.queryHint = "Search your song or artist"
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        viewModel.getSong(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        Log.d("aaa", newText)
                    }
                    return true
                }

            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logOut -> {
                viewModel.clearUser()
                isSignIn = false
                activity?.invalidateOptionsMenu()
                true
            }
            else -> {
                NavigationUI.onNavDestinationSelected(
                    item!!,
                    requireView().findNavController()
                )
                        || super.onOptionsItemSelected(item)
            }
        }

    }


}