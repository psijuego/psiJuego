package com.psijuego.ui.views.report

import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psijuego.R
import com.psijuego.core.Constants
import com.psijuego.core.utils.CoreModule
import com.psijuego.core.utils.ResourceState
import com.psijuego.core.utils.UtilConnection
import com.psijuego.data.model.ui.CategoryUI
import com.psijuego.data.model.ui.HomeUI
import com.psijuego.domain.usecase.CategoryUseCase
import com.psijuego.domain.usecase.ConclusionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase,
    private val conclusionUseCase: ConclusionUseCase
) : ViewModel() {

    private val utilConnection = UtilConnection.getInstance()
    private val context = CoreModule.getContext()!!

    //LiveData

    private val _homeUI = MutableLiveData<HomeUI>()
    val homeUI: LiveData<HomeUI> = _homeUI

    private var _categoryUI = MutableLiveData<List<CategoryUI>>()
    val categoryUI: LiveData<List<CategoryUI>> = _categoryUI

    private var _conclusion = MutableLiveData<String>()
    val conclusion: LiveData<String> = _conclusion

    private var _pdfDocument = MutableLiveData<File>()
    val pdfDocument: LiveData<File> = _pdfDocument

    private var _pdfStorageUrl = MutableLiveData<String>()
    val pdfStorageUrl: LiveData<String> = _pdfStorageUrl

    private val _dataState: MutableLiveData<ResourceState<List<CategoryUI>>> = MutableLiveData()
    val dataState: LiveData<ResourceState<List<CategoryUI>>> = _dataState

    private val _uploadState: MutableLiveData<ResourceState<String>> = MutableLiveData()
    val uploadState: LiveData<ResourceState<String>> = _uploadState

    fun setConclusion(conclusion: String) {
        _conclusion.postValue(conclusion)
    }

    fun setHomeUI(data: HomeUI) {
        _homeUI.postValue(data)
    }

    fun setCategoryUI(data: List<CategoryUI>) {
        _categoryUI.value = data
    }

    fun getCategoriesList() {
        _dataState.value = ResourceState.Loading
        viewModelScope.launch {
            try {
                if (utilConnection.checkInternetConnection()) {
                    val list = categoryUseCase.getCategoriesList()
                    if (list.isNotEmpty()) {
                        _dataState.value = ResourceState.Success(list)
                    } else {
                        _dataState.value =
                            ResourceState.Failure(Constants.CATEGORIES_NOT_FOUND)
                    }
                } else {
                    _dataState.value =
                        ResourceState.Failure(Constants.NOT_CONNECTION)
                }

            } catch (e: Exception) {
                _dataState.value =
                    ResourceState.Failure(Constants.CATEGORIES_FAILED)
                e.printStackTrace()
            }
        }
    }

    fun uploadDocument(file: File) {
        conclusionUseCase.uploadDocument(file) {
            if (it.isNotBlank()) {
                _uploadState.value = ResourceState.Success(it)
                _pdfStorageUrl.postValue(it)
            } else {
                _uploadState.value =
                    ResourceState.Failure(Constants.UPLOAD_FILE_FAILED)
            }
        }
    }
}