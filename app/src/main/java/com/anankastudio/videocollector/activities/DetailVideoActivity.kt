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
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.ActivityDetailVideoBinding
import com.anankastudio.videocollector.fragments.FragmentHome
import com.anankastudio.videocollector.viewmodels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailVideoBinding
    private val viewModel: DetailViewModel by viewModels()
    private var idVideo = 0L
    private var shimmerAnimation: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shimmerAnimation = AnimationUtils.loadAnimation(this, R.anim.shimmer_animation)
        idVideo = intent.getLongExtra(FragmentHome.EXTRA_ID_VIDEO, 0L)

        setupStatusBar()
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
    }
}