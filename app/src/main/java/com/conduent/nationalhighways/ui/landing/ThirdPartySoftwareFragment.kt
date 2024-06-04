package com.conduent.nationalhighways.ui.landing

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.databinding.FragmentThirdPartySoftwareBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.RaiseEnquiryActivity
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThirdPartySoftwareFragment : BaseFragment<FragmentThirdPartySoftwareBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentThirdPartySoftwareBinding {
       return FragmentThirdPartySoftwareBinding.inflate(inflater, container, false)

    }

    override fun init() {
     binding?.textTermsAndConditions?.setMovementMethod(LinkMovementMethod.getInstance())
        if(requireActivity() is RaiseEnquiryActivity){
            (requireActivity() as RaiseEnquiryActivity).focusToolBarRaiseEnquiry()
        }
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}