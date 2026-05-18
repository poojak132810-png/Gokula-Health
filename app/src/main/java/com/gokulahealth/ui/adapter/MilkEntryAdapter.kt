package com.gokulahealth.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gokulahealth.data.entity.MilkEntry
import com.gokulahealth.databinding.ItemMilkEntryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MilkEntryAdapter : ListAdapter<MilkEntry, MilkEntryAdapter.MilkEntryViewHolder>(MilkEntryDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilkEntryViewHolder {
        val binding = ItemMilkEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MilkEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MilkEntryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MilkEntryViewHolder(
        private val binding: ItemMilkEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: MilkEntry) {
            binding.tvDate.text = dateFormat.format(Date(entry.date))
            binding.tvYields.text = "AM: %.1f L | PM: %.1f L".format(entry.morningYield, entry.eveningYield)
            binding.tvTotal.text = "%.1f L".format(entry.totalYield)
        }
    }

    class MilkEntryDiffCallback : DiffUtil.ItemCallback<MilkEntry>() {
        override fun areItemsTheSame(oldItem: MilkEntry, newItem: MilkEntry) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MilkEntry, newItem: MilkEntry) = oldItem == newItem
    }
}
