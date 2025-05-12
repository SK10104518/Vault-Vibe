package com.example.budgettracker

import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.example.vv.R
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class PhotoViewerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PHOTO_URI = "extra_photo_uri"
        private const val TAG = "PhotoViewerActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_photo_view)

        val imageView = findViewById<ImageView>(R.id.ivFullPhoto)
        val photoUriString = intent.getStringExtra(EXTRA_PHOTO_URI)

        if (photoUriString.isNullOrEmpty()) {
            Toast.makeText(this, "No photo to display", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        try {
            val photoUri = Uri.parse(photoUriString)
            Glide.with(this)
                .load(photoUri)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageView)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading photo", e)
            Toast.makeText(this, "Failed to load photo", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
