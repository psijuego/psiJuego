package com.psijuego.ui.views.report.indicators.adapter.category

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.psijuego.core.Constants
import com.psijuego.core.utils.UtilText
import com.psijuego.data.model.ui.CategoryUI
import com.psijuego.databinding.ItemCategoryLayoutBinding
import com.psijuego.ui.views.report.indicators.CategoryListener
import com.psijuego.ui.views.report.indicators.adapter.parameter.ParameterRvAdapter

class CategoryRvViewHolder(
    private val binding: ItemCategoryLayoutBinding,
    private val size: Int
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var parameterRvAdapter: ParameterRvAdapter

    fun render(
        item: CategoryUI,
        itemPosition: Int,
        listener: CategoryListener?
    ) {
        with(binding) {
            tvTitle.text =item.name
            item.parameter.let {
                for (parameter in it) {
                    if (parameter.name == Constants.DESCRIPTION) {
                        if (parameter.description != "") {
                            tvDescription.visibility = View.VISIBLE
                            tvDescription.text = parameter.description
                        } else {
                            tvDescription.visibility = View.GONE
                        }

                    }
                }
            }

            //headerLayout.showBack(itemPosition == 0)
            btnNext.visibility = if (size - 1 == itemPosition) View.VISIBLE else View.GONE
            btnNext.setOnClickListener {
                listener?.onNextClicked()
            }
            rvParameters.layoutManager =
                LinearLayoutManager(rvParameters.context, LinearLayoutManager.VERTICAL, false)
            val filteredItem =
                item.parameter.filter { parameterUI -> parameterUI.name != Constants.DESCRIPTION }
            parameterRvAdapter = ParameterRvAdapter(filteredItem, itemPosition)
            if (listener != null) {
                parameterRvAdapter.setListener(listener)
            }
            rvParameters.adapter = parameterRvAdapter

        }
    }


}