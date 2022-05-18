package com.heandroid.ui.vehicle.vehiclelist

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.vehicle.*
import com.heandroid.databinding.FragmentVehicleListManagementDetailBinding
import com.heandroid.ui.account.creation.step4.businessaccount.dialog.AddBusinessVehicleListener
import com.heandroid.ui.account.creation.step4.businessaccount.dialog.BusinessAddConfirmDialog
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleListener
import com.heandroid.ui.vehicle.addvehicle.VehicleAddConfirmDialog
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleListManagementDetailFragment :
    BaseFragment<FragmentVehicleListManagementDetailBinding>(), View.OnClickListener,
    AddBusinessVehicleListener {

    private var rowItem: VehicleResponse? = null
    private var isChangeBtn: Boolean = true
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentVehicleListManagementDetailBinding.inflate(inflater, container, false)

    override fun init() {
        rowItem = arguments?.getParcelable(Constants.VEHICLE_ROW_ITEM)

        binding.regNum.setText(rowItem?.plateInfo?.number)
        binding.countryMarker.setText(rowItem?.plateInfo?.country)
        binding.vehicleClass.setText(rowItem?.vehicleInfo?.vehicleClassDesc)
        binding.make.setText(rowItem?.vehicleInfo?.make)
        binding.model.setText(rowItem?.vehicleInfo?.model)
        binding.color.setText(rowItem?.vehicleInfo?.color)
        binding.edtNote.setText(rowItem?.plateInfo?.vehicleComments)

        binding.regNum.isEnabled = false
        binding.countryMarker.isEnabled = false
        binding.vehicleClass.isEnabled = false
        binding.make.isEnabled = false
        binding.model.isEnabled = false
        binding.color.isEnabled = false
        binding.edtNote.isEnabled = false

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.changeVehicle.setOnClickListener(this@VehicleListManagementDetailFragment)
        binding.removeVehicle.setOnClickListener(this@VehicleListManagementDetailFragment)
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.vehicleListManagementEditVal, ::handleVehicleUpdateResponse)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.removeVehicle ->{
                BusinessAddConfirmDialog.newInstance(
                    resources.getString(R.string.add_vehicle),
                    rowItem?.plateInfo?.number.toString(),
                    this@VehicleListManagementDetailFragment)
                    .show(childFragmentManager, VehicleAddConfirmDialog.TAG)
            }

            R.id.changeVehicle -> {
                if (isChangeBtn) {
                    vehicleEditTextState()
                    binding.changeVehicle.text = resources.getString(R.string.add)
                    isChangeBtn = false
                } else {

                    if(TextUtils.isEmpty(binding.make.text.toString())){
                        Toast.makeText(context,  "Please enter vehicle make", Toast.LENGTH_SHORT).show()
                    } else if(TextUtils.isEmpty(binding.model.text.toString())){
                        Toast.makeText(context,  "Please enter vehicle model", Toast.LENGTH_SHORT).show()
                    }else if(TextUtils.isEmpty(binding.color.text.toString())){
                        Toast.makeText(context,  "Please enter vehicle color", Toast.LENGTH_SHORT).show()
                    }
                    else if(TextUtils.isEmpty(binding.vehicleClass.text.toString())){
                        Toast.makeText(context,  "Please enter vehicle class C or D", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val plateInfo = PlateInfoResponseManagement()
                        plateInfo.number = binding.regNum.text.toString()
                        plateInfo.country = binding.countryMarker.text.toString()
                        plateInfo.vehicleGroup = rowItem?.plateInfo?.vehicleGroup.toString()
                        plateInfo.state = rowItem?.plateInfo?.state.toString()
                        plateInfo.type = rowItem?.plateInfo?.type.toString()
                        plateInfo.vehicleComments = rowItem?.plateInfo?.vehicleComments.toString()

                        val vehicleInfo = VehicleInfoResponseManagement()
                        vehicleInfo.make = binding.make.text.toString()
                        vehicleInfo.model = binding.model.text.toString()
                        vehicleInfo.year = rowItem?.vehicleInfo?.year
                        vehicleInfo.rowId = rowItem?.vehicleInfo?.rowId
                        vehicleInfo.typeDescription = rowItem?.vehicleInfo?.typeDescription
                        vehicleInfo.vehicleClassDesc = VehicleClassTypeConverter.toClassCode(binding.vehicleClass.text.toString())
                        vehicleInfo.color = binding.color.text.toString()

                        loader?.show(requireActivity().supportFragmentManager, "")

                        val request = VehicleListManagementEditRequest(plateInfo, vehicleInfo)
                        vehicleMgmtViewModel.updateVehicleVRMData(request)
                    }
                }
            }
        }
    }

    private fun vehicleEditTextState() {
        binding.apply {
            vehicleClassAmendment()

            make.isEnabled = true
            make.isFocusable = true
            make.isFocusableInTouchMode = true

            model.isEnabled = true
            model.isFocusable = true
            model.isFocusableInTouchMode = true

            color.isEnabled = true
            color.isFocusable = true
            color.isFocusableInTouchMode = true

            edtNote.isEnabled = true
            edtNote.isFocusable = true
            edtNote.isFocusableInTouchMode = true
        }
    }

    private fun vehicleClassAmendment() {
        binding.apply {
            if (rowItem?.vehicleInfo?.vehicleClassDesc == "D") {
                vehicleClass.isEnabled = true
                vehicleClass.isFocusable = true
                vehicleClass.isFocusableInTouchMode = true
            } else {
                vehicleClass.isEnabled = false
                vehicleClass.isFocusable = false
                vehicleClass.isFocusableInTouchMode = false
            }
        }
    }

    private fun handleVehicleUpdateResponse(resource: Resource<String?>?) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }

        when (resource) {
            is Resource.Success -> {
                Toast.makeText(context,  "Vehicle details updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            is Resource.DataError -> {
            }
            else -> {

            }
        }
    }

    override fun onAddClick() {
        findNavController().popBackStack()
    }
}