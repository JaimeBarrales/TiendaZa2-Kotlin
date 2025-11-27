package com.example.tiendaza.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

fun decodeBase64ToBitmap(base64String: String): Bitmap? {
    return try {
        val base64Image = if (base64String.contains("base64,")) {
            base64String.substring(base64String.indexOf("base64,") + 7)
        } else {
            base64String
        }

        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        android.util.Log.e("Base64Decoder", "Error: ${e.message}")
        null
    }
}