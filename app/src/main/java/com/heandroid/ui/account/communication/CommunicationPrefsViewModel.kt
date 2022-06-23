package com.heandroid.ui.account.communication

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.account.AccountResponse
import com.heandroid.data.model.communicationspref.CommunicationPrefsRequestModel
import com.heandroid.data.model.communicationspref.CommunicationPrefsResp
import com.heandroid.data.repository.communicationprefs.CommunicationPrefsRepo
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunicationPrefsViewModel @Inject constructor(
    private val repository: CommunicationPrefsRepo, val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getAccountSettingsPrefs = MutableLiveData<Resource<AccountResponse?>?>()
    val getAccountSettingsPrefs: LiveData<Resource<AccountResponse?>?> get() = _getAccountSettingsPrefs


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateCommunicationPrefs = MutableLiveData<Resource<CommunicationPrefsResp?>?>()
    val updateCommunicationPrefs: LiveData<Resource<CommunicationPrefsResp?>?> get() = _updateCommunicationPrefs


    fun getAccountSettingsPrefs() {
        viewModelScope.launch {
            try {

                _getAccountSettingsPrefs.postValue(
                    ResponseHandler.success(
                        repository.getAccountSettingsPrefs(),
                        errorManager
                    )
                )

            } catch (e: Exception) {
                _getAccountSettingsPrefs.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun updateCommunicationPrefs(model: CommunicationPrefsRequestModel) {

        viewModelScope.launch {
            try {
                _updateCommunicationPrefs.postValue(
                    ResponseHandler.success(
                        repository.updateCommunicationSettingsPrefs(
                            model
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _updateCommunicationPrefs.postValue(ResponseHandler.failure(e))
            }
        }
    }

}
