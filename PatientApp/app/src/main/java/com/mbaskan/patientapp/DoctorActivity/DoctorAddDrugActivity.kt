package com.mbaskan.patientapp.DoctorActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.database.values
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mbaskan.patientapp.DoctorClass.Drug
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.ActivityDoctorAddDrugBinding

class DoctorAddDrugActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorAddDrugBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: FirebaseDatabase
    private var userId: String = ""
    private var drugId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorAddDrugBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore
        database = Firebase.database

        userId = intent.getStringExtra("userId").toString()

        drugId = intent.getStringExtra("drugId").toString()

        if(drugId != "null") {
            getDrugInfo(drugId)
        }


        setSupportActionBar(binding.toolbarAddDrug)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarAddDrug.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

    }

    fun addDrugButtonClick(view: View) {

        val name = binding.drugName.text.toString().trim()
        val usage = binding.drugUsage.text.toString().trim()
        val purpose = binding.drugPurpose.text.toString().trim()

        if (name == "" || usage == "" || purpose == ""){
            Toast.makeText(this,"Boş bırakılan bölümler bulunuyor!", Toast.LENGTH_LONG).show()
        }
        else{

            val databaseReference: DatabaseReference = database.reference
            val drug= Drug(name, purpose, usage)

            if(drugId != "null") {

                val updatedDrug = mapOf(
                    "name" to name,
                    "purpose" to purpose,
                    "usage" to usage
                )

                binding.addDrugProcess.visibility = View.VISIBLE

                databaseReference.child("Drugs").child(auth.uid!!).child(userId).child(drugId)
                    .updateChildren(updatedDrug).addOnSuccessListener {
                        databaseReference.child("Drugs").child(userId).child(auth.uid!!).child(drugId)
                            .updateChildren(updatedDrug)

                        Toast.makeText(this, "İlaç bilgileri güncellendi!", Toast.LENGTH_SHORT)
                            .show()


                        binding.addDrugProcess.visibility = View.GONE

                    }.addOnFailureListener {

                    Toast.makeText(
                        this,
                        "İlaç bilgileri güncellenemedi.",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.addDrugProcess.visibility = View.GONE

                    }


            }
            else {

                val newDrugId =
                    databaseReference.child("Drugs").child(auth.uid!!).child(userId).push().key

                binding.addDrugProcess.visibility = View.VISIBLE

                databaseReference.child("Drugs").child(auth.uid!!).child(userId).child(newDrugId!!)
                    .setValue(drug)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            databaseReference.child("Drugs").child(userId).child(auth.uid!!)
                                .child(newDrugId).setValue(drug)
                            Toast.makeText(this, "İlaç bilgileri kaydedildi!", Toast.LENGTH_SHORT)
                                .show()

                            binding.drugName.text = null
                            binding.drugUsage.text = null
                            binding.drugPurpose.text = null
                            binding.addDrugProcess.visibility = View.GONE

                        } else {
                            Toast.makeText(
                                this,
                                "İlaç bilgileri kaydedilemedi:",
                                Toast.LENGTH_SHORT
                            ).show()

                            binding.addDrugProcess.visibility = View.GONE
                        }
                    }
            }
        }
    }

    private fun getDrugInfo(drugId: String) {
        val reference =  database.getReference("Drugs/${auth.uid}/$userId/$drugId")
        binding.addDrugProcess.visibility = View.VISIBLE

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val name = dataSnapshot.child("name").getValue(String::class.java)
                val purpose = dataSnapshot.child("purpose").getValue(String::class.java)
                val usage = dataSnapshot.child("usage").getValue(String::class.java)

                binding.drugName.setText(name)
                binding.drugPurpose.setText(purpose)
                binding.drugUsage.setText(usage)

                binding.addDrugProcess.visibility = View.GONE

            }

            override fun onCancelled(databaseError: DatabaseError) {


            }
        })
    }
}