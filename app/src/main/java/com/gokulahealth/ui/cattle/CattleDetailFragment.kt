package com.gokulahealth.ui.cattle

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gokulahealth.R
import com.gokulahealth.databinding.FragmentCattleDetailBinding
import com.gokulahealth.ui.adapter.MilkEntryAdapter
import com.gokulahealth.ui.adapter.VaccinationAdapter
import com.gokulahealth.ui.adapter.VaccinationWithCattleName
import com.gokulahealth.viewmodel.CattleViewModel
import com.gokulahealth.viewmodel.MilkViewModel
import com.gokulahealth.viewmodel.VaccinationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CattleDetailFragment : Fragment() {

    private var _binding: FragmentCattleDetailBinding? = null
    private val binding get() = _binding!!

    private val cattleViewModel: CattleViewModel by viewModels()
    private val milkViewModel: MilkViewModel by viewModels()
    private val vaccinationViewModel: VaccinationViewModel by viewModels()

    private lateinit var milkAdapter: MilkEntryAdapter
    private lateinit var vaccinationAdapter: VaccinationAdapter

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private var cattleId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCattleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cattleId = arguments?.getLong("cattleId") ?: 0L

        milkAdapter = MilkEntryAdapter()
        binding.recyclerMilk.adapter = milkAdapter

        vaccinationAdapter = VaccinationAdapter()
        binding.recyclerVaccinations.adapter = vaccinationAdapter

        // Observe cattle details
        cattleViewModel.getCattleById(cattleId).observe(viewLifecycleOwner) { cattle ->
            cattle?.let {
                binding.tvName.text = it.name
                binding.tvEarTag.text = "Tag: ${it.earTagId}"
                binding.tvBreed.text = "Breed: ${it.breed}"
                binding.tvDob.text = "Born: ${dateFormat.format(Date(it.dateOfBirth))}"

                if (it.photoUri.isNotEmpty()) {
                    Glide.with(this)
                        .load(Uri.parse(it.photoUri))
                        .placeholder(R.drawable.ic_cow_placeholder)
                        .error(R.drawable.ic_cow_placeholder)
                        .centerCrop()
                        .into(binding.imgCattle)
                }

                // Observe milk entries
                milkViewModel.selectCattle(cattleId)
                milkViewModel.entriesForSelectedCattle.observe(viewLifecycleOwner) { entries ->
                    milkAdapter.submitList(entries.take(10))
                    binding.tvNoMilk.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
                }

                // Observe vaccinations
                vaccinationViewModel.getVaccinationsForCattle(cattleId).observe(viewLifecycleOwner) { vaccinations ->
                    val items = vaccinations.map { v ->
                        VaccinationWithCattleName(v, it.name)
                    }
                    vaccinationAdapter.submitList(items)
                    binding.tvNoVaccinations.visibility = if (vaccinations.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            cattleViewModel.getCattleById(cattleId).observe(viewLifecycleOwner) { cattle ->
                cattle?.let {
                    cattleViewModel.delete(it)
                    Toast.makeText(requireContext(), getString(R.string.deleted_successfully), Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
