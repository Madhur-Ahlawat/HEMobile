package com.conduent.nationalhighways.ui.account.creation.step3

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.account.CountryCodes
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.data.model.payment.PaymentMethodResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryResponseModel
import com.conduent.nationalhighways.data.repository.account.AccountCreationRepository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import com.conduent.nationalhighways.utils.common.ResponseHandler.failure
import com.conduent.nationalhighways.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountPostCodeViewModel @Inject constructor(
    val repository: AccountCreationRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _addresses = MutableLiveData<Resource<List<DataAddress?>?>?>()
    val addresses: LiveData<Resource<List<DataAddress?>?>?> get() = _addresses


    val _addressesState = MutableStateFlow<Resource<List<DataAddress?>?>?>(null)
    val addressesState: StateFlow<Resource<List<DataAddress?>?>?> get() = _addressesState


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _countriesList = MutableLiveData<Resource<List<CountriesModel?>?>?>()
    val countriesList: LiveData<Resource<List<CountriesModel?>?>?> get() = _countriesList

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _countriesCodesList = MutableLiveData<Resource<List<CountryCodes?>?>?>()
    val countriesCodeList: LiveData<Resource<List<CountryCodes?>?>?> get() = _countriesCodesList


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
    fun fetchAddressState(search: String) {
        viewModelScope.launch {
            try {
                _addressesState.emit(
                    success(
                        repository.getAddressListForPostalCode(search),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _addressesState.emit(failure(e))
            }
        }
    }

    fun getCountries() {
        viewModelScope.launch {
            try {
                _countriesList.postValue(
                    success(
                        repository.getCountriesList(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _countriesList.postValue(failure(e))
            }
        }
    }

    fun getCountryCodesList() {
        viewModelScope.launch {
            try {
                _countriesCodesList.postValue(
                    success(
                        repository.getCountryCodesList(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                _countriesCodesList.postValue(failure(e))
            }
        }
    }



}