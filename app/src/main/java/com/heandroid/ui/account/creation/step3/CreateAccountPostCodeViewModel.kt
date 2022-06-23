package com.heandroid.ui.account.creation.step3

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.address.DataAddress
import com.heandroid.data.repository.account.AccountCreationRepository
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler.failure
import com.heandroid.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountPostCodeViewModel @Inject constructor(
    val repository: AccountCreationRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _addresses = MutableLiveData<Resource<List<DataAddress>?>?>()
    val addresses: LiveData<Resource<List<DataAddress>?>?> get() = _addresses

    fun fetchAddress(search: String) {
        viewModelScope.launch {
            try {
                _addresses.postValue(
                    success(
                        repository.getAddressListForPostalCode(search),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _addresses.postValue(failure(e))
            }
        }
    }


}