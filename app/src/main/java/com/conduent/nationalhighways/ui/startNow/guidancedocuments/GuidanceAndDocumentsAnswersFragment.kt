package com.conduent.nationalhighways.ui.startNow.guidancedocuments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.GuidanceDocumentsAnswersFragmentBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.startNow.contactdartcharge.ContactDartChargeActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.customToolbar
import com.conduent.nationalhighways.utils.extn.makeLinks
import com.conduent.nationalhighways.utils.extn.openActivityWithDataBack
import com.conduent.nationalhighways.utils.extn.toolbar

class GuidanceAndDocumentsAnswersFragment :
    BaseFragment<GuidanceDocumentsAnswersFragmentBinding>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GuidanceDocumentsAnswersFragmentBinding {

        return GuidanceDocumentsAnswersFragmentBinding.inflate(inflater, container, false)
    }

    override fun init() {
        // not need to set any toolbar
        requireActivity().customToolbar(getString(R.string.str_accessibility))

        binding.tvAnswers.makeLinks(Pair("Dart Charge Online", View.OnClickListener {
            requireActivity().openActivityWithDataBack(ContactDartChargeActivity::class.java) {
                putInt(
                    Constants.FROM_LOGIN_TO_CASES,
                    Constants.FROM_ANSWER_TO_CASE_VALUE
                )
            }
        }))

    }

    override fun initCtrl() {


    }

    override fun observer() {

        // nothing here to observe
    }

    override fun onResume() {
        super.onResume()
        //requireActivity().toolbar(getString(R.string.str_guidance_and_documents))
    }
}