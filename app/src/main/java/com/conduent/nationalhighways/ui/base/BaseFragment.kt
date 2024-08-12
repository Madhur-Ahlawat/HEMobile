package com.conduent.nationalhighways.ui.base

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.ErrorResponseModel
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step5.CreateAccountVehicleViewModel
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_FROM
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.SHOW_BACK_BUTTON
import com.conduent.nationalhighways.utils.common.RequestPermissionListener
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack


abstract class BaseFragment<B : ViewBinding> : Fragment() {

    private var _binding: B? = null
    protected val binding get() = _binding!!

    lateinit var navFlowCall: String
    var navFlowFrom: String = ""
    var navData: Any? = null
    var backButton: Boolean = true
    var edit_summary: Boolean = false
    private var backPressListener: BackPressListener? = null
    private val viewModel: CreateAccountVehicleViewModel by viewModels()
    private var loaderDialog: LoaderDialog? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getFragmentBinding(inflater, container)

        navFlowCall = arguments?.getString(NAV_FLOW_KEY, "").toString()
        if (arguments?.containsKey(NAV_FLOW_FROM) == true) {
            navFlowFrom = arguments?.getString(NAV_FLOW_FROM, "").toString()
        }
        if (arguments?.containsKey(EDIT_SUMMARY) == true) {
            edit_summary = arguments?.getBoolean(EDIT_SUMMARY, false) ?: false
        }
        navData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(NAV_DATA_KEY, Any::class.java)
        } else {
            arguments?.getParcelable(NAV_DATA_KEY)
        }
        if (arguments?.containsKey(SHOW_BACK_BUTTON) == true) {
            backButton = arguments?.getBoolean(SHOW_BACK_BUTTON, true) ?: true
        }

        if ((requireActivity() is HomeActivityMain) && !backButton) {
            (requireActivity() as HomeActivityMain).hideBackIcon()
        }
        Log.e("TAG", "onCreateView: navFlowCall -> "+navFlowCall+" navFlowFrom -> "+navFlowFrom )

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observer()
        initCtrl()
        init()
        callOnBackListener()
    }

    private fun callOnBackListener() {
        if(onBackPressedCallback==null) {
            onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backPressListener?.onBackButtonPressed()
                    if (backButton) {
                        isEnabled = false
                    } else {
                        Utils.vibrate(requireActivity())
                    }
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                onBackPressedCallback!!
            )

        }
    }

    fun setBackPressListener(listener: BackPressListener) {
        backPressListener = listener
    }

    fun destroyBackPressListener() {
        backPressListener = null
    }




    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B
    abstract fun init()


    abstract fun initCtrl()
    abstract fun observer()


    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)
    }

    fun checkBackIcon() {
        val backIcon: ImageView? = requireActivity().findViewById(R.id.back_button)
        if (!backButton) {
            backIcon?.visibility = View.GONE
        } else {
            backIcon?.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    fun checkSessionExpiredOrServerError(errorResponsModel: ErrorResponseModel?): Boolean {
        return (errorResponsModel?.errorCode == Constants.TOKEN_FAIL && (errorResponsModel.error != null && errorResponsModel.error.equals(
            Constants.INVALID_TOKEN
        ))) || (errorResponsModel?.errorCode == Constants.INTERNAL_SERVER_ERROR &&
                (errorResponsModel.error != null && errorResponsModel.error.equals(
                    Constants.SERVER_ERROR
                )))
    }

    fun displaySessionExpireDialog(errorResponsModel: ErrorResponseModel?) {
        if (errorResponsModel?.errorCode == Constants.TOKEN_FAIL && errorResponsModel.error.equals(
                Constants.INVALID_TOKEN
            )
        ) {
            Utils.displaySesionExpiryDialog(requireActivity())
        } else if (errorResponsModel?.errorCode == Constants.INTERNAL_SERVER_ERROR && errorResponsModel.error.equals(
                Constants.SERVER_ERROR
            )
        ) {
            requireActivity().startNewActivityByClearingStack(LandingActivity::class.java) {
                putString(Constants.SHOW_SCREEN, Constants.SERVER_ERROR)
            }
        }
    }


    fun displayMessage(
        fTitle: String?,
        message: String,
        positiveBtnTxt: String,
        negativeBtnTxt: String,
        pListener: DialogPositiveBtnListener?,
        nListener: DialogNegativeBtnListener?
    ) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setCancelable(false)

        val alertDialog = builder.create()

        val customTitleView = LayoutInflater.from(requireActivity()).inflate(
            R.layout.alert_dialog_custom_title_layout, LinearLayout(requireActivity())
        )

        val titleText = customTitleView.findViewById<TextView>(R.id.alert_dialog_title_text)

        titleText.text = fTitle


        alertDialog.setCustomTitle(customTitleView)
        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            positiveBtnTxt
        ) { dialog, _ -> pListener?.positiveBtnClick(dialog) }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            negativeBtnTxt
        ) { dialog, _ -> nListener?.negativeBtnClick(dialog) }
        alertDialog.setOnShowListener {
            val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.blue
                )
            )
            val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.red
                )
            )
        }

        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    fun displayCustomMessage(
        fTitle: String?,
        message: String,
        positiveBtnTxt: String,
        negativeBtnTxt: String,
        pListener: DialogPositiveBtnListener?,
        nListener: DialogNegativeBtnListener?,
        cancelVisibility: Int = View.VISIBLE,
        cancelButtonColor: Int = 0,
        typeFace: Typeface? = null,
        lineView: Boolean? = false,
        messageContentDesc: String = ""
    ) {

        val dialog = Dialog(requireActivity(), R.style.CustomDialogTheme1)

        dialog.setCancelable(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setDimAmount(0.5f) // Adjust the dim amount as needed (0.0f for completely transparent, 1.0f for completely opaque)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog)
        val title = dialog.findViewById<TextView>(R.id.title)
        val textMessage = dialog.findViewById<TextView>(R.id.message)
        val cancel = dialog.findViewById<TextView>(R.id.cancel_btn)
        val ok = dialog.findViewById<TextView>(R.id.ok_btn)
        val firstView = dialog.findViewById<View>(R.id.firstView)
        val secondView = dialog.findViewById<View>(R.id.secondView)

        if (cancelButtonColor != 0) {
            cancel.setTextColor(cancelButtonColor)
        }
        if (typeFace != null) {
            cancel.typeface = typeFace
        }
        title.text = fTitle
        textMessage.text = message
        cancel.text = negativeBtnTxt
        ok.text = positiveBtnTxt
        cancel.visibility = cancelVisibility

        if (messageContentDesc.isNotEmpty()) {
            textMessage.contentDescription = messageContentDesc
        }

        if (lineView == true) {
            firstView.visibility = View.VISIBLE
        } else {
            firstView.visibility = cancelVisibility
        }
        secondView.visibility = cancelVisibility
        cancel.setOnClickListener {
            nListener?.negativeBtnClick(dialog)
            dialog.dismiss()
        }
        ok.setOnClickListener {
            pListener?.positiveBtnClick(dialog)
            dialog.dismiss()
        }
        dialog.show()


    }

    fun emailHeartBeatApi() {
        if (NewCreateAccountRequestModel.referenceId?.trim()
                ?.isNotEmpty() == true && NewCreateAccountRequestModel.referenceId != null
        ) {
            NewCreateAccountRequestModel.referenceId?.let {
                viewModel.heartBeat(
                    Constants.AGENCY_ID,
                    it
                )
            }
        }
    }

    fun smsHeartBeatApi() {

        if (NewCreateAccountRequestModel.sms_referenceId?.trim()
                ?.isNotEmpty() == true && NewCreateAccountRequestModel.sms_referenceId != null
        ) {
            NewCreateAccountRequestModel.sms_referenceId?.let {
                viewModel.heartBeat(
                    Constants.AGENCY_ID,
                    it
                )
            }
        }
    }

    fun checkRuntimePermission(
        requestName: String,
        requestCode: Int,
        listener: RequestPermissionListener
    ) {
        if (activity?.checkSelfPermission(requestName) == PackageManager.PERMISSION_GRANTED) {
            listener.onPermissionGranted()
        } else {
            requestPermissions(arrayOf(requestName), requestCode)
        }

    }

    override fun onRequestPermissionsResult(RC: Int, per: Array<String?>, PResult: IntArray) {
        when (RC) {
            Constants.READ_STORAGE_REQUEST_CODE ->
                if (PResult.isNotEmpty() && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    (this as RequestPermissionListener).onPermissionGranted()
                } else {

                }

        }
    }

    fun showLoaderDialog(type: Int = 0) {
        Log.e(
            "BaseFragment",
            "LoaderDialog showLoaderDialog: $type $loaderDialog ${loaderDialog?.isVisible} ${loaderDialog?.isVisible == false}"
        )
        val fragmentManager = requireActivity().supportFragmentManager
        val existingFragment = fragmentManager.findFragmentByTag(Constants.LOADER_DIALOG)
        if (existingFragment != null) {
            (existingFragment as LoaderDialog).dismiss()
        }
        if (loaderDialog == null) {
            loaderDialog = LoaderDialog()
            loaderDialog?.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomLoaderDialog)
        }
        if (loaderDialog?.isVisible == null || loaderDialog?.isVisible == false) {
            loaderDialog?.show(fragmentManager, Constants.LOADER_DIALOG)
        }


    }

    fun dismissLoaderDialog(type: Int = 0) {
        Log.e(
            "BaseFragment",
            "LoaderDialog dismissLoaderDialog: $type $loaderDialog ${loaderDialog?.showsDialog}"
        )
        if (loaderDialog != null) {
            loaderDialog?.dismiss()
            loaderDialog = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
        if (loaderDialog?.isVisible == true) {
            loaderDialog?.dismiss()
        }
        loaderDialog = null

        _binding = null
    }


}

interface BackPressListener {
    fun onBackButtonPressed()
}