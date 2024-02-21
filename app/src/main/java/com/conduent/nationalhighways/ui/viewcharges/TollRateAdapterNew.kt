package com.conduent.nationalhighways.ui.viewcharges

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.tollrates.TollRatesRespNew
import com.conduent.nationalhighways.databinding.ItemViewChargesBinding
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible

class TollRateAdapterNew(private val context: Context?, var list: List<TollRatesRespNew?>?) :
    RecyclerView.Adapter<TollRateAdapterNew.VehicleViewHolder>() {

    override fun getItemCount(): Int = list?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder =
        VehicleViewHolder(
            ItemViewChargesBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TollRateAdapterNew.VehicleViewHolder, position: Int) {
        var item = list?.get(position)

        if(position==0){
            holder.binding?.run {
                labelIfYouHaveAccoun.visible()
                labelTypeOfVehicle.visible()
                labelOneOffPayment.visible()
                labelVehicleClass.visible()
                viewFooter.visible()
            }
        }
        else if(position>0 && position<list!!.size){
            holder.binding?.run {
                labelIfYouHaveAccoun.gone()
                labelTypeOfVehicle.gone()
                labelOneOffPayment.gone()
                labelVehicleClass.gone()
                viewFooter.gone()
            }
        }
        else if(position==list!!.size){
            holder.binding?.run {
                labelIfYouHaveAccoun.gone()
                labelTypeOfVehicle.gone()
                labelOneOffPayment.gone()
                labelVehicleClass.gone()
                viewFooter.gone()

            }
        }
        if (position==3){
            holder.binding.view.visibility= View.GONE
        }else{
            holder.binding.view.visibility= View.VISIBLE
        }

        holder.binding.apply {
            data = list?.get(position)
            when(data?.vehicleType){
                "Motorcycle"->{
                    val uri =
                        "@drawable/ic_motorcycle"


                    val imageResource: Int =
                        context?.getResources()!!.getIdentifier(uri, null, context.getPackageName())

                    val res: Drawable = context.getResources().getDrawable(imageResource)
                    ivTypeOfVehicle.setImageDrawable(res)
                }
                "Car"->{
                    val uri =
                        "@drawable/ic_car"

                    val imageResource: Int =
                        context?.getResources()!!.getIdentifier(uri, null, context.getPackageName())

                    val res: Drawable = context.getResources().getDrawable(imageResource)
                    ivTypeOfVehicle.setImageDrawable(res)
                }
                "Bus"->{
                    val uri =
                        "@drawable/ic_bus"

                    val imageResource: Int =
                        context?.getResources()!!.getIdentifier(uri, null, context.getPackageName())

                    val res: Drawable = context.getResources().getDrawable(imageResource)
                    ivTypeOfVehicle.setImageDrawable(res)
                }
                "Truck"->{
                    val uri =
                        "@drawable/ic_truck_black"

                    val imageResource: Int =
                        context?.getResources()!!.getIdentifier(uri, null, context.getPackageName())

                    val res: Drawable = context.getResources().getDrawable(imageResource)
                    ivTypeOfVehicle.setImageDrawable(res)
                }
            }
            executePendingBindings()
        }
    }

    inner class VehicleViewHolder(val binding: ItemViewChargesBinding) : RecyclerView.ViewHolder(binding.root){

    }




}
