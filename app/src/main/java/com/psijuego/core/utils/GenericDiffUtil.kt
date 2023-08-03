package com.psijuego.core.utils

import androidx.recyclerview.widget.DiffUtil
import com.psijuego.data.model.ui.CategoryUI
import com.psijuego.data.model.ui.ParameterUI

class GenericDiffUtil<T>(
    private val oldList: List<T>,
    private val newList: List<T>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return when {
            oldItem is ParameterUI && newItem is ParameterUI -> oldItem == newItem
            oldItem is CategoryUI && newItem is CategoryUI -> oldItem == newItem
            else -> true
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}