package com.conduent.nationalhighways.ui.bottomnav.account.payments.method

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentPaymentMethodConfirmCardBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodConfirmCardFragment : BaseFragment<FragmentPaymentMethodConfirmCardBinding>(),
    View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private var loader: LoaderDialog? = null
    private val viewModel: PaymentMethodViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPaymentMethodConfirmCardBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.model = arguments?.getParcelable(Constants.DATA)
        binding.cbDefault.isChecked = binding.model?.default ?: false
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<LinearLayout>(R.id.tabLikeButtonsLayout).gone()
    }

    override fun initCtrl() {
        binding.apply {
            btnSave.setOnClickListener(this@PaymentMethodConfirmCardFragment)
        }
    }

    override fun observer() {
        observe(viewModel.saveNewCard, ::handleSaveNewCardResponse)
        observe(viewModel.accountDetail, ::handleAccountDetailResponse)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.accountDetail()
            }
        }
    }


    private fun handleSaveNewCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode?.equals("0") == true) {
                    findNavController().navigate(R.id.paymentMethodFragment)
                } else {
                    showError(binding.root, status.data?.message)
                }
            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun handleAccountDetailResponse(status: Resource<ProfileDetailModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                status.data?.run {
                    if (status.equals("500")) {
                        loader?.dismiss()
                        showError(binding.root, message)
                    } else {
                        val data = status.data.personalInformation
                        binding.model?.run {
                            city = data?.city
                            addressLine1 = data?.addressLine1
                            addressLine2 = data?.addressLine2
                            country = data?.country
                            state = data?.state
                            zipcode1 = data?.zipcode
                            zipcode2 = ""
                            expYear = "20$expYear"
                            default = null
                        }
                        viewModel.saveNewCard(binding.model)
                    }
                }
            }

            is Resource.DataError -> {
                loader?.dismiss()
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        binding.model?.default = isChecked
    }


}
