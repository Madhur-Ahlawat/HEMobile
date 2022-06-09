package com.heandroid.ui.payment

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.repository.nominatedcontacts.NominatedContactsRepo
import com.heandroid.utils.common.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class MakeOffPaymentViewModel @Inject constructor(
    private val repository: NominatedContactsRepo,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _createAccount = MutableLiveData<Resource<ResponseBody?>?>()
    val createAccount: LiveData<Resource<ResponseBody?>?> get() = _createAccount

}