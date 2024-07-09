package com.mbaskan.patientapp.PatientFragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
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
import com.mbaskan.patientapp.DoctorActivity.UploadChatImageActivity
import com.mbaskan.patientapp.DoctorClass.ImageObject
import com.mbaskan.patientapp.DoctorClass.Message
import com.mbaskan.patientapp.PatientAdapter.PatientMessageAdapter
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.FragmentPatientMessageBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar


class PatientMessageFragment : Fragment() {
    private var _binding: FragmentPatientMessageBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: FirebaseDatabase
    private var doctorUid: String = ""
    val messageList = mutableListOf<Message>()

    private lateinit var messageAdapter: PatientMessageAdapter

    private var selectedBitmap : Bitmap? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    var selectedPicture : Uri? = null


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

        _binding = FragmentPatientMessageBinding.inflate(inflater, container, false)
        val view = binding.root

        val binding2: PatientMessageFragmentArgs by navArgs()

        doctorUid = binding2.doctorId.toString()

        binding.patientChatsRv.layoutManager = LinearLayoutManager(requireContext())
        messageAdapter = PatientMessageAdapter(messageList, auth.uid!!)
        binding.patientChatsRv.adapter = messageAdapter

        binding.patientNoMessageText.visibility = View.GONE
        binding.patientMessageProcess.visibility = View.GONE

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.patientChatsSendButton.setOnClickListener {
            patientChatsSendButtonClick(view)
        }

        registerLauncher()

        binding.patientAddImageButton.setOnClickListener {
            selectImage(view)
        }
        val rootView = binding.root
        binding.patientChatsText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val rect = Rect()
                        rootView.getWindowVisibleDisplayFrame(rect)
                        binding.patientChatsRv.scrollToPosition(messageList.size - 1)
                        rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }

        (activity as? AppCompatActivity )?.setSupportActionBar(binding.toolbarPatientMessage)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarPatientMessage.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun patientChatsSendButtonClick(view: View) {
        val doctorId = doctorUid
        val message: String = binding.patientChatsText.text.toString().trim()


        if (message.isEmpty()) {
            binding.patientChatsText.setText("")
            binding.patientChatsText.hint = "Boş mesaj gönderilemez"
        } else {
            binding.patientChatsText.hint = "Mesajınızı yazabilirsiniz"
            val databaseReference: DatabaseReference = database.reference
            val messageId = databaseReference.child("Chats").child(auth.uid!!).child(doctorId).push().key

            if (messageId != null) {
                val messageMap = hashMapOf<String, Any>(
                    "message" to message,
                    "date" to getDate(),
                    "from" to auth.uid!!,
                    "imageUrl" to ""
                )

                databaseReference.child("Chats").child(auth.uid!!).child(doctorId).child(messageId).setValue(messageMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            databaseReference.child("Chats").child(doctorId).child(auth.uid!!).child(messageId).setValue(messageMap)
                        }
                    }
            }
            binding.patientChatsText.setText("")
        }
    }

    private fun getMessageFromFirebase() {
        val databaseReference: DatabaseReference = database.reference

        messageList.clear()
        binding.patientMessageProcess.visibility = View.VISIBLE
        binding.patientSendMessageCard.visibility = View.INVISIBLE

        databaseReference.child("Chats").child(auth.uid!!).child(doctorUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists() || dataSnapshot.childrenCount.toInt() == 0) {

                        binding.patientNoMessageText.visibility = View.VISIBLE
                        binding.patientMessageProcess.visibility = View.GONE
                        binding.patientSendMessageCard.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                    binding.patientMessageProcess.visibility = View.GONE
                    binding.patientSendMessageCard.visibility = View.VISIBLE
                }
            })

        databaseReference.child("Chats").child(auth.uid!!).child(doctorUid)
            .addChildEventListener(object : ChildEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    binding.patientMessageProcess.visibility = View.GONE
                    if(dataSnapshot.childrenCount.toInt() == 4 || dataSnapshot.childrenCount.toInt() == 1) {
                        val message = dataSnapshot.getValue(Message::class.java)
                        message?.let {
                            message.key = dataSnapshot.key
                            message.user = doctorUid

                            val messageExist = messageList.find { msg -> msg.key == dataSnapshot.key }
                            if (messageExist == null) {
                                messageList.add(it)
                                messageAdapter.notifyDataSetChanged()
                                binding.patientChatsRv.scrollToPosition(messageList.size - 1)
                            }
                        }
                    }
                    else if(dataSnapshot.childrenCount > 4) {
                        for (child in dataSnapshot.children) {
                            val message = child.getValue(Message::class.java)
                            message?.let {
                                message.key = dataSnapshot.key
                                message.user = doctorUid
                                val messageExist = messageList.find { msg -> msg.key == dataSnapshot.key }
                                if (messageExist == null) {
                                    messageList.add(it)
                                    messageAdapter.notifyDataSetChanged()
                                    binding.patientChatsRv.scrollToPosition(messageList.size - 1)
                                }
                            }
                        }
                    }

                    if (messageList.isEmpty()) {
                        binding.patientNoMessageText.visibility = View.VISIBLE
                    } else {
                        binding.patientNoMessageText.visibility = View.GONE
                    }

                    binding.patientSendMessageCard.visibility = View.VISIBLE

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val message = snapshot.getValue(Message::class.java)
                    message?.let {
                        val messageExist = messageList.find { msg -> msg.key == snapshot.key }
                        messageExist?.let {
                            val position = messageList.indexOf(messageExist)
                            messageList.removeAt(position)
                            messageAdapter.notifyItemRemoved(position)
                            messageAdapter.notifyItemRangeChanged(position, messageList.size)

                            if(messageExist.from == auth.uid)
                            {
                                Toast.makeText(
                                    requireContext(),
                                    "Mesajınız silindi!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                            else
                            {
                                Toast.makeText(
                                    requireContext(),
                                    "Kullanıcı bir mesaj sildi!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {
                    binding.patientMessageProcess.visibility = View.GONE
                    binding.patientSendMessageCard.visibility = View.VISIBLE

                }
            })
    }


    @SuppressLint("SimpleDateFormat")
    fun getDate(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        val today = Calendar.getInstance().time
        return dateFormat.format(today)
    }

    private fun selectImage(view: View) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Galeri izni gereklidir", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",
                        View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Galeri izni gereklidir", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",
                        View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }


    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(
                                requireActivity().contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)

                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                selectedPicture
                            )
                        }

                        ImageObject.setUri(selectedPicture!!)
                        ImageObject.setBitmap(selectedBitmap!!)

                        val intent = Intent(requireContext(), UploadChatImageActivity::class.java)
                        intent.putExtra("userId", doctorUid)
                        startActivity(intent)


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {

                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {

                Toast.makeText(requireActivity(), "Galeri izni gereklidir!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        getMessageFromFirebase()
    }

}