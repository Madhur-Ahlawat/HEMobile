package com.conduent.nationalhighways.utils.widgets

import android.graphics.Rect
import android.view.View
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridItemDecoratorRecyclerView
/**
 * constructor
 * @param margin desirable margin size in px between the views in the recyclerView
 * @param columns number of columns of the RecyclerView
 */(
    @param:IntRange(from = 0) private val margin: Int, @param:IntRange(
        from = 0
    ) private val columns: Int
) : ItemDecoration() {
    /**
     * Set different margins for the items inside the recyclerView: no top margin for the first row
     * and no left margin for the first column.
     */
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
        //set right margin to all
        outRect.right = margin
        //set bottom margin to all
        outRect.bottom = margin
        //we only add top margin to the first row
        if (position < columns) {
            outRect.left = margin
        }
        try{
            if (position % columns == 0) {
                outRect.left = margin
            }
        }
        catch (e:java.lang.Exception){}
        //add left margin only to the first column

    }
}