package com.gokulahealth.ui.reports

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.gokulahealth.R
import com.gokulahealth.data.entity.Cattle
import com.gokulahealth.databinding.FragmentReportsBinding
import com.gokulahealth.viewmodel.CattleViewModel
import com.gokulahealth.viewmodel.MilkViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val cattleViewModel: CattleViewModel by viewModels()
    private val milkViewModel: MilkViewModel by viewModels()

    private var cattleList: List<Cattle> = emptyList()
    private val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChart()

        // Observe cattle for spinner
        cattleViewModel.allCattle.observe(viewLifecycleOwner) { list ->
            cattleList = list
            val names = list.map { "${it.name} (${it.earTagId})" }
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCattle.adapter = spinnerAdapter

            if (list.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.lineChart.visibility = View.GONE
            }
        }

        binding.spinnerCattle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (cattleList.isNotEmpty()) {
                    loadChartData(cattleList[position].id)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setDragEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            legend.textSize = 14f

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                textSize = 12f
                setDrawGridLines(false)
            }

            axisLeft.apply {
                textSize = 12f
                axisMinimum = 0f
            }

            axisRight.isEnabled = false
        }
    }

    private fun loadChartData(cattleId: Long) {
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)

        lifecycleScope.launch {
            val entries = milkViewModel.getEntriesSinceSync(cattleId, thirtyDaysAgo)

            if (entries.isEmpty()) {
                binding.lineChart.visibility = View.GONE
                binding.emptyState.visibility = View.VISIBLE
                binding.tvAverageSummary.visibility = View.GONE
                return@launch
            }

            binding.lineChart.visibility = View.VISIBLE
            binding.emptyState.visibility = View.GONE

            val chartEntries = entries.mapIndexed { index, milkEntry ->
                Entry(index.toFloat(), milkEntry.totalYield)
            }

            val dateLabels = entries.map { dateFormat.format(Date(it.date)) }

            // Calculate average
            val totalYield = entries.sumOf { it.totalYield.toDouble() }.toFloat()
            val average = totalYield / entries.size

            val primaryColor = ContextCompat.getColor(requireContext(), R.color.primary)

            val dataSet = LineDataSet(chartEntries, "Milk Yield (L)").apply {
                color = primaryColor
                lineWidth = 2.5f
                setCircleColor(primaryColor)
                circleRadius = 4f
                setDrawValues(true)
                valueTextSize = 10f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = primaryColor
                fillAlpha = 30
            }

            binding.lineChart.apply {
                xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)

                // Add dashed average line
                axisLeft.removeAllLimitLines()
                val avgLine = LimitLine(average, "Avg: %.1f L".format(average)).apply {
                    lineWidth = 1.5f
                    lineColor = Color.parseColor("#FF8F00")
                    enableDashedLine(10f, 10f, 0f)
                    labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                    textSize = 12f
                    textColor = Color.parseColor("#FF8F00")
                }
                axisLeft.addLimitLine(avgLine)

                data = LineData(dataSet)
                animateX(500)
                invalidate()
            }

            // Show average summary
            binding.tvAverageSummary.text = String.format(
                "Monthly Average: %.1f L/day over %d days (Total: %.1f L)",
                average, entries.size, totalYield
            )
            binding.tvAverageSummary.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
