package com.heandroid.ui.account.creation.step3

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.address.DataAddress
import com.heandroid.data.repository.account.AccountCreationRepository
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountPostCodeViewModel @Inject constructor(val repository: AccountCreationRepository): BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _addresses = MutableLiveData<Resource<List<DataAddress>?>?>()
    val addresses : LiveData<Resource<List<DataAddress>?>?> get()  = _addresses

    fun fetchAddress(search: String) {
        viewModelScope.launch {
            try {
                _addresses.postValue(ResponseHandler.success(repository.getAddressListForPostalCode(search), errorManager))
            } catch (e: Exception) {
                _addresses.postValue(ResponseHandler.failure(e))
            }
        }
    }

}