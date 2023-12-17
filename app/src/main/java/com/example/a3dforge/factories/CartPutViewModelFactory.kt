package com.example.a3dforge.factories

import OkHttpConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.models.CartPutViewModel
import com.example.a3dforge.models.CartViewModel
import com.example.a3dforge.models.CategoriesViewModel
import com.example.a3dforge.models.ProductPictureViewModel

class CartPutViewModelFactory(private val okHttpConfig: OkHttpConfig) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartPutViewModel::class.java)) {
            return CartPutViewModel(okHttpConfig) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
