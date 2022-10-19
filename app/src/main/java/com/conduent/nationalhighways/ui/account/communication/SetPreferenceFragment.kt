package com.conduent.nationalhighways.ui.account.communication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.communicationspref.*
import com.conduent.nationalhighways.databinding.FragmentSelectCommunicationPreferenceBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetPreferenceFragment : BaseFragment<FragmentSelectCommunicationPreferenceBinding>() {

    private var communicationPref: String = ""
    private var adviseOnText: String = ""
    private var emailAdvise: String = ""
    private var loader: LoaderDialog? = null
    private var receiptId: String = ""
    private var eMailFlag = ""
    private var mailFlag = ""
    private var smsFlag = ""
    private var mAccountResp: AccountResponse? = null
    private var mCat: String = ""
    private var mReceiptModel: CommunicationPrefsModel? = null
    private val communicationPrefsViewModel: CommunicationPrefsViewModel by viewModels()
    private val mCommunicationsList = ArrayList<CommunicationPrefsModel>()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSelectCommunicationPreferenceBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        communicationPrefsViewModel.getAccountSettingsPrefs()
        val mSearchModel = SearchProcessParamsModelReq(
            "SIGNUP", "ENU", "FEES", "MAIL"
        )
        communicationPrefsViewModel.searchProcessParameters(mSearchModel)
    }

    override fun initCtrl() {
        binding.rbEmail.isChecked = true
        communicationPref = Constants.EMAIL
        binding.rgCommunicationPref.setOnCheckedChangeListener { _, checkedId ->
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

        adviseOnText = Constants.YES
        binding.rbYes.isChecked = true
        binding.changeImg.setOnClickListener {
            binding.numberTextInput.visible()
            binding.changeImg.gone()
        }
        binding.rgAnswer.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_yes -> {
                    adviseOnText = Constants.YES
                    binding.youHaveOptedNumberMsg.visible()
                    binding.changeImg.visible()

                }
                R.id.rb_no -> {
                    adviseOnText = Constants.NO
                    binding.youHaveOptedNumberMsg.gone()
                    binding.changeImg.gone()
                    binding.numberTextInput.gone()

                }
            }
        }

        emailAdvise = Constants.YES
        binding.btnSave.setOnClickListener {
            if (communicationPref.isNotEmpty()) {
                val mList = ArrayList<CommunicationPrefsRequestModelList?>()
                eMailFlag = if (communicationPref == Constants.EMAIL) {
                    "Y"
                } else {
                    "N"
                }
                mailFlag = if (communicationPref == Constants.POST_MAIL) {
                    "Y"
                } else {
                    "N"
                }

                smsFlag = if (adviseOnText == Constants.YES) {
                    "Y"
                } else {
                    "N"
                }

                if (mCommunicationsList.size > 0 && mCommunicationsList.isNotEmpty()) {

                    mCommunicationsList.forEach {
                        val mListModel = CommunicationPrefsRequestModelList(
                            it.id,
                            it.category,
                            it.oneMandatory,
                            it.defEmail,
                            eMailFlag,
                            mailFlag,
                            it.defSms,
                            smsFlag,
                            it.defVoice,
                            it.voiceFlag,
                            it.pushNotFlag
                        )
                        mList.add(mListModel)

                    }

                }

                val model = CommunicationPrefsRequestModel(mList)
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                communicationPrefsViewModel.updateCommunicationPrefs(model)
                val mAccountModelReq = UpdateProfileRequest(
                    addressLine1 = mAccountResp?.personalInformation?.addressLine1,
                    addressLine2 = mAccountResp?.personalInformation?.addressLine2,
                    city = mAccountResp?.personalInformation?.city,
                    state = "HE",
                    zipCode = mAccountResp?.personalInformation?.zipCode,
                    zipCodePlus = mAccountResp?.personalInformation?.zipCodePlus,
                    country = mAccountResp?.personalInformation?.countryType,
                    emailAddress = mAccountResp?.personalInformation?.emailAddress,
                    primaryEmailStatus = mAccountResp?.personalInformation?.primaryEmailStatus,
                    primaryEmailUniqueID = mAccountResp?.personalInformation?.pemailUniqueCode,
                    phoneCell = mAccountResp?.personalInformation?.cellPhone,
                    phoneDay = mAccountResp?.personalInformation?.phoneDay,
                    phoneFax = mAccountResp?.personalInformation?.fax,
                    smsOption = smsFlag,
                    phoneEvening = mAccountResp?.personalInformation?.eveningPhone,
                    correspDeliveryMode = communicationPref,
                    correspDeliveryFrequency = "MONTHLY"
                )
                communicationPrefsViewModel.updateAccountSettingsPrefs(mAccountModelReq)
            } else {

            }
        }

        binding.btnCancel.setOnClickListener {
            requireActivity().finish()
        }

    }

    override fun observer() {
        observe(communicationPrefsViewModel.getAccountSettingsPrefs, ::getCommunicationSettingsPref)
        observe(
            communicationPrefsViewModel.updateCommunicationPrefs,
            ::updateCommunicationSettingsPrefs
        )
        observe(communicationPrefsViewModel.searchProcessParameters, ::searchProcessParameters)
        observe(communicationPrefsViewModel.updateAccountSettingPrefs, ::updateAccountSettingPrefs)
    }

    private fun updateAccountSettingPrefs(resource: Resource<EmptyApiResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.let {
                    requireActivity().finish()
                }
            }

            is Resource.DataError -> {

            }
            else -> {
            }
        }

    }

    private fun searchProcessParameters(resource: Resource<SearchProcessParamsModelResp?>?) {
        when (resource) {

            is Resource.Success -> {
                resource.let {
                    binding.agreeCheckBox.text = getString(R.string.str_i_agree_txt, it.data?.value)
                }

            }

            is Resource.DataError -> {
                if (loader?.isVisible == true) {
                    loader?.dismiss()
                }

            }
            else -> {
            }
        }


    }

    private fun getCommunicationSettingsPref(resource: Resource<AccountResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.let { res ->
                    res.data?.let {
                        mAccountResp = it
                    }
                    if (res.data?.personalInformation?.countryType.equals(
                            Constants.COUNTRY_TYPE_UK,
                            true
                        )
                    ) {
                        binding.clLikeAdvice.visibility = View.VISIBLE
                    }
                    val mPhoneNumber = res.data?.personalInformation?.phoneNumber?.replace(
                        "\\d(?=\\d{4})",
                        "*"
                    )
                    binding.youHaveOptedNumberMsg.text =
                        "${getString(R.string.str_you_have_opted_to_receive_text_msgs)} $mPhoneNumber"

                    mCommunicationsList.clear()

                    res.data?.accountInformation?.communicationPreferences?.forEach {
                        mCommunicationsList.add(it!!)
                        if (it?.category.equals(Constants.CATEGORY_RECEIPTS, true)) {
                            receiptId = it?.id.toString()
                            mCat = it?.category.toString()
                            mReceiptModel = it
                            if (it?.emailFlag.equals("Y", true)) {
                                binding.rbEmail.isChecked = true
                                binding.rbPost.gone()
                            }
                            if (it?.smsFlag.equals("Y", true)) {
                                binding.rbYes.isChecked = true
                            }
                        }

                    }
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun updateCommunicationSettingsPrefs(resource: Resource<CommunicationPrefsResp?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.let { res ->
                    if (res.data?.statusCode == "0") {
                        ErrorUtil.showError(binding.root, "Updated Successfully")
                    }
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
    }

}