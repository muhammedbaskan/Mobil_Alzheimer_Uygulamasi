package com.mbaskan.patientapp.DoctorActivity

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
import com.mbaskan.patientapp.PatientActivity.MainActivity
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.ActivityDoctorLoginBinding

class DoctorLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore

        binding.doctorPasswordLayout.setEndIconOnClickListener {
            if (isPasswordVisible) {
                binding.doctorPasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.doctorPasswordLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.visibility_off)
            } else {
                binding.doctorPasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.doctorPasswordLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.visibility)
            }
            isPasswordVisible = !isPasswordVisible

            binding.doctorPasswordText.setSelection(binding.doctorPasswordText.text?.length ?: 0)
        }


    }

    fun doctorLoginButtonClick(view : View) {

        val email = binding.doctorEmailText.text.toString()
        val password = binding.doctorPasswordText.text.toString()

        if (email.equals("") || password.equals("")){
            Toast.makeText(this,"Boş bırakılan bölümler bulunuyor!", Toast.LENGTH_LONG).show()
        }
        else{
            binding.doctorLoginProcess.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener{
                var isExists = false
                firestore.collection("Doctors").get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val uid = document.getString("uid")
                            if (uid!! == auth.uid) {
                                isExists = true
                                break;
                            }
                        }

                        if (isExists) {

                            val intent =
                                Intent(this@DoctorLoginActivity, DoctorMainActivity::class.java)
                            startActivity(intent)
                            finish()

                            binding.doctorLoginProcess.visibility = View.GONE

                        } else {
                            Toast.makeText(
                                this@DoctorLoginActivity,
                                "Bu bilgilere sahip bir doktor bulunmuyor!",
                                Toast.LENGTH_LONG
                            ).show()
                            binding.doctorLoginProcess.visibility = View.GONE

                            auth.signOut()
                        }
                    }
            }.addOnFailureListener {
                binding.doctorLoginProcess.visibility = View.GONE
                Toast.makeText(this@DoctorLoginActivity,"E-mail ya da şifre yanlış!", Toast.LENGTH_LONG).show()
            }
        }



    }


    fun doctorSignupTextClick(view: View){
        val intent = Intent(this@DoctorLoginActivity, DoctorSignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun patientLoginTextClick(view: View){
        val intent = Intent(this@DoctorLoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}