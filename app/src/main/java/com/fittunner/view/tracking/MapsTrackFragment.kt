package com.fittunner.view.tracking

import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.fittunner.R
import com.fittunner.data.Constants
import com.fittunner.service.Polyline
import com.fittunner.service.Polylines
import com.fittunner.service.TrackingService
import com.fittunner.util.TimeUtility

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_maps_track.*
import java.lang.Exception

class MapsTrackFragment : Fragment(),View.OnClickListener {

    lateinit var map:GoogleMap

    private var currentTimeINMiils = 0L

    private val callback = OnMapReadyCallback { googleMap ->
        map=googleMap
        addAllPolyLines()
        /*val sydney = LatLng(27.2038, 77.5011)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in MY Room"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
    }

    private var isTracking = false
    private var pathPoints= mutableListOf<Polyline>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps_track, container, false)
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
            currentTimeINMiils=it
            val formatedTime=TimeUtility.getFormattedStopWatchTime(currentTimeINMiils,true)
            tvTimer.text=formatedTime
        })
    }

    private fun toggleRun(){
        if(isTracking){
            TrackingService.sendCommand(requireContext(),
                Constants.ACTION_TRACK_PAUSE
            )
        }else{
            TrackingService.sendCommand(requireContext(),
                Constants.ACTION_TRACK_START_OR_RESUME
            )
        }
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

                map?.addPolyline(polylineOptions)
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

                map?.addPolyline(polylineOptions)

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
                if(currentTimeINMiils>0L){
                    showCancelDialogue()
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
                dialogueInterface.dismiss()
                start.visibility=View.VISIBLE
                stop.visibility=View.GONE
                pause.visibility=View.GONE
                TrackingService.sendCommand(requireContext(),
                    Constants.ACTION_TRACK_STOP
                )
            }
            .setNegativeButton("No"){dialogueInterface,_ ->
                dialogueInterface.dismiss()
            }
        dialogue.show()
    }
}