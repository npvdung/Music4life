package com.example.beethozart

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.beethozart.databinding.ActivityMainBinding
import com.example.beethozart.fragments.SongManagerFragmentDirections
import com.example.beethozart.services.MusicPlayerService
import com.example.beethozart.utils.SongDatabaseBuilder
import com.example.beethozart.viewmodels.PlayerViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.coroutines.*


const val CHANNEL_ID = "channel"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mainActivityJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + mainActivityJob)

    private lateinit var musicPlayerServiceBinder: MusicPlayerService.MusicPlayerServiceBinder

    private lateinit var playerViewModel: PlayerViewModel

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            musicPlayerServiceBinder = service as MusicPlayerService.MusicPlayerServiceBinder

            initializePlayerViewModel()
            playerViewModel.musicPlayerServiceBinder = musicPlayerServiceBinder
        }

        override fun onServiceDisconnected(name: ComponentName) {
            TODO("Not yet implemented")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        // startForegroundService(Intent(this, MusicPlayerService::class.java))

        Intent(this, MusicPlayerService::class.java).also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.topAppBar)

        val bottomNavigationView = binding.bottomNavigation
        val navController = this.findNavController(R.id.navHostFragment)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        //call back after permission granted
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(this@MainActivity,
                    "Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(
                    this@MainActivity,
                    "Permission Denied\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //check all needed permissions together
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
            )
            .check()

        uiScope.launch {
            buildDatabase()
        }
    }

    suspend fun buildDatabase() {
        withContext(Dispatchers.IO) {
            SongDatabaseBuilder(application).build()
        }
    }

    private fun initializePlayerViewModel() {
        playerViewModel = ViewModelProvider(this@MainActivity)
                .get(PlayerViewModel::class.java)

        binding.playerViewModel = playerViewModel

        playerViewModel.isAttachedToPlayerFragment.observe(this, {isAttached ->
            if (playerViewModel.isPlaying && !isAttached) {
                binding.miniPlayer.visibility = View.VISIBLE
            }
            else {
                binding.miniPlayer.visibility = View.GONE
            }
        })

        binding.miniPlayer.setOnClickListener {
            this.findNavController(R.id.navHostFragment).navigate(
                SongManagerFragmentDirections.actionSongManagerFragmentToPlayerFragment(playerViewModel.songList!!)
            )
            binding.invalidateAll()
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivityJob.cancel()
    }
}