package com.anankastudio.videocollector.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.activities.DetailVideoActivity
import com.anankastudio.videocollector.adapters.VideoAdapter
import com.anankastudio.videocollector.bottomsheet.ConfirmBottomSheet
import com.anankastudio.videocollector.databinding.FragmentFavoriteBinding
import com.anankastudio.videocollector.interfaces.OnClickConfirm
import com.anankastudio.videocollector.interfaces.OnClickVideo
import com.anankastudio.videocollector.utilities.SpaceItemDecoration
import com.anankastudio.videocollector.viewmodels.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentFavorite : Fragment(), OnClickConfirm {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoriteViewModel
    private val videoAdapter = VideoAdapter()
    private val confirmBottomSheet = ConfirmBottomSheet()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListVideo()
        setupClickListener()
        observeData()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getAllFavoriteVideo()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllFavoriteVideo()
    }

    private fun setupListVideo() {
        binding.rvVideoFavorite.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvVideoFavorite.adapter = videoAdapter
        videoAdapter.onClickVideo = onClickVideo
        val space = resources.getDimensionPixelSize(R.dimen.item_spacing_video)
        binding.rvVideoFavorite.addItemDecoration(SpaceItemDecoration(space))
    }

    private fun setupClickListener() {
        binding.delete.setOnClickListener {
            showConfirmDeleteFavorite()
        }
    }

    private fun observeData() {
        viewModel.listFavoriteVideo.observe(viewLifecycleOwner) {
            it?.let { listFavoriteVideo ->
                videoAdapter.setDataFavorite(listFavoriteVideo)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it
        }

        viewModel.isDataAvailable.observe(viewLifecycleOwner) {
            binding.delete.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private val onClickVideo = object : OnClickVideo {
        override fun onClickDetail(id: Long) {
            val intent = Intent(requireContext(), DetailVideoActivity::class.java)
            intent.putExtra(DetailVideoActivity.EXTRA_ID_VIDEO, id)
            startActivity(intent)
        }
    }

    private fun showConfirmDeleteFavorite() {
        confirmBottomSheet.listener = this
        confirmBottomSheet.show(
            childFragmentManager,
            ConfirmBottomSheet.TAG
        )
    }

    override fun onClickYes() {
        viewModel.deleteAllFavoriteVideo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}