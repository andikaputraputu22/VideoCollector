package com.anankastudio.videocollector.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.adapters.DownloadSizeAdapter
import com.anankastudio.videocollector.databinding.BottomSheetDownloadBinding
import com.anankastudio.videocollector.interfaces.OnClickDownload
import com.anankastudio.videocollector.interfaces.OnSelectDownloadSize
import com.anankastudio.videocollector.models.VideoFile
import com.anankastudio.videocollector.utilities.parcelableArrayList
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DownloadBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDownloadBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: BottomSheetDialog
    private var videoList: List<VideoFile> = listOf()
    private var urlDownload: String? = null
    var listener: OnClickDownload? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoList = arguments?.parcelableArrayList(ARG_VIDEO_LIST) ?: listOf()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(
            requireContext(),
            R.style.BottomSheetDialog
        )

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListSize()
        setupClickListener()
    }

    private fun setupListSize() {
        binding.rvSize.adapter = downloadSizeAdapter
    }

    private fun setupClickListener() {
        binding.download.setOnClickListener {
            urlDownload?.let { url ->
                listener?.onClickDownload(url)
                dismiss()
            }
        }
    }

    private val downloadSizeAdapter by lazy {
        val sortedVideoList = videoList.sortedByDescending { it.size }
        DownloadSizeAdapter(sortedVideoList, object : OnSelectDownloadSize {
            override fun onClickSize(url: String) {
                urlDownload = url
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "download_bottom_sheet"
        const val ARG_VIDEO_LIST = "arg_video_list"
    }
}