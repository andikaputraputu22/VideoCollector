package com.anankastudio.videocollector.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.activities.DetailVideoActivity
import com.anankastudio.videocollector.adapters.VideoAdapter
import com.anankastudio.videocollector.databinding.FragmentHomeBinding
import com.anankastudio.videocollector.interfaces.OnClickVideo
import com.anankastudio.videocollector.utilities.SpaceItemDecoration
import com.anankastudio.videocollector.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private val videoAdapter = VideoAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListVideo()
        setupClickListener()
        observeData()
        checkScrollVideoList()
        binding.swipeRefresh.setOnRefreshListener {
            firstLoadVideo()
        }
        firstLoadVideo()
        viewModel.getWidgetVideo()
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
        viewModel.getPopularVideo()
    }

    private fun observeData() {
        viewModel.listVideo.observe(viewLifecycleOwner) {
            it?.let { listVideo ->
                videoAdapter.setData(listVideo)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it
        }

        viewModel.loadingMore.observe(viewLifecycleOwner) {
            binding.progressIndicator.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isDataAvailable.observe(viewLifecycleOwner) {
            binding.notFoundContainer.visibility = if (it) View.GONE else View.VISIBLE
        }
    }

    private val onClickVideo = object : OnClickVideo {
        override fun onClickDetail(id: Long) {
            val intent = Intent(requireContext(), DetailVideoActivity::class.java)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}