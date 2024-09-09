package com.tuanhn.smartmovie.screen.userscreen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.tuanhn.smartmovie.adapter.BillAdapter
import com.tuanhn.smartmovie.data.model.entities.Bill
import com.tuanhn.smartmovie.databinding.FragmentPurchaseHistoryBinding

class PurchaseHistoryFragment : Fragment() {

    private lateinit var binding: FragmentPurchaseHistoryBinding

    private var adapter: BillAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPurchaseHistoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialAdapter()

        observeData()
    }

    private fun initialAdapter() {

        adapter = BillAdapter(listOf())

        binding?.rcvBill?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding?.rcvBill?.adapter = adapter
    }

    private fun observeData() {
        val db = FirebaseFirestore.getInstance()

        val sharedPreferences = context?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

        val currentUser= sharedPreferences?.getString("current_user", "default_value")

        val listBill: MutableList<Bill> = mutableListOf()

        val ref = db.collection("bills").get().addOnSuccessListener { result ->
            for (document in result){
                if (document.getString("user") == currentUser)
                {
                    val bill = document.toObject(Bill::class.java)
                    listBill.add(bill)
                }
            }
            adapter?.updateBill(listBill)
        }
    }

}