package com.heandroid.ui.nominatedcontacts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.model.nominatedcontacts.*
import com.heandroid.data.repository.dashboard.DashBoardRepo
import com.heandroid.data.repository.nominatedcontacts.NominatedContactsRepo
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NominatedContactsViewModel @Inject constructor(private val repo: NominatedContactsRepo) :
    BaseViewModel() {

    val createAccountLiveData = MutableLiveData<Resource<SecondaryAccountResp?>>()

    val nominatedContactListLiveData = MutableLiveData<Resource<NominatedContactRes?>>()

    val getSecondaryRightsLiveData = MutableLiveData<Resource<GetSecondaryAccessRightsResp?>>()
    val updateSecondaryAccountLiveData = MutableLiveData<Resource<ResponseBody?>>()
    val updateSecondaryAccessRightsLivedata = MutableLiveData<Resource<ResponseBody?>>()


    fun createAccount(secondaryBody: SecondaryAccountBody) {
        viewModelScope.launch {
            try {
                createAccountLiveData.postValue(
                    ResponseHandler.success(
                        repo.createSecondaryAccount(
                            secondaryBody
                        ), errorManager
                    )
                )
            } catch (e: Exception) {

                createAccountLiveData.postValue(ResponseHandler.failure(e))

            }

        }

    }


    fun nominatedContactListFetch() {
        viewModelScope.launch {

            try {
                nominatedContactListLiveData.postValue(
                    ResponseHandler.success(
                        repo.getSecondaryAccount(),
                        errorManager
                    )
                )
            } catch (e: Exception) {
                nominatedContactListLiveData.postValue(ResponseHandler.failure(e))
            }

        }

    }

    fun getSecondaryRightsData(accId: String) {

        viewModelScope.launch {
            try {
                getSecondaryRightsLiveData.postValue(
                    ResponseHandler.success(
                        repo.getSecondaryAccessRights(
                            accId
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                getSecondaryRightsLiveData.postValue(ResponseHandler.failure(e))
            }

        }

    }

    fun updateSecondaryAccountData(body: UpdateSecAccountDetails) {
        viewModelScope.launch {
            try {
                updateSecondaryAccountLiveData.postValue(
                    ResponseHandler.success(
                        repo.updateSecondaryAccount(
                            body
                        ), errorManager
                    )
                )
            } catch (e: Exception) {
                updateSecondaryAccountLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }


    fun updateSecondaryAccessRightsData(body: UpdateSecAccessRightsReq) {
        viewModelScope.launch {
            try {
                updateSecondaryAccessRightsLivedata.postValue(
                    ResponseHandler.success(
                        repo.updateSecondaryAccessRights(
                            body
                        ), errorManager
                    )
                )

            } catch (e: Exception) {
                updateSecondaryAccessRightsLivedata.postValue(ResponseHandler.failure(e))
            }
        }
    }

}