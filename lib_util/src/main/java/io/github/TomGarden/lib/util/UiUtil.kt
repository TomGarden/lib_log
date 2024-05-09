package io.github.TomGarden.lib.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object UiUtil {

    inline fun <reified T : View> View.getParent(): T? {
        var curView: View = this
        while (curView is View) {
            val parent = curView.parent
            if (parent is T) return parent /* Cannot check for instance of erased type: T */
            if (parent is View) curView = parent
        }
        return null
    }

    inline fun <reified T : Activity> Context.getActivity(): T? {
        var mContext = this
        while (mContext is ContextWrapper) {
            if (mContext is T) {
                return mContext
            }
            mContext = mContext.baseContext
        }
        return null
    }

    inline fun <reified T : Activity> View.getActivity(): T? = context.getActivity<T>()

    /** 获取与之相关的 Fragment */
    fun <T : Fragment> View.getFragment(): T? {
        try {
            return FragmentManager.findFragment<T>(this)
        } catch (exception: Throwable) {
            exception.printStackTrace()
            return null
        }
    }
}