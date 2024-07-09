package com.mbaskan.patientapp.PatientFragment

import android.content.ContentValues.TAG
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
import com.mbaskan.patientapp.databinding.FragmentPatientPersonelInfoBinding

data class Patient(
    val name: String? = null,
    val phone: String? = null,
    val province: String? = null,
    val district: String? = null,
    val address: String? = null,
    val gender: String? = null,
    val doctor: String? = null,
    val doctorUid: String? = null,
    val createdDate: String? = null

)

class PatientPersonelInfoFragment : Fragment() {
    private var _binding: FragmentPatientPersonelInfoBinding? = null
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

        _binding = FragmentPatientPersonelInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {

            firestore.collection("Patients").document(currentUserUid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val patient = documentSnapshot.toObject(Patient::class.java)
                        if (patient != null) {

                            binding.PatientPersonalInfoName.setText(patient.name ?: "")
                            binding.PatientPersonalInfoPhone.setText(patient.phone ?: "")
                            binding.PatientPersonalInfoProvince.setText(patient.province ?: "")
                            binding.PatientPersonalInfoDistrict.setText(patient.district ?: "")
                            binding.PatientPersonalInfoAdress.setText(patient.address ?: "")
                            patient.gender?.let { gender ->
                                if (gender == "Erkek") {
                                    binding.patientPersonalInfoMaleRadioButton.isChecked = true
                                } else if (gender == "Kadın") {
                                    binding.patientPersonalInfoFemaleRadioButton.isChecked = true
                                }
                            }


                        }
                    }
                    else {

                        binding.PatientPersonalInfoName.setText("")
                        binding.PatientPersonalInfoPhone.setText("")
                        binding.PatientPersonalInfoProvince.setText("")
                        binding.PatientPersonalInfoDistrict.setText("")
                        binding.PatientPersonalInfoAdress.setText("")
                        binding.patientPersonalInfoMaleRadioButton.isChecked = false
                        binding.patientPersonalInfoFemaleRadioButton.isChecked = false

                        Log.d(TAG, "Belge bulunamadı.")
                    }
                }
                .addOnFailureListener { e ->

                    Log.e(TAG, "Firestore'dan belge alınamadı: ${e.message}")
                }



        }
        binding.patientPersonalInfoButton.setOnClickListener {
            patientPersonalInfoButtonClick()
        }


        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarPatientPersonelInfo)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarPatientPersonelInfo.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        return view
    }
    private fun patientPersonalInfoButtonClick() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val name = binding.PatientPersonalInfoName.text.toString().trim()
            val phone = binding.PatientPersonalInfoPhone.text.toString().trim()
            val province = binding.PatientPersonalInfoProvince.text.toString().trim()
            val district = binding.PatientPersonalInfoDistrict.text.toString().trim()
            val address = binding.PatientPersonalInfoAdress.text.toString().trim()
            val selectedGenderId = binding.patientPersonalInfoRadioGroup.checkedRadioButtonId
            val gender = when (selectedGenderId) {
                R.id.patientPersonalInfoMaleRadioButton -> "Erkek"
                R.id.patientPersonalInfoFemaleRadioButton -> "Kadın"
                else -> ""
            }
            if (name.isEmpty() || phone.isEmpty() || province.isEmpty() || district.isEmpty() || address.isEmpty() || gender == null) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurunuz!", Toast.LENGTH_SHORT).show()
                return
            }


            val updatedPatientInfo = mutableMapOf<String, Any>(
                "name" to name,
                "phone" to phone,
                "province" to province,
                "district" to district,
                "address" to address,
                "gender" to gender
            )

            firestore.collection("Patients").document(currentUserUid)
                .update(updatedPatientInfo)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Hasta bilgileri kaydedildi!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Hasta bilgileri kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

        companion object {


            @JvmStatic
            fun newInstance(param1: String, param2: String) =
                PatientPersonelInfoFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
        }

    }
