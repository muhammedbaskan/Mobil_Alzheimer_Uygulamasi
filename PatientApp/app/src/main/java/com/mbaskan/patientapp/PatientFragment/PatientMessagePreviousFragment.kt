package com.mbaskan.patientapp.PatientFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.FragmentPatientMessagePreviousBinding


class PatientMessagePreviousFragment : Fragment() {
    private var _binding: FragmentPatientMessagePreviousBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var doctorUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        firestore = Firebase.firestore

    }

    private fun getDoctorName() {
        binding.patientChatListProcess.visibility = View.VISIBLE
        binding.patientPreviousMessageCardView.visibility = View.INVISIBLE

        firestore.collection("Patients").document(auth.uid!!).get()
            .addOnSuccessListener { document ->
                var doctorName = document.getString("doctor")
                doctorUid = document.getString("doctoruid")
                if(doctorName!!.length > 15) {
                    doctorName = doctorName.substring(0,12) + "..."
                }
                binding.patientPreviousMessageUserName.text = doctorName
                binding.patientPreviousMessageUserIcon.text = doctorName.substring(0,1)

                binding.patientChatListProcess.visibility = View.GONE
                binding.patientPreviousMessageCardView.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Doktor yüklenemedi: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                binding.patientPreviousMessageCardView.isClickable = false

                binding.patientChatListProcess.visibility = View.VISIBLE
                binding.patientPreviousMessageCardView.visibility = View.INVISIBLE
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPatientMessagePreviousBinding.inflate(inflater, container, false)
        getDoctorName()

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarPatientMessagePrevious)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarPatientMessagePrevious.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //getDoctorName()

        binding.patientPreviousMessageCardView.setOnClickListener {
            messagePreviousToMessage(view)
        }
    }

    private fun messagePreviousToMessage(view: View) {
        val action = PatientMessagePreviousFragmentDirections.actionPatientMessagePreviousFragmentToPatientMessageFragment(doctorUid)
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()

    }
}