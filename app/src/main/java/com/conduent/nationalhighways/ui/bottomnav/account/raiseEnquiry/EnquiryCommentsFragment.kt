package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.data.model.contactdartcharge.UploadFileResponseModel
import com.conduent.nationalhighways.databinding.FragmentEnquiryCommentsBinding
import com.conduent.nationalhighways.ui.base.BackPressListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.FilePath
import com.conduent.nationalhighways.utils.StorageHelper
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.showToast
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class EnquiryCommentsFragment : BaseFragment<FragmentEnquiryCommentsBinding>(), BackPressListener {

    val viewModel: RaiseNewEnquiryViewModel by activityViewModels()
    var file: File? = null
    private var loader: LoaderDialog? = null
    var previousComments: String = ""
    var previousFile: String = ""
    var isViewCreated: Boolean = false
    private var editRequest: String = ""
    var isApiCalled: Boolean = false
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnquiryCommentsBinding =
        FragmentEnquiryCommentsBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.containsKey(Constants.Edit_REQUEST_KEY) == true) {
            editRequest = arguments?.getString(Constants.Edit_REQUEST_KEY, "").toString()
        }

        setBackPressListener(this)

        saveEditData()
        binding.btnNext.setOnClickListener {
            if (editRequest == Constants.EDIT_SUMMARY &&
                previousComments == viewModel.edit_enquiryModel.value?.comments &&
                previousFile == viewModel.edit_enquiryModel.value?.fileName
            ) {
                findNavController().navigate(
                    R.id.action_enquiryCommentsFragment_to_enquirySummaryFragment, getBundleData()
                )

            } else {
                findNavController().navigate(
                    R.id.action_enquiryCommentsFragment_to_enquiryContactDetailsFragment,
                    getBundleData()
                )
            }
        }

        binding.chooseFileBt.setOnClickListener {
            checkPermission()
        }

        binding.fileDeleteIv.setOnClickListener {
            file = null
            viewModel.edit_enquiryModel.value?.fileName = ""
            viewModel.edit_enquiryModel.value?.file = File("")

            visibleChooseFileBt()
        }

        binding.commentsEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.edit_enquiryModel.value?.comments = p0.toString()
                binding.charactersRemTv.setText(
                    requireActivity().resources.getString(
                        R.string.str_you_have_chars_remain,
                        ("" + (500 - binding.commentsEt.text.toString().length))
                    )
                )
                if (binding.commentsEt.text.toString().trim().isEmpty()) {
                    binding.btnNext.disable()
                } else {
                    binding.btnNext.enable()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun visibleChooseFileBt() {
        binding.chooseFileBt.visible()
        binding.fileCv.gone()
        binding.fileNameTv.setText("")
    }

    override fun initCtrl() {

    }


    private fun getBundleData(): Bundle {
        val bundle: Bundle = Bundle()
        if (editRequest == Constants.EDIT_SUMMARY) {
            bundle.putString(Constants.Edit_REQUEST_KEY, Constants.EDIT_COMMENTS_DATA)
        } else {
            bundle.putString(Constants.Edit_REQUEST_KEY, editRequest)
        }
        bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)

        return bundle
    }

    private fun saveEditData() {

        previousComments = viewModel.edit_enquiryModel.value?.comments ?: ""
        previousFile = viewModel.edit_enquiryModel.value?.fileName ?: ""
        Log.e("TAG", "saveEditData: previousFile " + previousFile)
        Log.e("TAG", "saveEditData: previousFile isNotEmpty " + previousFile.isEmpty())
        if (previousFile.isEmpty()) {
            visibleChooseFileBt()
        } else {
            hideChooseFileBt(2)
        }
        Log.e("TAG", "saveEditData: comments--> " + viewModel.edit_enquiryModel.value?.comments)
        Log.e("TAG", "saveEditData: comments-*-> " + viewModel.enquiryModel.value?.comments)
        binding.commentsEt.setText(viewModel.edit_enquiryModel.value?.comments.toString())
        binding.charactersRemTv.setText(
            requireActivity().resources.getString(
                R.string.str_you_have_chars_remain,
                ("" + (500 - binding.commentsEt.text.toString().length))
            )
        )
    }

    override fun observer() {
        Log.e("TAG", "observer: isViewCreated "+isViewCreated )
        binding.viewModel = viewModel
//        binding.lifecycleOwner = this
        if (!isViewCreated) {
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
            if(navFlowFrom==Constants.EDIT_SUMMARY || editRequest.isNotEmpty()) {
            }else{
                observe(viewModel.uploadFileLiveData, ::uploadFileResponse)
            }
        }
        isViewCreated = true

    }


    private fun uploadFileResponse(resource: Resource<UploadFileResponseModel?>?) {
        Log.e("TAG", "uploadFileResponse: isApiCalled "+isApiCalled )
        if (isApiCalled) {
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }

            when (resource) {
                is Resource.Success -> {
                    viewModel.edit_enquiryModel.value?.fileName = resource.data?.fileName ?: ""
                    viewModel.edit_enquiryModel.value?.file = file ?: File("")

                    hideChooseFileBt(1)
                }

                is Resource.DataError -> {

                }

                else -> {

                }
            }
        }
        isApiCalled = false

    }

    private fun hideChooseFileBt(type: Int) {
        Log.e("TAG", "hideChooseFileBt: type " + type)
        binding.fileNameTv.setText(viewModel.edit_enquiryModel.value?.fileName)
        binding.chooseFileBt.gone()
        binding.fileCv.visible()
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

    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, Utils.getFileUploadMIMETypes())
        fileManagerResult.launch(intent)
    }

    private val fileManagerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result?.data?.data?.let {
                    val path: String? = FilePath.getPath(requireContext(), it)
                    path?.let { pat ->
                        val file = File(pat)
                        if (file.length()
                                .toFloat() / (1000 * 1000) <= 8.0
                        ) { //checking file size which should be less than 8mb

                            isApiCalled = true
                            this.file = file
                            viewModel.uploadFileApi(file)
                            loader?.show(
                                requireActivity().supportFragmentManager,
                                Constants.LOADER_DIALOG
                            )

                        } else {
                            ErrorUtil.showError(binding.root, "File size must be less than 8MB")
                        }
                    } ?: run {
                        ErrorUtil.showError(binding.root, "Unable to upload the selected file")
                    }
                }
            }
        }


    private var onScopeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.chooseFileBt.performClick()
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
                    binding.chooseFileBt.performClick()
                }

                else -> {
                    requireActivity().showToast("Please enable permission")
                }
            }
        }

    override fun onBackButtonPressed() {
        saveOriginalDataToEditModel()
    }

    private fun saveOriginalDataToEditModel() {
        if (navFlowFrom == Constants.EDIT_SUMMARY) {
            viewModel.edit_enquiryModel.value?.firstname = viewModel.enquiryModel.value?.firstname ?: ""
            viewModel.edit_enquiryModel.value?.email = viewModel.enquiryModel.value?.email ?: ""
            viewModel.edit_enquiryModel.value?.mobileNumber =
                viewModel.enquiryModel.value?.mobileNumber ?: ""
            viewModel.edit_enquiryModel.value?.countryCode =
                viewModel.enquiryModel.value?.countryCode ?: ""
            viewModel.edit_enquiryModel.value?.fullcountryCode =
                viewModel.enquiryModel.value?.fullcountryCode ?: ""

            viewModel.edit_enquiryModel.value?.category =
                viewModel.enquiryModel.value?.category ?: CaseCategoriesModel("", "")
            viewModel.edit_enquiryModel.value?.subCategory =
                viewModel.enquiryModel.value?.subCategory ?: CaseCategoriesModel("", "")

            viewModel.edit_enquiryModel.value?.comments =
                viewModel.enquiryModel.value?.comments ?: ""
            viewModel.edit_enquiryModel.value?.file =
                viewModel.enquiryModel.value?.file ?: File("")
            viewModel.edit_enquiryModel.value?.fileName =
                viewModel.enquiryModel.value?.fileName ?: ""
        }
    }


}