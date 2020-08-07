package com.fittunner.view.home.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.fittunner.R

class RunAdapter:RecyclerView.Adapter<RunViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder
            =RunViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_run,parent,false))

    override fun getItemCount(): Int =10

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        holder.bind()
    }

}

class RunViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    fun bind() {

    }

}