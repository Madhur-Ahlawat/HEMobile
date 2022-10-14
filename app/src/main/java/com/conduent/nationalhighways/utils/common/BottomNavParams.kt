package com.conduent.nationalhighways.utils.common

import android.view.Menu
import androidx.annotation.ColorRes
import com.conduent.nationalhighways.R

class BottomNavParams {
    var menu: Menu? = null
    @ColorRes var activeColor: Int = R.color.bottomActiveColor
    @ColorRes var passiveColor: Int = R.color.bottomPassiveColor
    @ColorRes var pressedColor: Int = R.color.bottomPressedColor
    var itemPadding: Float = 16f
    var itemTextSize : Float = 40f
    var animationDuration: Int = 300
    var endScale: Float = 0.95f
    var startScale: Float = 1f
}