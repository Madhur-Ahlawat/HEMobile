package com.conduent.nationalhighways.ui.bottomnav.account.payments.history

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.databinding.AccountPaymentHistoryItemDetailBinding
import com.conduent.nationalhighways.databinding.DownloadAccountPaymentHistoryPdfBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.AccountPaymentActivity
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.StorageHelper
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.showToast
import java.io.File
import java.io.FileOutputStream

class AccountPaymentHistoryItemDetailFragment :
    BaseFragment<AccountPaymentHistoryItemDetailBinding>(), View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AccountPaymentHistoryItemDetailBinding.inflate(inflater, container, false)

    override fun observer() {}

    override fun init() {
        arguments?.getParcelable<TransactionData?>(Constants.DATA)?.let { tData ->
            binding.data = tData
            binding.paymentDate.text = DateUtils.convertDateFormat(tData.transactionDate, 0)
        }
    }

    override fun initCtrl() {
        binding.apply {
            downloadReceiptBtn.setOnClickListener(this@AccountPaymentHistoryItemDetailFragment)
            backBtn.setOnClickListener(this@AccountPaymentHistoryItemDetailFragment)
        }
        if (requireActivity() is AccountPaymentActivity) {
            (requireActivity() as AccountPaymentActivity).hideTabLayout()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (requireActivity() is AccountPaymentActivity) {
            (requireActivity() as AccountPaymentActivity).showTabLayout()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.downloadReceiptBtn -> {
                if (!StorageHelper.checkStoragePermissions(requireActivity())) {
                    StorageHelper.requestStoragePermission(
                        requireActivity(),
                        onScopeResultLaucher = onScopeResultLauncher,
                        onPermissionlaucher = onPermissionLauncher
                    )
                } else {
                    generatePaymentReceipt()
                }
            }
            R.id.backBtn -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun generatePaymentReceipt() {
        val inflater = LayoutInflater.from(context)
        val paymentBinding = DownloadAccountPaymentHistoryPdfBinding.inflate(inflater)
        arguments?.getParcelable<TransactionData?>(Constants.DATA)?.let { tData ->
            paymentBinding.transactionId.text = tData.transactionNumber
            paymentBinding.paymentDate.text = DateUtils.convertDateFormat(tData.transactionDate, 0)
            paymentBinding.paymentType.text = tData.exitPlazaName
            paymentBinding.paymentMethod.text = tData.rebillPaymentType
            paymentBinding.amount.text = tData.amount
        }
        val view = paymentBinding.root

        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context?.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
            )
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = view.measuredWidth.let {
            view.measuredHeight.let { it1 ->
                Bitmap.createBitmap(
                    it,
                    it1, Bitmap.Config.ARGB_8888
                )
            }
        }
        //Bitmap.createScaledBitmap(bitmap, 595, 842, true)
        val canvas = bitmap?.let { Canvas(it) }
        view.draw(canvas)

        val pdfDocument = PdfDocument()
        val pageInfo =
            PdfDocument.PageInfo.Builder(displayMetrics.widthPixels, displayMetrics.heightPixels, 1)
                .create()
        val page = pdfDocument.startPage(pageInfo)
        bitmap?.let { page.canvas.drawBitmap(it, 0F, 0F, null) }
        pdfDocument.finishPage(page)

//        val path = "${activity?.getExternalFilesDir(null)}${File.separator}paymentReceipt${
//            System.currentTimeMillis()
//        }.pdf"
//        val file = File(path)

        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).path + "/" + "Payment Receipt ${System.currentTimeMillis()}.pdf"
        )

        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        activity?.showToast("Payment receipt downloaded successfully")

    }

    private var onScopeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.downloadReceiptBtn.performClick()
            }
        }


    private var onPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var permission = true
            permissions.entries.forEach {
                if (!it.value) {
                    permission = it.value
                }
            }
            when (permission) {
                true -> {
                    binding.downloadReceiptBtn.performClick()
                }
                else -> {
                    requireActivity().showToast("Please enable permission to download")
                }
            }
        }
}