package com.fittunner.view.tracking

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fittunner.R
import com.fittunner.data.Constants
import com.fittunner.service.TrackingService

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_maps_track.*

class MapsTrackFragment : Fragment(),View.OnClickListener {

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(27.2038, 77.5011)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in MY Room"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

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
                start.visibility=View.VISIBLE
                stop.visibility=View.GONE
                pause.visibility=View.GONE

                TrackingService.sendCommand(requireContext(),
                    Constants.ACTION_TRACK_STOP
                )
            }
        }
    }
}