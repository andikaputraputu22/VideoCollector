package com.anankastudio.videocollector.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.adapters.VideoAdapter
import com.anankastudio.videocollector.databinding.FragmentHomeBinding
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
        observeData()
        checkScrollVideoList()
        binding.swipeRefresh.setOnRefreshListener {
            firstLoadVideo()
        }
        firstLoadVideo()
    }

    private fun setupListVideo() {
        binding.rvVideo.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvVideo.adapter = videoAdapter
        val space = resources.getDimensionPixelSize(R.dimen.item_spacing_video)
        binding.rvVideo.addItemDecoration(SpaceItemDecoration(space))
    }

    private fun firstLoadVideo() {
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
    }

    private fun checkScrollVideoList() {
        binding.rvVideo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.rvVideo.layoutManager as StaggeredGridLayoutManager
                val visibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                val lastVisibleItemPosition = visibleItemPositions.maxOrNull() ?: 0
                val totalItemCount = layoutManager.itemCount
                if (lastVisibleItemPosition == totalItemCount - 1
                    && viewModel.page <= viewModel.pageTotal
                    && viewModel.loadingMore.value == false) {
                    viewModel.loadingMore.value = true
                    viewModel.getPopularVideo()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}