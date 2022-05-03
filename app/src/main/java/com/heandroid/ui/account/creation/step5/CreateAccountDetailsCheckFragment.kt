package com.heandroid.ui.account.creation.step5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.CreateAccountResponseModel
import com.heandroid.databinding.FragmentCreateAccountDetailsCheckPayBinding
import com.heandroid.databinding.FragmentCreateAccountSuccessfulBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class CreateAccountDetailsCheckFragment :
    BaseFragment<FragmentCreateAccountDetailsCheckPayBinding>(), View.OnClickListener {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateAccountDetailsCheckPayBinding =
        FragmentCreateAccountDetailsCheckPayBinding.inflate(inflater, container, false)


    private val viewModel: CreateAccountPaymentViewModel by viewModels()
    private var loader: LoaderDialog? = null

    private var model: CreateAccountRequestModel? = null

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.tvEmailAddress.text = model?.emailAddress
        binding.tvAccountType.text = model?.accountType
        binding.tvRegistrationNumber.text = model?.vehicleNo
        binding.amount.text = model?.thresholdAmount
        model = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)

    }

    override fun initCtrl() {
        binding.payButton.setOnClickListener(this)
        binding.cancelButton.setOnClickListener(this)

    }

    override fun observer() {
        observe(viewModel.createAccount, ::handleCreateAccountResponse)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pay_button -> {
                loader?.show(requireActivity().supportFragmentManager, "")
                viewModel.createAccount(model)

            }
            R.id.cancel_button -> {

            }
        }
    }

    private fun handleCreateAccountResponse(status: Resource<CreateAccountResponseModel?>?) {
        try {
            loader?.dismiss()
            when (status) {
                is Resource.Success -> {
                    status.data?.accountType = model?.accountType

                    val bundle = Bundle()
                    bundle.putParcelable("response", status.data)
                    findNavController().navigate(
                        R.id.action_cardFragment_to_successfulFragment,
                        bundle
                    )
                }

                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
                else -> {
                }
            }

        } catch (e: Exception) {
        }
    }

}
