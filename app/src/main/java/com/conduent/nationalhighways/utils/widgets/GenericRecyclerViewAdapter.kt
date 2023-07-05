package com.conduent.nationalhighways.utils.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class GenericRecyclerViewAdapter<T>(
    val getViewLayout: (item: T) -> Int,
    areItemsSame: (oldItem: T, newItem: T) -> Boolean,
    areItemContentsEqual: (oldItem: T, newItem: T) -> Boolean = { old, new -> old == new },
    private val onItemClick: ((View, T) -> Unit)? = null,
    private val onBind: ((T, ViewDataBinding, position: Int) -> Unit)? = null,
) : ListAdapter<T, GenericRecyclerViewAdapter.DataBindingViewHolder<T>>(DiffCallback(areItemsSame, areItemContentsEqual)) {

    constructor(
        getViewLayout: (item: T) -> Int,
        areItemsSame: (oldItem: T, newItem: T) -> Boolean,
        areItemContentsEqual: (oldItem: T, newItem: T) -> Boolean = { old, new -> old == new },
        onItemClick: OnItemClickListener<T>,
        onBind: OnBindListener<T>,
    ) : this(
        getViewLayout, areItemsSame, areItemContentsEqual, onItemClick::onItemClick,
        onBind::onBind,
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)
         val binding= DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater, viewType, parent,
            false
        )
        return DataBindingViewHolder(binding)
    }

    override fun getItemViewType(position: Int) = getViewLayout(getItem(position))

    interface OnItemClickListener<T> {
        fun onItemClick(view: View, item: T)
    }

    interface OnBindListener<T> {
        fun onBind(item: T, binding: ViewDataBinding, position: Int)
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<T>, position: Int) =
        holder.bind(getItem(position), onItemClick, onBind, position)

    class DiffCallback<T>(
        val sameItems: (oldItem: T, newItem: T) -> Boolean,
        val sameItemContents: (oldItem: T, newItem: T) -> Boolean,
    ) : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) =
            sameItems(oldItem, newItem)
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return sameItemContents(oldItem, newItem)
        }
    }
    class DataBindingViewHolder<T>(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: T,
            onItemClick: ((View, T, position: Int) -> Unit)? = null,
            onBind: ((T, ViewDataBinding, position: Int) -> Unit)?,
            position: Int,
        ) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
            if (onItemClick != null) {
                binding.root.setOnClickListener {
                    onItemClick(it, item, position)
                }
            }
            if (onBind != null) {
                onBind(item, binding, position)
            }
        }

        fun bind(
            item: T,
            onItemClick: ((View, T) -> Unit)? = null,
            onBind: ((T, ViewDataBinding, position: Int) -> Unit)?,
            position: Int,
        ) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
            if (onItemClick != null) {
                binding.root.setOnClickListener {
                    onItemClick(it, item)
                }
            }
            if (onBind != null) {
                onBind(item, binding, position)
            }
        }
    }
    object BR {
        var _all = 0
        var controller = 1
        var expandableTextDetail = 2
        var item = 3
        var showLoading = 4
        var vm = 5
    }
}
