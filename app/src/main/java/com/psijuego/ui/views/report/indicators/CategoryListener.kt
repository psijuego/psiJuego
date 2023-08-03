package com.psijuego.ui.views.report.indicators

interface CategoryListener {
    fun onItemStateChanged(indicatorUIPosition: Int, parameterName: String, newStatus: Boolean)
    fun onNextClicked()
}