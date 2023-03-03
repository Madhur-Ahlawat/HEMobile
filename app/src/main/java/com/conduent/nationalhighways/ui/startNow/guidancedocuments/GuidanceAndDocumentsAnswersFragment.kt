package com.conduent.nationalhighways.ui.startNow.guidancedocuments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.GuidanceDocumentsAnswersFragmentBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.extn.customToolbar
import com.conduent.nationalhighways.utils.extn.makeLinks
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
        requireActivity().customToolbar(getString(R.string.str_guidance_and_documents))

        binding.tvAnswers.makeLinks(Pair("alternative languages.", View.OnClickListener {

        }), Pair("Dart Charge Online", View.OnClickListener {

        }))

    }

    override fun initCtrl() {


    }

    override fun observer() {

        // nothing here to observe
    }

    override fun onResume() {
        super.onResume()
        requireActivity().toolbar(getString(R.string.str_guidance_and_documents))
    }
}