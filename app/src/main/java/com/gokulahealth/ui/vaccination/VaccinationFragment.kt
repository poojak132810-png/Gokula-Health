package com.gokulahealth.ui.vaccination

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gokulahealth.R
import com.gokulahealth.data.entity.Cattle
import com.gokulahealth.data.entity.Vaccination
import com.gokulahealth.databinding.FragmentVaccinationBinding
import com.gokulahealth.notification.NotificationHelper
import com.gokulahealth.ui.adapter.VaccinationAdapter
import com.gokulahealth.ui.adapter.VaccinationWithCattleName
import com.gokulahealth.viewmodel.CattleViewModel
import com.gokulahealth.viewmodel.VaccinationViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VaccinationFragment : Fragment() {

    private var _binding: FragmentVaccinationBinding? = null
    private val binding get() = _binding!!

    private val cattleViewModel: CattleViewModel by viewModels()
    private val vaccinationViewModel: VaccinationViewModel by viewModels()

    private lateinit var vaccinationAdapter: VaccinationAdapter
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private var cattleList: List<Cattle> = emptyList()
    private var selectedCattleId: Long = -1L
    private var selectedCattleName: String = ""
    private var dateGiven: Long = 0L
    private var nextDueDate: Long = 0L

    private val vaccineNames = listOf("FMD", "HS", "BQ", "Deworming", "Other")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVaccinationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vaccinationAdapter = VaccinationAdapter()
        binding.recyclerVaccinations.adapter = vaccinationAdapter

        // Setup vaccine spinner
        val vaccineAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, vaccineNames)
        vaccineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerVaccine.adapter = vaccineAdapter

        // Observe cattle for spinner
        cattleViewModel.allCattle.observe(viewLifecycleOwner) { list ->
            cattleList = list
            val names = list.map { "${it.name} (${it.earTagId})" }
            val cattleSpinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
            cattleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCattle.adapter = cattleSpinnerAdapter
        }

        binding.spinnerCattle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (cattleList.isNotEmpty()) {
                    selectedCattleId = cattleList[position].id
                    selectedCattleName = cattleList[position].name
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Observe all vaccinations
        vaccinationViewModel.allVaccinations.observe(viewLifecycleOwner) { vaccinations ->
            lifecycleScope.launch {
                val items = vaccinations.map { v ->
                    val cattle = cattleViewModel.getAllCattleSync().find { it.id == v.cattleId }
                    VaccinationWithCattleName(v, cattle?.name ?: "Unknown")
                }.sortedBy { it.vaccination.nextDueDate }
                vaccinationAdapter.submitList(items)

                binding.emptyState.visibility = if (vaccinations.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerVaccinations.visibility = if (vaccinations.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        // Date pickers
        binding.etDateGiven.setOnClickListener {
            showDatePicker { millis ->
                dateGiven = millis
                binding.etDateGiven.setText(dateFormat.format(millis))
            }
        }

        binding.etNextDue.setOnClickListener {
            showDatePicker { millis ->
                nextDueDate = millis
                binding.etNextDue.setText(dateFormat.format(millis))
            }
        }

        // FAB toggle form
        binding.fabAddVaccination.setOnClickListener {
            if (binding.cardForm.visibility == View.GONE) {
                binding.cardForm.visibility = View.VISIBLE
                binding.fabAddVaccination.hide()
            }
        }

        binding.btnCancel.setOnClickListener {
            binding.cardForm.visibility = View.GONE
            binding.fabAddVaccination.show()
            clearForm()
        }

        binding.btnSaveVaccination.setOnClickListener { saveVaccination() }
    }

    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day, 0, 0, 0)
                onDateSelected(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveVaccination() {
        if (selectedCattleId == -1L) {
            Toast.makeText(requireContext(), "Please select cattle first", Toast.LENGTH_SHORT).show()
            return
        }

        val vaccineName = vaccineNames[binding.spinnerVaccine.selectedItemPosition]

        var valid = true
        if (dateGiven == 0L) {
            binding.tilDateGiven.error = getString(R.string.error_required)
            valid = false
        } else {
            binding.tilDateGiven.error = null
        }

        if (nextDueDate == 0L) {
            binding.tilNextDue.error = getString(R.string.error_required)
            valid = false
        } else {
            binding.tilNextDue.error = null
        }

        if (!valid) return

        val notes = binding.etNotes.text.toString().trim()

        val vaccination = Vaccination(
            cattleId = selectedCattleId,
            vaccineName = vaccineName,
            dateGiven = dateGiven,
            nextDueDate = nextDueDate,
            notes = notes
        )

        vaccinationViewModel.insert(vaccination) { id ->
            // Schedule alarm
            NotificationHelper.scheduleVaccinationAlarm(
                requireContext(),
                id,
                vaccineName,
                selectedCattleName,
                nextDueDate
            )
        }

        Toast.makeText(requireContext(), getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show()
        binding.cardForm.visibility = View.GONE
        binding.fabAddVaccination.show()
        clearForm()
    }

    private fun clearForm() {
        binding.etDateGiven.text?.clear()
        binding.etNextDue.text?.clear()
        binding.etNotes.text?.clear()
        dateGiven = 0L
        nextDueDate = 0L
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
