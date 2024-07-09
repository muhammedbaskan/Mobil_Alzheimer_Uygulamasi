package com.mbaskan.patientapp.DoctorAdapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mbaskan.patientapp.DoctorActivity.DoctorAddDrugActivity
import com.mbaskan.patientapp.DoctorClass.Drug
import com.mbaskan.patientapp.databinding.CardDoctorDrugListBinding

class DoctorDrugListAdapter(private val drugList: MutableList<Drug>)
    : RecyclerView.Adapter<DoctorDrugListAdapter.DoctorDrugListHolder>() {

    private var auth: FirebaseAuth = Firebase.auth
    private var database: FirebaseDatabase = Firebase.database

    class DoctorDrugListHolder(val binding: CardDoctorDrugListBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorDrugListHolder {
        val binding = CardDoctorDrugListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DoctorDrugListHolder(binding)
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: DoctorDrugListHolder, position: Int) {
        val drug = drugList[position]
        holder.binding.drugName.text = drug.name!!
        holder.binding.drugPurpose.text = drug.purpose
        holder.binding.drugUsage.text = drug.usage

        holder.binding.drugCardView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DoctorAddDrugActivity::class.java)
            intent.putExtra("drugId", drug.key)
            intent.putExtra("userId", drug.user)
            holder.itemView.context.startActivity(intent)
        }

        holder.binding.drugCardView.setOnLongClickListener {
            val alert = AlertDialog.Builder(holder.itemView.context)
            alert.setTitle("")
            alert.setMessage("İlaç silinsin mi ?")
                .setPositiveButton("Evet") { dialog, _ ->

                    val databaseReference: DatabaseReference = database.reference

                        databaseReference.child("Drugs").child(auth.uid!!).child(drug.user!!).child(drug.key!!)
                            .removeValue().addOnSuccessListener {
                                databaseReference.child("Drugs").child(drug.user!!).child(auth.uid!!).child(drug.key!!)
                                    .removeValue()

                                drugList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, drugList.size)

                                Toast.makeText(holder.itemView.context, "İlaç bilgileri silindi!", Toast.LENGTH_SHORT)
                                    .show()

                            }.addOnFailureListener {

                                Toast.makeText(
                                    holder.itemView.context,
                                    "İlaç bilgileri silinemedi.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    dialog.dismiss()
                }
                .setNegativeButton("Hayır") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            true
        }
    }

    override fun getItemCount(): Int {
        return drugList.size
    }
}