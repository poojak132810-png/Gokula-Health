package com.gokulahealth.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gokulahealth.R
import com.gokulahealth.data.entity.Vaccination
import com.gokulahealth.databinding.ItemVaccinationBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class VaccinationWithCattleName(
    val vaccination: Vaccination,
    val cattleName: String
)

class VaccinationAdapter : ListAdapter<VaccinationWithCattleName, VaccinationAdapter.VaccinationViewHolder>(VaccinationDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val sevenDaysMs = 7L * 24 * 60 * 60 * 1000

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccinationViewHolder {
        val binding = ItemVaccinationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VaccinationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VaccinationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VaccinationViewHolder(
        private val binding: ItemVaccinationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VaccinationWithCattleName) {
            val vaccination = item.vaccination
            val context = binding.root.context

            binding.tvVaccineName.text = vaccination.vaccineName
            binding.tvCattleName.text = item.cattleName
            binding.tvDueDate.text = "Due: ${dateFormat.format(Date(vaccination.nextDueDate))}"

            val now = System.currentTimeMillis()
            when {
                vaccination.nextDueDate < now -> {
                    // Overdue - Red
                    binding.tvStatus.text = "OVERDUE"
                    binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.overdue_red))
                    binding.tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.overdue_bg))
                    binding.cardVaccination.strokeColor = ContextCompat.getColor(context, R.color.overdue_red)
                    binding.cardVaccination.strokeWidth = 2
                }
                vaccination.nextDueDate - now <= sevenDaysMs -> {
                    // Due within 7 days - Yellow
                    binding.tvStatus.text = "DUE SOON"
                    binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.due_soon_yellow))
                    binding.tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.due_soon_bg))
                    binding.cardVaccination.strokeColor = ContextCompat.getColor(context, R.color.due_soon_yellow)
                    binding.cardVaccination.strokeWidth = 2
                }
                else -> {
                    // Upcoming - Green
                    binding.tvStatus.text = "UPCOMING"
                    binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.upcoming_green))
                    binding.tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.upcoming_bg))
                    binding.cardVaccination.strokeColor = ContextCompat.getColor(context, R.color.upcoming_green)
                    binding.cardVaccination.strokeWidth = 1
                }
            }
        }
    }

    class VaccinationDiffCallback : DiffUtil.ItemCallback<VaccinationWithCattleName>() {
        override fun areItemsTheSame(oldItem: VaccinationWithCattleName, newItem: VaccinationWithCattleName) =
            oldItem.vaccination.id == newItem.vaccination.id
        override fun areContentsTheSame(oldItem: VaccinationWithCattleName, newItem: VaccinationWithCattleName) =
            oldItem == newItem
    }
}
