package com.example.counter.data.modelentity

import com.example.counter.R

enum class CounterStatus (
    val color: Int
){
    // po czasie(czerwony), blisko wykonania(orange), na spokojnie(zielony)
    Late(
        color = R.color.red
    ),
    CloseTo(
        color = R.color.orange
    ),
    Done(
        color = R.color.green
    )

}
