package com.mbaskan.patientapp.PatientAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mbaskan.patientapp.DoctorClass.Drug
import com.mbaskan.patientapp.databinding.CardDoctorDrugListBinding

class PatientDrugListAdapter(private val drugList: MutableList<Drug>)
    : RecyclerView.Adapter<PatientDrugListAdapter.PatientDrugListHolder>() {

    class PatientDrugListHolder(val binding: CardDoctorDrugListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientDrugListHolder {
        val binding =
            CardDoctorDrugListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PatientDrugListHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PatientDrugListHolder, position: Int) {
        val drug = drugList[position]
        holder.binding.drugName.text = drug.name!!
        holder.binding.drugPurpose.text = drug.purpose
        holder.binding.drugUsage.text = drug.usage

    }

    override fun getItemCount(): Int {
        return drugList.size
    }
}