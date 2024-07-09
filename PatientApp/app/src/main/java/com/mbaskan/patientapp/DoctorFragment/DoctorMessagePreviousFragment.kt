package com.mbaskan.patientapp.DoctorFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mbaskan.patientapp.DoctorAdapter.DoctorChatListAdapter
import com.mbaskan.patientapp.DoctorClass.ChatList
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.FragmentDoctorMessagePreviousBinding


class DoctorMessagePreviousFragment : Fragment() {
    private var _binding: FragmentDoctorMessagePreviousBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    val chatList = mutableListOf<ChatList>()

    private lateinit var chatListAdapter: DoctorChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        firestore = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoctorMessagePreviousBinding.inflate(inflater, container, false)

        binding.doctorChatListRv.layoutManager = LinearLayoutManager(requireContext())
        chatListAdapter = DoctorChatListAdapter(findNavController(),chatList)
        binding.doctorChatListRv.adapter = chatListAdapter


        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarDoctorMessagePrevious)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarDoctorMessagePrevious.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getChatListFromFirebase() {
        chatList.clear()
        binding.doctorMessagePreviousProcess.visibility = View.VISIBLE

        val collectionReference: CollectionReference =
            firestore.collection("DoctorFields").document(auth.uid!!)
                .collection("Chats")
        collectionReference.addSnapshotListener { value, error ->
            if (value != null) {
                if(value.documents.size > 0) {
                    for (documentSnapshot in value.documents) {
                        val userData = documentSnapshot.data
                        val userId = userData!!["patientId"] as String
                        val userName = userData["patientName"] as String

                        val chatListTemp = ChatList()
                        chatListTemp.userName = userName
                        chatListTemp.userId = userId

                        chatList.add(chatListTemp)

                        chatListAdapter.notifyDataSetChanged()

                        binding.doctorMessagePreviousProcess.visibility = View.GONE


                        if (chatList.isEmpty()) {
                            binding.doctorNoChatText .visibility = View.VISIBLE
                        } else {
                            binding.doctorNoChatText.visibility = View.GONE
                        }
                    }
                }

                else {
                    binding.doctorMessagePreviousProcess.visibility = View.GONE
                    binding.doctorNoChatText.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getChatListFromFirebase()
    }
}