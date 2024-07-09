package com.mbaskan.patientapp.PatientActivity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mbaskan.patientapp.DoctorActivity.DoctorLoginActivity
import com.mbaskan.patientapp.DoctorActivity.DoctorMainActivity
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityMainBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.patientLoginProcess.visibility = View.VISIBLE

            var isExists = false
            firestore.collection("Patients").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val uid = document.id
                        if (uid == auth.currentUser?.uid) {
                            isExists = true
                            break;
                        }
                    }

                    if (isExists) {
                        val intent = Intent(this@MainActivity, PatientMainActivity::class.java)
                        startActivity(intent)
                        finish()
                        binding.patientLoginProcess.visibility = View.VISIBLE


                    } else {
                        val intent = Intent(this@MainActivity, DoctorMainActivity::class.java)
                        startActivity(intent)
                        finish()
                        binding.patientLoginProcess.visibility = View.VISIBLE
                    }
                }
        }

        binding.patientPasswordLayout.setEndIconOnClickListener {
            if (isPasswordVisible) {
                binding.patientPasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.patientPasswordLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.visibility_off)
            } else {
                binding.patientPasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.patientPasswordLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.visibility)
            }
            isPasswordVisible = !isPasswordVisible

            binding.patientPasswordText.setSelection(binding.patientPasswordText.text?.length ?: 0)
        }
    }

    fun patientLoginButtonClick(view : View) {

        val email = binding.patientEmailText.text.toString()
        val password = binding.patientPasswordText.text.toString()

        if (email.equals("") || password.equals("")){
            Toast.makeText(this,"Boş bırakılan bölümler bulunuyor!",Toast.LENGTH_LONG).show()

        }
        else{
            binding.patientLoginProcess.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                var isExists = false
                firestore.collection("Patients").get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val uid = document.id
                            if (uid == auth.uid) {
                                isExists = true
                                break;
                            }
                        }

                        if (isExists) {
                            val intent = Intent(this@MainActivity, PatientMainActivity::class.java)
                            startActivity(intent)
                            finish()
                            binding.patientLoginProcess.visibility = View.GONE

                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Bu bilgilere sahip bir hasta bulunmuyor!",
                                Toast.LENGTH_LONG
                            ).show()
                            binding.patientLoginProcess.visibility = View.GONE

                            auth.signOut()
                        }
                    }

            }.addOnFailureListener {
                binding.patientLoginProcess.visibility = View.GONE

                Toast.makeText(this@MainActivity,"E-mail ya da şifre yanlış!",Toast.LENGTH_LONG).show()
            }
        }



    }


    fun patientSignUpTextClick(view: View){

        val intent = Intent(this@MainActivity, PatientSignUpActivity::class.java)
        startActivity(intent)
        finish()

    }

    fun doctorLoginTextClick(view: View){
        val intent = Intent(this@MainActivity, DoctorLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}