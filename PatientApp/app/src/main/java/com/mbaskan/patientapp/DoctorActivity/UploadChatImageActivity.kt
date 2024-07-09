package com.mbaskan.patientapp.DoctorActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mbaskan.patientapp.DoctorClass.ImageObject
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.ActivityUploadChatImageBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

class UploadChatImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadChatImageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: FirebaseDatabase
    private var userId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadChatImageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore
        database = Firebase.database

        userId = intent.getStringExtra("userId").toString()

        binding.addPhotoImageview.setImageBitmap(ImageObject.getBitmap())

        setSupportActionBar(binding.toolbarAddPhoto)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarAddPhoto.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

    }

    fun addPhotoButtonClick(view: View) {
        binding.addPhotoButton.isClickable = false
        binding.addPhotoProcess.visibility = View.VISIBLE
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val storage = Firebase.storage
        val reference = storage.reference
        val imagesReference = reference.child("Images").child("Chats").child(auth.uid!!).child(imageName)

        if (ImageObject.getUri() != null) {
            imagesReference.putFile(ImageObject.getUri()!!).addOnSuccessListener {

                val uploadedPictureReference = storage.reference.child("Images").child("Chats").child(auth.uid!!).child(imageName)
                uploadedPictureReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    val databaseReference: DatabaseReference = database.reference
                    val messageId = databaseReference.child("Chats").child(auth.uid!!).child(userId).push().key

                    if (messageId != null) {
                        val messageMap = hashMapOf<String, Any>(
                            "message" to "",
                            "date" to getDate(),
                            "from" to auth.uid!!,
                            "imageUrl" to downloadUrl
                        )

                        databaseReference.child("Chats").child(auth.uid!!).child(userId).child(messageId).setValue(messageMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    databaseReference.child("Chats").child(userId).child(auth.uid!!).child(messageId).setValue(messageMap)
                                    binding.addPhotoProcess.visibility = View.GONE
                                    binding.addPhotoButton.isClickable = true
                                    this.onBackPressedDispatcher.onBackPressed()
                                }
                            }
                    }
                    else {
                        binding.addPhotoProcess.visibility = View.GONE
                        binding.addPhotoButton.isClickable = true
                    }
                }.addOnFailureListener { exception ->
                    binding.addPhotoProcess.visibility = View.GONE
                    binding.addPhotoButton.isClickable = true
                    Toast.makeText(
                        this,
                        exception.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        else {
            binding.addPhotoProcess.visibility = View.GONE
            binding.addPhotoButton.isClickable = true
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        val today = Calendar.getInstance().time
        return dateFormat.format(today)
    }
}
