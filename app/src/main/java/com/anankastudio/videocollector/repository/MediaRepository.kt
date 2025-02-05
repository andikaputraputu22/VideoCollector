package com.anankastudio.videocollector.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.anankastudio.videocollector.utilities.NotificationManager
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class MediaRepository @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManager
) {

    private val client = OkHttpClient()

    fun downloadVideo(
        context: Context,
        url: String,
        onSuccess: (videoUri: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        Thread {
            try {
                val response = downloadFromUrl(url)
                if (response.isSuccessful) {
                    val inputStream = response.body()?.byteStream()
                    inputStream?.let {
                        val videoUri = saveVideoToMediaStore(context, it)
                        if (videoUri != null) {
                            onSuccess(videoUri.toString())
                            notificationManager.showDownloadNotification(
                                context,
                                "Download Complete",
                                "Video saved to gallery.",
                                videoUri
                            )
                        } else {
                            throw Exception("Failed to save video")
                        }
                    } ?: throw Exception("Failed to download video")
                } else {
                    throw Exception("Request failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                onError(e)
            }
        }.start()
    }

    private fun downloadFromUrl(url: String): Response {
        val request = Request.Builder().url(url).build()
        return client.newCall(request).execute()
    }

    private fun saveVideoToMediaStore(
        context: Context,
        inputStream: InputStream
    ): Uri? {
        val contentResolver = context.contentResolver
        val videoCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "video_${System.currentTimeMillis()}.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
        }

        val uri = contentResolver.insert(videoCollection, contentValues)
        uri?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                outputStream?.let { stream ->
                    writeToStream(
                        inputStream,
                        stream
                    )
                }
            }
        }

        return uri
    }

    private fun writeToStream(
        inputStream: InputStream,
        outputStream: OutputStream
    ) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytes -> bytesRead = bytes } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        outputStream.flush()
        inputStream.close()
    }
}