package com.fittunner.view.home.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.fittunner.R
import com.fittunner.util.FitTunnerLogger
import com.google.android.gms.ads.*
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModels()
    var adapter:RunAdapter = RunAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvList: RecyclerView = view.findViewById(R.id.rvList)
        rvList.adapter=adapter
        homeViewModel.getAllRuns().observe(viewLifecycleOwner, Observer {records->
            adapter.setData(records)
        })
    }
    /*fun initGoogleAdds(){
        try{
            val testDeviceIds = Arrays.asList(AdRequest.DEVICE_ID_EMULATOR)
            val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }*/

}