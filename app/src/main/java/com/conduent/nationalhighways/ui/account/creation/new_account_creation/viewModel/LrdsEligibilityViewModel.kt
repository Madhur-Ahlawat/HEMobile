package com.conduent.nationalhighways.ui.account.creation.new_account_creation.viewModel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.request.LrdsEligibiltyRequest
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.response.LrdsEligibilityResponse
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.repo.LrdsEligibilityRepository
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LrdsEligibilityViewModel @Inject constructor(
    private val repository: LrdsEligibilityRepository,
    val errorManager: ErrorManager
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val _lrdsEligibilityCheck = MutableLiveData<Resource<LrdsEligibilityResponse?>?>()
    val lrdsEligibilityCheck: LiveData<Resource<LrdsEligibilityResponse?>?> get() = _lrdsEligibilityCheck

    fun getLrdsEligibilityResponse(request: LrdsEligibiltyRequest) {
        viewModelScope.launch {
            try {

                _lrdsEligibilityCheck.postValue(
                    ResponseHandler.success(
                        repository.lrdsEligibiltyCheck(request),
                        errorManager
                    )
                )

            } catch (e: Exception) {
                _lrdsEligibilityCheck.postValue(ResponseHandler.failure(e))
            }
        }
    }


}