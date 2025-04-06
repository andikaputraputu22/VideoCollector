package com.anankastudio.videocollector.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anankastudio.videocollector.BuildConfig
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.BottomSheetMenuBinding
import com.anankastudio.videocollector.interfaces.OnClickMenu
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: BottomSheetDialog
    var listener: OnClickMenu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(
            requireContext(),
            R.style.BottomSheetDialog
        )

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isDebug = if (BuildConfig.DEBUG) " (debug)" else ""
        val versionName = "${getString(R.string.version)} ${BuildConfig.VERSION_NAME}$isDebug"
        binding.version.text = versionName

        setupClickListener()
    }

    private fun setupClickListener() {
        binding.sendFeedback.setOnClickListener {
            listener?.onClickSendFeedback()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "menu_bottom_sheet"
    }
}