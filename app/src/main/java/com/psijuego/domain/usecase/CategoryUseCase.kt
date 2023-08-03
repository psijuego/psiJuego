package com.psijuego.domain.usecase

import com.psijuego.data.model.ui.CategoryUI
import com.psijuego.data.repositories.CategoryRepository
import javax.inject.Inject

class CategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend fun getCategoriesList(): List<CategoryUI> {
        return categoryRepository.getCategoriesList()
    }
}