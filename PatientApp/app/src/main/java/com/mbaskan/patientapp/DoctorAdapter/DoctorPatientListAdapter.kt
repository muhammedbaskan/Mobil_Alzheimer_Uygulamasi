package com.mbaskan.patientapp.DoctorAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.mbaskan.patientapp.DoctorClass.PatientList
import com.mbaskan.patientapp.DoctorFragment.DoctorPatientInfoFragmentDirections
import com.mbaskan.patientapp.databinding.CardDoctorChatListBinding

class DoctorPatientListAdapter(private val navController: NavController,
                               private val patientList: MutableList<PatientList>
)
    : RecyclerView.Adapter<DoctorPatientListAdapter.DoctorPatientListHolder>() {

    class DoctorPatientListHolder(val binding: CardDoctorChatListBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorPatientListHolder {
        val binding = CardDoctorChatListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DoctorPatientListHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorPatientListHolder, position: Int) {
        val user = patientList[position]
        holder.binding.doctorChatListUserIcon.text = user.userName!!.substring(0,1)
        holder.binding.doctorChatListUserName.text = user.userName
        if(user.userName!!.length > 15) {
            holder.binding.doctorChatListUserName.text = user.userName!!.substring(0,12) + "..."
        }

        holder.binding.doctorChatListCardView.setOnClickListener {
            val action = DoctorPatientInfoFragmentDirections.actionDoctorPatientInfoFragmentToDoctorPatientDetailFragment(user.userId, user.userName)
            navController.navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return patientList.size
    }
}