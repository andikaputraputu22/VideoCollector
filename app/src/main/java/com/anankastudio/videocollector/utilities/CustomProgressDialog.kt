package com.anankastudio.videocollector.utilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.anankastudio.videocollector.databinding.CustomProgressDialogBinding

class CustomProgressDialog(context: Context) : Dialog(context) {

    private var _binding: CustomProgressDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        _binding = CustomProgressDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun dismiss() {
        super.dismiss()
        _binding = null
    }
}