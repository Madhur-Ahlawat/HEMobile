package com.heandroid.ui.account.communication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.databinding.FragmentSelectCommunicationPreferenceBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SetPreferenceFragment : BaseFragment<FragmentSelectCommunicationPreferenceBinding>() {

    private var communicationPref: String = ""
    private var adviseOnText: String = ""
    private var phoneNumber: String = ""
    private var emailAdvise: String = ""

    private var loader: LoaderDialog? = null


    private fun setButtonClickListener() {

        binding.btnSave.setOnClickListener {
        }

        binding.btnCancel.setOnClickListener {
        }

    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectCommunicationPreferenceBinding =
        FragmentSelectCommunicationPreferenceBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
//        loader?.show(requireActivity().supportFragmentManager, "")

    }

    override fun initCtrl() {
        setButtonClickListener()


        binding.rbEmail.isChecked = true
        communicationPref = Constants.EMAIL
        binding.rgCommunicationPref.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.rb_email -> {
                    binding.printedMsgTxt.gone()
                    communicationPref = Constants.EMAIL
                }

                R.id.rb_post -> {
                    communicationPref = Constants.EMAIL
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

    }

    override fun observer() {
    }
}