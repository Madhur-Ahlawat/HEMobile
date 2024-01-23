package com.conduent.nationalhighways.ui.account.creation.step6

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.data.model.account.CreateProfileDetailModelModel
import com.conduent.nationalhighways.data.repository.auth.CreateAccountRespository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler.failure
import com.conduent.nationalhighways.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountPaymentViewModel @Inject constructor(
    private val repository: CreateAccountRespository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _createAccount = MutableLiveData<Resource<CreateProfileDetailModelModel?>?>()
    val createAccount: LiveData<Resource<CreateProfileDetailModelModel?>?> get() = _createAccount

    fun createAccount(model: CreateAccountRequestModel?) {
        viewModelScope.launch {
            try {
                _createAccount.postValue(success(repository.createAccount(model), errorManager))
            } catch (e: Exception) {
                _createAccount.postValue(failure(e))
            }
        }
    }

}