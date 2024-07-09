package com.mbaskan.patientapp.PatientActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.ActivityPatientSignUpBinding
import java.text.SimpleDateFormat
import java.util.Calendar


class PatientSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientSignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var doctorUid: String = ""
    val doctorUids = mutableListOf<String>()
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientSignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore
        loadDoctors()

        binding.patientSignupPasswordLayout.setEndIconOnClickListener {
            if (isPasswordVisible) {
                binding.patientSignupPasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.patientSignupPasswordLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.visibility_off)
            } else {
                binding.patientSignupPasswordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.patientSignupPasswordLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.visibility)
            }
            isPasswordVisible = !isPasswordVisible
            binding.patientSignupPasswordText.setSelection(binding.patientSignupPasswordText.text?.length ?: 0)
        }

    }
    private fun loadDoctors() {
        firestore.collection("Doctors").get()
            .addOnSuccessListener { documents ->
                val doctorNames = mutableListOf<String>()
                doctorNames.add("Doktor seçiniz")
                for (document in documents) {
                    val name = document.getString("name")
                    val surname = document.getString("surname")
                    val uid = document.getString("uid")
                    if (name != null && surname != null) {
                        doctorNames.add("$name $surname")
                        doctorUids.add(uid!!);
                    }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, doctorNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.doctorSpinner.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Doktorlar yüklenemedi: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
    fun patientSignupButtonClick(view: View){
        val name = binding.patientNameText.text.toString()
        val surname = binding.patientSurnameText.text.toString()
        val phone = binding.patientPhoneText.text.toString()
        val email = binding.patientEmailSignupText.text.toString()
        val password = binding.patientSignupPasswordText.text.toString()
        val selectedGenderId = binding.patientradioGroup.checkedRadioButtonId
        val gender = when (selectedGenderId) {
            R.id.patientMaleradioButton -> "Erkek"
            R.id.patientFemaleradioButton -> "Kadın"
            else -> null
        }
        val province = ""
        val district = ""
        val address = ""
        var selectedDoctor = ""
        if(binding.doctorSpinner.selectedItem != null) {
            selectedDoctor = binding.doctorSpinner.selectedItem.toString()
        }


        if (name.equals("") || surname.equals("") || phone.equals("") || email.equals("") || password.equals("")){
            Toast.makeText(this,"Boş bırakılan bölümler bulunuyor!",Toast.LENGTH_LONG).show()
            return
        }
        if (gender == null) {
            Toast.makeText(this, "Lütfen cinsiyetinizi seçiniz!", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedDoctor == "Doktor seçiniz" || selectedDoctor.isEmpty()) {
            Toast.makeText(this, "Lütfen bir doktor seçiniz!", Toast.LENGTH_SHORT).show()
            return
        }


        else{
            doctorUid = doctorUids[binding.doctorSpinner.selectedItemPosition -1]
            binding.patientSignupProcess.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener{

                patientInfoToFirebase(name, surname, phone, email,gender,province,district,address,selectedDoctor)

                val intent = Intent(this@PatientSignUpActivity, PatientMainActivity::class.java)
                startActivity(intent)
                finish()
                binding.patientSignupProcess.visibility = View.GONE
            }.addOnFailureListener {
                binding.patientSignupProcess.visibility = View.GONE
                Toast.makeText(this@PatientSignUpActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun patientInfoToFirebase(name: String, surname: String, phone: String, email: String, gender: String, province: String, district: String, address:String,doctor: String) {
        val newPatient = hashMapOf(
            "name" to name,
            "surname" to surname,
            "phone" to phone,
            "email" to email,
            "gender" to gender,
            "province" to province,
            "district" to district,
            "address" to address,
            "doctor" to doctor,
            "doctoruid" to doctorUid
        )

        firestore.collection("Patients").document(auth.uid!!)
            .set(newPatient)
            .addOnSuccessListener {
                createFirestoreChat("$name $surname")
                createFirestorePatient("$name $surname")
                Toast.makeText(this, "Hasta bilgileri kaydedildi!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Hasta bilgileri kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createFirestoreChat(userName: String) {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        val today = Calendar.getInstance().time
        val dateString = dateFormat.format(today)
        val dateLong = dateString.toLong()
        val user = userName
        val doctorId = doctorUid
        val newMessage = 0
        val newChat = hashMapOf<String, Any>(
            "patientId" to auth.uid!!,
            "doctorId" to doctorId,
            "patientName" to userName,
            "date" to getDate(),
            "dateLong" to dateLong
        )

        val documentReference: DocumentReference = firestore
            .collection("Patients")
            .document(auth.uid!!)

        documentReference.get().addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
            val userData = documentSnapshot.data
            val doctorName = userData?.get("doctor") as String
            newChat["doctorName"] = doctorName

            firestore
                .collection("DoctorFields")
                .document(doctorUid)
                .collection("Chats")
                .document(auth.uid!!)
                .set(newChat)

            firestore
                .collection("PatientFields")
                .document(auth.uid!!)
                .collection("Chats")
                .document(doctorUid)
                .set(newChat)
        }


    }

    @SuppressLint("SimpleDateFormat")
    private fun createFirestorePatient(userName: String) {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        val today = Calendar.getInstance().time
        val dateString = dateFormat.format(today)
        val dateLong = dateString.toLong()
        val patient = hashMapOf<String, Any>(
            "patientId" to auth.uid!!,
            "patientName" to userName,
            "date" to getDate(),
            "dateLong" to dateLong
        )

        firestore
            .collection("DoctorFields")
            .document(doctorUid)
            .collection("Patients")
            .document(auth.uid!!)
            .set(patient)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        val today = Calendar.getInstance().time
        return dateFormat.format(today)
    }

}