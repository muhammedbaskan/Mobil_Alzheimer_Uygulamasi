package com.mbaskan.patientapp.DoctorClass

import android.graphics.Bitmap
import android.net.Uri

object ImageObject {
    private var uri: Uri? = null
    private var bitmap: Bitmap? = null

    fun setUri(uri: Uri) {
        this.uri = uri
    }

    fun getUri(): Uri? {
        return uri
    }


    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }
}
