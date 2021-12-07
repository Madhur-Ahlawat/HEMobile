package com.heandroid.utils

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

object DeviceMatrix {

    const val SCREENSIZE_SMALL = "small"
    const val SCREENSIZE_LARGE = "large"

    const val DEFAULT_DEVICE_RATIO = 1.78f

    var width: Int = 0
    var height: Int = 0
    var screenSize = SCREENSIZE_SMALL
    var deviceRatio = 0f
    var deviceInch = 0f


    fun initDeviceMetrics(context: Context) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val point = Point()
        display.getRealSize(point)

        val deviceWidth = point.x
        val deviceHeight = point.y

        if (width != deviceWidth) {

            val dens = context.resources.displayMetrics.densityDpi

            var wi = deviceWidth.toDouble() / dens.toDouble()
            var hi = deviceHeight.toDouble() / dens.toDouble()
            var width = deviceWidth
            var height = deviceHeight

            if (height < width) {

                wi = deviceHeight.toDouble() / dens.toDouble()
                hi = deviceWidth.toDouble() / dens.toDouble()
                width = deviceHeight
                height = deviceWidth

            }

            val x = Math.pow(wi, 2.0)
            val y = Math.pow(hi, 2.0)
            val screenInches = Math.sqrt((x.plus(y))).toFloat()

            screenSize = if (screenInches >= 6.5) {
                SCREENSIZE_LARGE
            } else {
                SCREENSIZE_SMALL
            }

            DeviceMatrix.width = width
            DeviceMatrix.height = height
            deviceInch = screenInches
            deviceRatio = (width / height).toFloat()

        }


    }
}