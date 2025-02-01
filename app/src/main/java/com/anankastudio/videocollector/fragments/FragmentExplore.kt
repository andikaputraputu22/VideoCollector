package com.anankastudio.videocollector.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.activities.CollectionActivity
import com.anankastudio.videocollector.activities.DetailVideoActivity
import com.anankastudio.videocollector.adapters.ExploreAdapter
import com.anankastudio.videocollector.adapters.SearchHistoryAdapter
import com.anankastudio.videocollector.bottomsheet.FilterBottomSheet
import com.anankastudio.videocollector.databinding.FragmentExploreBinding
import com.anankastudio.videocollector.interfaces.OnClickCollection
import com.anankastudio.videocollector.interfaces.OnClickFilter
import com.anankastudio.videocollector.interfaces.OnClickVideo
import com.anankastudio.videocollector.interfaces.OnSelectSearchHistory
import com.anankastudio.videocollector.models.Video
import com.anankastudio.videocollector.utilities.Constants
import com.anankastudio.videocollector.utilities.SpaceItemDecoration
import com.anankastudio.videocollector.viewmodels.ExploreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentExplore : Fragment(), OnClickFilter {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: ExploreViewModel
    private var clearButtonDrawable: Drawable? = null
    private val exploreAdapter = ExploreAdapter()
    private val filterBottomSheet = FilterBottomSheet()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ExploreViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.typeContent = Constants.TYPE_CONTENT_COLLECTION
        clearButtonDrawable = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_clear
        )
        updateClearButtonVisibility()
        setupClickListener()
        setupListSearchHistory()
        setupListVideo()
        observeData()
        checkScrollVideoList()
        firstLoadCollection()
        checkFilter()
        binding.swipeRefresh.setOnRefreshListener {
            if (viewModel.typeContent == Constants.TYPE_CONTENT_COLLECTION) {
                firstLoadCollection()
            } else {
                firstLoadVideo()
            }
        }
    }

    override fun onClickApplyFilters() {
        if (viewModel.typeContent == Constants.TYPE_CONTENT_VIDEO) {
            firstLoadVideo()
        }
        checkFilter()
    }

    override fun onClickClearFilters() {
        if (viewModel.typeContent == Constants.TYPE_CONTENT_VIDEO) {
            firstLoadVideo()
        }
        checkFilter()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupClickListener() {
        binding.inputSearch.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = binding.inputSearch.compoundDrawables[2]
                if (drawableRight != null && event.rawX
                    >= (binding.inputSearch.right - drawableRight.bounds.width())) {
                    binding.inputSearch.text.clear()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateClearButtonVisibility()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.inputSearch.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    viewModel.query = binding.inputSearch.text.toString()
                    if (viewModel.query.isNotBlank()) {
                        viewModel.typeContent = Constants.TYPE_CONTENT_VIDEO
                        updateRecyclerViewLayout()
                        firstLoadVideo()
                        activity?.let { viewModel.hideKeyboard(it) }
                        viewModel.saveSearchKeyword(viewModel.query)
                    }
                    true
                }
                else -> false
            }
        }

        binding.back.setOnClickListener {
            loadCollectionContent()
        }

        binding.filter.setOnClickListener {
            showFilterBottomSheet()
        }

        binding.scrollToTop.setOnClickListener {
            binding.rvVideo.smoothScrollToPosition(0)
        }
    }

    fun loadCollectionContent() {
        binding.back.visibility = View.GONE
        binding.inputSearch.text.clear()
        viewModel.typeContent = Constants.TYPE_CONTENT_COLLECTION
        updateRecyclerViewLayout()
        firstLoadCollection()
    }

    private fun setupListSearchHistory() {
        binding.rvSearchHistory.adapter = searchHistoryAdapter
        val space = resources.getDimensionPixelSize(R.dimen.item_spacing_preview_video)
        binding.rvSearchHistory.addItemDecoration(SpaceItemDecoration(space))
    }

    private fun setupListVideo() {
        updateRecyclerViewLayout()
        binding.rvVideo.adapter = exploreAdapter
        exploreAdapter.onClickVideo = onClickVideo
        exploreAdapter.onClickCollection = onClickCollection
    }

    private fun updateRecyclerViewLayout() {
        val space = resources.getDimensionPixelSize(R.dimen.item_spacing_video)
        clearItemDecorations(binding.rvVideo)
        when (viewModel.typeContent) {
            Constants.TYPE_CONTENT_VIDEO -> {
                binding.rvVideo.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                binding.rvVideo.addItemDecoration(SpaceItemDecoration(space))
            }
            else -> {
                binding.rvVideo.layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun clearItemDecorations(recyclerView: RecyclerView) {
        while (recyclerView.itemDecorationCount > 0) {
            recyclerView.removeItemDecorationAt(0)
        }
    }

    private fun initList() {
        if (viewModel.loading.value == true
            || viewModel.loadingMore.value == true) return
        exploreAdapter.clear()
        viewModel.page = 1
        viewModel.pageTotal = 1
    }

    private val onClickVideo = object : OnClickVideo {
        override fun onClickDetail(id: Long) {
            val intent = Intent(requireContext(), DetailVideoActivity::class.java)
            intent.putExtra(DetailVideoActivity.EXTRA_ID_VIDEO, id)
            startActivity(intent)
        }
    }

    private val onClickCollection = object : OnClickCollection {
        override fun onClickCollection(id: String?, title: String?) {
            id?.let {
                val intent = Intent(requireContext(), CollectionActivity::class.java)
                intent.putExtra(CollectionActivity.EXTRA_ID_COLLECTION, it)
                intent.putExtra(CollectionActivity.EXTRA_TITLE_COLLECTION, title)
                startActivity(intent)
            }
        }
    }

    private fun firstLoadVideo() {
        initList()
        viewModel.getSearchVideo()
    }

    private fun firstLoadCollection() {
        initList()
        viewModel.getCollectionVideo()
    }

    private fun observeData() {
        viewModel.listVideo.observe(viewLifecycleOwner) {
            it?.let { listVideo ->
                exploreAdapter.setData(listVideo)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it
        }

        viewModel.loadingMore.observe(viewLifecycleOwner) {
            binding.progressIndicator.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isSearchActive.observe(viewLifecycleOwner) {
            binding.back.visibility = if (it) View.VISIBLE else View.GONE
            binding.rvSearchHistory.visibility = if (it) View.GONE else View.VISIBLE
        }

        viewModel.listSearchHistory.observe(viewLifecycleOwner) {
            it?.let { listSearchHistory ->
                searchHistoryAdapter.updateList(listSearchHistory)
            }
        }

        viewModel.isDataAvailable.observe(viewLifecycleOwner) {
            binding.notFoundContainer.visibility = if (it) View.GONE else View.VISIBLE
        }
    }

    private val searchHistoryAdapter by lazy {
        SearchHistoryAdapter(listOf(), object : OnSelectSearchHistory {
            override fun onClickKeyword(keyword: String) {
                viewModel.query = keyword
                viewModel.typeContent = Constants.TYPE_CONTENT_VIDEO
                updateRecyclerViewLayout()
                firstLoadVideo()
                binding.inputSearch.setText(keyword)
                binding.rvSearchHistory.visibility = View.GONE
                activity?.let { viewModel.hideKeyboard(it) }
            }
        })
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
                    if (viewModel.typeContent == Constants.TYPE_CONTENT_VIDEO) {
                        val layoutManager = binding.rvVideo.layoutManager as StaggeredGridLayoutManager
                        val visibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                        val lastVisibleItemPosition = visibleItemPositions.maxOrNull() ?: 0
                        val totalItemCount = layoutManager.itemCount

                        if (lastVisibleItemPosition == totalItemCount - 1) {
                            viewModel.onScrolledToEnd()
                        }
                    } else {
                        val layoutManager = binding.rvVideo.layoutManager as LinearLayoutManager
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        val totalItemCount = layoutManager.itemCount

                        if (lastVisibleItemPosition == totalItemCount - 1) {
                            viewModel.onScrolledToEnd()
                        }
                    }
                }
            }
        })
    }

    private fun updateClearButtonVisibility() {
        val searchBox = binding.inputSearch
        val drawables = searchBox.compoundDrawablesRelative
        if (searchBox.text.isNotEmpty()) {
            searchBox.setCompoundDrawablesRelativeWithIntrinsicBounds(
                drawables[0],
                drawables[1],
                clearButtonDrawable,
                drawables[3]
            )
        } else {
            searchBox.setCompoundDrawablesRelativeWithIntrinsicBounds(
                drawables[0],
                drawables[1],
                null,
                drawables[3]
            )
        }
    }

    private fun showFilterBottomSheet() {
        filterBottomSheet.listener = this
        filterBottomSheet.show(
            childFragmentManager,
            FilterBottomSheet.TAG
        )
    }

    private fun checkFilter() {
        if (viewModel.isFilterExist()) {
            binding.filter.setBackgroundResource(R.drawable.bg_btn_circle_outline_filter)
        } else {
            binding.filter.setBackgroundResource(R.drawable.bg_btn_circle_outline)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}