package com.conduent.nationalhighways.ui.account.communication

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.DialogUpdatePhoneNumberBinding
import com.conduent.nationalhighways.listener.UpdatePhoneNumberClickListener

class UpdatePhoneNumberDialog : DialogFragment() {
    private lateinit var dataBinding: DialogUpdatePhoneNumberBinding
    private var phoneNumber: String = ""


    companion object {

        const val TAG = "UpdateMobileNumberDialog"

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"
        private var mListener: UpdatePhoneNumberClickListener? = null

        fun newInstance(title: String, listener: UpdatePhoneNumberClickListener): UpdatePhoneNumberDialog {
            val args = Bundle()
            mListener = listener
            args.putString(KEY_TITLE, title)
            val fragment = UpdatePhoneNumberDialog()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_update_phone_number, container, false)
        return dataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        setupView()
        setupClickListeners()
    }

    private fun setupClickListeners() {

        dataBinding.btnCancel.setOnClickListener {
            dismiss()
            mListener?.onCancelClickedListener(this)
        }
        dataBinding.imvCancel.setOnClickListener {
            dismiss()
            mListener?.onCrossImageClickedListener(this)
        }
        dataBinding.btnSave.setOnClickListener {
            phoneNumber = dataBinding.edtPhone.text.toString()
            if(TextUtils.isEmpty(phoneNumber))
            {
                Toast.makeText(activity , getString(R.string.please_enter_phone_number), Toast.LENGTH_SHORT).show()
            }
            else
            {
                mListener!!.onSaveClickedListener(phoneNumber , this)
            }

        }
    }

    private fun setupView() {
        setBtnNormal()

//        dataBinding.addVrmInput.addTextChangedListener {
//            if (dataBinding.addVrmInput.getText().toString().isNotEmpty()) {
//                setBtnActivated()
//
//            } else {
//                setBtnNormal()
//
//            }
//        }
    }

    private fun setBtnActivated() {
//        dataBinding.addVehicleBtn.setBackgroundColor(
//            ContextCompat.getColor(
//                requireContext(),
//                R.color.btn_color
//            )
//        )
//
//        dataBinding.addVehicleBtn.setTextColor(
//            ContextCompat.getColor(
//                requireContext(),
//                R.color.white
//            )
//        )
//        dataBinding.addVehicleBtn.isEnabled = true
    }

    private fun setBtnNormal() {
//        dataBinding.addVehicleBtn.setBackgroundColor(
//            ContextCompat.getColor(
//                requireContext(),
//                R.color.hint_color
//            )
//        )
//        dataBinding.addVehicleBtn.setTextColor(
//            ContextCompat.getColor(
//                requireContext(),
//                R.color.black
//            )
//        )
//
//        dataBinding.addVehicleBtn.isEnabled = false

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background))
    }


}