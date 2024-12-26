package com.anankastudio.videocollector.fragments

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.FragmentExploreBinding

class FragmentExplore : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private var clearButtonDrawable: Drawable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clearButtonDrawable = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_clear
        )
        updateClearButtonVisibility()
        setupClickListener()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}