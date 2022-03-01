package com.heandroid.ui.vehicle.vehiclelist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.DialogRemoveVehicleBinding
import com.heandroid.ui.base.BaseDialog
import kotlinx.coroutines.launch

class RemoveVehicleDialog : BaseDialog<DialogRemoveVehicleBinding>() {

    private lateinit var removeAdapter : RemoveVehicleDialogAdapter
    private var selectedVehicleList = mutableListOf<String>()

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogRemoveVehicleBinding.inflate(inflater, container, false)

    override fun init() {
//        setBtnNormal()
        removeAdapter = RemoveVehicleDialogAdapter(this)
        binding.removeRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = removeAdapter
        }
        removeAdapter.setList(vehicleList)
    }

    override fun initCtrl() {
        binding.btnRemove.setOnClickListener {
            dismiss()
            mListener?.onRemoveClick(selectedVehicleList)
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    fun addRemoveVehicleData(id : String){
        if (selectedVehicleList.contains(id)){
            selectedVehicleList.remove(id)
        } else {
            selectedVehicleList.add(id)
        }
    }

    override fun observer() {
        lifecycleScope.launch {
//            observe(viewModel.logout,::handleLogout)
        }
    }

    companion object {
        var vehicleList : ArrayList<VehicleResponse?> = ArrayList()
        var mListener: RemoveVehicleListener? = null

        fun newInstance(
            list : ArrayList<VehicleResponse?>,
            listener: RemoveVehicleListener
        ): RemoveVehicleDialog {
            mListener = listener
            vehicleList = list
            return RemoveVehicleDialog()
        }
    }

    private fun setBtnActivated() {
        binding.btnRemove.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.btn_color
            )
        )
        binding.btnRemove.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.btnRemove.isEnabled = true
    }

    private fun setBtnNormal() {
        binding.btnRemove.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.hint_color
            )
        )
        binding.btnRemove.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )
        binding.btnRemove.isEnabled = false
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.dialog_background
            )
        )
    }
}