package com.mbaskan.patientapp.DoctorActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.ActivityFullscreenPhotoBinding
import com.squareup.picasso.Picasso

class FullscreenPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullscreenPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenPhotoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val imageUrl = intent.getStringExtra("imageUrl").toString()

        Picasso.get().load(imageUrl).into(binding.fullscreenImageView)


        setSupportActionBar(binding.toolbarFullscreenPhoto)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarFullscreenPhoto.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

    }
}