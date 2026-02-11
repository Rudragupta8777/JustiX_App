package com.justix.app.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.justix.app.data.CaseModel
import com.justix.app.databinding.ItemCaseBinding

class CasesAdapter(private val onClick: (CaseModel) -> Unit) :
    ListAdapter<CaseModel, CasesAdapter.CaseViewHolder>(CaseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseViewHolder {
        val binding = ItemCaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CaseViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class CaseViewHolder(private val binding: ItemCaseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(case: CaseModel) {
            binding.txtCaseTitle.text = case.title
            binding.root.setOnClickListener { onClick(case) }
        }
    }

    class CaseDiffCallback : DiffUtil.ItemCallback<CaseModel>() {
        override fun areItemsTheSame(oldItem: CaseModel, newItem: CaseModel) = oldItem._id == newItem._id
        override fun areContentsTheSame(oldItem: CaseModel, newItem: CaseModel) = oldItem == newItem
    }
}