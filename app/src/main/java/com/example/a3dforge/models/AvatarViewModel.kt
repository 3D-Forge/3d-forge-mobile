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

class AvatarViewModel(private val okHttpConfig: OkHttpConfig, private val userLogin: String) : ViewModel() {

    private val _avatarResult = MutableLiveData<Pair<Boolean, ByteArray?>>()

    val avatarResult: LiveData<Pair<Boolean, ByteArray?>>
        get() = _avatarResult

    fun getAvatar() {
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val avatar = withContext(Dispatchers.IO) {
                apiManager.getAvatar(userLogin)
            }
            val isSuccessful = avatar != null
            _avatarResult.value = Pair(isSuccessful, avatar)
        }
    }
}