package com.heandroid.ui.checkpaidcrossings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsRequest
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
import com.heandroid.databinding.FragmentPaidCrossingCheckBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class CheckPaidCrossingsFragment : BaseFragment<FragmentPaidCrossingCheckBinding>(),
    View.OnClickListener {

    private val viewModel: CheckPaidCrossingViewModel by viewModels()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPaidCrossingCheckBinding.inflate(inflater, container, false)

    override fun init() {
        binding.model = CheckPaidCrossingsOptionsModel(ref = "", vrm = "", enable = false)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.paymentRefNo.addTextChangedListener { isEnable() }
        binding.vrmNo.addTextChangedListener { isEnable() }
        binding.continueBtn.setOnClickListener(this)
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.loginWithRefAndPlateNumber, ::loginWithRefHeader)
        }
    }

    private fun isEnable() {
        if (binding.paymentRefNo.length() > 0 && binding.vrmNo.length() > 0) binding.model =
            CheckPaidCrossingsOptionsModel(
                enable = true,
                ref = binding.paymentRefNo.text.toString(),
                vrm = binding.vrmNo.text.toString()
            )
        else binding.model = CheckPaidCrossingsOptionsModel(
            enable = false,
            ref = binding.paymentRefNo.text.toString(),
            vrm = binding.vrmNo.text.toString()
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.continue_btn -> {
                hideKeyboard()
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                val checkPaidCrossingReq =
                    CheckPaidCrossingsRequest(binding.model?.ref, binding.model?.vrm)
                viewModel.checkPaidCrossings(checkPaidCrossingReq)
            }
        }

    }

    private fun loginWithRefHeader(status: Resource<CheckPaidCrossingsResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                val bundle = Bundle().apply {
                    putParcelable(Constants.CHECK_PAID_CHARGE_DATA_KEY, status.data)
                    putParcelable(Constants.CHECK_PAID_REF_VRM_DATA_KEY, binding.model)
                }
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
    }


}