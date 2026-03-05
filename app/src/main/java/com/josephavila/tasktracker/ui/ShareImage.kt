package com.josephavila.tasktracker.ui

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun shareBitmapImage(
    context: Context,
    bitmap: Bitmap,
    filename: String = "progress.png"
) {
    val appContext = context.applicationContext

    val cacheDir = File(appContext.cacheDir, "shared_images").apply { mkdirs() }
    val file = File(cacheDir, filename)

    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
    }

    val authority = "${appContext.packageName}.fileprovider"
    val uri = FileProvider.getUriForFile(appContext, authority, file)

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Mejora compatibilidad de permisos con muchas apps
        clipData = ClipData.newRawUri("Shared image", uri)
    }

    val chooser = Intent.createChooser(sendIntent, "Share progress")

    // Si no hay apps que puedan recibirlo, no hacemos nada y lo logueamos
    val pm = appContext.packageManager
    if (sendIntent.resolveActivity(pm) == null) {
        Log.e("TaskTrackerShare", "No app found to share image/png")
        return
    }

    // Usar Activity si se puede (mejor UX); si no, NEW_TASK
    if (context is Activity) {
        context.startActivity(chooser)
    } else {
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(chooser)
    }
}