package com.conduent.nationalhighways.ui.payment

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.OneOfPaymentModelResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentMakeOffPaymentSuccessfulBinding
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.openActivityWithData
import com.conduent.nationalhighways.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MakeOffPaymentSuccessfulFragment : BaseFragment<FragmentMakeOffPaymentSuccessfulBinding>(),
    RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel: MakeOneOfPaymentViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var mScreeType = 0
    private var list: MutableList<VehicleResponse> = ArrayList()
    private var mEmail = ""
    private var mPaymentResp: OneOfPaymentModelResponse? = null
    private val PERMISSION_REQUEST_CODE = 200
    private var pageHeight = 1120
    private var pageWidth = 792

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMakeOffPaymentSuccessfulBinding.inflate(inflater, container, false)

    override fun init() {

        AdobeAnalytics.setScreenTrack(
            "one of  payment:payment success",
            "vehicle",
            "english",
            "one of payment",
            "home",
            "one of  payment: payment success",
            sessionManager.getLoggedInUser()
        )
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        mEmail = arguments?.getString(Constants.EMAIL)!!
        list = arguments?.getParcelableArrayList(Constants.DATA)!!
        mPaymentResp = arguments?.getParcelable(Constants.ONE_OF_PAYMENTS_PAY_RESP)
//        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        binding.tvEmail.text = mEmail
        binding.tvAmount.text = "£ ${list[0].price}"
        binding.tvReceiptNo.text = mPaymentResp?.referenceNumber?:""
        binding.viewAllVechile.text = list[0].newPlateInfo!!.number
        binding.rbCreateAccount.isChecked = true
        binding.rgOptions.setOnCheckedChangeListener(this)

    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener {
            requireActivity().finish()

            if(mType) {

                requireActivity().openActivityWithData(CreateAccountActivity::class.java) {
                    putParcelableArrayList(Constants.DATA, ArrayList(list))
                    putString(Constants.EMAIL,mEmail)
                }
            }else{
                requireActivity().openActivityWithData(MakeOffPaymentActivity::class.java){
                    putParcelableArrayList(Constants.DATA, ArrayList(list))
                    putString(Constants.EMAIL,mEmail)

                }

            }
        }
        binding.downloadReceipt.setOnClickListener(this@MakeOffPaymentSuccessfulFragment)
    }

    override fun observer() {
        observe(viewModel.whereToReceivePaymentReceipt, ::receipt)

    }

    private fun receipt(resource: Resource<ResponseBody?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                }
            }
            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }
            else -> {}
        }

    }
    private var mType = true

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group?.checkedRadioButtonId) {
            R.id.rbCreateAccount -> {
                mType = true
            }
            R.id.rbMakePayment -> {
                mType= false
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.downloadReceipt -> {
                /*if (!StorageHelper.checkStoragePermissions(requireActivity())) {
                    StorageHelper.requestStoragePermission(
                        requireActivity(),
                        onScopeResultLaucher = onScopeResultLauncher,
                        onPermissionlaucher = onPermissionLauncher
                    )
                } else {
                    generatePDF()
                }*/
            }
        }
    }

    private var onScopeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.downloadReceipt.performClick()
            }
        }


    private var onPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var permission = true
        permissions.entries.forEach { if (!it.value) { permission = it.value } }
        when (permission) {
            true -> { binding.downloadReceipt.performClick() }
            else -> { requireActivity().showToast("Please enable permission to download") }
        }
    }

    private fun generatePDF() {
        val pdfDocument = PdfDocument()

        val myPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)
        val canvas: Canvas = myPage.canvas

        addText(canvas, 80F, 80F, Typeface.BOLD, "Receipt number")
        addText(canvas, 80F, 120F, Typeface.NORMAL, binding.tvReceiptNo.text.toString())
        addText(canvas, 80F, 200F, Typeface.BOLD, "Confirmation Email sent")
        addText(canvas, 80F, 240F, Typeface.NORMAL, binding.tvEmail.text.toString())
        addText(canvas, 80F, 300F, Typeface.BOLD, "Date")
        addText(canvas, 80F, 340F, Typeface.NORMAL, DateUtils.currentDate().toString())

        pdfDocument.finishPage(myPage)

        //val file = File(requireContext().getExternalFilesDir(null), "Payment Receipt.pdf")

        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).path + "/" + "Payment Receipt ${System.currentTimeMillis()}.pdf"
        )

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(requireContext(), "PDF file generated successfully.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        pdfDocument.close()
    }

    private fun addText(canvas: Canvas, horizontalSpace: Float, verticalSpace: Float, style: Int, text: String){
        val title = Paint()
        title.typeface = Typeface.create(Typeface.DEFAULT, style)
        title.textSize = 30F
        title.color = ContextCompat.getColor(requireContext(), R.color.black)
        canvas.drawText(text, horizontalSpace, verticalSpace, title)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {

                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (writeStorage && readStorage) {
                    Toast.makeText(context, "Permission Granted..", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Permission Denied.", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }
            }
        }
    }

}