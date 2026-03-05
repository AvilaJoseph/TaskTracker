package com.josephavila.tasktracker.ui

import android.graphics.Bitmap
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.view.drawToBitmap

fun ComponentActivity.captureScreenBitmap(): Bitmap {
    val root: View = window.decorView.rootView
    return root.drawToBitmap(Bitmap.Config.ARGB_8888)
}