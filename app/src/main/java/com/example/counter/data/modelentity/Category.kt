package com.example.counter.data.modelentity

import com.example.counter.R

enum class Category(
    val icon: Int,
    val underscoreColor: Int,
) {
    Substances(
        icon = R.drawable.ic_edit, underscoreColor = R.color.green
    ),
    Household(
        icon = R.drawable.ic_done, underscoreColor = R.color.orange
    ),
    Exercise(
        icon = R.drawable.ic_edit, underscoreColor = R.color.green
    ),
    Health(
        icon = R.drawable.ic_edit, underscoreColor = R.color.green
    ),
    SocialRelations(
        icon = R.drawable.ic_edit, underscoreColor = R.color.green
    ),
    Hygiene(
        icon = R.drawable.ic_edit, underscoreColor = R.color.green
    ),
    Learning(
        icon = R.drawable.ic_edit, underscoreColor = R.color.green
    ),
    Work(
        icon = R.drawable.ic_edit, underscoreColor = R.color.green
    ),
    Food(
        icon = R.drawable.ic_edit, underscoreColor = R.color.green
    ),
    Entertainment(
        icon = R.drawable.ic_edit, underscoreColor = R.color.green
    ),
}
