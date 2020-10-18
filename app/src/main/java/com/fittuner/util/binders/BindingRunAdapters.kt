package com.fittuner.util.binders

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.fittuner.util.TimeUtility

object BindingRunAdapters {
    @BindingAdapter("android:mapScreenshot")
    @JvmStatic
    fun setMapScreenshot(imageView: ImageView,screenshot: Bitmap){
        imageView.alpha=0f
        Glide.with(imageView)
            .load(screenshot)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions()
                    .override(imageView.width/2, imageView.height/2)
            )
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.animate().setDuration(250).alpha(1f).start()
                    return false
                }
            })
            .into(imageView)
    }

    @BindingAdapter("android:runDistence")
    @JvmStatic
    fun setRunDistence(view: TextView,distance: Int){
        view.text="${distance/1000f} km"
    }

    @BindingAdapter("android:runSpeed")
    @JvmStatic
    fun setRunSpeed(view: TextView,avgSpeedKMH: Float){
        view.text="$avgSpeedKMH km/h"
    }

    @BindingAdapter("android:caloriesBurned")
    @JvmStatic
    fun setCaloriesBurned(view: TextView,caloriesBurned: Int){
        view.text="$caloriesBurned kcal"
    }

    @BindingAdapter("android:runTotalTime")
    @JvmStatic
    fun setRunTotalTime(view: TextView,time: Long){
        view.text= TimeUtility.getFormattedStopWatchTime(time)
    }

    @BindingAdapter("android:runDate")
    @JvmStatic
    fun setRunDate(view: TextView,createdAt: Long){
        view.text= TimeUtility.getDate(createdAt)
    }
}