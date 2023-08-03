package com.psijuego.ui.views.report.indicators.adapter.parameter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.psijuego.data.model.ui.ParameterUI
import com.psijuego.databinding.ItemParametersBinding
import com.psijuego.ui.views.report.indicators.CategoryListener

class ParameterRvAdapter(
    private val indicatorUI: List<ParameterUI>,
    private val indicatorUIPosition: Int
) : RecyclerView.Adapter<ParameterRvViewHolder>() {

    private var listener: CategoryListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParameterRvViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemParametersBinding.inflate(layoutInflater, parent, false)
        return ParameterRvViewHolder(binding)
    }

    override fun getItemCount(): Int = indicatorUI.size
    fun setListener(listener: CategoryListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ParameterRvViewHolder, position: Int) {
        val item = indicatorUI[position]
        holder.render(item, listener, indicatorUIPosition, position)
    }
}