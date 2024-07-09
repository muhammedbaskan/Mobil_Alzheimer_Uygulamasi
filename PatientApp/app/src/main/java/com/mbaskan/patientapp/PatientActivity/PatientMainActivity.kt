package com.mbaskan.patientapp.PatientActivity

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.ActivityPatientMainBinding

class PatientMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.patient_nav_host_fragment) as NavHostFragment

        NavigationUI.setupWithNavController(binding.patientBottomNav,navHostFragment.navController)

    }
    }






