package com.fittunner.view.home.ui.home

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.fittunner.R
import com.fittunner.room.RunTrack
import com.fittunner.util.TimeUtility
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.item_run.view.*
import java.lang.Exception


class RunAdapter:RecyclerView.Adapter<RunViewHolder>(){

    private val mDiffer: AsyncListDiffer<RunTrack> = AsyncListDiffer<RunTrack>(this, RunAdapterDiffUtill())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder
            =RunViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_run,
            parent,
            false
        ))

    override fun getItemCount(): Int =mDiffer.currentList.size

    fun getItem(position: Int)=mDiffer.currentList.get(position)

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setData(records: List<RunTrack>) {
        mDiffer.submitList(records)
        //println("adapter runs===>"+runs.size)
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

    fun loadBannerAdd( addHolder:LinearLayout){
        try{
            addHolder.visibility=View.VISIBLE
            if(addHolder.childCount>0){
                addHolder.removeAllViews()
            }

            val adView = AdView(addHolder.context)
            adView.adSize = AdSize.SMART_BANNER
            adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
            addHolder.addView(adView)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}

class RunAdapterDiffUtill : DiffUtil.ItemCallback<RunTrack>(){
    override fun areItemsTheSame(oldItem: RunTrack, newItem: RunTrack): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: RunTrack, newItem: RunTrack): Boolean = oldItem.equals(newItem)
}