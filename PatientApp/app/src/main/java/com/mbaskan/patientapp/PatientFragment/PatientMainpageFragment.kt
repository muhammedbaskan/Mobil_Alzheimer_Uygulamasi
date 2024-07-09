package com.mbaskan.patientapp.PatientFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.FragmentPatientMainpageBinding

class PatientMainpageFragment : Fragment() {

    private var _binding: FragmentPatientMainpageBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPatientMainpageBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.alzheimerLifeInfoImageView.setOnClickListener {
            alzheimerLifeInfoClick(view)
        }
        binding.alzheimerInfoimageView.setOnClickListener {
            alzheimerLifeClick(view)
        }
        binding.alzheimerRiskInfoimageView.setOnClickListener {
            alzheimerRiskInfoClick(view)
        }
        binding.alzheimerExerciseimageView.setOnClickListener {
            alzheimerExerciseClick(view)
        }
    }

    fun alzheimerLifeInfoClick(view: View) {
        findNavController().navigate(R.id.action_patientMainpageFragment_to_patientAlzheimerLifeInfoFragment)
    }

    fun alzheimerLifeClick(view: View) {
        findNavController().navigate(R.id.action_patientMainpageFragment_to_patientAlzheimerInfoFragment)
    }

    fun alzheimerRiskInfoClick(view: View) {
        findNavController().navigate(R.id.action_patientMainpageFragment_to_patientAlzheimerRiskInfoFragment)
    }

    fun alzheimerExerciseClick(view: View) {
        findNavController().navigate(R.id.action_patientMainpageFragment_to_patientAlzheimerExerciseFragment)
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PatientMainpageFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
