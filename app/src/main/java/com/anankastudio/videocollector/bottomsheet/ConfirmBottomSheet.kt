package com.anankastudio.videocollector.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.BottomSheetConfirmBinding
import com.anankastudio.videocollector.interfaces.OnClickConfirm
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConfirmBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetConfirmBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: BottomSheetDialog
    var listener: OnClickConfirm? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetConfirmBinding.inflate(inflater, container, false)
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

        setupClickListener()
    }

    private fun setupClickListener() {
        with(binding) {
            yes.setOnClickListener {
                listener?.onClickYes()
                dismiss()
            }

            cancel.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "confirm_bottom_sheet"
    }
}