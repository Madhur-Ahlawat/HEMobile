package com.heandroid.ui.nominatedcontacts.list

import android.util.Patterns
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.email.LoginModel
import com.heandroid.data.model.nominatedcontacts.*
import com.heandroid.data.repository.dashboard.DashBoardRepo
import com.heandroid.data.repository.nominatedcontacts.NominatedContactsRepo
import com.heandroid.ui.base.BaseApplication
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import com.heandroid.utils.common.ResponseHandler.failure
import com.heandroid.utils.common.ResponseHandler.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NominatedContactListViewModel @Inject constructor(private val repo: NominatedContactsRepo) : BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _contactList = MutableLiveData<Resource<NominatedContactRes?>?>()
    val contactList : LiveData<Resource<NominatedContactRes?>?> get()  = _contactList


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _getSecondaryRights = MutableLiveData<Resource<GetSecondaryAccessRightsResp?>?>()
    val getSecondaryRights : LiveData<Resource<GetSecondaryAccessRightsResp?>?> get()  = _getSecondaryRights



    fun nominatedContactList() {
        viewModelScope.launch {

            try {
                _contactList.postValue(success(repo.getSecondaryAccount(), errorManager))
            } catch (e: Exception) {
                _contactList.postValue(failure(e))
            }

        }

    }


    // For Expand
    fun getSecondaryRights(accId: String) {
        viewModelScope.launch {
            try {
                _getSecondaryRights.postValue(success(repo.getSecondaryAccessRights(accId), errorManager))
            } catch (e: Exception) {
                _getSecondaryRights.postValue(failure(e))
            }
            }
    }

}