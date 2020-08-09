package com.fittuner.view.home.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.fittuner.R
import com.fittuner.room.RunTrack
import com.fittuner.util.GoogleAddsUtil
import com.fittuner.util.TimeUtility
import kotlinx.android.synthetic.main.item_run.view.*
import java.lang.Exception


class RunAdapterBkp:RecyclerView.Adapter<RunViewHolderBkp>(){

    private val mDiffer: AsyncListDiffer<RunTrack> = AsyncListDiffer<RunTrack>(this, RunAdapterDiffUtillBkp())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolderBkp
            = RunViewHolderBkp(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_run,
            parent,
            false
        ))

    override fun getItemCount(): Int =mDiffer.currentList.size

    fun getItem(position: Int)=mDiffer.currentList.get(position)

    override fun onBindViewHolder(holder: RunViewHolderBkp, position: Int) {
        holder.bind(getItem(position))
    }

    fun setData(records: List<RunTrack>) {
        mDiffer.submitList(records)
        //println("adapter runs===>"+runs.size)
    }

}

class RunViewHolderBkp(itemView: View):RecyclerView.ViewHolder(itemView){
    lateinit var run: RunTrack
    fun bind(item: RunTrack) {
        run=item
        with(run){
            with(itemView){
                totalRun.text="${distance/1000f} km"
                speed.text="$avgSpeedKMH km/h"
                totalCalories.text="$caloriesBurned kcal"
                totalTime.text=TimeUtility.getFormattedStopWatchTime(totalTimeTaken)
                Glide.with(this)
                    .load(screenshot)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivThumbnail)
                date.text=TimeUtility.getDate(createdAt)
                if(adapterPosition%5==0){
                    loadBannerAdd(addContainer)
                }else{
                    addContainer.visibility=View.GONE
                }
            }
        }
    }

    fun loadBannerAdd(addHolder:LinearLayout){
        try{
            addHolder.visibility=View.VISIBLE
            if(addHolder.childCount>0){
                addHolder.removeAllViews()
            }
            addHolder.addView(GoogleAddsUtil.getBannerAdd(addHolder.context))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}

class RunAdapterDiffUtillBkp : DiffUtil.ItemCallback<RunTrack>(){
    override fun areItemsTheSame(oldItem: RunTrack, newItem: RunTrack): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: RunTrack, newItem: RunTrack): Boolean = oldItem.equals(newItem)
}