package com.heandroid.ui.startNow.guidancedocuments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentsGuidanceAndDocumentsFaqBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.customToolbar

class GuidanceAndDocumentsQuestionsFragment : BaseFragment<FragmentsGuidanceAndDocumentsFaqBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentsGuidanceAndDocumentsFaqBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_guidance_and_documents))
    }

    override fun initCtrl() {
        binding.rlQuestion.setOnClickListener {
            findNavController().navigate(R.id.action_guidanceAndDocumentsQuestions_to_guidanceAndDocumentsAnswers)
        }
    }

    override fun observer() {}
}