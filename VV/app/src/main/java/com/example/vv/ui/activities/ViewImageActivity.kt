package com.example.vv.ui.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.vv.R

class ViewImageActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_photo_view)

        imageView = findViewById(R.id.ivFullPhoto)

        val imagePath = intent.getStringExtra("imagePath")
        imagePath?.let {
            val bitmap = BitmapFactory.decodeFile(it)
            imageView.setImageBitmap(bitmap)
        }
    }
}

