package com.anankastudio.videocollector.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.adapters.DetailVideoAdapter
import com.anankastudio.videocollector.databinding.ActivityDetailVideoBinding
import com.anankastudio.videocollector.utilities.VideoPlayerManager
import com.anankastudio.videocollector.viewmodels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailVideoBinding
    private val viewModel: DetailViewModel by viewModels()
    private var idVideo = 0L
    private var shimmerAnimation: Animation? = null
    private var videoPlayerManager: VideoPlayerManager? = null
    private lateinit var adapter: DetailVideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shimmerAnimation = AnimationUtils.loadAnimation(this, R.anim.shimmer_animation)
        idVideo = intent.getLongExtra(EXTRA_ID_VIDEO, 0L)

        setupStatusBar()
        setupListDetail()
        setupClickListener()
        observeData()
        viewModel.getDetailVideo(idVideo)
    }

    private fun setupStatusBar() {
        window?.apply {
            statusBarColor = ContextCompat.getColor(this@DetailVideoActivity, R.color.bg_color)
            navigationBarColor = ContextCompat.getColor(this@DetailVideoActivity, R.color.bg_color)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                decorView.windowInsetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
                decorView.windowInsetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            } else {
                val windowInsetsController = WindowInsetsControllerCompat(this, decorView)
                windowInsetsController.isAppearanceLightStatusBars = false
                windowInsetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    private fun setupClickListener() {
        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun setupListDetail() {
        videoPlayerManager = VideoPlayerManager(this)
        adapter = DetailVideoAdapter(videoPlayerManager)
        binding.rvDetail.layoutManager = LinearLayoutManager(this)
        binding.rvDetail.adapter = adapter
    }

    private fun observeData() {
        viewModel.loading.observe(this) {
            if (it) {
                binding.contentContainer.visibility = View.GONE
                binding.shimmerContainer.visibility = View.VISIBLE
                binding.shimmerContainer.startAnimation(shimmerAnimation)
            } else {
                binding.contentContainer.visibility = View.VISIBLE
                binding.shimmerContainer.visibility = View.GONE
                binding.shimmerContainer.clearAnimation()
            }
        }

        viewModel.title.observe(this) {
            binding.title.text = it
        }

        viewModel.listContent.observe(this) {
            adapter.setData(it)
        }
    }

    private fun manageVideoPlayback(action: (VideoPlayerManager?) -> Unit) {
        action(videoPlayerManager)
    }

    private fun releaseVideo() {
        manageVideoPlayback {
            it?.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseVideo()
    }

    companion object {
        const val EXTRA_ID_VIDEO = "extra_id_video"
    }
}