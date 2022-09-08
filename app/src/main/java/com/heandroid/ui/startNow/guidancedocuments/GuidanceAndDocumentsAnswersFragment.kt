package com.heandroid.ui.startNow.guidancedocuments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.FragmentAboutServiceBinding
import com.heandroid.databinding.GuidanceDocumentsAnswersFragmentBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.makeLinks
import com.heandroid.utils.extn.toolbar

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