package com.example.sampleproject.util

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sampleproject.R
import java.io.File

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

fun ImageView.loadImage(strUrl: String?, progressDrawable: CircularProgressDrawable) {
    val options = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.mipmap.ic_launcher_round)

    Glide.with(this.context)
        .applyDefaultRequestOptions(options)
        .load(strUrl)
        .into(this)
}

fun getFilePath(data: Intent?): String? {
    return data?.getStringExtra("extra.file_path")
}

fun getFile(data: Intent?): File? {
    val path = getFilePath(data)
    if (path != null) {
        return File(path)
    }
    return null
}