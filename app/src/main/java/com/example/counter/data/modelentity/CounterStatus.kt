package com.example.counter.data.modelentity

import com.example.counter.R

enum class CounterStatus (
    val color: Int
){
    Late(
        color = R.color.red
    ),
    CloseTo(
        color = R.color.orange
    ),
    Enough(
        color = R.color.green
    )

}
