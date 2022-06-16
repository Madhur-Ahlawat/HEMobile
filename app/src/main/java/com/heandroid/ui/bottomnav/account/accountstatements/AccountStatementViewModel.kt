package com.heandroid.ui.bottomnav.account.accountstatements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.model.account.AccountStatementResponse
import com.heandroid.data.model.account.StatementListModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountStatementViewModel @Inject constructor(
    private val repository: AccountStatementRepo,
    val errorManager: ErrorManager
) : ViewModel() {

    private val accountMutLiveData = MutableLiveData<Resource<List<StatementListModel?>?>?>()
    val accountLiveData: LiveData<Resource<List<StatementListModel?>?>?> get() = accountMutLiveData

    fun getAccountStatement() {
        viewModelScope.launch {
            try {
                accountMutLiveData.postValue(
                    ResponseHandler.success(repository.getAccountStatement(), errorManager)
                )
            } catch (e: Exception) {
                accountMutLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }


}