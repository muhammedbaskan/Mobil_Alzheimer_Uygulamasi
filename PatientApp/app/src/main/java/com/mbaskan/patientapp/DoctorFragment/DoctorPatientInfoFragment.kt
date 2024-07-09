package com.mbaskan.patientapp.DoctorFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mbaskan.patientapp.DoctorAdapter.DoctorPatientListAdapter
import com.mbaskan.patientapp.DoctorClass.PatientList
import com.mbaskan.patientapp.databinding.FragmentDoctorPatientInfoBinding

class DoctorPatientInfoFragment : Fragment() {
    private var _binding: FragmentDoctorPatientInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    val patientList = mutableListOf<PatientList>()

    private lateinit var patientListAdapter: DoctorPatientListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        firestore = Firebase.firestore
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoctorPatientInfoBinding.inflate(inflater, container, false)

        binding.doctorPatientListRv.layoutManager = LinearLayoutManager(requireContext())
        patientListAdapter = DoctorPatientListAdapter(findNavController(),patientList)
        binding.doctorPatientListRv.adapter = patientListAdapter


        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getPatientsFromFirebase() {
        patientList.clear()
        binding.patientInfoProcess.visibility = View.VISIBLE

        val collectionReference: CollectionReference =
            firestore.collection("DoctorFields").document(auth.uid!!)
                .collection("Patients")
        collectionReference.addSnapshotListener { value, error ->
            if (value != null) {
                if(value.documents.size > 0) {
                    for (documentSnapshot in value.documents) {
                        val patientData = documentSnapshot.data
                        val patientId = patientData!!["patientId"] as String

                        firestore.collection("Patients").document(patientId)
                            .get().addOnSuccessListener {
                                if (it.exists()) {
                                    val patientName =
                                        it.get("name").toString() + " " + it.get("surname")

                                    val patientListTemp = PatientList()
                                    patientListTemp.userName = patientName
                                    patientListTemp.userId = patientId

                                    patientList.add(patientListTemp)

                                    patientListAdapter.notifyDataSetChanged()

                                    binding.patientInfoProcess.visibility = View.GONE


                                    if (patientList.isEmpty()) {
                                        binding.patientInfoNoPatientText.visibility = View.VISIBLE
                                    } else {
                                        binding.patientInfoNoPatientText.visibility = View.GONE
                                    }

                                }

                            }
                    }
                }
                else {
                    binding.patientInfoProcess.visibility = View.GONE
                    binding.patientInfoNoPatientText.visibility = View.VISIBLE
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        getPatientsFromFirebase()
    }
}