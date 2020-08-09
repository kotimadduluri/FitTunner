package com.fittuner.view.home.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.fittuner.R
import com.fittuner.room.RunTrack
import com.fittuner.util.GoogleAddsUtil
import com.fittuner.util.TimeUtility
import kotlinx.android.synthetic.main.item_run.view.*


class RunAdapter:
    PagedListAdapter<RunTrack, RunViewHolder>(RunAdapterDiffUtill()){

  //  private val mDiffer: AsyncListDiffer<RunTrack> = AsyncListDiffer<RunTrack>(this, RunAdapterDiffUtill())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder
            =RunViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_run,
            parent,
            false
        ))

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}

class RunViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    lateinit var run: RunTrack
    fun bind(item: RunTrack) {
        run=item
        with(run){
            with(itemView){
                totalRun.text="${distance/1000f} km"
                speed.text="$avgSpeedKMH km/h"
                totalCalories.text="$caloriesBurned kcal"
                totalTime.text=TimeUtility.getFormattedStopWatchTime(totalTimeTaken)

                val myOptions = RequestOptions()
                    .override(ivThumbnail.width/2, ivThumbnail.height/2)

                Glide.with(this)
                    .load(screenshot)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(myOptions)
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

class RunAdapterDiffUtill : DiffUtil.ItemCallback<RunTrack>(){
    override fun areItemsTheSame(oldItem: RunTrack, newItem: RunTrack): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: RunTrack, newItem: RunTrack): Boolean = oldItem.equals(newItem)
}