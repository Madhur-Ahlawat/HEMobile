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
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.vehicle.DeleteVehicleRequest
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleListBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.addvehicle.dialog.AddVehicleDialog
import com.heandroid.ui.vehicle.addvehicle.dialog.AddVehicleListener
import com.heandroid.ui.vehicle.crossinghistory.dialog.DownloadFilterDialogListener
import com.heandroid.ui.vehicle.crossinghistory.dialog.DownloadFormatSelectionFilterDialog
import com.heandroid.ui.vehicle.vehiclelist.adapter.VehicleListAdapter
import com.heandroid.ui.vehicle.vehiclelist.dialog.ItemClickListener
import com.heandroid.ui.vehicle.vehiclelist.dialog.RemoveVehicleDialog
import com.heandroid.ui.vehicle.vehiclelist.dialog.RemoveVehicleListener
import com.heandroid.utils.StorageHelper
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.visible
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
    private var isBusinessAccount = false

    @Inject
    lateinit var sessionManager: SessionManager
    private var selectionType: String = Constants.PDF
    private var isDownload = false

    private var startIndex: Long = 1
    private val count: Long = Constants.ITEM_COUNT
    private var totalCount: Int = 0
    private var isLoading = false
    private var isFirstTime = true

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentVehicleListBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = VehicleListAdapter(requireContext(), this, isBusinessAccount)
    }

    override fun init() {
        binding.rvVehicleList.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvVehicleList.adapter = mAdapter

        val buttonVisibility = arguments?.getBoolean(
            Constants.FROM_DASHBOARD_TO_VEHICLE_LIST,
            false
        )

        if (buttonVisibility == true) {
            binding.addVehicleBtn.gone()
            binding.removeVehicleBtn.gone()
        }
        sessionManager.fetchAccountType()?.let {
            if (it == Constants.BUSINESS_ACCOUNT) {
                isBusinessAccount = true
                binding.download.visible()
            }
        }

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
        vehicleMgmtViewModel.getVehicleInformationApi(startIndex.toString(), count.toString())
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
                        requireContext().showToast("No vehicles to download")
                    } else {
                        val dialog = DownloadFormatSelectionFilterDialog()
                        dialog.setListener(this@VehicleListFragment)
                        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                        dialog.show(
                            requireActivity().supportFragmentManager,
                            Constants.DOWNLOAD_FORMAT_SELECTION_DIALOG
                        )
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
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                requireContext().showToast("vehicle deleted successfully")
                startIndex = 1
                totalCount = 0
                mList.clear()
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
        binding.rvVehicleList.visible()
        binding.progressBar.gone()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    val response = resource.data
                    totalCount = response.size
                    mList.addAll(response)
                    isLoading = false
                    mAdapter.setList(mList)
                    binding.rvVehicleList.adapter?.notifyDataSetChanged()

                    if (mList.size == 0) {
                        binding.rvVehicleList.gone()
                        binding.tvNoVehicles.visible()
                        binding.progressBar.gone()
                    } else {
                        binding.rvVehicleList.visible()
                        binding.progressBar.gone()
                        binding.tvNoVehicles.gone()
                    }
                    endlessScroll()
                }
            }
            is Resource.DataError -> {
                if (resource.errorModel?.errorCode != Constants.NO_DATA_FOR_GIVEN_INDEX) {
                    binding.rvVehicleList.gone()
                    binding.progressBar.gone()
                    binding.removeVehicleBtn.gone()
                    binding.tvNoVehicles.visible()
                    binding.download.gone()
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }
            else -> {
            }
        }
    }

    private fun handleDownloadVehicleListData(resource: Resource<ResponseBody?>?) {
        if (isDownload) {
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

    private fun endlessScroll() {
        if (isFirstTime) {
            isFirstTime = false
            binding.rvVehicleList.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        if (!isLoading) {
                            if (linearLayoutManager != null &&
                                linearLayoutManager.findLastCompletelyVisibleItemPosition() ==
                                (mList.size - 1) && totalCount > count - 1
                            ) {
                                startIndex += count
                                isLoading = true
                                binding.progressBar.visible()
                                vehicleMgmtViewModel.getVehicleInformationApi(
                                    startIndex.toString(),
                                    count.toString()
                                )
                            }
                        }
                    }
                }

            })
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse?, pos: Int) {}

    override fun onItemClick(details: VehicleResponse?, pos: Int) {
        details?.isExpanded = details?.isExpanded != true
        mList[pos]?.isExpanded = details?.isExpanded
        mAdapter.notifyItemChanged(pos)
    }

    override fun onAddClick(details: VehicleResponse) {
        val bundle = Bundle().apply {
            putParcelable(Constants.DATA, details)
            putInt(Constants.VEHICLE_SCREEN_KEY, Constants.VEHICLE_SCREEN_TYPE_LIST)
            putParcelable(
                Constants.CREATE_ACCOUNT_DATA,
                arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
            )
        }
        findNavController().navigate(R.id.addVehicleDetailsFragment, bundle)
    }

    override fun onRemoveClick(selectedVehicleList: List<String?>) {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        vehicleMgmtViewModel.deleteVehicleApi(selectedVehicleList)
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

    override fun onCancelClicked() {}

    private fun callCoroutines(body: ResponseBody) {
        lifecycleScope.launch(Dispatchers.IO) {

            val ret = async {
                return@async StorageHelper.writeResponseBodyToDisk(
                    requireActivity(),
                    selectionType,
                    body
                )
            }.await()

            if (ret) {
                withContext(Dispatchers.Main) {
                    if (loader?.isVisible == true) {
                        loader?.dismiss()
                    }
                    requireActivity().showToast("Document downloaded successfully")
                }
            } else {
                withContext(Dispatchers.Main) {
                    if (loader?.isVisible == true) {
                        loader?.dismiss()
                    }
                    requireActivity().showToast("Document download failed")
                }
            }
        }
    }
}