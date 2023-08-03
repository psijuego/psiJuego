package com.psijuego.ui.views.report.indicators.adapter.parameter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.psijuego.R
import com.psijuego.core.Constants
import com.psijuego.data.model.ui.ParameterUI
import com.psijuego.databinding.ItemParametersBinding
import com.psijuego.ui.views.report.indicators.CategoryListener

class ParameterRvViewHolder(
    private val binding: ItemParametersBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var morePressed = false
    fun render(
        item: ParameterUI,
        listener: CategoryListener?,
        indicatorUIPosition: Int,
        parameterPosition: Int
    ) {
        with(binding) {
            if (item.name != Constants.DESCRIPTION) {

                indicatorName.text = item.name

                if(item.description == "") btnMore.visibility = View.GONE

                llMore.setOnClickListener {
                    onMorePressed(item)
                }

                btnState.isChecked = item.selected
                btnState.setOnClickListener {
                    val bool = btnState.isChecked
                    listener?.onItemStateChanged(
                        indicatorUIPosition,
                        item.name,
                        bool
                    )
                }

            }
        }
    }

    private fun onMorePressed(item: ParameterUI) {
        with(binding) {
            if (morePressed) {
                indicatorDescription.visibility = View.GONE
                btnMore.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.btnMore.context,
                        R.drawable.ic_arrow_right
                    )
                )
                morePressed = false
            } else {
                indicatorDescription.visibility = View.VISIBLE
                if (item.description.isNullOrEmpty()) {
                    indicatorDescription.visibility = View.GONE
                } else {
                    indicatorDescription.text = item.description
                }
                btnMore.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.btnMore.context,
                        R.drawable.ic_arrow_drop_down
                    )
                )
                morePressed = true
            }
        }

    }
}
