package com.example.musicplayer.ui

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.musicplayer.R
import com.example.musicplayer.ui.mainfragmentcontainer.FullScreenPlayerFragment
import com.example.musicplayer.ui.mainfragmentcontainer.LibraryFragment
import com.example.musicplayer.ui.mainfragmentcontainer.MusicCompilationFragment

class MainActivity : AppCompatActivity() {
    private var isPlaying = false
    private var isShuffling = false
    private var isRepeating = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnPlaylists = findViewById<ImageButton>(R.id.btn_playlists)
        val btnLibrary = findViewById<ImageButton>(R.id.btn_music_library)
        val btnFullScreenPlayer = findViewById<ImageButton>(R.id.btn_full_screen_player)

        setupToggleButtons()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set a default screen when the app opens
        replaceFragment(LibraryFragment())

        btnPlaylists.setOnClickListener {
            val fragment = MusicCompilationFragment()
            val bundle = Bundle()
            bundle.putInt("compilationMode", 2) // 2 = Playlist Mode
            bundle.putBoolean("isPlaylist", true) // Instruction: Show Playlists
            fragment.arguments = bundle
            replaceFragment(fragment)
        }

        btnLibrary.setOnClickListener {
            replaceFragment(LibraryFragment())
        }

        btnFullScreenPlayer.setOnClickListener {
            replaceFragment(FullScreenPlayerFragment())
        }

    }

    // Helper function to swap the "window" content
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setupToggleButtons() {
        val btnPlay = findViewById<ImageButton>(R.id.btn_play)
        val btnShuffle = findViewById<ImageButton>(R.id.btn_shuffle)
        val btnRepeat = findViewById<ImageButton>(R.id.btn_repeat)

        btnPlay.setOnClickListener {
            isPlaying = !isPlaying
            btnPlay.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow)
        }

        btnShuffle.setOnClickListener {
            isShuffling = !isShuffling
            btnShuffle.setImageResource(if (isShuffling) R.drawable.ic_shuffle_on else R.drawable.ic_shuffle_off)
        }

        btnRepeat.setOnClickListener {
            isRepeating = !isRepeating
            btnRepeat.setImageResource(if (isRepeating) R.drawable.ic_repeat_on else R.drawable.ic_repeat_off)
        }
    }
}