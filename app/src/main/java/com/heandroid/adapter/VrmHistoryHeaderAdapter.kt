package com.heandroid.adapter

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.heandroid.R
import com.heandroid.listener.ItemClickListener
import com.heandroid.model.VehicleResponse
import com.heandroid.model.VehicleTitleAndSub
import com.heandroid.utils.Logg
import kotlinx.android.synthetic.main.item_vrm_history_header_editable_adapter.view.*
import kotlinx.android.synthetic.main.vrm_header_lyt.view.*

class VrmHistoryHeaderAdapter(
    private val mContext: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var vehicleList: List<VehicleTitleAndSub> = mutableListOf()

    private val viewTypeNormal = 1
    private val viewTypeEditable = 2

    fun setList(list: List<VehicleTitleAndSub>?) {
        if (list != null) {

            vehicleList = list
        }
    }

    override fun getItemViewType(position: Int): Int {
        Logg.logging(TAG, " vehicleList.size - 1  ${vehicleList.size - 1} ")
        Logg.logging(TAG, " position  $position ")

        return if (position == (vehicleList.size - 1)) {
            viewTypeEditable
        } else {
            viewTypeNormal

        }

    }

    class VrmHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val TAG = "VrmHeaderAdapter"

        private val vrmTitleTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_title)
        private val vrmNoTxt: AppCompatTextView = itemView.findViewById(R.id.vrm_number)

        fun setView(context: Context, vehicleItem: VehicleTitleAndSub) {

            vrmNoTxt.text = "${vehicleItem.type}"
            vrmTitleTxt.text = "${vehicleItem.title}"

        }

    }


    class VrmHeaderEditableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val TAG = "VrmHeaderEditableViewHolder"

        private val notes: AppCompatTextView = itemView.findViewById(R.id.tvNotes)
        private val editNotes: TextInputEditText = itemView.findViewById(R.id.edt_note)

        fun setView(context: Context, vehicleItem: VehicleTitleAndSub) {

            notes.text = "${vehicleItem.title}"
            editNotes.setText(vehicleItem.type)
            editNotes.imeOptions = EditorInfo.IME_ACTION_DONE

            editNotes.setRawInputType(InputType.TYPE_CLASS_TEXT)



        }

    }

    val TAG = "VrmHistoryHeaderAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Logg.logging(TAG, " viewType  $viewType ")

        var view: View? = null
        when (viewType) {

            viewTypeNormal -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_vrm_history_header_adapter, parent, false)
                return VrmHeaderViewHolder(view!!)

            }
            viewTypeEditable -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_vrm_history_header_editable_adapter, parent, false)
                return VrmHeaderEditableViewHolder(view!!)

            }
            else -> {
                throw IllegalArgumentException("Unsupported layout") // in case populated with a model we don't know how to display.

            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        Logg.logging(TAG, " viewType  onBindViewHolder position $position ")

        when (holder) {

            is VrmHeaderViewHolder -> {

                val vehicleItem = vehicleList[position]
                holder.setView(mContext, vehicleItem)

            }

            is VrmHeaderEditableViewHolder -> {
                val vehicleItem = vehicleList[position]
                holder.setView(mContext, vehicleItem)

                holder.itemView.edt_note.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {

                        Logg.logging(
                            TAG,
                            " viewType  onBindViewHolder afterTextChanged called  ${s.toString()} "
                        )

                        Logg.logging(
                            TAG,
                            " viewType  onBindViewHolder total Text  ${holder.itemView.edt_note.text.toString()} "
                        )

                    }

                })
                holder.itemView.edt_note.setOnKeyListener { v, keyCode, event ->

                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        Logg.logging(
                            TAG,
                            " viewType  onBindViewHolder setOnKeyListener  $position "
                        )

                        Logg.logging(
                            TAG,
                            " viewType  onBindViewHolder setOnKeyListener  ${holder.itemView.edt_note.text.toString()} "
                        )


                    }

                    true

                }

            }


        }

    }

    override fun getItemCount(): Int {
        return vehicleList.size

    }
}
