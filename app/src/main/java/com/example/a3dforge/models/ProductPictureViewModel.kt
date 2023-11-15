package com.example.a3dforge.models

import OkHttpConfig
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a3dforge.base.ApiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductPictureViewModel(private val okHttpConfig: OkHttpConfig) : ViewModel() {

    private val _pictureResult = MutableLiveData<Pair<Int, ByteArray?>>()

    val pictureResult: LiveData<Pair<Int, ByteArray?>>
        get() = _pictureResult

    fun getProductPicture(pictureId: Int) {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val picture = withContext(Dispatchers.IO) {
                apiManager.getProductPicture(pictureId)
            }
            _pictureResult.value = Pair(pictureId, picture)
        }
    }
}