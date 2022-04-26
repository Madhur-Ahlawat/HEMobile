package com.heandroid.ui.account.communication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.databinding.FragmentSelectCommunicationPreferenceBinding
import com.heandroid.listener.UpdatePhoneNumberClickListener
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SetPreferenceFragment : BaseFragment<FragmentSelectCommunicationPreferenceBinding>(), UpdatePhoneNumberClickListener {

    private var showUpdatePhoneView: Boolean = true
    private var communicationPref: String = ""
    private var adviseOnText: String = ""
    private var phoneNumber: String = ""
    private var emailAdvise: String = ""

    private var loader: LoaderDialog?=null

    private fun setIndicatorListener() {

        binding.imvOne.setOnClickListener {
            setFirstTab()
        }
        binding.imvTwo.setOnClickListener {
            setSecondTab()
        }
        binding.imvThree.setOnClickListener {
            setThirdTab()
        }
    }

    private fun setButtonClickListener() {

        binding.btnNext.setOnClickListener {
            setSecondTab()
        }

        binding.btnNextTwo.setOnClickListener {
            setThirdTab()
        }

        binding.btnPrevTwo.setOnClickListener {
            setFirstTab()
        }

        binding.btnPrevious.setOnClickListener {
            setSecondTab()
        }

        binding.btnUpdateNow.setOnClickListener {
            showAlertDialog()
        }

        binding.btnChange.setOnClickListener {
            showAlertDialog()
        }

        binding.btnChangeTwo.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun setFirstTab() {
        // set top view
        binding.clSelectPref.visibility = VISIBLE
        binding.clLikeAdvice.visibility = GONE
        binding.clEmailAdvice.visibility = GONE

        // set button
        binding.btnNext.visibility = VISIBLE
        binding.llTwoBtnView.visibility = GONE
        binding.btnPrevious.visibility = GONE

        // set indicator
        binding.imvOne.setImageResource(R.drawable.selected_tab)
        binding.imvTwo.setImageResource(R.drawable.unselect_tab)
        binding.imvThree.setImageResource(R.drawable.unselect_tab)

    }

    private fun setSecondTab() {
        // set top view
        binding.clSelectPref.visibility = GONE
        binding.clLikeAdvice.visibility = VISIBLE
        binding.clEmailAdvice.visibility = GONE

        // set button
        binding.btnNext.visibility = GONE
        binding.llTwoBtnView.visibility = VISIBLE
        binding.btnPrevious.visibility = GONE

        // set indicator
        binding.imvOne.setImageResource(R.drawable.unselect_tab)
        binding.imvTwo.setImageResource(R.drawable.selected_tab)
        binding.imvThree.setImageResource(R.drawable.unselect_tab)

        setUpdatePhoneViewVisibility()
    }

    private fun setUpdatePhoneViewVisibility() {

        if (showUpdatePhoneView) {
            // initially show update button
            binding.updatePhoneView.visibility = VISIBLE
            binding.clPhoneNumber.visibility = GONE
        } else {

            binding.updatePhoneView.visibility = GONE
            binding.clPhoneNumber.visibility = VISIBLE
        }

    }


    private fun setThirdTab() {
        // set top view
        binding.clSelectPref.visibility = GONE
        binding.clLikeAdvice.visibility = GONE
        binding.clEmailAdvice.visibility = VISIBLE

        // set button
        binding.btnNext.visibility = GONE
        binding.llTwoBtnView.visibility = GONE
        binding.btnPrevious.visibility = VISIBLE

        // set indicator
        binding.imvOne.setImageResource(R.drawable.unselect_tab)
        binding.imvTwo.setImageResource(R.drawable.unselect_tab)
        binding.imvThree.setImageResource(R.drawable.selected_tab)

        setUpdatePhoneViewVisibility()
    }

    private fun showAlertDialog() {
        UpdatePhoneNumberDialog.newInstance(
            getString(R.string.str_update_mobile_number),
            this
        ).show(requireActivity().supportFragmentManager, UpdatePhoneNumberDialog.TAG)
    }

    override fun onSaveClickedListener(number: String, d: UpdatePhoneNumberDialog) {
        showUpdatePhoneView = false
        binding.tvPhone.text = number
        binding.tvPhoneTwo.text = number
        phoneNumber = number
        setUpdatePhoneViewVisibility()
        d.dismiss()
    }

    override fun onCancelClickedListener(d: UpdatePhoneNumberDialog) {
        showUpdatePhoneView = true
        d.dismiss()
    }

    override fun onCrossImageClickedListener(d: UpdatePhoneNumberDialog) {
        showUpdatePhoneView = true
        d.dismiss()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectCommunicationPreferenceBinding = FragmentSelectCommunicationPreferenceBinding.inflate(inflater,container,false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
//        loader?.show(requireActivity().supportFragmentManager, "")

    }

    override fun initCtrl() {
        setFirstTab()
        setButtonClickListener()
        setIndicatorListener()

        binding.rbEmail.isChecked = true
        communicationPref = Constants.EMAIL
        binding.rgCommunicationPref.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.rb_email -> {

                    communicationPref = Constants.EMAIL
                }

                R.id.rb_post -> {
                    communicationPref = Constants.EMAIL
                }
            }


        }


        adviseOnText = Constants.YES
        binding.rbYes.isChecked = true
        binding.rgAnswer.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.rb_yes -> {
                    adviseOnText = Constants.YES
                }
                R.id.rb_no -> {
                    adviseOnText = Constants.NO
                }
            }
        }


        emailAdvise = Constants.YES
        binding.rbEmailYes.isChecked  = true
        binding.rgEmailPref.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_email_yes -> {
                    adviseOnText = Constants.YES
                }
                R.id.rb_email_no -> {
                    adviseOnText = Constants.NO
                }
            }

        }


    }

    override fun observer() {
    }
}