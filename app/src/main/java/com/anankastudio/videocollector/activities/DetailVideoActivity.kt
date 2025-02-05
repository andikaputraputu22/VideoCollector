package com.anankastudio.videocollector.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.adapters.DetailVideoAdapter
import com.anankastudio.videocollector.bottomsheet.DownloadBottomSheet
import com.anankastudio.videocollector.bottomsheet.MoreFeatureBottomSheet
import com.anankastudio.videocollector.databinding.ActivityDetailVideoBinding
import com.anankastudio.videocollector.interfaces.OnClickCreator
import com.anankastudio.videocollector.interfaces.OnClickDownload
import com.anankastudio.videocollector.interfaces.OnClickMoreFeature
import com.anankastudio.videocollector.models.item.ContentDetailProfile
import com.anankastudio.videocollector.utilities.Utils
import com.anankastudio.videocollector.utilities.VideoPlayerManager
import com.anankastudio.videocollector.viewmodels.DetailViewModel
import com.anankastudio.videocollector.viewmodels.ResultStatus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailVideoActivity : AppCompatActivity(), OnClickDownload, OnClickMoreFeature {

    @Inject
    lateinit var utils: Utils

    private lateinit var binding: ActivityDetailVideoBinding
    private val viewModel: DetailViewModel by viewModels()
    private var idVideo = 0L
    private var shimmerAnimation: Animation? = null
    private var videoPlayerManager: VideoPlayerManager? = null
    private lateinit var adapter: DetailVideoAdapter
    private val downloadBottomSheet = DownloadBottomSheet()
    private val moreFeatureBottomSheet = MoreFeatureBottomSheet()
    private var urlDownload = ""

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

        binding.favorite.setOnClickListener {
            if (viewModel.onFavorite) {
                viewModel.dataDetailVideo?.id?.let { data ->
                    viewModel.deleteVideoFromFavorite(data)
                }
            } else {
                viewModel.dataDetailVideo?.let { data ->
                    viewModel.saveVideoToFavorite(data)
                }
            }
        }

        binding.download.setOnClickListener {
            showDownloadDialog()
        }

        binding.actionDownload.setOnClickListener {
            showDownloadDialog()
        }

        binding.actionMore.setOnClickListener {
            showMoreFeatureDialog()
        }
    }

    private fun setupListDetail() {
        videoPlayerManager = VideoPlayerManager(this)
        adapter = DetailVideoAdapter(videoPlayerManager)
        binding.rvDetail.layoutManager = LinearLayoutManager(this)
        binding.rvDetail.adapter = adapter
        adapter.onClickCreator = onClickCreator
    }

    private fun showDownloadDialog() {
        val bundle = Bundle()
        bundle.putParcelableArrayList(
            DownloadBottomSheet.ARG_VIDEO_LIST,
            viewModel.dataDetailVideo?.videoFiles?.let { ArrayList(it) }
        )
        downloadBottomSheet.arguments = bundle
        downloadBottomSheet.listener = this
        downloadBottomSheet.show(
            supportFragmentManager,
            DownloadBottomSheet.TAG
        )
    }

    private fun showMoreFeatureDialog() {
        moreFeatureBottomSheet.listener = this
        moreFeatureBottomSheet.show(
            supportFragmentManager,
            MoreFeatureBottomSheet.TAG
        )
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

        viewModel.isVideoOnFavorite.observe(this) {
            binding.favorite.setImageResource(
                if (it) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite
            )
        }

        viewModel.downloadStatus.observe(this) {
            when(it) {
                is ResultStatus.Prepare -> {
                    showToast(getString(R.string.download_video_start))
                }
                is ResultStatus.Success -> {}
                is ResultStatus.Error -> {
                    showToast(getString(R.string.download_video_failed))
                }
            }
        }
    }

    private val onClickCreator = object : OnClickCreator {
        override fun onClickShareCreator(creator: ContentDetailProfile?) {
            creator?.userUrl?.let {
                viewModel.shareContent(it, this@DetailVideoActivity)
            }
        }

        override fun onClickViewCreator(creator: ContentDetailProfile?) {
            creator?.userUrl?.let {
                viewModel.showCreatorProfile(it, this@DetailVideoActivity)
            }
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

    private fun showToast(message: String) {
        Toast.makeText(
            this@DetailVideoActivity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun downloadVideoForShare() {
        val video = viewModel.dataDetailVideo?.videoFiles?.maxByOrNull { it.size ?: 0L }
        video?.link?.let {
            viewModel.downloadVideo(
                this,
                it,
                true
            )
        }
    }

    override fun onClickDownload(url: String) {
        urlDownload = url
        utils.checkStoragePermission(this) {
            viewModel.downloadVideo(this, urlDownload)
        }
    }

    override fun onClickCopyLink() {
        viewModel.dataDetailVideo?.url?.let {
            viewModel.copyUrlVideo(it, this@DetailVideoActivity)
            showToast(getString(R.string.url_copied))
        }
    }

    override fun onClickShareLink() {
        viewModel.dataDetailVideo?.url?.let {
            viewModel.shareContent(it, this@DetailVideoActivity)
        }
    }

    override fun onClickShareFileVideo() {
        viewModel.onShareFileVideo = true
        utils.checkStoragePermission(this) {
            downloadVideoForShare()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        utils.handlePermissionResult(requestCode, grantResults, {
            if (viewModel.onShareFileVideo) {
                downloadVideoForShare()
            } else {
                viewModel.downloadVideo(this, urlDownload)
            }
        }, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseVideo()
    }

    companion object {
        const val EXTRA_ID_VIDEO = "extra_id_video"
    }
}