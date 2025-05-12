package com.example.vv.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vv.data.db.daos.CategorySpending
import com.example.vv.databinding.ListItemCategorySpendingBinding
import java.text.NumberFormat
import java.util.*

class CategorySpendingAdapter(
    private val getCategoryName: (Int) -> String // Function to get category name from ID
) : ListAdapter<CategorySpending, CategorySpendingAdapter.CategorySpendingViewHolder>(CategorySpendingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorySpendingViewHolder {
        val binding = ListItemCategorySpendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategorySpendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategorySpendingViewHolder, position: Int) {
        val categorySpending = getItem(position)
        holder.bind(categorySpending)
    }

    inner class CategorySpendingViewHolder(private val binding: ListItemCategorySpendingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(categorySpending: CategorySpending) {
            // Get and display category name
            val categoryName = getCategoryName(categorySpending.categoryId)
            binding.tvCategoryName.text = categoryName

            // Format and display total amount
            val formattedAmount = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(categorySpending.totalAmount)
            binding.tvTotalAmount.text = formattedAmount
        }
    }
}

class CategorySpendingDiffCallback : DiffUtil.ItemCallback<CategorySpending>() {
    override fun areItemsTheSame(oldItem: CategorySpending, newItem: CategorySpending): Boolean {
        // Assuming categoryId is unique for CategorySpending within a report
        return oldItem.categoryId == newItem.categoryId
    }

    override fun areContentsTheSame(oldItem: CategorySpending, newItem: CategorySpending): Boolean {
        return oldItem == newItem
    }
}
