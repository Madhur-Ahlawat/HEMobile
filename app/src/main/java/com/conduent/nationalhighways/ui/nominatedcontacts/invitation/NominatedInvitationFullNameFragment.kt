package com.conduent.nationalhighways.ui.nominatedcontacts.invitation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.nominatedcontacts.CreateAccountRequestModel
import com.conduent.nationalhighways.databinding.FragmentNominatedInvitationFullNameBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NominatedInvitationFullNameFragment :
    BaseFragment<FragmentNominatedInvitationFullNameBinding>(), View.OnClickListener {

    private val viewModel: NominatedInvitationViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNominatedInvitationFullNameBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.getBoolean("edit") == true) binding.model = arguments?.getParcelable("data")
        else binding.model = CreateAccountRequestModel("", "", "", "", "READ", "", "")

        if (arguments?.getBoolean("FromNormalEdit", false) == true)
            binding.model = arguments?.getParcelable("data")
    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnNext -> {
                val validation = viewModel.validationFullName(binding.model)
                if (validation.first) {
                    val bundle = Bundle()
                    bundle.putBoolean("edit", arguments?.getBoolean("edit") ?: false)
                    bundle.putParcelable("data", binding.model)
                    findNavController().navigate(
                        R.id.action_ncFullNameFragment_to_ncEmailFragment,
                        bundle
                    )
                } else {
                    showError(binding.root, validation.second)
                }
            }
        }
    }
}