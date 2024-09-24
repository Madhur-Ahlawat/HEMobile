package com.conduent.nationalhighways.data.model.landing

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.repository.websiteservice.WebsiteServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LandingViewModel  @Inject constructor(
    private val repository: WebsiteServiceRepository,
    val errorManager: ErrorManager
) : ViewModel() {
    var fromReminderPage: MutableLiveData<Boolean> = MutableLiveData()

}