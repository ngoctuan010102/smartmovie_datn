package com.tuanhn.smartmovie.screen.homescreen.bookticket

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.databinding.FragmentBookSeatsInformationBinding
import com.tuanhn.smartmovie.databinding.PaymentLayoutBinding
import com.tuanhn.smartmovie.payment.Api.CreateOrder
import com.tuanhn.smartmovie.screen.homescreen.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener


class PaymentActivity : AppCompatActivity() {


    private var binding: PaymentLayoutBinding? = null

    private var totalCount: Float = 10000.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentLayoutBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX)

        binding?.btnPay?.setOnClickListener {
            lifecycleScope.launch {
                CreateOrderClickHandler()
            }
        }
       /* val intent = intent
        bindingtxtSoluong?.setText(intent.getStringExtra("soluong"))
        val total = intent.getDoubleExtra("total", 0.0)
        val totalString = String.format("%.0f", total)
        txtTongTien?.setText(java.lang.Double.toString(total))*/

    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    suspend fun CreateOrderClickHandler() {
        withContext(Dispatchers.IO) {
            val orderApi = CreateOrder()
            try {
                val total: Double = totalCount.toDouble()
                val totalString = String.format("%.0f", total)
                val data = orderApi.createOrder(totalString)
                Log.d("Amount", totalString)
                val code = data.getString("return_code")
                Log.d("API Response", "${data.toString()} return_code: $code")
                //Toast.makeText(requireContext(), "return_code: $code", Toast.LENGTH_LONG).show()
                withContext(Dispatchers.Main) {
                    binding?.lblZpTransToken?.visibility = View.VISIBLE

                    if (code == "1") {
                        binding?.lblZpTransToken?.text = "zptranstoken"
                        binding?.txtToken?.text = data.getString("zp_trans_token")
                        IsDone()
                        Log.d("Amount", "totalCount.toString()wweee")
                        PayButtonClickHandler()
                    } else
                        Log.d("Amount", "totalCount.toString()")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun IsDone() {
        binding?.lblZpTransToken?.visibility = View.VISIBLE
        binding?.txtToken?.visibility = View.VISIBLE
        binding?.btnPay?.visibility = View.VISIBLE
    }
    suspend fun PayButtonClickHandler() {
        Log.d("Success", "Success-1-2")
        val token = binding?.txtToken?.text.toString()
        Log.d("Success", "$token")

            ZaloPaySDK.getInstance().payOrder(this@PaymentActivity, token, "demozpdk://app", object :
                PayOrderListener {
                override fun onPaymentSucceeded(
                    transactionId: String,
                    transToken: String,
                    appTransID: String
                ) {
                    Log.d("Success", "Success")
                    //  finishPayment()
                    //   finishPay(transactionId, transToken)
                }

                override fun onPaymentCanceled(zpTransToken: String, appTransID: String) {/*
                AlertDialog.Builder(requireContext())
                    .setTitle("User Cancel PaymentActivity")
                    .setMessage(String.format("zpTransToken: %s", zpTransToken))
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Cancel", null)
                    .show()*/
                    Log.d("Success", "Cancel")
                }

                override fun onPaymentError(
                    zaloPayError: ZaloPayError,
                    zpTransToken: String,
                    appTransID: String
                ) {
                    Log.d("Success", "Error")
                    /*  AlertDialog.Builder(requireContext())
                      .setTitle("PaymentActivity Fail")
                      .setMessage(
                          String.format(
                              "ZaloPayErrorCode: %s\nTransToken: %s",
                              zaloPayError.toString(),
                              zpTransToken
                          )
                      )
                      .setPositiveButton("OK", null)
                      .setNegativeButton("Cancel", null)
                      .show()*/
                }
            })
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("dsjd","djsd")
        ZaloPaySDK.getInstance().onResult(intent)
    }
}