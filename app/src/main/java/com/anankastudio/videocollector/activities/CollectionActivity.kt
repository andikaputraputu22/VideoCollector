package com.anankastudio.videocollector.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.adapters.VideoAdapter
import com.anankastudio.videocollector.databinding.ActivityCollectionBinding
import com.anankastudio.videocollector.interfaces.OnClickVideo
import com.anankastudio.videocollector.utilities.SpaceItemDecoration
import com.anankastudio.videocollector.viewmodels.CollectionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCollectionBinding
    private val viewModel: CollectionViewModel by viewModels()
    private val videoAdapter = VideoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.collectionId = intent.getStringExtra(EXTRA_ID_COLLECTION) ?: ""
        val title = intent.getStringExtra(EXTRA_TITLE_COLLECTION)
        binding.title.text = title

        setupStatusBar()
        setupListVideo()
        setupClickListener()
        observeData()
        checkScrollVideoList()
        binding.swipeRefresh.setOnRefreshListener {
            firstLoadVideo()
        }
        firstLoadVideo()
    }

    private fun setupStatusBar() {
        window?.apply {
            statusBarColor = ContextCompat.getColor(this@CollectionActivity, R.color.bg_color)
            navigationBarColor = ContextCompat.getColor(this@CollectionActivity, R.color.bg_color)
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

    private fun setupListVideo() {
        binding.rvVideo.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvVideo.adapter = videoAdapter
        videoAdapter.onClickVideo = onClickVideo
        val space = resources.getDimensionPixelSize(R.dimen.item_spacing_video)
        binding.rvVideo.addItemDecoration(SpaceItemDecoration(space))
    }

    private fun setupClickListener() {
        binding.scrollToTop.setOnClickListener {
            binding.rvVideo.smoothScrollToPosition(0)
        }
    }

    private fun firstLoadVideo() {
        if (viewModel.loading.value == true
            || viewModel.loadingMore.value == true) return
        videoAdapter.clear()
        viewModel.page = 1
        viewModel.pageTotal = 1
        viewModel.getCollectionVideo()
    }

    private fun observeData() {
        viewModel.listVideo.observe(this) {
            it?.let { listVideo ->
                videoAdapter.setData(listVideo)
            }
        }

        viewModel.loading.observe(this) {
            binding.swipeRefresh.isRefreshing = it
        }

        viewModel.loadingMore.observe(this) {
            binding.progressIndicator.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isDataAvailable.observe(this) {
            binding.notFoundContainer.visibility = if (it) View.GONE else View.VISIBLE
        }
    }

    private val onClickVideo = object : OnClickVideo {
        override fun onClickDetail(id: Long) {
            val intent = Intent(this@CollectionActivity, DetailVideoActivity::class.java)
            intent.putExtra(DetailVideoActivity.EXTRA_ID_VIDEO, id)
            startActivity(intent)
        }
    }

    private fun checkScrollVideoList() {
        binding.rvVideo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1)) {
                    binding.scrollToTop.hide()
                } else {
                    if (dy > 0 && binding.scrollToTop.visibility != View.VISIBLE) {
                        binding.scrollToTop.show()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = binding.rvVideo.layoutManager as StaggeredGridLayoutManager
                    val visibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                    val lastVisibleItemPosition = visibleItemPositions.maxOrNull() ?: 0
                    val totalItemCount = layoutManager.itemCount

                    if (lastVisibleItemPosition == totalItemCount - 1) {
                        viewModel.onScrolledToEnd()
                    }
                }
            }
        })
    }

    companion object {
        const val EXTRA_ID_COLLECTION = "extra_id_collection"
        const val EXTRA_TITLE_COLLECTION = "extra_title_collection"
    }
}