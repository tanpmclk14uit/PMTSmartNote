package com.example.btcanhan2_todolistapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


private const val ARG_PARAM = "param"

class statisticFragment : Fragment() {

    private lateinit var anyChartView: AnyChartView
    private var doneTask: Int = 0
    private var unDoneTask: Int = 0
    private lateinit var mMainActivity: MainActivity


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_statistic, container, false)
        anyChartView = view.findViewById(R.id.chartView)

        mMainActivity = activity as MainActivity
        countDoneAndUnDoneTask( AppUtil.listtask)
        val doneTaskView: TextView = view.findViewById(R.id.doneTask)
        val unDoneTaskView: TextView = view.findViewById(R.id.unDoneTasks)
        doneTaskView.text = "Done tasks: $doneTask"
        unDoneTaskView.text = "Undone tasks: $unDoneTask"
        setUpPieChart()
        return view
    }

    private fun countDoneAndUnDoneTask(data: ArrayList<Task>) {
        for (i in data) {
            if (i.taskDone == true)
                doneTask++
            else
                unDoneTask++
        }
    }

    private fun setUpPieChart() {
        var pie: Pie = AnyChart.pie()
        var dataEntries: ArrayList<DataEntry> = ArrayList()
        dataEntries.add(ValueDataEntry("Done tasks", doneTask))
        dataEntries.add(ValueDataEntry("Undone tasks", unDoneTask))
        pie.data(dataEntries)
        anyChartView.setChart(pie)

    }
}