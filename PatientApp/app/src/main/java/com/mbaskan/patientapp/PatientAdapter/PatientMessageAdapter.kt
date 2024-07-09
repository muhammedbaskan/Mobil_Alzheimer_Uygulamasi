package com.mbaskan.patientapp.PatientAdapter


import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mbaskan.patientapp.DoctorClass.Message
import com.mbaskan.patientapp.DoctorActivity.FullscreenPhotoActivity
import com.mbaskan.patientapp.databinding.CardChatImageLeftBinding
import com.mbaskan.patientapp.databinding.CardChatImageRightBinding
import com.mbaskan.patientapp.databinding.CardMessageLeftBinding
import com.mbaskan.patientapp.databinding.CardMessageRightBinding
import com.squareup.picasso.Picasso

class PatientMessageAdapter(private val messageList: MutableList<Message>, private val uid: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var auth: FirebaseAuth = Firebase.auth
    private var database: FirebaseDatabase = Firebase.database

    companion object {
        private const val VIEW_TYPE_RIGHT = 1
        private const val VIEW_TYPE_LEFT = 2
        private const val VIEW_IMAGE_RIGHT = 3
        private const val VIEW_IMAGE_LEFT = 4
    }

    inner class RightMessageHolder(val binding: CardMessageRightBinding) : RecyclerView.ViewHolder(binding.root)

    inner class LeftMessageHolder(val binding: CardMessageLeftBinding) : RecyclerView.ViewHolder(binding.root)

    inner class RightImageMessageHolder(val binding: CardChatImageRightBinding) : RecyclerView.ViewHolder(binding.root)

    inner class LeftImageMessageHolder(val binding: CardChatImageLeftBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_RIGHT) {
            val binding = CardMessageRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RightMessageHolder(binding)
        } else if (viewType == VIEW_TYPE_LEFT){
            val binding = CardMessageLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LeftMessageHolder(binding)
        }
        else if (viewType == VIEW_IMAGE_RIGHT) {
            val binding = CardChatImageRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RightImageMessageHolder(binding)
        }
        else {
            val binding = CardChatImageLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LeftImageMessageHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_RIGHT -> {
                val rightHolder = holder as RightMessageHolder
                rightHolder.binding.messageTextRight.text = message.message
                rightHolder.binding.messageDateRight.text = message.date
            }
            VIEW_TYPE_LEFT -> {
                val leftHolder = holder as LeftMessageHolder
                leftHolder.binding.messageTextLeft.text = message.message
                leftHolder.binding.messageDateLeft.text = message.date
            }
            VIEW_IMAGE_RIGHT -> {
                val rightImageHolder = holder as RightImageMessageHolder
                rightImageHolder.binding.messageDateRight.text = message.date
                Picasso.get().load(message.imageUrl).into(rightImageHolder.binding.chatPhotoRight)
            }
            VIEW_IMAGE_LEFT -> {
                val leftImageHolder = holder as LeftImageMessageHolder
                leftImageHolder.binding.messageDateLeft.text = message.date
                Picasso.get().load(message.imageUrl).into(leftImageHolder.binding.chatPhotoLeft)
            }
        }

        holder.itemView.setOnClickListener {
            if(holder.itemViewType == VIEW_IMAGE_LEFT || holder.itemViewType == VIEW_IMAGE_RIGHT) {
                val intent = Intent(holder.itemView.context, FullscreenPhotoActivity::class.java)
                intent.putExtra("imageUrl", message.imageUrl)
                holder.itemView.context.startActivity(intent)
            }
        }

        holder.itemView.setOnLongClickListener {
            if(holder.itemViewType == VIEW_TYPE_RIGHT || holder.itemViewType == VIEW_IMAGE_RIGHT) {
                val alert = AlertDialog.Builder(holder.itemView.context)
                alert.setTitle("")
                alert.setMessage("Mesaj silinsin mi ?")
                    .setPositiveButton("Evet") { dialog, _ ->

                        val databaseReference: DatabaseReference = database.reference

                        databaseReference.child("Chats").child(auth.uid!!).child(message.user!!)
                            .child(message.key!!)
                            .removeValue().addOnCompleteListener {
                                databaseReference.child("Chats").child(message.user!!)
                                    .child(auth.uid!!).child(message.key!!)
                                    .removeValue().addOnCompleteListener{

                                    }

                            }.addOnFailureListener {

                                Toast.makeText(
                                    holder.itemView.context,
                                    "Mesajınız silinemedi.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        dialog.dismiss()
                    }
                    .setNegativeButton("Hayır") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.imageUrl?.isNotEmpty() == true && message.from == uid) {
            VIEW_IMAGE_RIGHT
        } else if (message.imageUrl?.isNotEmpty() == true && message.from != uid){
            VIEW_IMAGE_LEFT
        }
        else if (message.imageUrl?.isEmpty() == true && message.from == uid){
            VIEW_TYPE_RIGHT
        }
        else {
            VIEW_TYPE_LEFT
        }
    }
}
