package com.fittuner.view.home.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.fittuner.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_nodata.*

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModels()
    var adapter:RunAdapter = RunAdapter()
    lateinit var rvList: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvList = view.findViewById(R.id.rvList)
        rvList.adapter=adapter
        /*homeViewModel.getAllRuns().observe(viewLifecycleOwner, Observer {records->
            adapter.setData(records)
        })*/
        observerData()
    }

    private fun observerData() {
        try{
            homeViewModel.concertList.observe(viewLifecycleOwner, Observer {records->
                adapter.submitList(records)
                handleNoData(records.size==0)
            })
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun handleNoData(noData: Boolean) {
        if(noData){
            noDataHolder.visibility=View.VISIBLE
            rvList.visibility=View.GONE
        }else{
            noDataHolder.visibility=View.GONE
            rvList.visibility=View.VISIBLE
        }
    }
}