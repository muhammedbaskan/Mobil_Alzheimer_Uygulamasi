package com.mbaskan.patientapp.PatientAdapter
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mbaskan.patientapp.PatientFragment.PatientAlzheimerExerciseFragment
import com.mbaskan.patientapp.PatientFragment.PatientAlzheimerExerciseFragmentDirections
import com.mbaskan.patientapp.PatientFragment.Question
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.QuestionItemBinding
import java.lang.ref.WeakReference

class QuizAdapter(
    private val context: Context,
    private val questions: List<Question>,
    private val fragmentReference: WeakReference<PatientAlzheimerExerciseFragment>
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    inner class QuizViewHolder(val binding: QuestionItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = QuestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val question = questions[position]

        if (question.imageResId != null) {
            holder.binding.questionImage.setImageResource(question.imageResId)
            holder.binding.questionText.text = question.text
            holder.binding.questionImage.visibility = View.VISIBLE
            holder.binding.questionText.visibility = View.VISIBLE
        } else {
            holder.binding.questionText.text = question.text
            holder.binding.questionText.visibility = View.VISIBLE
            holder.binding.questionImage.visibility = View.GONE
        }

        holder.binding.optionsGroup.removeAllViews()
        for ((index, option) in question.options.withIndex()) {
            val radioButton = RadioButton(context)
            radioButton.id = index
            radioButton.text = option
            holder.binding.optionsGroup.addView(radioButton)
        }

        holder.binding.answerButton.setOnClickListener {
            val fragment = fragmentReference.get()
            val selectedOption = holder.binding.optionsGroup.checkedRadioButtonId
            if (selectedOption == question.correctAnswer) {
                fragment?.setImageViewVisible(true)
            } else {
                fragment?.setImageViewVisible(false)
            }
        }
    }

    override fun getItemCount(): Int {
        return questions.size
    }
}
