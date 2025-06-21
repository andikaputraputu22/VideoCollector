package com.anankastudio.videocollector.interfaces

interface OnPermissionCallback {

    fun showPermissionExplanationDialog(onGranted: () -> Unit)
    fun showPermissionDeniedDialog()
    fun storePendingCallback(onGranted: () -> Unit)
}