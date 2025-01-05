package com.anankastudio.videocollector.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.anankastudio.videocollector.databinding.ActivityDetailVideoBinding
import com.anankastudio.videocollector.fragments.FragmentHome
import com.anankastudio.videocollector.viewmodels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailVideoBinding
    private val viewModel: DetailViewModel by viewModels()
    private var idVideo = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idVideo = intent.getLongExtra(FragmentHome.EXTRA_ID_VIDEO, 0L)

        viewModel.getDetailVideo(idVideo)
    }
}