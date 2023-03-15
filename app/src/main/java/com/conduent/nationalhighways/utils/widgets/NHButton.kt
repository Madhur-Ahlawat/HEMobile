package com.conduent.nationalhighways.utils.widgets

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.conduent.apollo.util.CMGradientDrawableBuilder
import com.conduent.nationalhighways.R

/**
 * Created by Mohammed Sameer Ahmad .
 */
open class NHButton : AppCompatButton {

    internal var isSelected: Boolean = true
    open var isButtonEnabled: Boolean = true
    internal var shape: Int = 1
    internal var borderWidth = resources.getDimension(R.dimen.apollo_zero)
    internal var radius = resources.getDimension(R.dimen.apollo_zero)
    internal var borderColor: Int = 0
    internal var backgroundColor: Int = 0
    internal var backgroundColorPressed: Int = 0
    internal var gradientStartColor: Int = 0
    internal var gradientEndColor: Int = 0
    internal var gradientAngle: Int = 0

    internal var context: Context? = null
    internal var attributeSet: AttributeSet? = null



    constructor(context: Context) : super(context) {
        this.context = context
        setup()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, R.attr.borderlessButtonStyle) {

        this.context = context
        this.attributeSet = attrs
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NHButtonStyle, 0, 0)
        isSelected = typedArray.getBoolean(R.styleable.CMButton_isPrimary, isSelected)
        isButtonEnabled = typedArray.getBoolean(R.styleable.CMButton_isEnabled, isButtonEnabled)
        shape = typedArray.getInt(R.styleable.CMButton_shape, shape)
        borderWidth = typedArray.getDimension(R.styleable.CMButton_borderWidth_, borderWidth)
        radius = typedArray.getDimension(R.styleable.CMButton_radius, radius)
        borderColor = typedArray.getColor(R.styleable.CMButton_borderColor, ContextCompat.getColor(context, R.color.buttonColorPrimary))
        backgroundColor = typedArray.getColor(R.styleable.CMButton_backgroundColor, ContextCompat.getColor(context, R.color.buttonColorPrimary))
        backgroundColorPressed = typedArray.getColor(R.styleable.CMButton_backgroundColorPressed, backgroundColor)
        gradientStartColor = typedArray.getColor(R.styleable.CMButton_gradientStartColor, backgroundColor)
        gradientEndColor = typedArray.getColor(R.styleable.CMButton_gradientEndColor, backgroundColor)
        gradientAngle = typedArray.getColor(R.styleable.CMButton_gradientAngle, gradientAngle)
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, R.attr.borderlessButtonStyle) {
        this.context = context
        this.attributeSet = attrs
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CMButton, 0, 0)
        isSelected = typedArray.getBoolean(R.styleable.CMButton_isPrimary, false)
        isButtonEnabled = typedArray.getBoolean(R.styleable.CMButton_isEnabled, isButtonEnabled)
        shape = typedArray.getInt(R.styleable.CMButton_shape, shape)
        borderWidth = typedArray.getFloat(R.styleable.CMButton_borderWidth_, 1.0f)
        radius = typedArray.getFloat(R.styleable.CMButton_radius, 5.0f)
        borderColor = typedArray.getColor(R.styleable.CMButton_borderColor, ContextCompat.getColor(context, R.color.buttonColorPrimary))
        backgroundColor = typedArray.getColor(R.styleable.CMButton_borderColor, ContextCompat.getColor(context, R.color.buttonColorPrimary))
        backgroundColorPressed = typedArray.getColor(R.styleable.CMButton_backgroundColorPressed, backgroundColor)
        setup()
    }

    companion object {
        //var backgroundColor: Int? = null
        val SHAPE_RECTANGLE = 1
        val SHAPE_OVAL = 2
    }

    override fun setSelected(isSelected: Boolean) {
        this.isSelected = isSelected
        setup()
    }


    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (enabled) this.alpha = 1.0f
        else this.alpha = 0.5f
    }


    private fun setup() {

        setShape()
        //setBackgroundResource(if (isSelected) R.drawable.primary_button else R.drawable.secondary_button)
        val textColorAttrs = intArrayOf(android.R.attr.textColor)
        val textColorArray = context?.obtainStyledAttributes(attributeSet, textColorAttrs)
        val color = textColorArray?.getColor(0, if (isSelected) resources.getColor(R.color.white) else resources.getColor(R.color.buttonColorPrimary))
        color?.let { setTextColor(it) }
        val textSizeAttr = intArrayOf(android.R.attr.textSize)
        val textSizeTypedArray = context?.obtainStyledAttributes(attributeSet, textSizeAttr)
        //val textSize = textSizeTypedArray.getDimension(0, resources.getDimension(R.dimen.apollo_primary_button_text_size))
        var textSize = resources.getDimension(R.dimen.apollo_primary_button_text_size)
        attributeSet?.let {
            if (context != null)
                textSize = readDimentionAttr(context!!, it, android.R.attr.textSize, resources.getDimension(R.dimen.apollo_primary_button_text_size))
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        if (!isButtonEnabled) {
            this.alpha = 0.5f
            this.isEnabled = false
        }
        letterSpacing = 0.115f

        if (!isButtonEnabled) {
            this.alpha = 0.5f
            this.isEnabled = false
        }
        textColorArray?.recycle()
        textSizeTypedArray?.recycle()
    }




    private fun readDimentionAttr(context: Context, attributeSet: AttributeSet, attribute: Int, defaultValue: Float): Float {
        val arrAttr = intArrayOf(attribute)
        val typedArray = context.obtainStyledAttributes(attributeSet, arrAttr)
        val retVal = typedArray.getDimension(0, defaultValue)
        typedArray.recycle()
        return retVal
    }

    private fun setShape() {

        var gdNormalState: GradientDrawable? = null

        when (gradientAngle) {
            1 -> {
                gdNormalState = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(gradientStartColor, gradientEndColor))
            }
            2 -> {
                gdNormalState = GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    intArrayOf(gradientStartColor, gradientEndColor))
            }
            3 -> {
                gdNormalState = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(gradientStartColor, gradientEndColor))
            }
            4 -> {
                gdNormalState = GradientDrawable(
                    GradientDrawable.Orientation.RIGHT_LEFT,
                    intArrayOf(gradientStartColor, gradientEndColor))
            }
            // TO-DO: check and remove
            0 -> {
                gdNormalState = GradientDrawable()
                gdNormalState?.setColor(backgroundColor)
            }
        }

        //gDNormal?.setColor(keyBackgroundColor)
        gdNormalState?.shape = if (shape == 1) GradientDrawable.RECTANGLE else GradientDrawable.OVAL
        gdNormalState?.cornerRadius = radius
        gdNormalState?.setStroke(borderWidth.toInt(), borderColor)

        // TO-DO: Create a helper for constructing Drawable
        val gDPressed = CMGradientDrawableBuilder()
            .setColor(backgroundColorPressed)
            .setShape(if (shape == 1) GradientDrawable.RECTANGLE else GradientDrawable.OVAL)
            .setCornerRadius(radius)
            .setStroke(borderWidth.toInt(), borderColor)
            .build()
