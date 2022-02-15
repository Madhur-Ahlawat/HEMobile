package com.heandroid.dialog

import android.app.Dialog
import android.content.Context
import com.heandroid.R
import com.heandroid.dialog.IOSTypeDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.TextView
import android.text.TextUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.view.ViewGroup
import android.view.WindowManager
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button


class IOSTypeDialog(context: Context?) : Dialog(context!!, R.style.ios_dialog_style) {
    class Builder(private val mContext: Context) {
        private var mIosDialog: IOSTypeDialog? = null
        private var mTitle: CharSequence? = null
        private var mMessage: CharSequence? = null
        private var mPositiveButtonText: CharSequence? = null
        private var mNegativeButtonText: CharSequence? = null
        private var mContentView: View? = null
        private var mPositiveButtonClickListener: DialogInterface.OnClickListener? = null
        private var mNegativeButtonClickListener: DialogInterface.OnClickListener? = null
        private var mCancelable = true
        fun setTitle(titleId: Int): Builder {
            mTitle = mContext.getText(titleId)
            return this
        }

        fun setTitle(title: CharSequence?): Builder {
            mTitle = title
            return this
        }

        fun setMessage(messageId: Int): Builder {
            mMessage = mContext.getText(messageId)
            return this
        }

        fun setMessage(message: CharSequence?): Builder {
            mMessage = message
            return this
        }

        fun setPositiveButton(textId: Int, listener: DialogInterface.OnClickListener?): Builder {
            mPositiveButtonText = mContext.getText(textId)
            mPositiveButtonClickListener = listener
            return this
        }

        fun setPositiveButton(
            text: CharSequence?,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            mPositiveButtonText = text
            mPositiveButtonClickListener = listener
            return this
        }

        fun setNegativeButton(textId: Int, listener: DialogInterface.OnClickListener?): Builder {
            mNegativeButtonText = mContext.getText(textId)
            mNegativeButtonClickListener = listener
            return this
        }

        fun setNegativeButton(
            text: CharSequence?,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            mNegativeButtonText = text
            mNegativeButtonClickListener = listener
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            mCancelable = cancelable
            return this
        }

        fun setContentView(contentView: View?): Builder {
            mContentView = contentView
            return this
        }

        fun create(): IOSTypeDialog {
            val inflater = LayoutInflater.from(mContext)
            val dialogView = inflater.inflate(R.layout.ios_type_dialog, null)
            mIosDialog = IOSTypeDialog(mContext)
            mIosDialog!!.setCancelable(mCancelable)
            val tvTitle = dialogView.findViewById<View>(R.id.title) as TextView
            val tvMessage = dialogView.findViewById<View>(R.id.message) as TextView
            val btnCancel = dialogView.findViewById<View>(R.id.cancel_btn) as Button
            val btnConfirm = dialogView.findViewById<View>(R.id.confirm_btn) as Button
            val horizontal_line = dialogView.findViewById<View>(R.id.horizontal_line)
            val vertical_line = dialogView.findViewById<View>(R.id.vertical_line)
            val btns_panel = dialogView.findViewById<View>(R.id.btns_panel)

            // set title
            // fix #1,if title is null,set title visibility GONE
            if (TextUtils.isEmpty(mTitle)) {
                tvTitle.visibility = View.GONE
            } else {
                tvTitle.text = mTitle
            }
            // set content view
            if (mContentView != null) {
                // if no message set add the contentView to the dialog body
                val rl = dialogView
                    .findViewById<View>(R.id.message_layout) as LinearLayout
                rl.removeAllViews()
                val params = LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                rl.addView(mContentView, params)
            } else {
                tvMessage.text = mMessage
            }
            // set buttons
            if (mPositiveButtonText == null && mNegativeButtonText == null) {
                setPositiveButton(R.string.str_ok, null)
                btnCancel.visibility = View.GONE
                vertical_line.visibility = View.GONE
            } else if (mPositiveButtonText != null && mNegativeButtonText == null) {
                btnCancel.visibility = View.GONE
                vertical_line.visibility = View.GONE
            } else if (mPositiveButtonText == null && mNegativeButtonText != null) {
                btnConfirm.visibility = View.GONE
                vertical_line.visibility = View.GONE
            }
            if (mPositiveButtonText != null) {
                btnConfirm.text = mPositiveButtonText
                btnConfirm.setOnClickListener {
                    if (mPositiveButtonClickListener != null) {
                        mPositiveButtonClickListener!!.onClick(
                            mIosDialog,
                            BUTTON_POSITIVE
                        )
                    }
                    mIosDialog!!.dismiss()
                }
            }
            if (mNegativeButtonText != null) {
                btnCancel.text = mNegativeButtonText
                btnCancel.setOnClickListener {
                    if (mNegativeButtonClickListener != null) {
                        mNegativeButtonClickListener!!.onClick(
                            mIosDialog,
                            BUTTON_NEGATIVE
                        )
                    }
                    mIosDialog!!.dismiss()
                }
            }
            dialogView.measure(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val dialogHeight = dialogView.measuredHeight
            val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(metrics)
            val maxHeight = (metrics.heightPixels * 0.8).toInt()
            val dialogParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            if (dialogHeight >= maxHeight) {
                dialogParams.height = maxHeight
            }
            mIosDialog!!.setContentView(dialogView, dialogParams)
            return mIosDialog!!
        }

        fun show(): IOSTypeDialog? {
            mIosDialog = create()
            mIosDialog!!.show()
            return mIosDialog
        }
    }
}