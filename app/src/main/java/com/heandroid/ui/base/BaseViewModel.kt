package com.heandroid.ui.base

import androidx.lifecycle.ViewModel
import com.heandroid.data.error.errorUsecase.ErrorManager
import javax.inject.Inject

abstract  class BaseViewModel : ViewModel() {

     @Inject
     lateinit var errorManager: ErrorManager

}