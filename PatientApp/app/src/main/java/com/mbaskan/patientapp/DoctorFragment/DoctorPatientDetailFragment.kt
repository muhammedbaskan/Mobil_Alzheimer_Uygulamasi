package com.mbaskan.patientapp.DoctorFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
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
import com.mbaskan.patientapp.DoctorActivity.DoctorAddDrugActivity
import com.mbaskan.patientapp.DoctorAdapter.DoctorDrugListAdapter
import com.mbaskan.patientapp.DoctorClass.Drug
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.FragmentDoctorPatientDetailBinding

class DoctorPatientDetailFragment : Fragment() {
    private var _binding: FragmentDoctorPatientDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: FirebaseDatabase
    private var userUid: String = ""
    private var userName: String = ""
    val drugList = mutableListOf<Drug>()

    private lateinit var drugAdapter: DoctorDrugListAdapter

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
        _binding = FragmentDoctorPatientDetailBinding.inflate(inflater, container, false)
        val view = binding.root



        binding.doctorPatientDetailRv.layoutManager = LinearLayoutManager(requireContext())
        drugAdapter = DoctorDrugListAdapter(drugList)
        binding.doctorPatientDetailRv.adapter = drugAdapter

        val binding2: DoctorPatientDetailFragmentArgs by navArgs()

        userUid = binding2.userId.toString()
        userName = binding2.userName.toString()

        binding.patientDetailText.text = userName

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarPatientDetail)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarPatientDetail.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        return view
    }

    private fun getDrugsFromFirebase() {
        val databaseReference: DatabaseReference = database.reference
        binding.patientDetailProcess.visibility = View.VISIBLE
        drugList.clear()

        databaseReference.child("Drugs").child(auth.uid!!).child(userUid)

            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists() || dataSnapshot.childrenCount.toInt() == 0) {

                        binding.patientDetailNoDrugText.visibility = View.VISIBLE
                        binding.patientDetailProcess.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                    binding.patientDetailProcess.visibility = View.GONE
                }
            })

        databaseReference.child("Drugs").child(auth.uid!!).child(userUid)
            .addChildEventListener(object : ChildEventListener {

                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    binding.patientDetailProcess.visibility = View.GONE
                    if(dataSnapshot.childrenCount.toInt() == 3 || dataSnapshot.childrenCount.toInt() == 1) {
                        val drug = dataSnapshot.getValue(Drug::class.java)
                        drug?.let {
                            drug.key = dataSnapshot.key
                            drug.user = userUid
                            drugList.add(it)
                            drugAdapter.notifyDataSetChanged()
                        }
                    }
                    else if(dataSnapshot.childrenCount > 3) {
                        for (child in dataSnapshot.children) {
                            val drug = dataSnapshot.getValue(Drug::class.java)
                            drug?.let {
                                drug.key = dataSnapshot.key
                                drug.key = userUid
                                drugList.add(it)
                                drugAdapter.notifyDataSetChanged()
                            }
                        }
                    }

                    if (drugList.isEmpty()) {
                        binding.patientDetailNoDrugText.visibility = View.VISIBLE
                    } else {
                        binding.patientDetailNoDrugText.visibility = View.GONE
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {
                    binding.patientDetailProcess.visibility = View.GONE

                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.patientDetailFab.setOnClickListener {
            val intent = Intent(activity, DoctorAddDrugActivity::class.java)
            intent.putExtra("userId", userUid)
            startActivity(intent)
        }
    }

    override fun onResume() {
        binding.patientDetailNoDrugText .visibility = View.GONE
        binding.patientDetailProcess.visibility = View.GONE
        super.onResume()
        getDrugsFromFirebase()
    }
}