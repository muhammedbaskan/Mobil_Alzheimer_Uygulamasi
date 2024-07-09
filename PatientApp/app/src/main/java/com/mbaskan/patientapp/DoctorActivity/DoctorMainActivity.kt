package com.mbaskan.patientapp.DoctorActivity

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.ActivityDoctorMainBinding


class DoctorMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val navHostFragment1 = supportFragmentManager.findFragmentById(R.id.doctor_nav_host_fragment) as NavHostFragment

        NavigationUI.setupWithNavController(binding.doctorBottomNav,navHostFragment1.navController)
    }


}