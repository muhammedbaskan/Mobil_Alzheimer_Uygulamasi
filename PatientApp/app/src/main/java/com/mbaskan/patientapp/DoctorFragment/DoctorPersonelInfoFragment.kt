package com.mbaskan.patientapp.DoctorFragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.FragmentDoctorPersonelInfoBinding


data class Doctor(
    val name: String? = null,
    val surname: String? = null,
    val phone: String? = null,
    val province: String? = null,
    val district: String? = null,
    val address: String? = null,
    val gender: String? = null

)
class DoctorPersonelInfoFragment : Fragment() {
    private var _binding: FragmentDoctorPersonelInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoctorPersonelInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val currentUserUid = auth.currentUser?.uid

        if (currentUserUid != null) {

            firestore.collection("Doctors").document(currentUserUid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val doctor = documentSnapshot.toObject(Doctor::class.java)
                        if (doctor != null) {

                            binding.DoctorPersonalInfoName.setText(
                                (doctor.name + " " + doctor.surname) ?: ""
                            )
                            binding.DoctorPersonalInfoPhone.setText(doctor.phone ?: "")
                            binding.DoctorPersonalInfoProvince.setText(doctor.province ?: "")
                            binding.DoctorPersonalInfoDistrict.setText(doctor.district ?: "")
                            binding.DoctorPersonalInfoAdress.setText(doctor.address ?: "")
                            doctor.gender?.let { gender ->
                                if (gender == "Erkek") {
                                    binding.doctorPersonalInfoMaleRadioButton.isChecked = true
                                } else if (gender == "Kadın") {
                                    binding.doctorPersonalInfoFemaleRadioButton.isChecked = true
                                }
                            }


                        }
                    }
                    else {

                        binding.DoctorPersonalInfoName.setText("")
                        binding.DoctorPersonalInfoPhone.setText("")
                        binding.DoctorPersonalInfoProvince.setText("")
                        binding.DoctorPersonalInfoDistrict.setText("")
                        binding.DoctorPersonalInfoAdress.setText("")
                        binding.doctorPersonalInfoMaleRadioButton.isChecked = false
                        binding.doctorPersonalInfoFemaleRadioButton.isChecked = false

                        Log.d(ContentValues.TAG, "Belge bulunamadı.")
                    }
                }
                .addOnFailureListener { e ->

                    Log.e(ContentValues.TAG, "Firestore'dan belge alınamadı: ${e.message}")
                }



        }
        binding.doctorPersonalInfoButton.setOnClickListener {
            doctorPersonalInfoButtonClick()
        }

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarDoctorPersonelInfo)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarDoctorPersonelInfo.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }


        return view
    }
    private fun doctorPersonalInfoButtonClick() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val name = binding.DoctorPersonalInfoName.text.toString().trim()
            val phone = binding.DoctorPersonalInfoPhone.text.toString().trim()
            val province = binding.DoctorPersonalInfoProvince.text.toString().trim()
            val district = binding.DoctorPersonalInfoDistrict.text.toString().trim()
            val address = binding.DoctorPersonalInfoAdress.text.toString().trim()
            val selectedGenderId = binding.doctorPersonalInfoRadioGroup.checkedRadioButtonId
            val gender = when (selectedGenderId) {
                R.id.doctorPersonalInfoMaleRadioButton -> "Erkek"
                R.id.doctorPersonalInfoFemaleRadioButton -> "Kadın"
                else -> ""
            }
            if (name.isEmpty() || phone.isEmpty() || province.isEmpty() || district.isEmpty() || address.isEmpty() || gender == null) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurunuz!", Toast.LENGTH_SHORT).show()
                return
            }


            val updatedDoctorInfo = mutableMapOf<String, Any>(
                "name" to name,
                "phone" to phone,
                "province" to province,
                "district" to district,
                "address" to address,
                "gender" to gender
            )

            firestore.collection("Doctors").document(currentUserUid)
                .update(updatedDoctorInfo)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Doktor bilgileri kaydedildi!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Doktor bilgileri kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}