package com.heandroid.ui.vehicle.vehiclelist

import android.app.Activity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.vehicle.DeleteVehicleRequest
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleListBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleListener
import com.heandroid.ui.vehicle.crossinghistory.DownloadFilterDialogListener
import com.heandroid.ui.vehicle.crossinghistory.DownloadFormatSelectionFilterDialog
import com.heandroid.utils.StorageHelper
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import javax.inject.Inject


@AndroidEntryPoint
class VehicleListFragment : BaseFragment<FragmentVehicleListBinding>(), View.OnClickListener,
    ItemClickListener, AddVehicleListener, RemoveVehicleListener, DownloadFilterDialogListener {

    private var mList: ArrayList<VehicleResponse?> = ArrayList()
    private lateinit var mAdapter: VehicleListAdapter
    private var loader: LoaderDialog? = null
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var isAccountVehicle: Boolean? = false
    private var pos: Int = 0
    private var isBusinessAccount = false
    @Inject
    lateinit var sessionManager: SessionManager
    private var currentPos: Int = 0
    private var selectionType: String = Constants.PDF
    private var isDownload = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentVehicleListBinding.inflate(inflater, container, false)

    override fun init() {

        val buttonVisibility = arguments?.getBoolean(Constants.DATA, false) == true
        isAccountVehicle = arguments?.getBoolean("IsAccountVehicle")

        if (buttonVisibility) {
            binding.addVehicleBtn.gone()
            binding.removeVehicleBtn.gone()
        }
        sessionManager.fetchAccountType()?.let {
            if (it == Constants.BUSINESS_ACCOUNT){
                isBusinessAccount = true
            }
        }

        val content = SpannableString("Download")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.download.text = content

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.addVehicleBtn.setOnClickListener(this)
        binding.removeVehicleBtn.setOnClickListener(this)
        binding.download.setOnClickListener(this)

        binding.addVehicleBtn.text = resources.getString(R.string.str_add_another_vehicle)
        binding.removeVehicleBtn.text = resources.getString(R.string.str_remove_vehicle)
        getVehicleListData()
    }

    private fun getVehicleListData() {
        loader?.show(requireActivity().supportFragmentManager, "")
        vehicleMgmtViewModel.getVehicleInformationApi()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addVehicleBtn -> {

                AddVehicleDialog.newInstance(
                    getString(R.string.str_title),
                    getString(R.string.str_sub_title),
                    this
                ).show(childFragmentManager, AddVehicleDialog.TAG)
            }

            R.id.download -> {
                if (!StorageHelper.checkStoragePermissions(requireActivity())) {
                    StorageHelper.requestStoragePermission(
                        requireActivity(),
                        onScopeResultLaucher = onScopeResultLauncher,
                        onPermissionlaucher = onPermissionLauncher
                    )
                } else {
                    if (mList.isEmpty()) {
                        requireContext().showToast("No vehicle to download")
                    } else {
                        val dialog = DownloadFormatSelectionFilterDialog()
                        dialog.setListener(this@VehicleListFragment)
                        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                        dialog.show(requireActivity().supportFragmentManager, "")
                    }
                }
            }

            R.id.removeVehicleBtn -> {
                RemoveVehicleDialog.newInstance(
                    mList,
                    this
                ).show(childFragmentManager, AddVehicleDialog.TAG)
            }
        }
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.vehicleListVal, ::handleVehicleListData)
        observe(vehicleMgmtViewModel.deleteVehicleApiVal, ::handleDeleteVehicle)
        observe(vehicleMgmtViewModel.vehicleVRMDownloadVal, ::handleDownloadVehicleListData)
    }

    private fun handleDeleteVehicle(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()

        when (resource) {
            is Resource.Success -> {
                requireContext().showToast("vehicle deleted successfully")
                getVehicleListData()
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }

    }

    private fun handleVehicleListData(resource: Resource<List<VehicleResponse?>?>?) {
        loader?.dismiss()

        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (!it.isNullOrEmpty()) {
                        mList.clear()
                        mList.addAll(it)
                        setVehicleListAdapter(mList)
                    }
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun handleDownloadVehicleListData(resource: Resource<ResponseBody?>?) {

        if(isDownload){
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        callCoroutines(resource.data)
                    }
                }
                is Resource.DataError -> {
                    requireContext().showToast("failed to download the document")
                }
                else -> {

                }
            }
            isDownload = false
        }

    }

    private fun setVehicleListAdapter(mList: ArrayList<VehicleResponse?>) {
        this.mList = mList
        mAdapter = VehicleListAdapter(requireContext(), this, isBusinessAccount)
        mAdapter.setList(mList)
        binding.rvVehicleList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse?, pos: Int) {

    }

    override fun onItemClick(details: VehicleResponse?, pos: Int) {

        details?.isExpanded = details?.isExpanded != true
        mList[pos]?.isExpanded = details?.isExpanded

        mAdapter.notifyItemChanged(pos)
    }

    override fun onAddClick(details: VehicleResponse) {
        val bundle = Bundle().apply {
            putParcelable(Constants.DATA, details)
            putParcelable(
                Constants.CREATE_ACCOUNT_DATA,
                arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
            )
        }
        findNavController().navigate(R.id.addVehicleDetailsFragment, bundle)
    }

    override fun onRemoveClick(selectedVehicleList: List<String?>) {
        loader?.show(requireActivity().supportFragmentManager, "")
        vehicleMgmtViewModel.deleteVehicleApi(DeleteVehicleRequest(selectedVehicleList[0]))
    }

    private var onScopeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.download.performClick()
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
                    binding.download.performClick()
                }
                else -> {
                    requireActivity().showToast("Please enable permission to download")
                }
            }
        }

    override fun onOkClickedListener(type: String) {
        selectionType = type
        isDownload = true
        requireContext().showToast("Document download started")
        vehicleMgmtViewModel.downloadVehicleList(selectionType)
    }

    override fun onCancelClicked() {
    }

    private fun callCoroutines(body: ResponseBody) {
        lifecycleScope.launch(Dispatchers.IO) {

            val ret = async {
                return@async StorageHelper.writeResponseBodyToDisk(requireActivity(), selectionType, body)
            }.await()

            if (ret) {
                withContext(Dispatchers.Main) {
                    loader?.dismiss()
                    requireActivity().showToast("Document downloaded successfully")
                }
            } else {
                withContext(Dispatchers.Main) {
                    loader?.dismiss()
                    requireActivity().showToast("Document download failed")
                }
            }
        }
    }
}