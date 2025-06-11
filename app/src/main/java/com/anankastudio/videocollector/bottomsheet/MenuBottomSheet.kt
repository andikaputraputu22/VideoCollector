package com.anankastudio.videocollector.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.BottomSheetMenuBinding
import com.anankastudio.videocollector.interfaces.OnClickMenu
import com.anankastudio.videocollector.utilities.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MenuBottomSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var utils: Utils

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

        binding.version.text = utils.getVersionName(requireContext())

        setupClickListener()
    }

    private fun setupClickListener() {
        binding.sendFeedback.setOnClickListener {
            listener?.onClickSendFeedback()
            dismiss()
        }

        binding.privacyPolicy.setOnClickListener {
            listener?.onClickPrivacyPolicy()
            dismiss()
        }

        binding.termsOfUse.setOnClickListener {
            listener?.onClickTermsOfUse()
            dismiss()
        }

        binding.about.setOnClickListener {
            listener?.onClickAbout()
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