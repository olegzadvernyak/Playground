package com.oz.playground

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.work.PeriodicWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.android.synthetic.main.activity_with_radio_button.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class SampleWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}

sealed class Period(val value: Long, val unit: TimeUnit) {

    object FifteenSeconds : Period(15, TimeUnit.SECONDS)

    object OneMinute : Period(1, TimeUnit.MINUTES)

    object OneHour : Period(1, TimeUnit.HOURS)

}

class SampleViewModel : ViewModel() {

    fun selectPeriod(period: Period) {
        PeriodicWorkRequest.Builder(SampleWorker::class.java, period.value, period.unit).build()
    }

}

class SampleActivity : AppCompatActivity() {

    private val sampleViewModel: SampleViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_radio_button)

        periodGroup.setOnCheckedChangeListener { _, checkedId ->
            sampleViewModel.selectPeriod(when (checkedId) {
                R.id.periodFifteenSecondButton -> Period.FifteenSeconds
                R.id.periodOneMinuteButton -> Period.OneMinute
                else -> Period.OneHour
            })
        }
    }

}