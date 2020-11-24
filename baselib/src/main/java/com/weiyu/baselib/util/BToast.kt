package com.weiyu.baselib.util

import android.content.Context
import android.view.Gravity
import android.widget.Toast

open class BToast{
    companion object StaticParams {
        // Toast
        private var toast: Toast? = null

        /**
         * 短时间显示Toast
         *
         * @param context
         * @param message
         */
        fun showShort(context: Context, message: CharSequence) {
            if (null == toast) {
                println(message.toString() + "Toast")
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast!!.setText(message)
            }
            //        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast!!.show()
        }

        /**
         * 短时间显示Toast
         *
         * @param context
         * @param message
         */
        fun showShort(context: Context, message: Int) {
            if (null == toast) {
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast!!.setText(message)
            }
            //        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast!!.show()
        }

        /**
         * 长时间显示Toast
         *
         * @param context
         * @param message
         */
        fun showLong(context: Context, message: CharSequence) {
            if (null == toast) {
                toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast!!.setText(message)
            }
            //        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast!!.show()
        }

        /**
         * 长时间显示Toast
         *
         * @param context
         * @param message
         */
        fun showLong(context: Context, message: Int) {
            if (null == toast) {
                toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast!!.setText(message)
            }
            //        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast!!.show()
        }

        /**
         * 自定义显示Toast时间
         *
         * @param context
         * @param message
         * @param duration
         */
        fun show(context: Context, message: CharSequence, duration: Int) {
            if (null == toast) {
                toast = Toast.makeText(context, message, duration)
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast!!.setText(message)
            }
            //        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast!!.show()
        }

        /**
         * 自定义显示Toast时间
         *
         * @param context
         * @param message
         * @param duration
         */
        fun show(context: Context, message: Int, duration: Int) {
            if (null == toast) {
                toast = Toast.makeText(context, message, duration)
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast!!.setText(message)
            }
            //        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast!!.show()
        }

        /**
         * Hide the toast, if any.
         */
        fun hideToast() {
            if (null != toast) {
                toast!!.cancel()
            }
        }

        fun showShortTop(context: Context, message: CharSequence) {
            if (null == toast) {
                println(message.toString() + "Toast")
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast!!.setText(message)
            }
            toast!!.setGravity(Gravity.CENTER or Gravity.TOP, 0, 250)
            toast!!.show()
        }

        fun showLongTop(context: Context, message: CharSequence) {
            if (null == toast) {
                println(message.toString() + "Toast")
                toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast!!.setText(message)
            }
            toast!!.setGravity(Gravity.CENTER or Gravity.TOP, 0, 250)
            toast!!.show()
        }
    }
}