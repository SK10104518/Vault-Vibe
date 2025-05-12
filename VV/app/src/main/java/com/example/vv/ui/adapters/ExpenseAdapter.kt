package com.example.vv.ui.adapters

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vv.data.db.entities.Expense
import com.example.vv.databinding.ListItemExpenseBinding
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(
    private val onExpenseClick: (Expense) -> Unit,
    private val getCategoryName: (Int) -> String // Function to get category name from ID
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        // Inflate the layout using the generated binding class
        val binding = ListItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Pass the binding object to the ViewHolder
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense)
    }

    inner class ExpenseViewHolder(private val binding: ListItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {// binding.root gives you the root view of the layout

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onExpenseClick(getItem(position))
                }
            }
        }

        fun bind(expense: Expense) {
            // Access views using the binding object
            binding.tvExpenseDescription.text = expense.description
            binding.tvExpenseAmount.text = String.format(Locale.getDefault(), "$%.2f", expense.amount)

            // Format date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.tvExpenseDate.text = dateFormat.format(Date(expense.date))

            // Display time range
            binding.tvExpenseTime.text = "${expense.startTime} - ${expense.endTime}"

            // Get and display category name
            val categoryName = getCategoryName(expense.categoryId)
            binding.tvExpenseCategory.text = "Category: $categoryName"

            // Load photo preview if available
            if (expense.photoUri != null) {
                binding.ivExpensePhotoPreview.visibility = View.VISIBLE
                Glide.with(binding.ivExpensePhotoPreview.context)
                    .load(expense.photoUri)
                    .placeholder(R.drawable.ic_menu_gallery) // Placeholder
//                    .error(R.drawable.ic_menu_report_problem) // Error placeholder
                    .into(binding.ivExpensePhotoPreview)
            } else {
                binding.ivExpensePhotoPreview.visibility = View.GONE
            }
        }
    }
}

class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
    override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem == newItem
    }
}
