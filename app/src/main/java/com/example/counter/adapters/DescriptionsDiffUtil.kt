package com.example.counter.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.counter.data.modelentity.Description

class DescriptionsDiffUtil(
    private val oldList: List<Description>,
    private val newList: List<Description>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].descriptionId == newList[newItemPosition].descriptionId
                && oldList[oldItemPosition].descriptionDate == newList[newItemPosition].descriptionDate
                && oldList[oldItemPosition].descriptionNote == newList[newItemPosition].descriptionNote
    }

}