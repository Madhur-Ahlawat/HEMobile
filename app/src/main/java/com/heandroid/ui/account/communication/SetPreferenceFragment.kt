package com.heandroid.ui.account.communication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.account.AccountResponse
import com.heandroid.data.model.account.UpdateProfileRequest
import com.heandroid.data.model.communicationspref.*
import com.heandroid.databinding.FragmentSelectCommunicationPreferenceBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetPreferenceFragment : BaseFragment<FragmentSelectCommunicationPreferenceBinding>() {

    private var communicationPref: String = ""
    private var adviseOnText: String = ""
    private var emailAdvise: String = ""
    private var loader: LoaderDialog? = null
    private var receiptId: String = ""
    private var eMailFlag = ""
    private var smsFlag = ""
    private var mAccountResp: AccountResponse? = null
    private var mCat: String = ""
    private var mReceiptModel: CommunicationPrefsModel? = null
    private val communicationPrefsViewModel: CommunicationPrefsViewModel by viewModels()

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

        adviseOnText = Constants.YES
        binding.rbYes.isChecked = true
        binding.changeImg.setOnClickListener {
            binding.numberTextInput.visible()
            binding.changeImg.gone()
        }
        binding.rgAnswer.setOnCheckedChangeListener { group, checkedId ->
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

                smsFlag = if (adviseOnText == Constants.YES) {
                    "Y"
                } else {
                    "N"
                }

                val mListModel = CommunicationPrefsRequestModelList(
                    mReceiptModel?.id,
                    mReceiptModel?.category,
                    mReceiptModel?.oneMandatory,
                    mReceiptModel?.defEmail,
                    eMailFlag,
                    mReceiptModel?.mailFlag,
                    mReceiptModel?.defSms,
                    smsFlag,
                    mReceiptModel?.defVoice,
                    mReceiptModel?.voiceFlag,
                    mReceiptModel?.pushNotFlag
                )

                mList.add(mListModel)
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
                    country = mAccountResp?.personalInformation?.country,
                    emailAddress = mAccountResp?.personalInformation?.emailAddress,
                    primaryEmailStatus = mAccountResp?.personalInformation?.primaryEmailStatus,
                    primaryEmailUniqueID = mAccountResp?.personalInformation?.pemailUniqueCode,
                    phoneCell = mAccountResp?.personalInformation?.cellPhone,
                    phoneDay = mAccountResp?.personalInformation?.phoneDay,
                    phoneFax = mAccountResp?.personalInformation?.fax,
                    smsOption = smsFlag,
                    phoneEvening = mAccountResp?.personalInformation?.eveningPhone,
                    correspDeliveryMode = "EMAIL",
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

                }
            }

            is Resource.DataError -> {

            }
            else -> {
            }
        }

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

                    res.data?.accountInformation?.communicationPreferences?.forEach {
                        if (it?.category.equals(Constants.CATEGORY_RECEIPTS, true)) {
                            receiptId = it?.id.toString()
                            mCat = it?.category.toString()
                            mReceiptModel = it
                            if (it?.mailFlag.equals("Y", true)) {
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