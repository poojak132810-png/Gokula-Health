package com.gokulahealth.ui.milk

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gokulahealth.R
import com.gokulahealth.data.entity.Cattle
import com.gokulahealth.data.entity.MilkEntry
import com.gokulahealth.databinding.FragmentMilkDiaryBinding
import com.gokulahealth.ui.adapter.MilkEntryAdapter
import com.gokulahealth.viewmodel.CattleViewModel
import com.gokulahealth.viewmodel.MilkViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MilkDiaryFragment : Fragment() {

    private var _binding: FragmentMilkDiaryBinding? = null
    private val binding get() = _binding!!

    private val cattleViewModel: CattleViewModel by viewModels()
    private val milkViewModel: MilkViewModel by viewModels()

    private lateinit var milkAdapter: MilkEntryAdapter
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private var selectedDate: Long = System.currentTimeMillis()
    private var cattleList: List<Cattle> = emptyList()
    private var selectedCattleId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMilkDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        milkAdapter = MilkEntryAdapter()
        binding.recyclerMilkEntries.adapter = milkAdapter

        // Set today's date
        binding.etDate.setText(dateFormat.format(System.currentTimeMillis()))
        binding.etDate.setOnClickListener { showDatePicker() }

        // Setup yield auto-calculation
        val yieldWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { calculateTotal() }
        }
        binding.etMorningYield.addTextChangedListener(yieldWatcher)
        binding.etEveningYield.addTextChangedListener(yieldWatcher)

        // Observe cattle for spinner
        cattleViewModel.allCattle.observe(viewLifecycleOwner) { list ->
            cattleList = list
            val names = list.map { "${it.name} (${it.earTagId})" }
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCattle.adapter = spinnerAdapter

            if (list.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.recyclerMilkEntries.visibility = View.GONE
            }
        }

        binding.spinnerCattle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (cattleList.isNotEmpty()) {
                    selectedCattleId = cattleList[position].id
                    milkViewModel.selectCattle(selectedCattleId)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Observe entries for selected cattle
        milkViewModel.entriesLast30Days.observe(viewLifecycleOwner) { entries ->
            milkAdapter.submitList(entries.reversed())
            binding.emptyState.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerMilkEntries.visibility = if (entries.isEmpty()) View.GONE else View.VISIBLE
        }

        // Observe monthly average
        milkViewModel.totalYield.observe(viewLifecycleOwner) { total ->
            milkViewModel.entryCount.observe(viewLifecycleOwner) { count ->
                if (total != null && count != null && count > 0) {
                    val average = total / count
                    binding.tvMonthlyAverage.text = String.format("Monthly Average: %.1f L/day", average)
                    binding.tvMonthlyAverage.visibility = View.VISIBLE
                } else {
                    binding.tvMonthlyAverage.visibility = View.GONE
                }
            }
        }

        binding.btnAddEntry.setOnClickListener { addEntry() }
    }

    private fun calculateTotal() {
        val morning = binding.etMorningYield.text.toString().toFloatOrNull() ?: 0f
        val evening = binding.etEveningYield.text.toString().toFloatOrNull() ?: 0f
        val total = morning + evening
        binding.tvTotalYield.text = String.format("Total: %.1f L", total)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day, 0, 0, 0)
                selectedDate = calendar.timeInMillis
                binding.etDate.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun addEntry() {
        if (selectedCattleId == -1L) {
            Toast.makeText(requireContext(), "Please select cattle first", Toast.LENGTH_SHORT).show()
            return
        }

        val morning = binding.etMorningYield.text.toString().toFloatOrNull()
        val evening = binding.etEveningYield.text.toString().toFloatOrNull()

        var valid = true
        if (morning == null) {
            binding.tilMorning.error = getString(R.string.error_required)
            valid = false
        } else {
            binding.tilMorning.error = null
        }

        if (evening == null) {
            binding.tilEvening.error = getString(R.string.error_required)
            valid = false
        } else {
            binding.tilEvening.error = null
        }

        if (!valid) return

        val entry = MilkEntry(
            cattleId = selectedCattleId,
            date = selectedDate,
            morningYield = morning!!,
            eveningYield = evening!!,
            totalYield = morning + evening
        )

        milkViewModel.insert(entry)
        Toast.makeText(requireContext(), getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show()

        // Clear fields
        binding.etMorningYield.text?.clear()
        binding.etEveningYield.text?.clear()
        binding.tvTotalYield.text = "Total: 0.0 L"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
