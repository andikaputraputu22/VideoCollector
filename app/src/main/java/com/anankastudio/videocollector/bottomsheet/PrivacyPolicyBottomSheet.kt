package com.anankastudio.videocollector.bottomsheet

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.BottomSheetPrivacyPolicyBinding
import com.anankastudio.videocollector.interfaces.OnClickPrivacyPolicy
import com.anankastudio.videocollector.utilities.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PrivacyPolicyBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPrivacyPolicyBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: BottomSheetDialog
    var listener: OnClickPrivacyPolicy? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetPrivacyPolicyBinding.inflate(inflater, container, false)
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

        setupClickableText()
        setupClickListener()
    }

    private fun setupClickableText() {
        val text = "By continuing to use this app, you acknowledge that you have read our Terms of Use and Privacy Policy."
        val spannableString = SpannableString(text)

        val termsStart = text.indexOf("Terms of Use")
        val termsEnd = termsStart + "Terms of Use".length

        val privacyStart = text.indexOf("Privacy Policy")
        val privacyEnd = privacyStart + "Privacy Policy".length

        val termsClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                listener?.onClickMustRead(Constants.URL_TERMS_OF_USE)
            }
        }

        val privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                listener?.onClickMustRead(Constants.URL_PRIVACY_POLICY)
            }
        }

        spannableString.setSpan(
            termsClickableSpan,
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            UnderlineSpan(),
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            privacyClickableSpan,
            privacyStart,
            privacyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            UnderlineSpan(),
            privacyStart,
            privacyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            privacyStart,
            privacyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.descriptionBottomSheet.text = spannableString
        binding.descriptionBottomSheet.movementMethod = LinkMovementMethod.getInstance()
        binding.descriptionBottomSheet.highlightColor = Color.TRANSPARENT
    }

    private fun setupClickListener() {
        binding.next.setOnClickListener {
            listener?.onClickContinue()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onPrivacyPolicyDialogDismissed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "privacy_policy_bottom_sheet"
    }
}