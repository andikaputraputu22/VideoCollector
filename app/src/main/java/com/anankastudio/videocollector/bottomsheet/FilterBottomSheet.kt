package com.anankastudio.videocollector.bottomsheet

import android.app.Dialog
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.BottomSheetFilterBinding
import com.anankastudio.videocollector.interfaces.OnClickFilter
import com.anankastudio.videocollector.utilities.SharedPreferencesManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FilterBottomSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: BottomSheetDialog

    private var selectedOrientation: String = ""
    private var selectedSize: String = ""
    var listener: OnClickFilter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
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

        setupFilter()
        setupClickListener()
    }

    private fun setupFilter() {
        val orientation = sharedPreferencesManager.getString(SharedPreferencesManager.FILTER_ORIENTATION)
        val size = sharedPreferencesManager.getString(SharedPreferencesManager.FILTER_SIZE)

        with(binding) {
            val textViewOrientationMap = mapOf(
                allOrientation to "",
                portrait to "portrait",
                landscape to "landscape"
            )
            textViewOrientationMap.forEach { (textView, value) ->
                if (value == orientation) {
                    textView.setTextColor(ContextCompat.getColor(textView.context, android.R.color.white))
                    textView.compoundDrawablesRelative[2]?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(requireContext(), android.R.color.white), PorterDuff.Mode.MULTIPLY
                    )
                } else {
                    textView.setTextColor(ContextCompat.getColor(textView.context, android.R.color.darker_gray))
                    textView.compoundDrawablesRelative[2]?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(requireContext(), android.R.color.transparent), PorterDuff.Mode.MULTIPLY
                    )
                }
            }

            val textViewSizeMap = mapOf(
                allSizes to "",
                large to "large",
                medium to "medium",
                small to "small"
            )
            textViewSizeMap.forEach { (textView, value) ->
                if (value == size) {
                    textView.setTextColor(ContextCompat.getColor(textView.context, android.R.color.white))
                    textView.compoundDrawablesRelative[2]?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(requireContext(), android.R.color.white), PorterDuff.Mode.MULTIPLY
                    )
                } else {
                    textView.setTextColor(ContextCompat.getColor(textView.context, android.R.color.darker_gray))
                    textView.compoundDrawablesRelative[2]?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(requireContext(), android.R.color.transparent), PorterDuff.Mode.MULTIPLY
                    )
                }
            }
        }
    }

    private fun setupClickListener() {
        with(binding) {
            applyFilters.setOnClickListener {
                sharedPreferencesManager.setString(SharedPreferencesManager.FILTER_ORIENTATION, selectedOrientation)
                sharedPreferencesManager.setString(SharedPreferencesManager.FILTER_SIZE, selectedSize)
                listener?.onClickApplyFilters()
                dismiss()
            }

            clearFilters.setOnClickListener {
                resetFilter()
                listener?.onClickClearFilters()
                dismiss()
            }

            val textViewOrientationMap = mapOf(
                allOrientation to "",
                portrait to "portrait",
                landscape to "landscape"
            )
            textViewOrientationMap.forEach { (textView, value) ->
                textView.setOnClickListener {
                    selectedOrientation = value
                    textViewOrientationMap.keys.forEach { tv ->
                        if (tv == textView) {
                            tv.setTextColor(ContextCompat.getColor(tv.context, android.R.color.white))
                            tv.compoundDrawablesRelative[2]?.colorFilter = PorterDuffColorFilter(
                                ContextCompat.getColor(requireContext(), android.R.color.white), PorterDuff.Mode.MULTIPLY
                            )
                        } else {
                            tv.setTextColor(ContextCompat.getColor(tv.context, android.R.color.darker_gray))
                            tv.compoundDrawablesRelative[2]?.colorFilter = PorterDuffColorFilter(
                                ContextCompat.getColor(requireContext(), android.R.color.transparent), PorterDuff.Mode.MULTIPLY
                            )
                        }
                    }
                }
            }

            val textViewSizeMap = mapOf(
                allSizes to "",
                large to "large",
                medium to "medium",
                small to "small"
            )
            textViewSizeMap.forEach { (textView, value) ->
                textView.setOnClickListener {
                    selectedSize = value
                    textViewSizeMap.keys.forEach { tv ->
                        if (tv == textView) {
                            tv.setTextColor(ContextCompat.getColor(tv.context, android.R.color.white))
                            tv.compoundDrawablesRelative[2]?.colorFilter = PorterDuffColorFilter(
                                ContextCompat.getColor(requireContext(), android.R.color.white), PorterDuff.Mode.MULTIPLY
                            )
                        } else {
                            tv.setTextColor(ContextCompat.getColor(tv.context, android.R.color.darker_gray))
                            tv.compoundDrawablesRelative[2]?.colorFilter = PorterDuffColorFilter(
                                ContextCompat.getColor(requireContext(), android.R.color.transparent), PorterDuff.Mode.MULTIPLY
                            )
                        }
                    }
                }
            }
        }
    }

    private fun resetFilter() {
        selectedOrientation = ""
        selectedSize = ""
        sharedPreferencesManager.setString(SharedPreferencesManager.FILTER_ORIENTATION, selectedOrientation)
        sharedPreferencesManager.setString(SharedPreferencesManager.FILTER_SIZE, selectedSize)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "filter_bottom_sheet"
    }
}