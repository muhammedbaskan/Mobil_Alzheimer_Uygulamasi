package com.mbaskan.patientapp.DoctorFragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mbaskan.patientapp.DoctorActivity.DoctorLoginActivity
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.FragmentDoctorSettingsBinding

class DoctorSettingsFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentDoctorSettingsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDoctorSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarDoctorSettings)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarDoctorSettings.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.doctorPersonelInfocardView.setOnClickListener{
            doctorPersonelInfoClick(view)
        }
        binding.doctorSignOutcardView.setOnClickListener{
            doctorSignOutClick(view)
        }

        binding.doctorDeleteAccountText.setOnClickListener {
            deleteAccount(view)
        }
    }

    private fun deleteAccount(view: View) {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Hesabını Sil")
        alert.setMessage("Hesabınızı silmek istediğinize emin misiniz?" +
                "Tüm bilgileriniz kalıcı olarak silinecektir.")
            .setPositiveButton("Evet") { dialog, _ ->
                auth.currentUser?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            auth.signOut()
                            signOut()
                            dialog.dismiss()
                            Toast.makeText(requireContext(), "Hesabınız silindi!", Toast.LENGTH_LONG).show()
                        } else {
                            task.exception?.let { exception ->
                                println("Hata: ${exception.message}")
                            }
                        }
                    }
                    ?: run {
                        println("Kullanıcı oturumu açık değil.")
                    }
            }.setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    fun doctorPersonelInfoClick(view: View){
        findNavController().navigate(R.id.action_doctorSettingsFragment_to_doctorPersonelInfoFragment)
    }
    fun doctorSignOutClick(view: View){

        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Çıkış Yap")
        alert.setMessage("Çıkış yapmak istediğinize emin misiniz?")
            .setPositiveButton("Evet"){dialog, _ ->
                signOut()
                dialog.dismiss()
            }
            .setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }

            .show()

    }
    private fun signOut() {

        val intent = Intent(requireActivity(), DoctorLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
        auth.signOut()
    }
}