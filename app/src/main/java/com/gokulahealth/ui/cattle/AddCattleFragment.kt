package com.gokulahealth.ui.cattle

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gokulahealth.R
import com.gokulahealth.data.entity.Cattle
import com.gokulahealth.databinding.FragmentAddCattleBinding
import com.gokulahealth.viewmodel.CattleViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCattleFragment : Fragment() {

    private var _binding: FragmentAddCattleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CattleViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private var selectedDob: Long = 0L
    private var selectedPhotoUri: String = ""

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it.toString()
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_cow_placeholder)
                .centerCrop()
                .into(binding.imgCattlePhoto)

            // Take persistent permission so photo survives restarts
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) { }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCattleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardPhoto.setOnClickListener { pickImage.launch("image/*") }
        binding.imgCameraOverlay.setOnClickListener { pickImage.launch("image/*") }

        binding.etDob.setOnClickListener { showDatePicker() }

        binding.btnSave.setOnClickListener { saveCattle() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDob = calendar.timeInMillis
                binding.etDob.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveCattle() {
        val earTag = binding.etEarTag.text.toString().trim()
        val name = binding.etName.text.toString().trim()
        val breed = binding.etBreed.text.toString().trim()

        // Validate
        var valid = true
        if (earTag.isEmpty()) {
            binding.tilEarTag.error = getString(R.string.error_required)
            valid = false
        } else {
            binding.tilEarTag.error = null
        }

        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.error_required)
            valid = false
        } else {
            binding.tilName.error = null
        }

        if (breed.isEmpty()) {
            binding.tilBreed.error = getString(R.string.error_required)
            valid = false
        } else {
            binding.tilBreed.error = null
        }

        if (selectedDob == 0L) {
            binding.tilDob.error = getString(R.string.error_required)
            valid = false
        } else {
            binding.tilDob.error = null
        }

        if (!valid) return

        val cattle = Cattle(
            earTagId = earTag,
            name = name,
            breed = breed,
            dateOfBirth = selectedDob,
            photoUri = selectedPhotoUri
        )

        viewModel.insert(cattle) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
