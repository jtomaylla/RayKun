package com.ecandle.raykun.extensions

import android.annotation.SuppressLint
import android.net.Uri
import android.support.v4.provider.DocumentFile
import com.ecandle.raykun.BuildConfig
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.MyBaseSimpleActivity
import com.ecandle.raykun.helpers.IcsExporter
import com.simplemobiletools.commons.extensions.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

fun MyBaseSimpleActivity.shareEvents(ids: List<Int>) {
    val file = getTempFile()
    if (file == null) {
        toast(R.string.unknown_error_occurred)
        return
    }

    val events = dbHelper.getEventsWithIds(ids)
    IcsExporter().exportEvents(this, file, events) {
        if (it == IcsExporter.ExportResult.EXPORT_OK) {
            val uri = getFilePublicUri(file, BuildConfig.APPLICATION_ID)
            shareUri(uri, BuildConfig.APPLICATION_ID)
        }
    }
}

fun MyBaseSimpleActivity.getTempFile(): File? {
    val folder = File(cacheDir, "events")
    if (!folder.exists()) {
        if (!folder.mkdir()) {
            toast(R.string.unknown_error_occurred)
            return null
        }
    }

    return File(folder, "events.ics")
}

fun MyBaseSimpleActivity.restartActivity() {
    finish()
    startActivity(intent)
}

fun MyBaseSimpleActivity.getFileOutputStream(file: File, callback: (outputStream: OutputStream?) -> Unit) {
    if (needsStupidWritePermissions(file.absolutePath)) {
        handleSAFDialog(file) {
            var document = getFileDocument(file.absolutePath)
            if (document == null) {
                val error = String.format(getString(R.string.could_not_create_file), file.absolutePath)
                showErrorToast(error)
                callback(null)
                return@handleSAFDialog
            }

            if (!file.exists()) {
                document = document.createFile("", file.name)
            }
            callback(contentResolver.openOutputStream(document!!.uri))
        }
    } else {
        callback(FileOutputStream(file))
    }
}

@SuppressLint("NewApi")
fun MyBaseSimpleActivity.getFileDocument(path: String): DocumentFile? {
    if (!isLollipopPlus())
        return null

    var relativePath = path.substring(sdCardPath.length)
    if (relativePath.startsWith(File.separator))
        relativePath = relativePath.substring(1)

    var document = DocumentFile.fromTreeUri(this, Uri.parse(baseConfig.treeUri))
    val parts = relativePath.split("/")
    for (part in parts) {
        val currDocument = document.findFile(part)
        if (currDocument != null)
            document = currDocument
    }

    return document
}

fun MyBaseSimpleActivity.useEnglishToggled() {
    val conf = resources.configuration
    conf.locale = if (baseConfig.useEnglish) Locale.ENGLISH else Locale.getDefault()
    resources.updateConfiguration(conf, resources.displayMetrics)
    restartActivity()
}