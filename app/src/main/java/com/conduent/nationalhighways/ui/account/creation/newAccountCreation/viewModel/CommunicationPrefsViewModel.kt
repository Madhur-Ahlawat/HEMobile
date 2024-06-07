package com.conduent.nationalhighways.ui.account.creation.newAccountCreation.viewModel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsRequestModel
import com.conduent.nationalhighways.data.model.communicationspref.CommunicationPrefsResp
import com.conduent.nationalhighways.data.model.communicationspref.SearchProcessParamsModelReq
import com.conduent.nationalhighways.data.model.communicationspref.SearchProcessParamsModelResp
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.data.repository.communicationprefs.CommunicationPrefsRepo
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunicationPrefsViewModel @Inject constructor(
    private val repository: CommunicationPrefsRepo, val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getAccountSettingsPrefs = MutableLiveData<Resource<ProfileDetailModel?>?>()
    val getAccountSettingsPrefs: LiveData<Resource<ProfileDetailModel?>?> get() = _getAccountSettingsPrefs


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateCommunicationPrefs = MutableLiveData<Resource<CommunicationPrefsResp?>?>()
    val updateCommunicationPrefs: LiveData<Resource<CommunicationPrefsResp?>?> get() = _updateCommunicationPrefs

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateAccountSettingPrefs = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val updateAccountSettingPrefs: LiveData<Resource<EmptyApiResponse?>?> get() = _updateAccountSettingPrefs

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _searchProcessParameters =
        MutableLiveData<Resource<SearchProcessParamsModelResp?>?>()
    val searchProcessParameters: LiveData<Resource<SearchProcessParamsModelResp?>?> get() = _searchProcessParameters


    fun updateAccountSettingsPrefs(model: UpdateProfileRequest) {

        viewModelScope.launch {
            try {

                _updateAccountSettingPrefs.postValue(
                    ResponseHandler.success(
                        repository.updateAccountSettingPrefs(
                            model
                        ), errorManager
                    )
                )

            } catch (e: Exception) {
                _updateAccountSettingPrefs.postValue(ResponseHandler.failure(e))
            }
        }

    }

    fun searchProcessParameters(model: SearchProcessParamsModelReq) {
        viewModelScope.launch {
            try {
                _searchProcessParameters.postValue(
                    ResponseHandler.success(
                        repository.searchProcessParameters(
                            model
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                _searchProcessParameters.postValue(ResponseHandler.failure(e))
            }
        }
    }

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