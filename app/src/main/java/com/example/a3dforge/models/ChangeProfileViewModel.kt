package com.example.a3dforge.models

import OkHttpConfig
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a3dforge.base.ApiManager
import com.example.a3dforge.entities.ProfileBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ChangeProfileViewModel(private val okHttpConfig: OkHttpConfig, private val querry: JSONObject,  private val userLogin: String) : ViewModel() {
    private val _profileChangeResult = MutableLiveData<Boolean>()

    val profileChangeResult: LiveData<Boolean>
        get() = _profileChangeResult

    fun changeProfile(){
        val apiManager = ApiManager(okHttpConfig)

        viewModelScope.launch {
            val changeProfile = withContext(Dispatchers.IO) {
                apiManager.changeProfile(querry, userLogin)
            }
            val isSuccessful = true
            _profileChangeResult.value = isSuccessful
        }
    }
}