package com.heandroid.ui.checkpaidcrossings

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.checkpaidcrossings.*
import com.heandroid.data.repository.checkpaidcrossings.CheckPaidCrossingsRepo
import com.heandroid.utils.common.Logg
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import com.heandroid.utils.common.ResponseHandler.failure
import com.heandroid.utils.common.ResponseHandler.success
import com.heandroid.utils.common.SessionManager
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
    private val _loginWithRefAndPlateNumber = MutableLiveData<Resource<CheckPaidCrossingsResponse?>?>()
    val loginWithRefAndPlateNumber: LiveData<Resource<CheckPaidCrossingsResponse?>?> get() = _loginWithRefAndPlateNumber


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _usedTollTransactions = MutableLiveData<Resource<List<UsedTollTransactionResponse?>?>?>()
    val usedTollTransactions: LiveData<Resource<List<UsedTollTransactionResponse?>?>?> get() = _usedTollTransactions


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _balanceTransfer = MutableLiveData<Resource<BalanceTransferResponse?>?>()
    val balanceTransfer: LiveData<Resource<BalanceTransferResponse?>?> get() = _balanceTransfer


    fun checkPaidCrossings(model: CheckPaidCrossingsRequest?) {
        viewModelScope.launch {
            try {
                val response = repository.loginWithRefAndPlateNumber(model)
                response?.let {
                    if (response.isSuccessful) {
                        val serverToken =
                            response.headers().get("Authorization")?.split("Bearer ")?.get(1)
                        Logg.logging("CheckpaidCrossi","serverToken ${serverToken}")

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

    fun resetPassword(model: BalanceTransferRequest?) {
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
                    ResponseHandler.success(
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

