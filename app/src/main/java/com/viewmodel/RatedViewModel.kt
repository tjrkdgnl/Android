package com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RatedViewModel : ViewModel() {

    var reviewCount =MutableLiveData<Int>(0)
}