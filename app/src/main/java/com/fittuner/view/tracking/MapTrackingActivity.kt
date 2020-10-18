package com.fittuner.view.tracking

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.fittuner.R
import com.fittuner.data.Constants
import com.fittuner.data.Constants.POLYLINE_COLOR
import com.fittuner.data.Constants.POLYLINE_SIZE
import com.fittuner.repository.SessionRepository
import com.fittuner.room.RunTrack
import com.fittuner.service.Polyline
import com.fittuner.service.TrackingService
import com.fittuner.util.TimeUtility
import com.fittuner.util.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_map_tracking.*
import kotlinx.android.synthetic.main.fragment_maps_track.tvTimer
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject
import kotlin.math.round


@AndroidEntryPoint
class MapTrackingActivity : AppCompatActivity(),
    OnMapReadyCallback,
    View.OnClickListener,
EasyPermissions.PermissionCallbacks{

    @Inject lateinit var sessionRepository: SessionRepository

    lateinit var map: GoogleMap
    lateinit var mapFragment: SupportMapFragment
    private var currentTimeInMills = 0L
    private val viewmodel: MapsTrackViewModel by viewModels()
    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    lateinit var fab_Pause: FloatingActionButton
    lateinit var fab_Cancel: FloatingActionButton
    lateinit var fab_Start: FloatingActionButton
    lateinit var fab_Save: FloatingActionButton

    val weight:Long by lazy {
        sessionRepository.getUserWeight()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_tracking)

        initMap()
        fab_Pause = findViewById(R.id.pause)
        fab_Start = findViewById(R.id.start)
        fab_Cancel = findViewById(R.id.cancel)
        fab_Save = findViewById(R.id.save)
        subScribeObserver()
    }

    fun initMap(){
        if(checkPermissions()){
            mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    private fun checkPermissions(): Boolean {
        if(TrackingUtility.hasLocationPermissions(this)){
            return true
        }
        TrackingUtility.requestLocationPermissions(this)
        return false
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //map.setMapType(GoogleMap.MAP_TYPE_SATELLITE)
        map.isMyLocationEnabled=true

        val success = googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, R.raw.maps_custom_style
            )
        )

       // map.myLocation
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        addAllPolyLines()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.start -> {
              //  startRun()
            }
            R.id.pause -> {
               // pauseRun()

            }
            R.id.save -> {
                if (currentTimeInMills > 0L) {
                    zoomToWholeTrack()
                    endAndSaveRun()
                }
            }
            R.id.cancel -> {
                if (currentTimeInMills > 0L) {
                    showCancelDialogue()
                }
            }

            R.id.tvTimerHolder->runTrigger()
        }

    }

    fun runTrigger() {
        if(isTracking){
            TrackingService.sendCommand(
                this,
                Constants.ACTION_TRACK_PAUSE
            )
            runControllButtonLable.text = "Tap to RESUME"
        }else{
            TrackingService.sendCommand(
                this,
                Constants.ACTION_TRACK_START_OR_RESUME
            )
            runControllButtonLable.text = "Tap to PAUSE"

        }

    }

    fun startRun() {
        fab_Start.hide()
        fab_Save.show()
        fab_Cancel.show()
        fab_Pause.show()
        TrackingService.sendCommand(
            this,
            Constants.ACTION_TRACK_START_OR_RESUME
        )
        runControllButtonLable.text = "Tap to PAUSE"
    }

    fun pauseRun() {
        fab_Start.show()
        fab_Save.show()
        fab_Cancel.show()
        fab_Pause.hide()

        TrackingService.sendCommand(
            this,
            Constants.ACTION_TRACK_PAUSE
        )

        runControllButtonLable.text = "Tap to RESUME"
    }

    private fun resetActions() {
        //fab_Start.show()
        fab_Save.hide()
        fab_Cancel.hide()
        // fab_Pause.hide()
        runControllButtonLable.text = "Tap to START"
    }

    //business logic

    private fun subScribeObserver() {
        TrackingService.isTracking.observe(this, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(this, Observer {
            pathPoints = it
            addLatestPolyLine()
            moveCameraToUser()
        })

        TrackingService.timeRunInMills.observe(this, Observer {
            currentTimeInMills = it
            if(currentTimeInMills==0L){
                fab_Save.hide()
                fab_Cancel.hide()
                tvTimer.text="START"
            }else{
                val formatedTime = TimeUtility.getFormattedStopWatchTime(currentTimeInMills, true)
                tvTimer.text = formatedTime
            }
        })
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking ) {
            fab_Save.show()
            fab_Cancel.show()
        } else {
            fab_Save.hide()
            fab_Cancel.hide()
        }
    }

    private fun moveCameraToUser() {
        if (this::map.isInitialized) {
            if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
                map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        pathPoints.last().last(),
                        15f
                    )
                )
            }
        }
    }

    private fun addAllPolyLines() {
        if (this::map.isInitialized) {
            for (polyline in pathPoints) {
                val polylineOptions = PolylineOptions()
                    .color(Color.parseColor(POLYLINE_COLOR))
                    .width(POLYLINE_SIZE)
                    .addAll(polyline)

                map.addPolyline(polylineOptions)
            }
        }
    }

    private fun addLatestPolyLine() {
        if (this::map.isInitialized) {
            if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
                val prelastLatLng = pathPoints.last()[pathPoints.last().size - 2]
                val lastLatLng = pathPoints.last().last()
                val polylineOptions = PolylineOptions()
                    .color(Color.parseColor(POLYLINE_COLOR))
                    .width(POLYLINE_SIZE)
                    .add(prelastLatLng)
                    .add(lastLatLng)

                map.addPolyline(polylineOptions)

            }
        }
    }

    fun showCancelDialogue() {
        val dialogue = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle("Stop the run?")
            .setMessage("Are you sure to stop current run and delete all it`s data?")
            .setIcon(R.drawable.ic_run)
            .setPositiveButton("Yes") { dialogueInterface, _ ->
                TrackingService.sendCommand(
                    this,
                    Constants.ACTION_TRACK_STOP
                )
                dialogueInterface.dismiss()
                resetActions()
                showMessage("Cancelled run")
            }
            .setNegativeButton("No") { dialogueInterface, _ ->
                dialogueInterface.dismiss()
            }
        dialogue.show()
    }

    //saving track

    private fun zoomToWholeTrack() {
        try{
            if(pathPoints.size>0){
                val bounds = LatLngBounds.Builder()
                for (polyline in pathPoints) {
                    for (pos in polyline) {
                        bounds.include(pos)
                    }
                }
                map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds.build(),
                        640,
                        480,
                        (480 * 0.1f).toInt()
                    )
                )
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun endAndSaveRun() {
        try {
            map.snapshot { bmp ->
               // testImage.setImageBitmap(bmp)
                var distenceInMeters = 0
                for (polyline in pathPoints) {
                    distenceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
                }

                val distenceInKiloMeters = distenceInMeters / 1000f
                val currentTimeInHours = currentTimeInMills / 3.6e+6

                val avgSpeed =
                    (round((distenceInKiloMeters / currentTimeInHours) * 10) / 10f).toFloat()
                val dateTimestamp = Calendar.getInstance().timeInMillis
                val caloriesBurned = (distenceInKiloMeters * weight).toInt()

                //save here
                val run = RunTrack(
                    bmp,
                    avgSpeed,
                    distenceInMeters,
                    caloriesBurned,
                    currentTimeInMills,
                    dateTimestamp
                )
                viewmodel.insertRun(run)

                TrackingService.sendCommand(
                    this,
                    Constants.ACTION_TRACK_STOP
                )

                resetActions()
                showMessage("Run saved successfully")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showMessage(message:String){
        Snackbar.make(
            findViewById(R.id.rootView),
            message,
        Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            checkPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        initMap()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,this)
    }
}