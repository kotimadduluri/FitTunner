package com.fittunner.view.tracking

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.fittunner.R
import com.fittunner.data.Constants
import com.fittunner.room.RunTrack
import com.fittunner.service.Polyline
import com.fittunner.service.TrackingService
import com.fittunner.util.TimeUtility
import com.fittunner.util.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_maps_track.*
import java.lang.Exception
import java.util.*
import kotlin.math.floor
import kotlin.math.round

@AndroidEntryPoint
class MapsTrackFragment : Fragment(R.layout.fragment_maps_track), View.OnClickListener {

    lateinit var map: GoogleMap
    private var currentTimeInMills = 0L
    private val viewmodel:MapsTrackViewModel by viewModels()
    private var isTracking = false
    private var pathPoints= mutableListOf<Polyline>()

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        addAllPolyLines()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        with(view){
            start.setOnClickListener(this@MapsTrackFragment)
            stop.setOnClickListener(this@MapsTrackFragment)
            pause.setOnClickListener(this@MapsTrackFragment)
        }

        subScribeoObserver()
    }

    private fun subScribeoObserver(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints=it
            addLatestPolyLine()
            moveCameraToUser()
        })

        TrackingService.timeRunInMills.observe(viewLifecycleOwner, Observer {
            currentTimeInMills=it
            val formatedTime=TimeUtility.getFormattedStopWatchTime(currentTimeInMills,true)
            tvTimer.text=formatedTime
        })
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if(!isTracking){
            start.visibility=View.VISIBLE
            stop.visibility=View.GONE
            pause.visibility=View.GONE
        }else{
            start.visibility=View.GONE
            stop.visibility=View.VISIBLE
            pause.visibility=View.VISIBLE
        }
    }

    private fun moveCameraToUser(){
        if(this::map.isInitialized){
            if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
                map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        pathPoints.last().last(),
                        15f
                    )
                )
            }
        }
    }

    private fun addAllPolyLines(){
        if(this::map.isInitialized){
            for(polyline in pathPoints){
                val polylineOptions=PolylineOptions()
                    .color(Color.RED)
                    .width(8f)
                    .addAll(polyline)

                map.addPolyline(polylineOptions)
            }
        }
    }

    private fun addLatestPolyLine(){
        if(this::map.isInitialized){
            if(pathPoints.isNotEmpty() && pathPoints.last().size>1){
                val prelastLatLng = pathPoints.last()[pathPoints.last().size-2]
                val lastLatLng = pathPoints.last().last()
                val polylineOptions=PolylineOptions()
                    .color(Color.RED)
                    .width(8f)
                    .add(prelastLatLng)
                    .add(lastLatLng)

                map.addPolyline(polylineOptions)

            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.start->{
                start.visibility=View.GONE
                stop.visibility=View.VISIBLE
                pause.visibility=View.VISIBLE

                TrackingService.sendCommand(requireContext(),
                    Constants.ACTION_TRACK_START_OR_RESUME
                )
            }
            R.id.pause->{
                start.visibility=View.VISIBLE
                stop.visibility=View.VISIBLE
                pause.visibility=View.GONE

                TrackingService.sendCommand(requireContext(),
                    Constants.ACTION_TRACK_PAUSE
                )
            }
            R.id.stop->{
                if(currentTimeInMills>0L){
                    zoomToWholeTrack()
                    endAndSaveRun()
                }
            }
        }
    }

    fun showCancelDialogue(){
        val dialogue= MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
            .setTitle("Stop the run?")
            .setMessage("Are you sure to stop current run and delete all it`s data?")
            .setIcon(R.drawable.ic_run)
            .setPositiveButton("Yes"){dialogueInterface,_ ->
                start.visibility=View.VISIBLE
                stop.visibility=View.GONE
                pause.visibility = View.GONE
                TrackingService.sendCommand(
                    requireContext(),
                    Constants.ACTION_TRACK_STOP
                )

                dialogueInterface.dismiss()
            }
            .setNegativeButton("No") { dialogueInterface, _ ->
                dialogueInterface.dismiss()
            }
        dialogue.show()
    }

    //saving track

    private fun zoomToWholeTrack() {

        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        map.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                requireView().width,
                requireView().height,
                (requireView().height * 0.05f).toInt()
            )
        )
    }

    val weight=60F

    private fun endAndSaveRun(){
        try{
            map.snapshot { bmp->

                var distenceInMeters=0
                for(polyline in pathPoints){
                    distenceInMeters+=TrackingUtility.calculatePolylineLength(polyline).toInt()
                }

                val distenceInKiloMeters=distenceInMeters/1000f
                val currentTimeInHours= currentTimeInMills/3.6e+6

                val avgSpeed= (round((distenceInKiloMeters /currentTimeInHours)*10) /10f).toFloat()
                val dateTimestamp=Calendar.getInstance().timeInMillis
                val caloriesBurned = (distenceInKiloMeters*weight).toInt()

                //save here
                val run= RunTrack(
                    bmp,avgSpeed,distenceInMeters,caloriesBurned,currentTimeInMills,dateTimestamp
                )
                viewmodel.insertRun(run)
                showCancelDialogue()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}