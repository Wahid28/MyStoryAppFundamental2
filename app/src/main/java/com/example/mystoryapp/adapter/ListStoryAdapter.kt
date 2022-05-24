package com.example.mystoryapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.databinding.StoryItemCardBinding
import com.example.mystoryapp.response.ListStoryItem

//class ListStoryAdapter(private val story: ArrayList<ListStoryItem>, onItemClick: OnItemClickCallback): RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {

class ListStoryAdapter(onItemClick: OnItemClickCallback): PagingDataAdapter<ListStoryItem, ListStoryAdapter.ViewHolder>(
    DIFF_CALLBACK) {
    private var onItemClickCallback: OnItemClickCallback = onItemClick

    class ViewHolder (val binding: StoryItemCardBinding): RecyclerView.ViewHolder(binding.root){
//        fun bind(story: ListStoryItem){
//
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)

        with(holder.binding){
            userName.text = data?.name
            Glide.with(holder.itemView.context)
                .load(data?.photoUrl)
                .into(imgItemPhoto)
        }
        holder.itemView.setOnClickListener {
            if (data != null) {
                onItemClickCallback.onItemClicked(data)
            }
        }
    }

//    override fun getItemCount(): Int {
//        return story.size
//    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}