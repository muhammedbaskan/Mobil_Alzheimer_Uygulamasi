package com.mbaskan.patientapp.PatientFragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.FragmentPatientAlzheimerLifeInfoBinding

class PatientAlzheimerLifeInfoFragment : Fragment() {
    private var _binding: FragmentPatientAlzheimerLifeInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPatientAlzheimerLifeInfoBinding.inflate(inflater, container, false)
        val view = binding.root


        val videoView = binding.videoView1


        val uri = Uri.parse("android.resource://com.mbaskan.patientapp/" + R.raw.alzheimer)
        videoView.setVideoURI(uri)


        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)

        videoView.setMediaController(mediaController)
        videoView.requestFocus()
        videoView.setOnPreparedListener { mp ->
            mp.start()
            mediaController.show()
            mp.isLooping = true
            mp.setVolume(1.0f, 1.0f)
        }

        setToggleListener(binding.alzheimerLifeCardView, binding.textViewTitle1, binding.textViewContent1)
        setToggleListener(binding.alzheimerHealthCardView, binding.textViewTitle2, binding.textViewContent2)
        setToggleListener(binding.alzheimerContactCardView, binding.textViewTitle3, binding.textViewContent3)
        setToggleListener(binding.alzheimerEmotionCardView, binding.textViewTitle4, binding.textViewContent4)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarAlzheimerLifeInfo)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarAlzheimerLifeInfo.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }


        return view
    }
    private fun setToggleListener(cardView: View, titleView: View, contentView: View) {
        val listener = View.OnClickListener {
            toggleVisibility(contentView)
        }
        cardView.setOnClickListener(listener)
        titleView.setOnClickListener(listener)
    }
    private fun toggleVisibility(view: View) {
        view.visibility = if (view.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PatientAlzheimerLifeInfoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
