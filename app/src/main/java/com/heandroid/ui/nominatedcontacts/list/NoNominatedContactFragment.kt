package com.heandroid.ui.nominatedcontacts.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentNoNomiatedContactBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NoNominatedContactFragment : BaseFragment<FragmentNoNomiatedContactBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNoNomiatedContactBinding =
        FragmentNoNomiatedContactBinding.inflate(inflater, container, false)

    @Inject
    lateinit var sessionManager: SessionManager
    override fun init() {
        binding.btnNotimateContact.setOnClickListener {
            findNavController().navigate(
                R.id.action_ncNoListFragment_to_ncFullNameFragment
            )

            if (sessionManager.fetchAccountType()
                    .equals(
                        Constants.PERSONAL_ACCOUNT,
                        true
                    ) && sessionManager.fetchSubAccountType()
                    .equals(Constants.STANDARD, true)
            ) {
                binding.youCanNominateTxt.text = getString(R.string.str_u_can_nominate_upto_users,"2")
            }
            if (sessionManager.fetchAccountType()
                    .equals(
                        Constants.BUSINESS_ACCOUNT,
                        true)) {
                binding.youCanNominateTxt.text = getString(R.string.str_u_can_nominate_upto_users,"5")
            }

        }
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }
}