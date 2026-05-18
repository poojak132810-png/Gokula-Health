package com.gokulahealth.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gokulahealth.R
import com.gokulahealth.data.entity.Cattle
import com.gokulahealth.databinding.ItemCattleBinding

class CattleAdapter(
    private val onItemClick: (Cattle) -> Unit
) : ListAdapter<Cattle, CattleAdapter.CattleViewHolder>(CattleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CattleViewHolder {
        val binding = ItemCattleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CattleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CattleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CattleViewHolder(
        private val binding: ItemCattleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cattle: Cattle) {
            binding.tvName.text = cattle.name
            binding.tvEarTag.text = "Tag: ${cattle.earTagId}"
            binding.tvBreed.text = cattle.breed

            if (cattle.photoUri.isNotEmpty()) {
                Glide.with(binding.imgCattle.context)
                    .load(Uri.parse(cattle.photoUri))
                    .placeholder(R.drawable.ic_cow_placeholder)
                    .error(R.drawable.ic_cow_placeholder)
                    .centerCrop()
                    .into(binding.imgCattle)
            } else {
                binding.imgCattle.setImageResource(R.drawable.ic_cow_placeholder)
            }

            binding.root.setOnClickListener { onItemClick(cattle) }
        }
    }

    class CattleDiffCallback : DiffUtil.ItemCallback<Cattle>() {
        override fun areItemsTheSame(oldItem: Cattle, newItem: Cattle) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Cattle, newItem: Cattle) = oldItem == newItem
    }
}
