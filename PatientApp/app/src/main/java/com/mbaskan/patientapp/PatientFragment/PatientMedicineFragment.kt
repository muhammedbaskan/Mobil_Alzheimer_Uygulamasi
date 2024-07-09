package com.mbaskan.patientapp.PatientFragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mbaskan.patientapp.DoctorClass.Drug
import com.mbaskan.patientapp.PatientAdapter.PatientDrugListAdapter
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.FragmentPatientMedicineBinding

class PatientMedicineFragment : Fragment() {
    private var _binding: FragmentPatientMedicineBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: FirebaseDatabase
    private var doctorUid: String? = null

    val drugList = mutableListOf<Drug>()

    private lateinit var drugAdapter: PatientDrugListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore
        database = Firebase.database

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPatientMedicineBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.patientDrugListRv.layoutManager = LinearLayoutManager(requireContext())
        drugAdapter = PatientDrugListAdapter(drugList)
        binding.patientDrugListRv.adapter = drugAdapter

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar6)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbar6.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        return view
    }

    private fun getDrugsFromFirebase() {
        firestore.collection("Patients").document(auth.uid!!).get()
            .addOnSuccessListener { document ->

                binding.patientDrugListProcess.visibility = View.VISIBLE

                doctorUid = document.getString("doctoruid")
                val databaseReference: DatabaseReference = database.reference

                databaseReference.child("Drugs").child(auth.uid!!).child(doctorUid!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.exists() || dataSnapshot.childrenCount.toInt() == 0) {
                                // No data present
                                binding.patientNoDrugText.visibility = View.VISIBLE
                                binding.patientDrugListProcess.visibility = View.GONE
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                            binding.patientDrugListProcess.visibility = View.GONE
                        }
                    })



                databaseReference.child("Drugs").child(auth.uid!!).child(doctorUid!!)
                    .addChildEventListener(object : ChildEventListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onChildAdded(
                            dataSnapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            binding.patientDrugListProcess.visibility = View.GONE

                            if (dataSnapshot.childrenCount.toInt() == 3 || dataSnapshot.childrenCount.toInt() == 1) {
                                val drug = dataSnapshot.getValue(Drug::class.java)
                                drug?.let {
                                    drugList.add(it)
                                    drugAdapter.notifyDataSetChanged()

                                }
                            } else if (dataSnapshot.childrenCount > 1) {
                                for (child in dataSnapshot.children) {
                                    val drug = dataSnapshot.getValue(Drug::class.java)
                                    drug?.let {
                                        drugList.add(it)
                                        drugAdapter.notifyDataSetChanged()

                                    }
                                }
                            }

                            if (drugList.isEmpty()) {
                                binding.patientNoDrugText.visibility = View.VISIBLE
                            } else {
                                binding.patientNoDrugText.visibility = View.GONE
                            }
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {

                        }

                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {

                        }

                        override fun onCancelled(error: DatabaseError) {
                            binding.patientDrugListProcess.visibility = View.GONE
                        }
                    })
            }
    }

    override fun onResume() {
        binding.patientNoDrugText.visibility = View.GONE
        binding.patientDrugListProcess.visibility = View.GONE
        super.onResume()
        getDrugsFromFirebase()

    }
}