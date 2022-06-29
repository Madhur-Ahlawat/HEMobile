package com.heandroid.ui.payment

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.heandroid.R
import com.heandroid.data.model.makeoneofpayment.OneOfPaymentModelResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentMakeOffPaymentSuccessfulBinding
import com.heandroid.ui.account.creation.controller.CreateAccountActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.DateUtils
import com.heandroid.utils.StorageHelper
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class MakeOffPaymentSuccessfulFragment : BaseFragment<FragmentMakeOffPaymentSuccessfulBinding>(),
    RadioGroup.OnCheckedChangeListener, View.OnClickListener {

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
    ): FragmentMakeOffPaymentSuccessfulBinding =
        FragmentMakeOffPaymentSuccessfulBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        mEmail = arguments?.getString(Constants.EMAIL)!!
        list = arguments?.getParcelableArrayList(Constants.DATA)!!
        mPaymentResp = arguments?.getParcelable(Constants.ONE_OF_PAYMENTS_PAY_RESP)
//        loader?.show(requireActivity().supportFragmentManager, "")
        binding.tvEmail.text = "$mEmail"
        binding.tvAmount.text = "£ ${list[0].price}"
        binding.tvReceiptNo.text = mPaymentResp?.refrenceNumber?:""
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
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    loader?.dismiss()
                    it?.let {
                        Logg.logging(
                            "testing",
                            " MakeOffPaymentSuccessfulFragment success it  $it"
                        )


                    }
                }
            }
            is Resource.DataError -> {
                Logg.logging("testing", " MakeOffPaymentSuccessfulFragment error called")

                loader?.dismiss()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
        }

    }
    private var mType = true

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

        Logg.logging(
            "testing",
            " MakeOffPaymentSuccessfulFragment onCheckedChanged rbCreateAccount"
        )

        when (group?.checkedRadioButtonId) {

            R.id.rbCreateAccount -> {
                Logg.logging(
                    "testing",
                    " MakeOffPaymentSuccessfulFragment rbCreateAccount"
                )
                mType = true
            }
            R.id.rbMakePayment -> {
                Logg.logging(
                    "testing",
                    " MakeOffPaymentSuccessfulFragment success rbMakePayment"
                )
                mType= false

            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.downloadReceipt -> {
                if (!StorageHelper.checkStoragePermissions(requireActivity())) {
                    StorageHelper.requestStoragePermission(
                        requireActivity(),
                        onScopeResultLaucher = onScopeResultLauncher,
                        onPermissionlaucher = onPermissionLauncher
                    )
                } else {
                    generatePDF()
                }
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

        val file = File(requireContext().getExternalFilesDir(null), "Payment Receipt.pdf")

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