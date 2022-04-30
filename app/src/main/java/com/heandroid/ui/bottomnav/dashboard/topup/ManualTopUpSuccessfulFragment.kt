package com.heandroid.ui.bottomnav.dashboard.topup

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.heandroid.R
import com.heandroid.data.model.payment.PaymentMethodDeleteResponseModel
import com.heandroid.databinding.FragmentManualTopUpSuccessfulBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.HomeActivityMain
import com.heandroid.utils.DateUtils
import com.heandroid.utils.StorageHelper
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class ManualTopUpSuccessfulFragment : BaseFragment<FragmentManualTopUpSuccessfulBinding>(), View.OnClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)=FragmentManualTopUpSuccessfulBinding.inflate(inflater,container,false)

    private var pageHeight = 1120
    private var pageWidth = 792
    private val PERMISSION_REQUEST_CODE = 200
    private var model : PaymentMethodDeleteResponseModel? = null

    override fun init() {
        binding.tvAmount.text="£ ${arguments?.getString("amount")}"
        model=arguments?.getParcelable<PaymentMethodDeleteResponseModel>(Constants.DATA)
        binding.tvReceiptNo.text=model?.transactionId
        binding.tvEmail.text=model?.emailMessage
        binding.tvDate.text=DateUtils.currentDate()
    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
        binding.downloadReceipt.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnContinue ->{
                requireActivity().finish()
                requireActivity().startNormalActivity(HomeActivityMain::class.java)
            }

            R.id.downloadReceipt -> {
                if (!StorageHelper.checkStoragePermissions(requireActivity())) {
                    StorageHelper.requestStoragePermission(
                        requireActivity(),
                        onScopeResultLaucher = onScopeResultLauncher,
                        onPermissionlaucher = onPermissionLauncher
                    )
                } else {
                    generatePDF(model)
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

    private fun generatePDF(model: PaymentMethodDeleteResponseModel?) {
        val pdfDocument = PdfDocument()

        val myPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)
        val canvas: Canvas = myPage.canvas

        addText(canvas, 80F, 80F, Typeface.BOLD, "Receipt Number")
        addText(canvas, 80F, 120F, Typeface.NORMAL, model?.transactionId.toString())
        addText(canvas, 80F, 200F, Typeface.BOLD, "Confirmation Email sent")
        addText(canvas, 80F, 240F, Typeface.NORMAL, model?.emailMessage.toString())
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