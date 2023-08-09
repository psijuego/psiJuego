package com.psijuego.data.repositories

import com.psijuego.data.model.ui.CategoryUI
import com.psijuego.data.model.ui.ParameterUI
import com.psijuego.data.network.DatabaseFirestore
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val db: DatabaseFirestore
) {
    suspend fun getCategoriesList(): List<CategoryUI> {
        val categoriesList = db.getDataFromFirestore().map { document ->
            val category = document.id
            val parameterList = document.data?.entries?.mapNotNull {
                ParameterUI(it.key, it.value.toString(), false)
            } ?: emptyList()

            CategoryUI(removeNumericPrefix(category), parameterList)
        }

        return categoriesList
    }

    private fun removeNumericPrefix(categoryName: String): String {
        val underscoreIndex = categoryName.indexOf('_')
        return if (underscoreIndex != -1) {
            categoryName.substring(underscoreIndex + 1)
        } else {
            categoryName
        }
    }
}