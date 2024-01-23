package com.conduent.nationalhighways.ui.account.creation.newAccountCreation.viewModel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.account.CountryCodes
import com.conduent.nationalhighways.data.model.account.CreateProfileDetailModelModel
import com.conduent.nationalhighways.data.model.account.payment.AccountCreationRequest
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.data.repository.account.AccountCreationRepository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler.failure
import com.conduent.nationalhighways.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    val repository: AccountCreationRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _account = MutableLiveData<Resource<CreateProfileDetailModelModel?>?>()
    val account: LiveData<Resource<CreateProfileDetailModelModel?>?> get() = _account


    fun createAccountNew(model: AccountCreationRequest?) {
        viewModelScope.launch {
            try {
                _account.postValue(
                    success(
                        repository.createAccountNew(model),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _account.postValue(failure(e))
            }
        }
    }


}