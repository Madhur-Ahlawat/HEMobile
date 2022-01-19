package com.heandroid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentSelectCommunicationPreferenceBinding
import com.heandroid.dialog.UpdatePhoneNumberDialog
import com.heandroid.listener.UpdatePhoneNumberClickListener
import com.heandroid.utils.Constants


class SetPreferenceFragment : BaseFragment(), UpdatePhoneNumberClickListener {

    private var showUpdatePhoneView: Boolean = true
    private lateinit var dataBinding: FragmentSelectCommunicationPreferenceBinding
    private var communicationPref: String = ""
    private var adviseOnText: String = ""
    private var phoneNumber: String = ""
    private var emailAdvise: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_select_communication_preference,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFirstTab()
        setButtonClickListener()
        setIndicatorListener()


        dataBinding.rbEmail.isChecked = true
        communicationPref = Constants.EMAIL
        dataBinding.rgCommunicationPref.setOnCheckedChangeListener { group, checkedId ->

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
        dataBinding.rbYes.isChecked = true
        dataBinding.rgAnswer.setOnCheckedChangeListener { group, checkedId ->

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
        dataBinding.rbEmailYes.isChecked  = true
        dataBinding.rgEmailPref.setOnCheckedChangeListener { group, checkedId ->
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

    private fun setIndicatorListener() {

        dataBinding.imvOne.setOnClickListener {
            setFirstTab()
        }
        dataBinding.imvTwo.setOnClickListener {
            setSecondTab()
        }
        dataBinding.imvThree.setOnClickListener {
            setThirdTab()
        }
    }

    private fun setButtonClickListener() {

        dataBinding.btnNext.setOnClickListener {
            setSecondTab()
        }

        dataBinding.btnNextTwo.setOnClickListener {
            setThirdTab()
        }

        dataBinding.btnPrevTwo.setOnClickListener {
            setFirstTab()
        }

        dataBinding.btnPrevious.setOnClickListener {
            setSecondTab()
        }

        dataBinding.btnUpdateNow.setOnClickListener {
            showAlertDialog()
        }

        dataBinding.btnChange.setOnClickListener {
            showAlertDialog()
        }

        dataBinding.btnChangeTwo.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun setFirstTab() {
        // set top view
        dataBinding.clSelectPref.visibility = VISIBLE
        dataBinding.clLikeAdvice.visibility = GONE
        dataBinding.clEmailAdvice.visibility = GONE

        // set button
        dataBinding.btnNext.visibility = VISIBLE
        dataBinding.llTwoBtnView.visibility = GONE
        dataBinding.btnPrevious.visibility = GONE

        // set indicator
        dataBinding.imvOne.setImageResource(R.drawable.selected_tab)
        dataBinding.imvTwo.setImageResource(R.drawable.unselect_tab)
        dataBinding.imvThree.setImageResource(R.drawable.unselect_tab)

    }

    private fun setSecondTab() {
        // set top view
        dataBinding.clSelectPref.visibility = GONE
        dataBinding.clLikeAdvice.visibility = VISIBLE
        dataBinding.clEmailAdvice.visibility = GONE

        // set button
        dataBinding.btnNext.visibility = GONE
        dataBinding.llTwoBtnView.visibility = VISIBLE
        dataBinding.btnPrevious.visibility = GONE

        // set indicator
        dataBinding.imvOne.setImageResource(R.drawable.unselect_tab)
        dataBinding.imvTwo.setImageResource(R.drawable.selected_tab)
        dataBinding.imvThree.setImageResource(R.drawable.unselect_tab)

        setUpdatePhoneViewVisibility()
    }

    private fun setUpdatePhoneViewVisibility() {

        if (showUpdatePhoneView) {
            // initially show update button
            dataBinding.updatePhoneView.visibility = VISIBLE
            dataBinding.clPhoneNumber.visibility = GONE
        } else {

            dataBinding.updatePhoneView.visibility = GONE
            dataBinding.clPhoneNumber.visibility = VISIBLE
        }

    }


    private fun setThirdTab() {
        // set top view
        dataBinding.clSelectPref.visibility = GONE
        dataBinding.clLikeAdvice.visibility = GONE
        dataBinding.clEmailAdvice.visibility = VISIBLE

        // set button
        dataBinding.btnNext.visibility = GONE
        dataBinding.llTwoBtnView.visibility = GONE
        dataBinding.btnPrevious.visibility = VISIBLE

        // set indicator
        dataBinding.imvOne.setImageResource(R.drawable.unselect_tab)
        dataBinding.imvTwo.setImageResource(R.drawable.unselect_tab)
        dataBinding.imvThree.setImageResource(R.drawable.selected_tab)

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
        dataBinding.tvPhone.text = number
        dataBinding.tvPhoneTwo.text = number
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

}