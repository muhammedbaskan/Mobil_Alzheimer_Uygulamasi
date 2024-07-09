package com.mbaskan.patientapp.DoctorAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.mbaskan.patientapp.DoctorClass.ChatList
import com.mbaskan.patientapp.DoctorFragment.DoctorMessagePreviousFragmentDirections
import com.mbaskan.patientapp.databinding.CardDoctorChatListBinding

class DoctorChatListAdapter(private val navController: NavController,
                            private val chatList: MutableList<ChatList>) : RecyclerView.Adapter<DoctorChatListAdapter.DoctorChatListHolder>() {

    class DoctorChatListHolder(val binding: CardDoctorChatListBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorChatListHolder {
        val binding = CardDoctorChatListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DoctorChatListHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorChatListHolder, position: Int) {
        val user = chatList[position]
        holder.binding.doctorChatListUserIcon.text = user.userName!!.substring(0,1)
        holder.binding.doctorChatListUserName.text = user.userName
        if(user.userName!!.length > 15) {
            holder.binding.doctorChatListUserName.text = user.userName!!.substring(0,12) + "..."
        }

        holder.binding.doctorChatListCardView.setOnClickListener {
            val action = DoctorMessagePreviousFragmentDirections.actionDoctorMessagePreviousFragmentToDoctorMessageFragment(user.userId)
            navController.navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}