//
//        gDPressed.setColor(keyBackgroundColorPressed)
//        gDPressed.shape = if (shape == 1) GradientDrawable.RECTANGLE else GradientDrawable.OVAL
//        gDPressed.cornerRadius = radius
//        gDPressed.setStroke(borderWidth.toInt(), borderColor)


        val res = StateListDrawable()
        res.addState(intArrayOf(android.R.attr.state_pressed), gDPressed)
        res.addState(intArrayOf(), gdNormalState)

        (this as AppCompatButton).setBackground(res)


    }

    fun enable() {
        isButtonEnabled = true
        this.alpha = 1.0f
        this.isEnabled = true
    }

    fun disable() {
        isButtonEnabled = false
        this.alpha = 0.5f
        this.isEnabled = false
    }

    fun setShape(shape:Int){
        this.shape = shape
        setup()
    }

    fun setBorderWidth(borderWidth:Float){
        this.borderWidth = borderWidth
        setup()
    }

    fun setBorderRadius(borderRadius:Float){
        this.radius = borderRadius
        setup()
    }

    fun setBorderColor(borderColor:Int){
        this.borderColor = borderColor
        setup()
    }
    fun setBackgroundColor_(backgroundColor:Int){
        this.backgroundColor = backgroundColor
        setup()
    }
    fun setBackgroundColorPressed(backgroundColorPressed:Int){
        this.backgroundColorPressed = backgroundColorPressed
        setup()
    }
    fun setGradientColorStart(gradientStartColor:Int){
        this.gradientStartColor = gradientStartColor
        setup()
    }
    fun setGradientColorEnd(gradientEndColor:Int){
        this.gradientEndColor = gradientEndColor
        setup()
    }
    fun setGradientAngle(gradientAngle:Int){
        this.gradientAngle = gradientAngle
        setup()
    }
}
