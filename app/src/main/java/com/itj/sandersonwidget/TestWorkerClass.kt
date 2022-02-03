package com.itj.sandersonwidget

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*
import java.util.Calendar.HOUR
import java.util.Calendar.MINUTE

class TestWorkerClass(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {

        val time = Calendar.getInstance(Locale.UK)
        val hour = time.get(HOUR)
        val minute = time.get(MINUTE)

        Log.d("JamesDebug:", "The time is $hour:$minute")

        return Result.success()
    }
}
