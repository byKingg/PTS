package com.kingsecurity.pts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kingsecurity.pts.databinding.ItemSuspiciousPlateBinding
import com.kingsecurity.pts.models.SuspiciousPlate

class SuspiciousPlateAdapter(
    private val plates: List<SuspiciousPlate>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<SuspiciousPlateAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemSuspiciousPlateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(plate: SuspiciousPlate) {
            binding.apply {
                plateNumberTextView.text = plate.plateNumber
                reasonTextView.text = plate.reason
                severityTextView.text = "Seviye: ${plate.severity}"
                
                deleteButton.setOnClickListener {
                    onDeleteClick(plate.documentId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSuspiciousPlateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(plates[position])
    }

    override fun getItemCount() = plates.size
}