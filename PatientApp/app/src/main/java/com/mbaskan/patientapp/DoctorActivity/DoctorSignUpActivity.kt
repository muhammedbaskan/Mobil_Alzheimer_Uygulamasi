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
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.ActivityDoctorSignUpBinding

class DoctorSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorSignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorSignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore

        binding.doctorSignupPasswordLayout.setEndIconOnClickListener {
            if (isPasswordVisible) {
                binding.doctorSignupPasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.doctorSignupPasswordLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.visibility_off)
            } else {
                binding.doctorSignupPasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.doctorSignupPasswordLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.visibility)
            }
            isPasswordVisible = !isPasswordVisible
            binding.doctorSignupPasswordText.setSelection(binding.doctorSignupPasswordText.text?.length ?: 0)
        }


    }

    fun doctorSignupButtonClick(view: View){

        val name = binding.doctorNameText.text.toString()
        val surname = binding.doctorSurnameText.text.toString()
        val phone = binding.doctorPhoneText.text.toString()
        val email = binding.doctorSignupEmailText.text.toString()
        val password = binding.doctorSignupPasswordText.text.toString()
        val selectedGenderId = binding.doctorradioGroup.checkedRadioButtonId
        val gender = when (selectedGenderId) {
            R.id.doctorMaleradioButton -> "Erkek"
            R.id.doctorFemaleradioButton -> "Kadın"
            else -> null
        }
        val province = ""
        val district = ""
        val address = ""


        if (name.equals("") || surname.equals("") || phone.equals("") || email.equals("") || password.equals("")){
            Toast.makeText(this,"Boş bırakılan bölümler bulunuyor!", Toast.LENGTH_LONG).show()
            return
        }
        if (gender == null) {
            Toast.makeText(this, "Lütfen cinsiyetinizi seçiniz!", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            binding.doctorSignupProcess.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener{

                doctorInfoToFirebase(name, surname, phone, email,gender,province,district,address)
                val intent = Intent(this@DoctorSignUpActivity, DoctorMainActivity::class.java)
                startActivity(intent)
                finish()
                binding.doctorSignupProcess.visibility = View.GONE

            }.addOnFailureListener {
                Toast.makeText(this@DoctorSignUpActivity,it.localizedMessage, Toast.LENGTH_LONG).show()
                binding.doctorSignupProcess.visibility = View.GONE
            }
        }
    }



    private fun doctorInfoToFirebase(name: String, surname: String, phone: String, email: String,gender: String, province: String, district: String, address:String) {
        val newDoctor = hashMapOf(
            "name" to name,
            "surname" to surname,
            "phone" to phone,
            "email" to email,
            "gender" to gender,
            "province" to province,
            "district" to district,
            "address" to address,
            "uid" to auth.uid!!
        )

        firestore.collection("Doctors").document(auth.uid!!)
            .set(newDoctor)
            .addOnSuccessListener {
                Toast.makeText(this, "Doktor bilgileri kaydedildi!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Doktor bilgileri kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



}