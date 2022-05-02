package com.heandroid.ui.account.communication

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.account.AccountResponse
import com.heandroid.data.model.account.ThresholdAmountApiResponse
import com.heandroid.data.model.auth.forgot.email.ForgotUsernameApiResponse
import com.heandroid.data.model.communicationspref.CommunicationPrefsRequestModel
import com.heandroid.data.model.communicationspref.CommunicationPrefsResp
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.notification.AlertMessageApiResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.data.repository.communicationprefs.CommunicationPrefsRepo
import com.heandroid.data.repository.dashboard.DashBoardRepo
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CommunicationPrefsViewModel @Inject constructor(private val repository: CommunicationPrefsRepo) : BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getAccountSettingsPrefs = MutableLiveData<Resource<AccountResponse?>?>()
    val getAccountSettingsPrefs: LiveData<Resource<AccountResponse?>?> get() = _getAccountSettingsPrefs


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _updateCommunicationPrefs = MutableLiveData<Resource<CommunicationPrefsResp?>?>()
    val updateCommunicationPrefs : LiveData<Resource<CommunicationPrefsResp?>?> get() = _updateCommunicationPrefs


    fun getAccountSettingsPrefs() {
        viewModelScope.launch {
            try {

                _getAccountSettingsPrefs.postValue(ResponseHandler.success(repository.getAccountSettingsPrefs(),errorManager))

            } catch (e: Exception) {
                _getAccountSettingsPrefs.postValue(ResponseHandler.failure(e))
            }
        }
    }

    fun updateCommunicationPrefs(model : CommunicationPrefsRequestModel) {

        viewModelScope.launch {
            try {
                _updateCommunicationPrefs.postValue(ResponseHandler.success(repository.updateCommunicationSettingsPrefs(model),errorManager))
            } catch (e: Exception) {
                _updateCommunicationPrefs.postValue(ResponseHandler.failure(e))
            }
        }
    }

}
