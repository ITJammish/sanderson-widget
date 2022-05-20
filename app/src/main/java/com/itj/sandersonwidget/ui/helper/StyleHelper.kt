package com.itj.sandersonwidget.ui.helper

import android.content.res.ColorStateList
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.itj.sandersonwidget.R
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

private const val SET_PROGRESS_TINT_LIST_METHOD_NAME = "setProgressTintList"

/**
 * Invokes the `setProgressTintList()` method from [android.widget.ProgressBar]. This method is not visible from the
 * RemoteViews API, so we have to resort to reflection. There are different ways to do this based on API level.
 */
internal fun setProgressBarColorCompat(remoteViews: RemoteViews, progressColor: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        setProgressBarColor(remoteViews, progressColor)
    } else {
        setProgressBarColorManual(remoteViews, progressColor)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
private fun setProgressBarColor(remoteViews: RemoteViews, progressColor: Int) {
    remoteViews.setColorStateList(
        R.id.item_progress_bar,
        SET_PROGRESS_TINT_LIST_METHOD_NAME,
        ColorStateList.valueOf(progressColor),
    )
}

private fun setProgressBarColorManual(remoteViews: RemoteViews, progressColor: Int) {
    var setTintMethod: Method? = null
    try {
        setTintMethod =
            RemoteViews::class.java.getMethod(
                SET_PROGRESS_TINT_LIST_METHOD_NAME,
                Int::class.javaPrimitiveType,
                ColorStateList::class.java,
            )
    } catch (e: SecurityException) {
        e.printStackTrace()
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    }
    if (setTintMethod != null) {
        try {
            setTintMethod.invoke(
                remoteViews,
                R.id.item_progress_bar,
                ColorStateList.valueOf(progressColor),
            )
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}
