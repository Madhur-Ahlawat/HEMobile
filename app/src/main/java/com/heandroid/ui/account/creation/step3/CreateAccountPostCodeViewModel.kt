package com.heandroid.ui.account.creation.step3

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.account.CountriesModel
import com.heandroid.data.model.account.CountryCodes
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
    private val _addresses = MutableLiveData<Resource<List<DataAddress?>?>?>()
    val addresses: LiveData<Resource<List<DataAddress?>?>?> get() = _addresses

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