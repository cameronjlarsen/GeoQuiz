package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

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
            quizViewModel.moveToNext()
            updateQuestion()
        }

        binding.prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        binding.restartButton.setOnClickListener {
            quizViewModel.resetQuiz()
            binding.restartButton.isVisible = false
            updateQuestion()
        }

        updateQuestion()
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
        toggleAnswerButtons(!quizViewModel.currentQuestionAnswered)
        binding.prevButton.isEnabled = !quizViewModel.isFirstQuestion()
        binding.nextButton.isEnabled = !quizViewModel.isLastQuestion()
    }

    private fun showQuizGrade() {
        val finalScore = quizViewModel.gradeQuiz()
        val messageResId = "You got $finalScore% correct!"
        Snackbar.make(findViewById(R.id.question_text_view), messageResId, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        toggleAnswerButtons(false)
        quizViewModel.currentQuestionAnswered = true
        binding.nextButton.isEnabled = !quizViewModel.isLastQuestion()
        binding.prevButton.isEnabled = !quizViewModel.isFirstQuestion()

        if (userAnswer == correctAnswer) {
            quizViewModel.updateScore()
        }
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Snackbar.make(findViewById(R.id.question_text_view), messageResId, Snackbar.LENGTH_SHORT)
            .show()

        if (quizViewModel.isLastQuestion()) {
            showQuizGrade()
        }
        binding.restartButton.isVisible = quizViewModel.isLastQuestion()
    }

    private fun toggleAnswerButtons(value: Boolean) {
        binding.trueButton.isEnabled = value
        binding.falseButton.isEnabled = value
    }
}
