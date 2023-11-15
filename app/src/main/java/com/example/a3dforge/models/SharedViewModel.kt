package com.example.a3dforge.models

import androidx.lifecycle.ViewModel
import com.example.a3dforge.entities.FilterParametersBody

class SharedViewModel : ViewModel() {
    var filterParameters = FilterParametersBody()
}
