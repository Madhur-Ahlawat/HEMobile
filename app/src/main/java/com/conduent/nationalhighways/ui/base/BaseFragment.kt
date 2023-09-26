package com.conduent.nationalhighways.ui.base

import android.app.Dialog
import android.graphics.Color
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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_FROM
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.SHOW_BACK_BUTTON
import com.conduent.nationalhighways.utils.common.Utils


abstract class BaseFragment<B : ViewBinding> : Fragment() {

    protected lateinit var binding: B
    lateinit var navFlowCall: String
    var navFlowFrom: String = ""
    var navData: Any? = null
    var backButton: Boolean = true
    private var backPressListener: BackPressListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(inflater, container)

        navFlowCall = arguments?.getString(NAV_FLOW_KEY, "").toString()
        if (arguments?.containsKey(NAV_FLOW_FROM) == true) {
            navFlowFrom = arguments?.getString(NAV_FLOW_FROM, "").toString()
        }
        navData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(NAV_DATA_KEY, Any::class.java)
        } else {
            arguments?.getParcelable(NAV_DATA_KEY)
        }
        if (arguments?.containsKey(SHOW_BACK_BUTTON) == true) {
            backButton = arguments?.getBoolean(SHOW_BACK_BUTTON, true) ?: true
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observer()
        initCtrl()
        init()

        backClickListener()
    }

    fun setBackPressListener(listener: BackPressListener) {
        backPressListener = listener
    }

    private fun backClickListener() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                backPressListener?.onBackButtonPressed()

                if (backButton) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                    // Implement your custom back navigation logic
                } else {

                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        if(navData!=null && navData is CrossingDetailsModelsResponse){
            Log.e("EXPIRY",(navData as CrossingDetailsModelsResponse).expirationDate)
        }
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B
    abstract fun init()


    abstract fun initCtrl()
    abstract fun observer()

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)
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


    fun displaySessionExpireDialog() {
        Utils.displaySesionExpiryDialog(requireActivity())
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
        cancelVisibility: Int = View.VISIBLE
    ) {

        val dialog = Dialog(requireActivity())
        dialog.setCancelable(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog)
        val title = dialog.findViewById<TextView>(R.id.title)
        val textMessage = dialog.findViewById<TextView>(R.id.message)
        val cancel = dialog.findViewById<TextView>(R.id.cancel_btn)
        val ok = dialog.findViewById<TextView>(R.id.ok_btn)
        val firstView = dialog.findViewById<View>(R.id.firstView)
        val secondView = dialog.findViewById<View>(R.id.secondView)

        title.text = fTitle
        textMessage.text = message
        cancel.text = negativeBtnTxt
        ok.text = positiveBtnTxt
        cancel.visibility = cancelVisibility
        firstView.visibility = cancelVisibility
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

}

interface BackPressListener {
    fun onBackButtonPressed()
}