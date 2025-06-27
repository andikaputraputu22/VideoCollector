package com.anankastudio.videocollector.interfaces

interface OnClickPrivacyPolicy {

    fun onClickMustRead(url: String)
    fun onClickContinue()
    fun onPrivacyPolicyDialogDismissed()
}