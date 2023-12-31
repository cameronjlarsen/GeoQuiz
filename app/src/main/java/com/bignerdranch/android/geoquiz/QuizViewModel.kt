package com.bignerdranch.android.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val CURRENT_SCORE_KEY = "CURRENT_SCORE_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val questionBank = listOf<Question>(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, false),
        Question(R.string.question_mideast, true),
        Question(R.string.question_africa, true),
        Question(R.string.question_americas, false),
        Question(R.string.question_asia, true),
    )

    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    private var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    var currentQuestionAnswered: Boolean
        get() = questionBank[currentIndex].answered
        set(value) {
            questionBank[currentIndex].answered = value
        }

    private var currentScore: Int
        get() = savedStateHandle.get(CURRENT_SCORE_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_SCORE_KEY, value)

    fun moveToNext() {
        if (!isLastQuestion()) {
            currentIndex = (currentIndex + 1) % questionBank.size
        }
    }

    fun moveToPrev() {
        if (!isFirstQuestion()) {
            currentIndex = (currentIndex - 1) % questionBank.size
        }
    }

    fun updateScore() {
        currentScore += 1
    }

    fun gradeQuiz(): Int {
        return currentScore / questionBank.size * 100
    }

    fun isLastQuestion(): Boolean {
        return currentIndex == questionBank.size - 1
    }

    fun isFirstQuestion(): Boolean {
        return currentIndex == 0
    }

    fun resetQuiz() {
        currentScore = 0
        currentIndex = 0
        isCheater = false
        for (i in questionBank.indices) {
            questionBank[i].answered = false
        }

    }

}
