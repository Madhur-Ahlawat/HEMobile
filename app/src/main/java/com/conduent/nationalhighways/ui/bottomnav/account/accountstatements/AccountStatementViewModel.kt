package com.conduent.nationalhighways.ui.bottomnav.account.accountstatements

import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.StatementListModel
import com.conduent.nationalhighways.data.model.account.ViewStatementsReqModel
import com.conduent.nationalhighways.data.repository.account.AccountStatementRepo
import com.conduent.nationalhighways.ui.startNow.contactdartcharge.ContactDartChargeViewModel
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
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

  private val viewStatementsMutLiveData = MutableLiveData<Resource<EmptyApiResponse?>?>()
    val viewStatementsLiveData: LiveData<Resource<EmptyApiResponse?>?> get() = viewStatementsMutLiveData

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
    fun viewStatements(request : ViewStatementsReqModel) {
        viewModelScope.launch {
            try {
                viewStatementsMutLiveData.postValue(
                    ResponseHandler.success(repository.viewStatements(request), errorManager)
                )
            } catch (e: Exception) {
                viewStatementsMutLiveData.postValue(ResponseHandler.failure(e))
            }
        }
    }


}