package com.heandroid.ui.startNow.contactdartcharge

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import android.provider.OpenableColumns
import android.database.Cursor
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.heandroid.data.model.contactdartcharge.CaseProvideDetailsModel
import com.heandroid.utils.StorageHelper
import com.heandroid.utils.common.Logg


@AndroidEntryPoint
class NewCaseCommentsFragment : BaseFragment<FragmentNewCaseCommentBinding>(),
    View.OnClickListener {

    private val FILE_MANAGER_KEY = 1000
    private var clickedButton : String = Constants.CHOOSE_FILE_1

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewCaseCommentBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_raise_new_enquiry))
        Logg.logging(
            "NewCaseComments",
            "bundle data CaseProvideDetailsModel ${
                arguments?.getParcelable<CaseProvideDetailsModel>(Constants.CASES_PROVIDE_DETAILS_KEY)
            }"
        )
        Logg.logging(
            "NewCaseComments",
            "bundle data cat  ${arguments?.getString(Constants.CASES_CATEGORY)}"
        )
        Logg.logging(
            "NewCaseComments",
            "bundle data  sub Cat ${arguments?.getString(Constants.CASES_SUB_CATEGORY)}"
        )
    }

    override fun initCtrl() {
        binding.apply {
            btnNext.setOnClickListener(this@NewCaseCommentsFragment)
            tvSelectedCategory.text = arguments?.getString(Constants.CASES_CATEGORY)
            tvSubSelectCategory.text = arguments?.getString(Constants.CASES_SUB_CATEGORY)

            chooseFileBtn1.setOnClickListener(this@NewCaseCommentsFragment)
            chooseFileBtn2.setOnClickListener(this@NewCaseCommentsFragment)
            chooseFileBtn3.setOnClickListener(this@NewCaseCommentsFragment)
            chooseFileBtn4.setOnClickListener(this@NewCaseCommentsFragment)

            upload1.setOnClickListener(this@NewCaseCommentsFragment)
            upload2.setOnClickListener(this@NewCaseCommentsFragment)
            upload3.setOnClickListener(this@NewCaseCommentsFragment)
            upload4.setOnClickListener(this@NewCaseCommentsFragment)
        }
    }

    override fun observer() {}

    override fun onClick(it: View?) {

        when (it?.id) {
            R.id.btnNext -> {
                if (binding.tfDescriptionInput.text.toString().isNotEmpty()) {
                    findNavController().navigate(
                        R.id.action_NewCaseCommentsFragment_to_NewCaseSummeryFragment,
                        arguments?.apply {
                            putString(
                                Constants.CASE_COMMENTS_KEY,
                                binding.tfDescriptionInput.text.toString()
                            )
                        }
                    )
                } else {
                    requireActivity().showToast("Please add case comment")
                }
            }
            R.id.chooseFileBtn1 -> {
                clickedButton = Constants.CHOOSE_FILE_1
                checkPermission()
            }
            R.id.chooseFileBtn2 -> {
                clickedButton = Constants.CHOOSE_FILE_2
                checkPermission()
            }
            R.id.chooseFileBtn3 -> {
                clickedButton = Constants.CHOOSE_FILE_3
                checkPermission()
            }
            R.id.chooseFileBtn4 -> {
                clickedButton = Constants.CHOOSE_FILE_4
                checkPermission()
            }
            R.id.upload1 -> {

            }
            R.id.upload2 -> {

            }
            R.id.upload3 -> {

            }
            R.id.upload4 -> {

            }

            else -> {

            }
        }
    }

    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, FILE_MANAGER_KEY)
    }

    private fun checkPermission() {
        if (!StorageHelper.checkStoragePermissions(requireActivity())) {
            StorageHelper.requestStoragePermission(
                requireActivity(),
                onScopeResultLaucher = onScopeResultLauncher,
                onPermissionlaucher = onPermissionLauncher
            )
        } else {
            openFileManager()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == FILE_MANAGER_KEY) {
            data?.data?.let {
                val fileName = getFileName(it)
                setFileName(fileName)
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        val returnCursor: Cursor = context?.contentResolver?.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    private var onScopeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                clickButton()
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
                    clickButton()
                }
                else -> {
                    requireActivity().showToast("Please enable permission")
                }
            }
        }

    private fun clickButton() {
        when(clickedButton){
            Constants.CHOOSE_FILE_1 -> {
                binding.chooseFileBtn1.performClick()
            }
            Constants.CHOOSE_FILE_2 -> {
                binding.chooseFileBtn2.performClick()
            }
            Constants.CHOOSE_FILE_3 -> {
                binding.chooseFileBtn3.performClick()
            }
            else  -> {
                binding.chooseFileBtn4.performClick()
            }
        }
    }

    private fun setFileName(fileName: String) {
        when(clickedButton){
            Constants.CHOOSE_FILE_1 -> {
                binding.fileName1.text = fileName
            }
            Constants.CHOOSE_FILE_2 -> {
                binding.fileName2.text = fileName
            }
            Constants.CHOOSE_FILE_3 -> {
                binding.fileName3.text = fileName
            }
            else  -> {
                binding.fileName4.text = fileName
            }
        }
    }

}