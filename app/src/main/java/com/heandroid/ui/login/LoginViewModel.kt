package com.heandroid.ui.login

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.response.LoginResponse
import com.heandroid.data.repository.LoginRepository
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.Resource
import com.heandroid.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(private val loginRepository: LoginRepository):BaseViewModel()  {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val loginUserValPrivate = MutableLiveData<Resource<LoginResponse>>()
    val loginUserVal : LiveData<Resource<LoginResponse>> get()  = loginUserValPrivate

    /** Error handling as UI **/

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate



    fun loginUser(
        clientID: String,
        grantType: String,
        agencyId: String,
        clientSecret: String,
        value: String,
        password: String,
        validatePasswordCompliance: String
    ) {

        viewModelScope.launch {
            // loginUserVal.postValue(Resource.loading(null))
            try {
                //todo reduce parameter
                val usersFromApi = loginRepository.loginApiCall(clientID,
                    grantType,
                    agencyId,
                    clientSecret,
                    value,
                    password,
                    validatePasswordCompliance)

                loginUserValPrivate.postValue(setLoginUserResponse(usersFromApi))
                //loginUserValPrivate.postValue((usersFromApi)
            } catch (e: Exception) {
                loginUserValPrivate.postValue(e.message?.let { Resource.DataError(it) })
            }
        }
    }

    private fun setLoginUserResponse(usersFromApi: Response<LoginResponse>): Resource<LoginResponse>? {
        if(usersFromApi.isSuccessful)
        {
            usersFromApi.body()?.let {
                return Resource.Success(usersFromApi.body()!!)
            }


        }
        else
        {
            var errorCode = usersFromApi.code()
            return Resource.DataError(errorManager.getError(errorCode).description)
            //return Resource.DataError(setErrorMsg(usersFromApi))

        }

        return null
    }

    fun showToastMessage(errorMsg: String?) {
        // val error = errorManager.getError(errorCode)
        // showToastPrivate.value = SingleEvent(error.description)
        showToastPrivate.value = SingleEvent(errorMsg.toString())

    }

    fun setErrorMsg(response: Response<LoginResponse>): String {
        val code = response.code().toString()
        val message = try {
            val jObjError = JSONObject(response.errorBody()?.string())
            jObjError.getJSONObject("error").getString("error_description")
        } catch (e: Exception) {
            e.message
        }
//       return if (message.isNullOrEmpty()) " error code = $code " else " error code = $code  & error message = $message "
        return if (message.isNullOrEmpty()) " error code = $code " else "$message "
    }


}