package com.psijuego.data.model.ui

data class CategoryUI(
    var name: String,
    val parameter: List<ParameterUI>
)

data class ParameterUI(
    val name: String,
    val description: String,
    var selected: Boolean
)
