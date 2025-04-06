package com.anankastudio.videocollector.fragments

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.activities.DetailVideoActivity
import com.anankastudio.videocollector.adapters.VideoAdapter
import com.anankastudio.videocollector.bottomsheet.CustomizeWidgetBottomSheet
import com.anankastudio.videocollector.bottomsheet.MenuBottomSheet
import com.anankastudio.videocollector.databinding.FragmentHomeBinding
import com.anankastudio.videocollector.interfaces.OnClickCustomizeWidget
import com.anankastudio.videocollector.interfaces.OnClickMenu
import com.anankastudio.videocollector.interfaces.OnClickVideo
import com.anankastudio.videocollector.utilities.CustomProgressDialog
import com.anankastudio.videocollector.utilities.LetterSpacingSpan
import com.anankastudio.videocollector.utilities.SpaceItemDecoration
import com.anankastudio.videocollector.viewmodels.HomeViewModel
import com.anankastudio.videocollector.widget.SlideWidgetHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentHome : Fragment(), OnClickMenu, OnClickCustomizeWidget {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private val videoAdapter = VideoAdapter()
    private val menuBottomSheet = MenuBottomSheet()
    private val customizeWidgetBottomSheet = CustomizeWidgetBottomSheet()
    private lateinit var progressDialog: CustomProgressDialog

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

        progressDialog = CustomProgressDialog(requireContext())
        progressDialog.setCancelable(false)

        setupLogo()
        setupListVideo()
        setupClickListener()
        observeData()
        checkScrollVideoList()
        binding.swipeRefresh.setOnRefreshListener {
            firstLoadVideo()
        }
        firstLoadVideo()
    }

    private fun setupLogo() {
        val titleLogo = SpannableString(getString(R.string.app_name_uppercase))
        titleLogo.setSpan(LetterSpacingSpan(-0.1f), 1, titleLogo.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.titleLogo.text = titleLogo
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

        binding.more.setOnClickListener {
            showMenuBottomSheet()
        }

        binding.customizeWidget.setOnClickListener {
            showCustomizeWidgetBottomSheet()
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

        viewModel.loadingWidget.observe(viewLifecycleOwner) {
            if (it) progressDialog.show() else progressDialog.dismiss()
        }

        viewModel.isDataAvailable.observe(viewLifecycleOwner) {
            binding.notFoundContainer.visibility = if (it) View.GONE else View.VISIBLE
        }

        viewModel.isWidgetSetupSuccess.observe(viewLifecycleOwner) {
            if (it) {
                SlideWidgetHelper.sendWidgetUpdateBroadcast(requireContext())
                Toast.makeText(
                    requireContext(),
                    getString(R.string.setup_widget_success),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.setup_widget_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val onClickVideo = object : OnClickVideo {
        override fun onClickDetail(id: Long) {
            val intent = Intent(requireContext(), DetailVideoActivity::class.java)
            intent.putExtra(DetailVideoActivity.EXTRA_ID_VIDEO, id)
            startActivity(intent)
        }
    }

    private fun showMenuBottomSheet() {
        menuBottomSheet.show(
            childFragmentManager,
            MenuBottomSheet.TAG
        )
    }

    private fun showCustomizeWidgetBottomSheet() {
        customizeWidgetBottomSheet.listener = this
        customizeWidgetBottomSheet.show(
            childFragmentManager,
            CustomizeWidgetBottomSheet.TAG
        )
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

    override fun onClickSendFeedback() {

    }

    override fun onClickApplyCustomize() {
        viewModel.getWidgetVideo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}