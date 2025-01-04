package com.anankastudio.videocollector.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.ActivityDetailVideoBinding

class DetailVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}