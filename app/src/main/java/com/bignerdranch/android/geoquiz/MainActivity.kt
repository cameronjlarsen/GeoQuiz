package com.bignerdranch.android.geoquiz

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val questionBank = listOf<Question>(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
    )

    private var answerButtonState = BooleanArray(questionBank.size) { true }

    private var currentIndex = 0

    private var currentScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trueButton.setOnClickListener {
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener {
            checkAnswer(false)
        }

        binding.nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestionText()
            updateAnswerButtonState()
        }

        binding.prevButton.setOnClickListener {
            currentIndex = (currentIndex - 1) % questionBank.size
            updateQuestionText()
            updateAnswerButtonState()
        }

        updateQuestionText()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestionText() {
        val questionTextResId = questionBank[currentIndex].textResId
        binding.questionTextView.setText(questionTextResId)
    }

    private fun gradeQuiz() {
        val finalScore = currentScore / questionBank.size * 100
        val messageResId = "Final Score: $finalScore%"
        Snackbar.make(findViewById(R.id.question_text_view), messageResId, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = questionBank[currentIndex].answer
        answerButtonState[currentIndex] = false

        if (userAnswer == correctAnswer) {
            currentScore += 1
        }
        val messageResId = if (userAnswer == correctAnswer) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        Snackbar.make(findViewById(R.id.question_text_view), messageResId, Snackbar.LENGTH_SHORT)
            .show()
        updateAnswerButtonState()

        if (currentIndex == questionBank.size - 1) {
            gradeQuiz()
        }
    }

    private fun updateAnswerButtonState() {
        binding.trueButton.isEnabled = answerButtonState[currentIndex]
        binding.falseButton.isEnabled = answerButtonState[currentIndex]
    }
}
