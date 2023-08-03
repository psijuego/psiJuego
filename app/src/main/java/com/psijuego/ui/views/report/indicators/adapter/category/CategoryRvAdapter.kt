package com.psijuego.ui.views.report.indicators.adapter.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.psijuego.core.utils.GenericDiffUtil
import com.psijuego.data.model.ui.CategoryUI
import com.psijuego.databinding.ItemCategoryLayoutBinding
import com.psijuego.ui.views.report.indicators.CategoryListener

class CategoryRvAdapter(
    private var categoryUIList: List<CategoryUI>
) : RecyclerView.Adapter<CategoryRvViewHolder>() {

    private var listener: CategoryListener? = null

    fun updateList(newList: List<CategoryUI>){
        val indicatorUIDiff = GenericDiffUtil(categoryUIList, newList)
        val result = DiffUtil.calculateDiff(indicatorUIDiff)
        categoryUIList = newList
        result.dispatchUpdatesTo(this)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRvViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryLayoutBinding.inflate(layoutInflater, parent, false)
        return CategoryRvViewHolder(binding, itemCount)
    }

    override fun getItemCount(): Int = categoryUIList.size

    fun setListener(listener: CategoryListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: CategoryRvViewHolder, position: Int) {
        val item = categoryUIList[position]
        holder.render(item, position, listener)
    }
}
