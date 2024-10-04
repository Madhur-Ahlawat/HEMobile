package com.conduent.nationalhighways.ui.account.biometric

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.data.model.profile.ReplenishmentInformation
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityBiometricBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.logout.LogoutListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BiometricFragment : BaseFragment<ActivityBiometricBinding>(), View.OnClickListener,
    LogoutListener {

    private var biometricToggleButtonState: String? = null
    private var twoFA: Boolean = false
    private var suspended: Boolean = false
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private val toggleDelay: Long = 200

    private var personalInformation: PersonalInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private var accountInformation: AccountInformation? = null
    private var isScreenLaunchedBefore: Boolean = false
    private var isAuthenticated: Boolean = false
    private var cardValidationRequired: Boolean = false
    private var navigateFrom:String=""

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var crossingCount: String = ""
    private var currentBalance: String = ""

    @Inject
    lateinit var api: ApiService
    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): ActivityBiometricBinding = ActivityBiometricBinding.inflate(inflater, container, false)


    override fun init() {

    }

    override fun initCtrl() {

    }

    override fun observer() {

    }

    override fun onClick(v: View?) {

    }

    override fun onLogout() {

    }

}