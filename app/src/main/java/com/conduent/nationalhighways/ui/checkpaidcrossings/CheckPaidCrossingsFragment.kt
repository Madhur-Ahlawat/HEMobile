package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.conduent.nationalhighways.data.model.checkpaidcrossings.CheckPaidCrossingsRequest
import com.conduent.nationalhighways.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
import com.conduent.nationalhighways.databinding.FragmentPaidCrossingCheckBinding
import com.conduent.nationalhighways.databinding.FragmentPaidPreviousCrossingsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CheckPaidCrossingsFragment : BaseFragment<FragmentPaidPreviousCrossingsBinding>(),
    View.OnClickListener {

    private val viewModel: CheckPaidCrossingViewModel by activityViewModels()
    private var loader: LoaderDialog? = null
    private var isCalled = false
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPaidPreviousCrossingsBinding.inflate(inflater, container, false)

    override fun init() {

        AdobeAnalytics.setScreenTrack(
            "check crossings:login",
            "login",
            "english",
            "check crossings",
            "home",
            "check crossings:login",
            sessionManager.getLoggedInUser()
        )

//        binding.model = CheckPaidCrossingsOptionsModel(ref = "", vrm = "", enable = false)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        isEnable()
    }

    override fun initCtrl() {
//        binding.paymentRefNo.addTextChangedListener { isEnable() }
//        binding.vrmNo.addTextChangedListener { isEnable() }
//        binding.continueBtn.setOnClickListener(this)
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.loginWithRefAndPlateNumber, ::loginWithRefHeader)
        }
    }

    private fun isEnable() {
        /*if (binding.paymentRefNo.length() > 0 && binding.vrmNo.length() > 0) binding.model =
            CheckPaidCrossingsOptionsModel(
                enable = true,
                ref = binding.paymentRefNo.text.toString(),
                vrm = binding.vrmNo.text.toString()
            )
        else binding.model = CheckPaidCrossingsOptionsModel(
            enable = false,
            ref = binding.paymentRefNo.text.toString(),
            vrm = binding.vrmNo.text.toString()
        )*/
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.continue_btn -> {

                AdobeAnalytics.setActionTrack(
                    "continue",
                    "check crossings:login",
                    "login",
                    "english",
                    "check crossings",
                    "home",
                    sessionManager.getLoggedInUser()
                )

                hideKeyboard()
                isCalled = true
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
//                val checkPaidCrossingReq = CheckPaidCrossingsRequest(binding.model?.ref, binding.model?.vrm)
//                viewModel.checkPaidCrossings(checkPaidCrossingReq)
            }
        }

    }

    private fun loginWithRefHeader(status: Resource<CheckPaidCrossingsResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        if (isCalled) {
            when (status) {
                is Resource.Success -> {
                    val bundle = Bundle().apply {
//                        putParcelable(Constants.CHECK_PAID_CHARGE_DATA_KEY, status.data)
//                        putParcelable(Constants.CHECK_PAID_REF_VRM_DATA_KEY, binding.model)
                    }
//                    viewModel.setPaidCrossingOption(binding.model)
                    viewModel.setPaidCrossingResponse(status.data)
                    findNavController().navigate(
                        R.id.action_crossingCheck_to_checkChargesOption,
                        bundle
                    )
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
                else -> {
                }
            }
            isCalled = false
        }

    }


}