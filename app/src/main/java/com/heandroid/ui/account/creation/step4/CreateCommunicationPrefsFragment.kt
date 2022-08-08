package com.heandroid.ui.account.creation.step4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.account.AccountResponse
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.UpdateProfileRequest
import com.heandroid.data.model.communicationspref.*
import com.heandroid.databinding.FragmentSelectCommunicationPreferenceBinding
import com.heandroid.ui.account.communication.CommunicationPrefsViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.makeLinks
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateCommunicationPrefsFragment :
    BaseFragment<FragmentSelectCommunicationPreferenceBinding>() {

    private var communicationPref: String = ""
    private var adviseOnText: String = ""
    private var loader: LoaderDialog? = null
    private var mCat: String = ""
    private val communicationPrefsViewModel: CommunicationPrefsViewModel by viewModels()
    private var model: CreateAccountRequestModel? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSelectCommunicationPreferenceBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        model = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        binding.tvLabel.visible()
        binding.tvStep.visible()
        binding.youHaveOptedNumberMsg.gone()
        binding.changeImg.gone()
        binding.numberTextInput.gone()
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 4, 6)
        Logg.logging("AccountCreatePin", " step 4  model  $model")

        val mSearchModel = SearchProcessParamsModelReq(
            "SIGNUP", "ENU", "FEES", "MAIL"
        )
        communicationPrefsViewModel.searchProcessParameters(mSearchModel)

        binding.termsConditionsTxt.makeLinks(Pair("terms and conditions", View.OnClickListener {

        }))
        if (model?.countryType == Constants.COUNTRY_TYPE_UK && model?.cellPhoneCountryCode == "+44") {
            binding.clLikeAdvice.visible()
            binding.termsConditionsTxt.visible()

        } else {
            binding.clLikeAdvice.gone()
        }

    }

    override fun initCtrl() {
        binding.rbEmail.isChecked = true
        communicationPref = Constants.EMAIL


        binding.rgCommunicationPref.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_email -> {
                    binding.printedMsgTxt.gone()
                    communicationPref = Constants.EMAIL
                }

                R.id.rb_post -> {
                    communicationPref = Constants.POST_MAIL
                    binding.printedMsgTxt.visible()

                }
            }
        }

        adviseOnText = Constants.N

        binding.changeImg.setOnClickListener {
            binding.numberTextInput.visible()
            binding.changeImg.gone()
        }
        binding.btnSave.text = getString(R.string.str_continue)
        binding.btnCancel.gone()
        binding.btnSave.setOnClickListener {
            model?.correspDeliveryMode = communicationPref
            model?.correspDeliveryFrequency = "MONTHLY"
            model?.smsOption = adviseOnText
            val bundle = Bundle().apply {
                putParcelable(Constants.CREATE_ACCOUNT_DATA, model)
            }
            findNavController().navigate(
                R.id.action_createCommunicationPrefsFragment_to_findYourVehicleFragment,
                bundle
            )

        }
        binding.rgAnswer.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_yes -> {
                    adviseOnText = Constants.Y
//                    binding.youHaveOptedNumberMsg.visible()
//                    binding.changeImg.visible()

                }
                R.id.rb_no -> {
                    adviseOnText = Constants.N
//                    binding.youHaveOptedNumberMsg.gone()
//                    binding.changeImg.gone()
//                    binding.numberTextInput.gone()

                }
            }
        }


    }

    override fun observer() {
        observe(communicationPrefsViewModel.searchProcessParameters, ::searchProcessParameters)
    }


    private fun searchProcessParameters(resource: Resource<SearchProcessParamsModelResp?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {

            is Resource.Success -> {
                resource.let {
                    binding.agreeCheckBox.text = getString(R.string.str_i_agree_txt, it.data?.value)
                }

            }

            is Resource.DataError -> {

            }
            else -> {
            }
        }


    }

}