package com.conduent.nationalhighways.ui.account.creation.controller

import android.content.DialogInterface.OnShowListener
import android.util.Log
import android.util.TypedValue
import com.codemybrainsout.ratingdialog.RatingDialog
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityCreateAccountBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.AccountSuccessfullyCreationFragment
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CreateAccountActivity : BaseActivity<Any>(),LogoutListener {
    lateinit var binding: ActivityCreateAccountBinding
    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var api: ApiService
    override fun initViewBinding() {
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
        binding.toolBarLyt.titleTxt.text = getString(R.string.str_create_an_account)
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }


        AdobeAnalytics.setScreenTrack(
            "create account",
            "create account",
            "english",
            "create account",
            "home",
            "create account",
            sessionManager.getLoggedInUser()
        )

        ratingDialog()

    }

    private fun ratingDialog() {
       /* val ratingDialog: RatingDialog = RatingDialog.Builder(this)
            .threshold(3)
            .session(1)
            .onRatingBarFormSubmit { feedback -> Log.i("TAG", "onRatingBarFormSubmit: $feedback") }
            .build()

        ratingDialog.show()*/

//        val ratingDialog = RatingDialog.Builder(this)
//            .icon(R.mipmap.ic_launcher)
//            .session(3)
//            .threshold(3)
//            .title(text = R.string.rating_dialog_experience, textColor = R.color.primaryTextColor)
//            .positiveButton(text = R.string.rating_dialog_maybe_later, textColor = R.color.colorPrimary, background = R.drawable.button_selector_positive)
//            .negativeButton(text = R.string.rating_dialog_never, textColor = R.color.secondaryTextColor)
//            .formTitle(R.string.submit_feedback)
//            .formHint(R.string.rating_dialog_suggestions)
//            .feedbackTextColor(R.color.feedbackTextColor)
//            .formSubmitText(R.string.rating_dialog_submit)
//            .formCancelText(R.string.rating_dialog_cancel)
//            .ratingBarColor(R.color.ratingBarColor)
//            .playstoreUrl("https://play.google.com/store/apps/details?id=com.conduent.nationalhighways")
//            .onThresholdCleared { dialog, rating, thresholdCleared -> Log.e("TAG", "onThresholdCleared: $rating $thresholdCleared") }
//            .onThresholdFailed { dialog, rating, thresholdCleared -> Log.e("TAG", "onThresholdFailed: $rating $thresholdCleared") }
//            .onRatingChanged { rating, thresholdCleared -> Log.e("TAG", "onRatingChanged: $rating $thresholdCleared") }
//            .onRatingBarFormSubmit { feedback -> Log.e("TAG", "onRatingBarFormSubmit: $feedback") }
//            .build()

//        ratingDialog.show()
    }

    override fun observeViewModel() {}



    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let {fragment->
                if (fragment is AccountSuccessfullyCreationFragment){

                }else{
                    onBackPressedDispatcher.onBackPressed()
                }

            }
        }

    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        loadSession()
    }

    private fun loadSession() {
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        LogoutUtil.stopLogoutTimer()
        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager,api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }
}

