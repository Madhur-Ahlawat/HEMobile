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
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.heandroid.data.model.contactdartcharge.CaseProvideDetailsModel
import com.heandroid.utils.StorageHelper
import java.io.File
import com.heandroid.data.model.contactdartcharge.UploadFileResponseModel
import com.heandroid.ui.loader.LoaderDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.heandroid.utils.FilePath
import com.heandroid.utils.MimeType
import com.heandroid.utils.common.*
import com.heandroid.utils.onTextChanged
import okhttp3.*
import okhttp3.MultipartBody
import okhttp3.RequestBody


@AndroidEntryPoint
class NewCaseCommentsFragment : BaseFragment<FragmentNewCaseCommentBinding>(),
    View.OnClickListener {

    private var clickedChooseButton: String = Constants.CHOOSE_FILE_1
    private var clickedUploadButton: String = Constants.CHOOSE_FILE_1
    private var isUploaded: Boolean = false

    private var file1: File? = null
    private var file2: File? = null
    private var file3: File? = null
    private var file4: File? = null

    private var fileUploaded1: Boolean = false
    private var fileUploaded2: Boolean = false
    private var fileUploaded3: Boolean = false
    private var fileUploaded4: Boolean = false

    private var loader: LoaderDialog? = null
    private val mList = mutableListOf<String>()
    private val viewModel: ContactDartChargeViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewCaseCommentBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_raise_new_enquiry))
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        setView()
    }

    private fun setView() {
        binding.apply {
            file1?.let {
                if (fileUploaded1) {
                    fileName1.text = it.name.toString()
                    chooseFileBtn1.gone()
                    upload1.gone()
                    success1.visible()
                }
            }
            file2?.let {
                if (fileUploaded2) {
                    fileName2.text = it.name.toString()
                    chooseFileBtn2.gone()
                    upload2.gone()
                    success2.visible()
                }
            }
            file3?.let {
                if (fileUploaded3) {
                    fileName3.text = it.name.toString()
                    chooseFileBtn3.gone()
                    upload3.gone()
                    success3.visible()
                }
            }
            file4?.let {
                if (fileUploaded4) {
                    fileName4.text = it.name.toString()
                    chooseFileBtn4.gone()
                    upload4.gone()
                    success4.visible()
                }
            }
        }
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

            binding.tfDescriptionInput.onTextChanged {
                binding.model = binding.tfDescriptionInput.text.toString().isNotEmpty()
            }
        }
    }

    override fun observer() {
        observe(viewModel.uploadFileVal, ::handleUploadFileResponse)
    }

    private fun handleUploadFileResponse(resource: Resource<UploadFileResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        if (isUploaded) {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        checkUploadedFile(it.originalFileName)
                        it.originalFileName?.let { it1 -> mList.add(it1) }
                    }
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, "failed to upload the selected file")
                }
                else -> {
                }
            }
            isUploaded = false
        }

    }

    private fun checkUploadedFile(originalFileName: String?) {
        file1?.let {
            if (it.name.equals(originalFileName, true)) {
                binding.loader1.gone()
                binding.success1.visible()
                binding.upload1.gone()
                binding.chooseFileBtn1.gone()
                fileUploaded1 = true
            }
        }
        file2?.let {
            if (it.name.equals(originalFileName, true)) {
                binding.loader2.gone()
                binding.success2.visible()
                binding.upload2.gone()
                binding.chooseFileBtn2.gone()
                fileUploaded2 = true
            }
        }
        file3?.let {
            if (it.name.equals(originalFileName, true)) {
                binding.loader3.gone()
                binding.success3.visible()
                binding.upload3.gone()
                binding.chooseFileBtn3.gone()
                fileUploaded3 = true
            }
        }
        file4?.let {
            if (it.name.equals(originalFileName, true)) {
                binding.loader4.gone()
                binding.success4.visible()
                binding.upload4.gone()
                binding.chooseFileBtn4.gone()
                fileUploaded4 = true
            }
        }
    }

    override fun onClick(it: View?) {
        when (it?.id) {
            R.id.btnNext -> {
                arguments?.putStringArrayList(Constants.FILE_NAMES_KEY, ArrayList(mList))
                findNavController().navigate(
                    R.id.action_NewCaseCommentsFragment_to_NewCaseSummeryFragment,
                    arguments?.apply {
                        putString(
                            Constants.CASE_COMMENTS_KEY,
                            binding.tfDescriptionInput.text.toString()
                        )
                    }
                )
            }
            R.id.chooseFileBtn1 -> {
                binding.loader1.gone()
                binding.success1.gone()
                clickedChooseButton = Constants.CHOOSE_FILE_1
                checkPermission()
            }
            R.id.chooseFileBtn2 -> {
                binding.loader2.gone()
                binding.success2.gone()
                clickedChooseButton = Constants.CHOOSE_FILE_2
                checkPermission()
            }
            R.id.chooseFileBtn3 -> {
                binding.loader3.gone()
                binding.success3.gone()
                clickedChooseButton = Constants.CHOOSE_FILE_3
                checkPermission()
            }
            R.id.chooseFileBtn4 -> {
                binding.loader4.gone()
                binding.success4.gone()
                clickedChooseButton = Constants.CHOOSE_FILE_4
                checkPermission()
            }
            R.id.upload1 -> {
                clickedUploadButton = Constants.CHOOSE_FILE_1
                checkUploadFile()
            }
            R.id.upload2 -> {
                clickedUploadButton = Constants.CHOOSE_FILE_2
                checkUploadFile()
            }
            R.id.upload3 -> {
                clickedUploadButton = Constants.CHOOSE_FILE_3
                checkUploadFile()
            }
            R.id.upload4 -> {
                clickedUploadButton = Constants.CHOOSE_FILE_4
                checkUploadFile()
            }
            else -> {
            }
        }
    }

    private fun checkUploadFile() {
        when (clickedUploadButton) {
            Constants.CHOOSE_FILE_1 -> {
                file1?.let {
                    uploadFile(it)
                }
            }
            Constants.CHOOSE_FILE_2 -> {
                file2?.let {
                    uploadFile(it)
                }
            }
            Constants.CHOOSE_FILE_3 -> {
                file3?.let {
                    uploadFile(it)
                }
            }
            else -> {
                file4?.let {
                    uploadFile(it)
                }
            }
        }
    }

    private fun uploadFile(file: File) {
        val requestFile: RequestBody =
            RequestBody.create(MimeType.selectMimeType(file).toMediaTypeOrNull(), file)
        val data = MultipartBody.Part.createFormData("file", file.name, requestFile)
        loader?.show(requireActivity().supportFragmentManager, "Loader")
        isUploaded = true
        viewModel.uploadFileApi(data)
    }

    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        fileManagerResult.launch(intent)
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

    private val fileManagerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result?.data?.data?.let {
                    val path: String? = FilePath.getPath(requireContext(), it)
                    path?.let { pat ->
                        val file = File(pat)
                        setFileName(file.name)
                        saveFile(file)
                    }

                }
            }
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
        when (clickedChooseButton) {
            Constants.CHOOSE_FILE_1 -> {
                binding.chooseFileBtn1.performClick()
            }
            Constants.CHOOSE_FILE_2 -> {
                binding.chooseFileBtn2.performClick()
            }
            Constants.CHOOSE_FILE_3 -> {
                binding.chooseFileBtn3.performClick()
            }
            else -> {
                binding.chooseFileBtn4.performClick()
            }
        }
    }

    private fun setFileName(fileName: String?) {
        when (clickedChooseButton) {
            Constants.CHOOSE_FILE_1 -> {
                binding.fileName1.text = fileName ?: ""
                binding.upload1.visible()
            }
            Constants.CHOOSE_FILE_2 -> {
                binding.fileName2.text = fileName ?: ""
                binding.upload2.visible()
            }
            Constants.CHOOSE_FILE_3 -> {
                binding.fileName3.text = fileName ?: ""
                binding.upload3.visible()
            }
            else -> {
                binding.fileName4.text = fileName ?: ""
                binding.upload4.visible()
            }
        }
    }

    private fun saveFile(file: File) {
        when (clickedChooseButton) {
            Constants.CHOOSE_FILE_1 -> {
                file1 = file
            }
            Constants.CHOOSE_FILE_2 -> {
                file2 = file
            }
            Constants.CHOOSE_FILE_3 -> {
                file3 = file
            }
            else -> {
                file4 = file
            }
        }
    }

}