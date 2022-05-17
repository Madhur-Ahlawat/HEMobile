package com.heandroid.ui.account.creation.step4.ukvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentCreateAccountVehicleDetailsBinding
import com.heandroid.ui.account.creation.step4.CreateAccountVehicleViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountVehicleListDetailsFragment : BaseFragment<FragmentCreateAccountVehicleDetailsBinding>(), View.OnClickListener,
    ItemClickListener {

    private var mList: ArrayList<VehicleResponse?> = ArrayList()
    private lateinit var mAdapter: CreateAccountVehicleListDetailsAdapter
    private var loader: LoaderDialog? = null
    private var isAccountVehicle: Boolean? = false
    private var pos: Int = 0
    private val createAccVehicleViewModel: CreateAccountVehicleViewModel by viewModels()
    private var isNonUKVehicleUpdating: Boolean? = false
    private var currentPos : Int =0

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountVehicleDetailsBinding.inflate(inflater, container, false)

    override fun init() {
        val buttonVisibility = arguments?.getBoolean(Constants.DATA, false) == true
        isAccountVehicle = arguments?.getBoolean("IsAccountVehicle")
        isNonUKVehicleUpdating = arguments?.getBoolean("isNonUKVehicleUpdating")

        if (buttonVisibility) {
            binding.addVehicleBtn.gone()
            binding.removeVehicleBtn.gone()
        }

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.addVehicleBtn.setOnClickListener(this)
        binding.removeVehicleBtn.setOnClickListener(this)

        if (isAccountVehicle == true || isNonUKVehicleUpdating == true) {
            hitCreateAccVehicleList()
            binding.addVehicleBtn.text = resources.getString(R.string.str_confirm)
            binding.removeVehicleBtn.text = resources.getString(R.string.not_vehicle)
        }
    }

    private fun hitCreateAccVehicleList() {
        loader?.show(requireActivity().supportFragmentManager, "")
        createAccVehicleViewModel.getVehicleData(VehicleHelper.createAccountList?.get(0)?.plateInfo?.number, Constants.AGENCY_ID)

        /*
        // TODO - Will be used in the future for multiple vehicle add
         CoroutineScope(Dispatchers.Main).launch {
               coroutineScope {
                   withContext(Dispatchers.IO){
                       async {
                           loadVehicleData()
                       }.await()
                   }
                   setVehicleListAdapter(mList)

               }
           } */

    }

    /*
      // TODO - Will be used in the future for multiple vehicle add
     suspend fun loadVehicleData() {
         for (i in VehicleHelper.createAccountList?.indices!!) {

             if (VehicleHelper.createAccountList?.get(i)?.plateInfo?.country == "UK") {
                 loader?.show(requireActivity().supportFragmentManager, "")
                 currentPos = i
                 CoroutineScope(Dispatchers.IO).async {
                     createAccVehicleViewModel.getVehicleData(VehicleHelper.createAccountList?.get(i)?.plateInfo?.number, Constants.AGENCY_ID)
                 }.join()

             } else {
                 val vehicleRes = VehicleHelper.createAccountList?.get(i)
                 if(!mList.contains(vehicleRes)) mList.add(vehicleRes)

             }
         }
     } */


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addVehicleBtn -> {
                when {
                    isAccountVehicle == true -> {

                        val bundle =  Bundle()
                        bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA,arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA))
                        findNavController().navigate(R.id.action_createAccVehicleList_to_choosePaymentFragment,bundle)
                    }
                    isNonUKVehicleUpdating == true -> {
                        val bundle =  Bundle()
                        bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA,arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA))
                        findNavController().navigate(R.id.action_NonUkDropDownVehicleListFragment_to_choosePaymentFragment,bundle)
                    }
                    else -> {
                    }
                }
            }

            R.id.removeVehicleBtn -> {
                if(isAccountVehicle == true){
                    val bundle =  Bundle()
                    bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA,arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA))
                    findNavController().navigate(R.id.action_createAccVehicleList_to_findYourVehicleFragment,bundle)
                }else {
                }
            }
        }
    }

    override fun observer() {
        observe(createAccVehicleViewModel.findVehicleLiveData, ::handleCreateAccVehicleResponse)
    }

    private fun handleCreateAccVehicleResponse(resource: Resource<VehicleInfoDetails?>?) {
        loader?.dismiss()

        when(resource) {
            is Resource.Success -> {

                val plateInfo = resource.data?.retrievePlateInfoDetails

                if (plateInfo != null) {
                    val vehicleInfoRes = VehicleInfoResponse(
                        plateInfo.vehicleMake, plateInfo.vehicleModel, "", "", "",
                        "", plateInfo.vehicleColor, plateInfo.vehicleClass, ""
                    )

                    val plateInfoRes = PlateInfoResponse(plateInfo.plateNumber.toString())

                    val vehicleRes = VehicleResponse(PlateInfoResponse(), plateInfoRes,vehicleInfoRes)
                    if(!mList.contains(vehicleRes))
                        mList.add(vehicleRes)
                    setVehicleListAdapter(mList)

                    //    val bundle =  Bundle()
                    //    bundle.putBoolean("IsAccountVehicle", isAccountVehicle == true)
                    //    findNavController().navigate(R.id.action_makePaymentAddVehicleFragment_to_CreateAccountVehicleDetailsFragment, bundle)
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
        }
    }


    private fun setVehicleListAdapter(mList: ArrayList<VehicleResponse?>) {
        this.mList = mList
        mAdapter = CreateAccountVehicleListDetailsAdapter(requireContext(), this)
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
}