package com.conduent.nationalhighways.ui.checkpaidcrossings

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.account.LoginWithPlateAndReferenceNumberResponseModel
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.data.model.checkpaidcrossings.*
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.repository.checkpaidcrossings.CheckPaidCrossingsRepo
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler.failure
import com.conduent.nationalhighways.utils.common.ResponseHandler.success
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckPaidCrossingViewModel @Inject constructor(
    private val repository: CheckPaidCrossingsRepo,
    val errorManager: ErrorManager
) : ViewModel() {

    @Inject
    lateinit var sessionManager: SessionManager

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val findVehicleMutData = MutableLiveData<Resource<VehicleInfoDetails?>?>()
    val findVehicleLiveData: LiveData<Resource<VehicleInfoDetails?>?> get() = findVehicleMutData


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _loginWithRefAndPlateNumber = MutableLiveData<Resource<LoginWithPlateAndReferenceNumberResponseModel?>?>()
    val loginWithRefAndPlateNumber: LiveData<Resource<LoginWithPlateAndReferenceNumberResponseModel?>?> get() = _loginWithRefAndPlateNumber


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _usedTollTransactions = MutableLiveData<Resource<List<UsedTollTransactionResponse?>?>?>()
    val usedTollTransactions: LiveData<Resource<List<UsedTollTransactionResponse?>?>?> get() = _usedTollTransactions


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _balanceTransfer = MutableLiveData<Resource<BalanceTransferResponse?>?>()
    val balanceTransfer: LiveData<Resource<BalanceTransferResponse?>?> get() = _balanceTransfer

    private val _paidCrossingOption = MutableLiveData<CheckPaidCrossingsOptionsModel?>()
    val paidCrossingOption: LiveData<CheckPaidCrossingsOptionsModel?> get() = _paidCrossingOption

    private val _paidCrossingResponse = MutableLiveData<CrossingDetailsModelsResponse?>()
    val paidCrossingResponse: LiveData<CrossingDetailsModelsResponse?> get() = _paidCrossingResponse


    fun setPaidCrossingOption(data : CheckPaidCrossingsOptionsModel?){
        _paidCrossingOption.value = data
    }
    fun setPaidCrossingResponse(data : CrossingDetailsModelsResponse?){
        _paidCrossingResponse.value = data
    }


    fun checkPaidCrossings(model: CheckPaidCrossingsRequest?) {
        viewModelScope.launch {
            try {
                val response = repository.loginWithRefAndPlateNumber(model)
                response?.let {
                    if (response.isSuccessful) {
                        val serverToken =
                            response.headers()["Authorization"]?.split("Bearer ")?.get(1)
                        Logg.logging("CheckpaidCrossi","serverToken $serverToken")

                        sessionManager.saveAuthToken(serverToken ?: "")
                        _loginWithRefAndPlateNumber.postValue(Resource.Success(response.body()))
                    } else {
                        _loginWithRefAndPlateNumber.postValue(
                            Resource.DataError(
                                errorManager.getError(
                                    response.code()
                                ).description
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _loginWithRefAndPlateNumber.postValue(failure(e))
            }
        }
    }


    fun usedTollTransactions(model: UsedTollTransactionsRequest?) {
        viewModelScope.launch {
            try {
                _usedTollTransactions.postValue(success(repository.getTollTransactions(model), errorManager))
            } catch (e: Exception) {
                _usedTollTransactions.postValue(failure(e))
            }
        }
    }
    fun balanceTransfer(model: BalanceTransferRequest?) {
        viewModelScope.launch {
            try {
                _balanceTransfer.postValue(success(repository.balanceTransfer(model), errorManager))
            } catch (e: Exception) {
                _balanceTransfer.postValue(failure(e))
            }
        }
    }

    fun getVehicleData(vehicleNumber: String?, agencyId: Int?) {
        viewModelScope.launch {
            try {
                findVehicleMutData.setValue(
                    success(
                        repository.getVehicleDetail(
                            vehicleNumber,
                            agencyId
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                findVehicleMutData.setValue(failure(e))
            }
        }
    }

}

