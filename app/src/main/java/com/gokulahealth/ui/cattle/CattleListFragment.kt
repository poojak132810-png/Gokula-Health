package com.gokulahealth.ui.cattle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gokulahealth.R
import com.gokulahealth.databinding.FragmentCattleListBinding
import com.gokulahealth.ui.adapter.CattleAdapter
import com.gokulahealth.viewmodel.CattleViewModel

class CattleListFragment : Fragment() {

    private var _binding: FragmentCattleListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CattleViewModel by viewModels()
    private lateinit var adapter: CattleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCattleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CattleAdapter { cattle ->
            val bundle = Bundle().apply {
                putLong("cattleId", cattle.id)
            }
            findNavController().navigate(R.id.action_cattle_to_detail, bundle)
        }
        binding.recyclerCattle.adapter = adapter

        viewModel.allCattle.observe(viewLifecycleOwner) { cattleList ->
            adapter.submitList(cattleList)
            binding.emptyState.visibility = if (cattleList.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerCattle.visibility = if (cattleList.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.fabAddCattle.setOnClickListener {
            findNavController().navigate(R.id.action_cattle_to_add)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
