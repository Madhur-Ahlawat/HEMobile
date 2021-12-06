package com.heandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.model.*
import com.heandroid.network.ApiHelper
import com.heandroid.repo.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class RecoveryUsernamePasswordViewModel(private val apiHelper: ApiHelper) : ViewModel() {

    val confirmationOptionVal = MutableLiveData<Resource<Response<ConfirmationOptionsResponseModel>>>()
    val getSecurityCodeVal = MutableLiveData<Resource<Response<GetSecurityCodeResponseModel>>>()
    val verifySecurityCodeVal = MutableLiveData<Resource<Response<VerifySecurityCodeResponseModel>>>()
    val setNewPasswordVal = MutableLiveData<Resource<Response<VerifySecurityCodeResponseModel>>>()

    fun getConfirmationOptionsApi(
         agencyId :String , requestParam: ConfirmationOptionRequestModel
    ) {

        viewModelScope.launch {
            confirmationOptionVal.postValue(Resource.loading(null))
            try { val respFromApi = apiHelper.getConfirmationOptionsApiCall(agencyId , requestParam)
                confirmationOptionVal.postValue(confirmationOptionApiResponse(respFromApi))
            } catch (e: Exception) {
                confirmationOptionVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun confirmationOptionApiResponse(respFromApi: Response<ConfirmationOptionsResponseModel>): Resource<Response<ConfirmationOptionsResponseModel>>? {
        return if(respFromApi.isSuccessful) {
            Resource.success(respFromApi)
        } else {
            var errorCode = respFromApi.code()
            if(errorCode==401) {
                Resource.error(null, "Invalid token")
            } else {
                Resource.error(null, "Unknown error")
            }

        }
    }
    fun getSecurityCodeApi(
        agencyId :String , requestParam: GetSecurityCodeRequestModel
    ) {

        viewModelScope.launch {
            getSecurityCodeVal.postValue(Resource.loading(null))
            try { val respFromApi = apiHelper.getSecurityCodeApiCall(agencyId , requestParam)
                getSecurityCodeVal.postValue(getSecurityCodeApiResponse(respFromApi))
            } catch (e: Exception) {
                getSecurityCodeVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun getSecurityCodeApiResponse(respFromApi: Response<GetSecurityCodeResponseModel>): Resource<Response<GetSecurityCodeResponseModel>>? {
        return if(respFromApi.isSuccessful) {
            Resource.success(respFromApi)
        } else {
            var errorCode = respFromApi.code()
            if(errorCode==401) {
                Resource.error(null, "Invalid token")
            } else {
                Resource.error(null, "Unknown error")
            }

        }
    }
    fun verifySecurityCodeApi(requestParam: VerifySecurityCodeRequestModel) {

        viewModelScope.launch {
            verifySecurityCodeVal.postValue(Resource.loading(null))
            try { val respFromApi = apiHelper.verifySecurityCodeApiCall(requestParam)
                verifySecurityCodeVal.postValue(verifySecurityCodeApiResponse(respFromApi))
            } catch (e: Exception) {
                verifySecurityCodeVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun verifySecurityCodeApiResponse(respFromApi: Response<VerifySecurityCodeResponseModel>): Resource<Response<VerifySecurityCodeResponseModel>>? {
        return if(respFromApi.isSuccessful) {
            Resource.success(respFromApi)
        } else {
            var errorCode = respFromApi.code()
            if(errorCode==401) {
                Resource.error(null, "Invalid token")
            } else {
                Resource.error(null, "Unknown error")
            }

        }
    }

    fun setNewPasswordApi(requestParam: SetNewPasswordRequest) {

        viewModelScope.launch {
            setNewPasswordVal.postValue(Resource.loading(null))
            try { val respFromApi = apiHelper.setNewPasswordApiCall(requestParam)
                setNewPasswordVal.postValue(setNewPasswordApiResponse(respFromApi))
            } catch (e: Exception) {
                setNewPasswordVal.postValue(Resource.error(null , e.toString()))
            }
        }
    }

    private fun setNewPasswordApiResponse(respFromApi: Response<VerifySecurityCodeResponseModel>): Resource<Response<VerifySecurityCodeResponseModel>>? {
        return if(respFromApi.isSuccessful) {
            Resource.success(respFromApi)
        } else {
            var errorCode = respFromApi.code()
            if(errorCode==401) {
                Resource.error(null, "Invalid token")
            } else {
                Resource.error(null, "Unknown error")
            }

        }
    }
}