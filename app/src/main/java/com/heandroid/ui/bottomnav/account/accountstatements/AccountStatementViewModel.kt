package com.heandroid.ui.bottomnav.account.accountstatements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.heandroid.data.model.account.AccountStatementResponse
import com.heandroid.ui.base.BaseViewModel
import com.heandroid.utils.common.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountStatementViewModel @Inject constructor(private val repository: AccountStatementRepo): BaseViewModel()  {

    private val accountMutLiveData = MutableLiveData<Resource<AccountStatementResponse?>?>()
    val accountLiveData : LiveData<Resource<AccountStatementResponse?>?> get() = accountMutLiveData
}