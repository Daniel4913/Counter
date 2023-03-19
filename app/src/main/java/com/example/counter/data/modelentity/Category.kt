package com.example.counter.data.modelentity

import com.example.counter.R

data class UserCategory(
    val name: String,
    val icon: Int,
    val underscoreColor: Int
)

enum class Category(
    val icon: Int,
    val underscoreColor: Int
) {
    Hygiene(R.drawable.ic_clean_hands, R.color.hygiene),
    Exercises(R.drawable.ic_exercises, R.color.exercises),
    Household(R.drawable.ic_house, R.color.household),
    Learning(R.drawable.ic_book, R.color.learning),
    Work(R.drawable.ic_work, R.color.work),
    Relations(R.drawable.ic_people, R.color.relations)
}