package com.psijuego.domain.usecase

import com.psijuego.data.repositories.ConclusionRepository
import java.io.File
import javax.inject.Inject

class ConclusionUseCase @Inject constructor(
    private val conclusionRepository: ConclusionRepository
) {
    fun uploadDocument(file: File, getUrl: (String) -> Unit){
        conclusionRepository.uploadDocument(file, getUrl)
    }
}