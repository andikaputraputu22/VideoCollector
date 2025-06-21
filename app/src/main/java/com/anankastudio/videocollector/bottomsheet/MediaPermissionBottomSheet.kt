package com.anankastudio.videocollector.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.BottomSheetPermissionBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MediaPermissionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPermissionBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: BottomSheetDialog
    private var description = ""
    private var titleButton = ""
    private var onClickAllowListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        description = arguments?.getString(ARG_DESCRIPTION) ?: ""
        titleButton = arguments?.getString(ARG_TITLE_BUTTON) ?: ""
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

        setupInfoDialog()
        setupClickListener()
    }

    private fun setupInfoDialog() {
        with(binding) {
            descriptionBottomSheet.text = description
            allow.text = titleButton
        }
    }

    private fun setupClickListener() {
        with(binding) {
            allow.setOnClickListener {
                onClickAllowListener?.invoke()
                dismiss()
            }

            cancel.setOnClickListener {
                dismiss()
            }
        }
    }

    fun setOnClickAllowListener(listener: () -> Unit) {
        onClickAllowListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "media_permission_bottom_sheet"
        const val ARG_DESCRIPTION = "arg_description"
        const val ARG_TITLE_BUTTON = "arg_title_button"
    }
}