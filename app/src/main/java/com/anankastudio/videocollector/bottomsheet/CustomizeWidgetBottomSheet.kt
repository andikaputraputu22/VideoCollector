package com.anankastudio.videocollector.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.adapters.KeywordWidgetAdapter
import com.anankastudio.videocollector.databinding.BottomSheetCustomizeWidgetBinding
import com.anankastudio.videocollector.interfaces.OnClickCustomizeWidget
import com.anankastudio.videocollector.interfaces.OnSelectKeywordWidget
import com.anankastudio.videocollector.utilities.SharedPreferencesManager
import com.anankastudio.videocollector.utilities.SpaceItemDecoration
import com.anankastudio.videocollector.utilities.Utils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CustomizeWidgetBottomSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    private var _binding: BottomSheetCustomizeWidgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: BottomSheetDialog
    private lateinit var keywordWidgetAdapter: KeywordWidgetAdapter
    private var clearButtonDrawable: Drawable? = null
    var listener: OnClickCustomizeWidget? = null
    val minValueTotalVideo = 15
    var totalVideo = minValueTotalVideo
    var listKeyword: ArrayList<String> = arrayListOf()
    var orientation: String = ""
    var refreshTime: Long = 180000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetCustomizeWidgetBinding.inflate(inflater, container, false)
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

        clearButtonDrawable = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_clear
        )
        updateClearButtonVisibility()

        val stringKeyword = sharedPreferencesManager.getString(SharedPreferencesManager.WIDGET_LIST_KEYWORD)
        listKeyword = if (stringKeyword.isNullOrEmpty()) arrayListOf()
        else stringKeyword.split("#").toCollection(ArrayList())

        setupCustomize()
        setupClickListener()
        setupListKeywordWidget()
    }

    private fun setupCustomize() {
        val orientation = sharedPreferencesManager.getString(SharedPreferencesManager.WIDGET_ORIENTATION)
        val refreshTime = sharedPreferencesManager.getLong(SharedPreferencesManager.WIDGET_REFRESH_TIME, 180000)
        val totalShowingVideo = sharedPreferencesManager.getInt(SharedPreferencesManager.WIDGET_TOTAL_VIDEO, 15)

        with(binding) {
            totalVideoSlider.post {
                totalVideoSlider.progress = totalShowingVideo - minValueTotalVideo
            }
            totalVideo.text = totalShowingVideo.toString()

            val containerOrientation = mapOf(
                orientationAll to "",
                orientationPortrait to "portrait",
                orientationLandscape to "landscape"
            )
            containerOrientation.forEach { (linearLayout, value) ->
                linearLayout.setBackgroundResource(
                    if (value == orientation) R.drawable.bg_filter_selected
                    else R.drawable.bg_filter
                )
            }

            val containerRefreshTime = mapOf(
                refreshTime3 to 180000L,
                refreshTime5 to 300000L,
                refreshTime10 to 600000L
            )
            containerRefreshTime.forEach { (linearLayout, value) ->
                linearLayout.setBackgroundResource(
                    if (value == refreshTime) R.drawable.bg_filter_selected
                    else R.drawable.bg_filter
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupClickListener() {
        binding.inputKeyword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = binding.inputKeyword.compoundDrawables[2]
                if (drawableRight != null && event.rawX
                    >= (binding.inputKeyword.right - drawableRight.bounds.width())) {
                    binding.inputKeyword.text.clear()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        binding.inputKeyword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateClearButtonVisibility()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.inputKeyword.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    addKeyword()
                    true
                }
                else -> false
            }
        }

        binding.totalVideoSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                totalVideo = minValueTotalVideo + progress
                binding.totalVideo.text = totalVideo.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        val containerOrientation = mapOf(
            binding.orientationAll to "",
            binding.orientationPortrait to "portrait",
            binding.orientationLandscape to "landscape"
        )
        containerOrientation.forEach { (linearLayout, value) ->
            linearLayout.setOnClickListener {
                orientation = value
                containerOrientation.keys.forEach { ly ->
                    ly.setBackgroundResource(
                        if (ly == linearLayout) R.drawable.bg_filter_selected
                        else R.drawable.bg_filter
                    )
                }
            }
        }

        val containerRefreshTime = mapOf(
            binding.refreshTime3 to 180000L,
            binding.refreshTime5 to 300000L,
            binding.refreshTime10 to 600000L
        )
        containerRefreshTime.forEach { (linearLayout, value) ->
            linearLayout.setOnClickListener {
                refreshTime = value
                containerRefreshTime.keys.forEach { ly ->
                    ly.setBackgroundResource(
                        if (ly == linearLayout) R.drawable.bg_filter_selected
                        else R.drawable.bg_filter
                    )
                }
            }
        }

        binding.addKeyword.setOnClickListener {
            addKeyword()
        }

        binding.apply.setOnClickListener {
            saveCustomizeWidget()
            listener?.onClickApplyCustomize()
            dismiss()
        }
    }

    private fun updateClearButtonVisibility() {
        val searchBox = binding.inputKeyword
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

    private fun addKeyword() {
        val keyword = binding.inputKeyword.text.toString()
        if (keyword.isNotBlank()) {
            binding.inputKeyword.clearFocus()
            if (listKeyword.none { it.equals(keyword, ignoreCase = true) }) {
                listKeyword.add(keyword)
                keywordWidgetAdapter.updateList(listKeyword)
                binding.inputKeyword.text.clear()
                utils.hideKeyboard(binding.inputKeyword)
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.keyword_already_added),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveCustomizeWidget() {
        sharedPreferencesManager.setString(SharedPreferencesManager.WIDGET_LIST_KEYWORD, listKeyword.joinToString("#"))
        sharedPreferencesManager.setString(SharedPreferencesManager.WIDGET_ORIENTATION, orientation)
        sharedPreferencesManager.setLong(SharedPreferencesManager.WIDGET_REFRESH_TIME, refreshTime)
        sharedPreferencesManager.setInt(SharedPreferencesManager.WIDGET_TOTAL_VIDEO, totalVideo)
    }

    private fun setupListKeywordWidget() {
        keywordWidgetAdapter = KeywordWidgetAdapter(listKeyword, object : OnSelectKeywordWidget {
            override fun onDeleteKeyword(position: Int, keyword: String) {
                listKeyword.removeAt(position)
                keywordWidgetAdapter.notifyDataSetChanged()
            }
        })

        val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }

        val space = resources.getDimensionPixelSize(R.dimen.item_spacing_preview_video)
        binding.rvKeyword.apply {
            layoutManager = flexboxLayoutManager
            adapter = keywordWidgetAdapter
            addItemDecoration(SpaceItemDecoration(space))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "customize_widget_bottom_sheet"
    }
}