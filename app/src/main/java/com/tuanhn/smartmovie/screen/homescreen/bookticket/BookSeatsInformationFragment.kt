package com.tuanhn.smartmovie.screen.homescreen.bookticket

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.tuanhn.smartmovie.data.model.entities.Bill
import com.tuanhn.smartmovie.data.model.entities.Coupon
import com.tuanhn.smartmovie.databinding.FragmentBookSeatsInformationBinding
import com.tuanhn.smartmovie.payment.Api.CreateOrder
import com.tuanhn.smartmovie.screen.homescreen.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class BookSeatsInformationFragment : Fragment() {


    private val args: BookSeatsInformationFragmentArgs by navArgs()

    private var binding: FragmentBookSeatsInformationBinding? = null

    private var totalCount: Float = 0F

    private var currentSelectedVoucher: Int = 0

    private var token: String? = null

    val listCoupon: MutableList<Coupon> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBookSeatsInformationBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()

        totalCount = args.total

        binding?.tvTotal?.text = "$totalCount VND"

        val seats = StringBuilder()

        for (item in args.listSeat.indices) {
            if (item == 0)
                seats.append(args.listSeat[item])
            else
                seats.append(" - ${args.listSeat[item]}")
        }
        binding?.tvSeat?.text = "$seats"

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        IsLoading()

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX)

        // Handle CreateOrder button click
        binding?.btnLastConfirm?.setOnClickListener {
            val seats = binding?.tvSeat?.text.toString()

            val edtEmail = binding?.edtEmail?.text.toString()

            val edtNumber = binding?.edtNumber?.text.toString()

            if (edtEmail.isNullOrEmpty() || edtNumber.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Please fill in the blank field before submit",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                lifecycleScope.launch {
                    CreateOrderClickHandler()
                }
            }
        }

        /*    // Handle Pay button click
            binding?.btnPay?.setOnClickListener {
                PayButtonClickHandler()
            }*/
    }

    private fun finishPayment() {


        val seats = binding?.tvSeat?.text.toString()

        val edtEmail = binding?.edtEmail?.text.toString()

        val edtNumber = binding?.edtNumber?.text.toString()


        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val currentDate = dateFormat.format(Date())

        setBillRealTime(seats, args.cinemaName, currentDate)

        setDataRealTime(edtEmail, currentDate)

        val requestQueue = Volley.newRequestQueue(context)

        sendEmailDirectly(requestQueue, seats)

        val intent = Intent(requireContext(), MainActivity::class.java)

        startActivity(intent)
    }

    private fun observeData() {
        val db = FirebaseFirestore.getInstance()

        val listCoupon: MutableList<Coupon> = mutableListOf()

        db.collection("coupons").get().addOnSuccessListener { result ->
            for (document in result) {

                val coupon = document.toObject<Coupon>()

                listCoupon.add(coupon)
            }
            getCoupons(db, listCoupon)
        }

    }

    private fun getCoupons(db: FirebaseFirestore, coupons: List<Coupon>) {

        val list: MutableList<String> = mutableListOf()

        list.add("Voucher")

        val sharedPreferences = context?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

        val currentUser = sharedPreferences?.getString("current_user", "default_value")

        db.collection("userCoupons").get().addOnSuccessListener { result ->
            for (document in result) {
                if (document.getString("user") == currentUser) {
                    val id = document.getLong("couponId")

                    id?.let {
                        for (item in coupons.indices) {
                            if (coupons[item].id == id.toInt()) {
                                if (isValidCoupon(coupons[item])) {
                                    list.add("Voucher ${coupons[item].discountValue}%")
                                    listCoupon.add(coupons[item])
                                }
                                break
                            }
                        }
                    }

                }
            }
            setDataSpinner(list)
            setEventSpinner()
        }
    }

    private fun setDataSpinner(list: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, list)

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        binding?.spinner?.adapter = adapter
    }

    private fun setEventSpinner() {

        binding?.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                val stringNumber = StringBuilder()

                currentSelectedVoucher = if (position != 0)
                    listCoupon[position - 1].id
                else
                    0
                for (item in selectedItem.indices) {
                    if (selectedItem[item].isDigit())
                        stringNumber.append(selectedItem[item])
                }

                if (stringNumber.toString() != "") {

                    val numberDiscount: Float = 1F - (stringNumber.toString().toFloat() / 100)

                    totalCount = args.total * numberDiscount

                    binding?.tvTotal?.text = "$totalCount VND"

                    Log.d("sd", "sh Selected $totalCount $numberDiscount")
                } else {
                    totalCount = args.total
                    binding?.tvTotal?.text = "$totalCount VND"
                }
                Log.d("sd", "success Selected")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                totalCount = args.total
                binding?.tvTotal?.text = "$totalCount VND"
                Log.d("sd", "failed Selected")
            }
        }
    }

    private fun isValidCoupon(coupon: Coupon): Boolean {
        val currentDate = LocalDate.now()

        val startDate = coupon.startDate

        val endDate = coupon.endDate

        var isValidCoupon = false

        isValidCoupon = currentDate.isAfter(convertDatetime(startDate).minusDays(1))

        isValidCoupon = currentDate.isBefore(convertDatetime(endDate).plusDays(1))

        return isValidCoupon
    }

    private fun convertDatetime(dateString: String): LocalDate {

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return LocalDate.parse(dateString, formatter)
    }

    private fun setBillRealTime(
        seats: String,
        cinemaName: String,
        currentDate: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("bills")

        documentRef.get().addOnSuccessListener { result ->

            val billId = result.size() + 1

            setUp(db, billId, seats, totalCount, cinemaName, currentDate)
        }
    }

    private fun setUp(
        db: FirebaseFirestore,
        billId: Int,
        seats: String,
        totalMoney: Float,
        cinemaName: String,
        currentDate: String
    ) {
        val sharedPreferences = context?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

        val currentUser = sharedPreferences?.getString("current_user", "default_value")

        currentUser?.let {
            val item = Bill(
                billId,
                args.listFilm.film_id,
                cinemaName, seats, totalMoney,
                currentUser,
                currentDate
            )
            db.collection("bills").add(item)
        }
    }

    private fun setDataRealTime(edtEmail: String, currentDate: String) {

        val db = FirebaseFirestore.getInstance()

        val bookedSeats = hashMapOf<String, String>()

        for (item in args.listSeat) {
            bookedSeats[item] = edtEmail
        }

        // Tham chiếu đến document cần cập nhật
        val documentRef = db.collection("bookedSeats")
            .document(currentDate)
            .collection(args.cinemaName)
            .document(args.time)

        // Ghi tất cả ghế vào document
        documentRef.set(bookedSeats)
            .addOnSuccessListener {
                // Xử lý khi ghi dữ liệu thành công
                Log.d("Firestore", "Seats added successfully")
            }
            .addOnFailureListener { e ->
                // Xử lý khi có lỗi
                Log.w("Firestore", "Error adding seats", e)
            }
    }


    private fun sendEmailDirectly(requestQueue: RequestQueue, seats: String) {

        val recipient = binding?.edtEmail?.text.toString()

        val subject = "${args.cinemaName} sent you ticket"

        val message = "Your Total Cost you paid: $totalCount VND with seats: $seats"

        val url = "https://api.sendgrid.com/v3/mail/send"

        val jsonObject = JSONObject().apply {
            put("personalizations", JSONArray().put(JSONObject().apply {
                put("to", JSONArray().put(JSONObject().apply {
                    put("email", recipient)
                }))
                put("subject", subject)
            }))
            put("from", JSONObject().apply {
                put("email", "hongocbin1999@gmail.com")
            })
            put("content", JSONArray().put(JSONObject().apply {
                put("type", "text/plain")
                put("value", message)
            }))
        }

        val requestBody = jsonObject.toString()

        val request = object : JsonObjectRequest(
            Method.POST, url, null,
            Response.Listener { response ->
                Log.d("Email", "Email sent successfully: $response")
            },
            Response.ErrorListener { error ->
                Log.e("Email", "Failed to send email: ${error.message}")
            }
        ) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): Map<String, String> {
                return mapOf("Authorization" to "Bearer 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")
            }
        }

        requestQueue.add(request)
    }

    fun IsLoading() {

        binding?.lblZpTransToken?.visibility = View.INVISIBLE
        binding?.txtToken?.visibility = View.INVISIBLE
        binding?.btnPay?.visibility = View.INVISIBLE
    }

    private fun IsDone() {
        binding?.lblZpTransToken?.visibility = View.VISIBLE
        binding?.txtToken?.visibility = View.VISIBLE
        binding?.btnPay?.visibility = View.VISIBLE
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
                withContext(Dispatchers.Main) {

                    if (code == "1") {
                        // binding?.lblZpTransToken?.text = "zptranstoken"
                        // binding?.txtToken?.text = data.getString("zp_trans_token")
                        token = data.getString("zp_trans_token")
                        finishPayment()
                        PayButtonClickHandler()
                    } else
                        Log.d("Amount", "totalCount.toString()")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun PayButtonClickHandler() {
        token?.let {
            ZaloPaySDK.getInstance().payOrder(requireActivity(), it, "demozpdk://app", object :
                PayOrderListener {
                override fun onPaymentSucceeded(
                    transactionId: String,
                    transToken: String,
                    appTransID: String
                ) {
                    Log.d("Success", "Success")
                    finishPayment()
                    //   finishPay(transactionId, transToken)
                }

                override fun onPaymentCanceled(zpTransToken: String, appTransID: String) {
                    Log.d("Success", "Cancel")
                }

                override fun onPaymentError(
                    zaloPayError: ZaloPayError,
                    zpTransToken: String,
                    appTransID: String
                ) {
                    Log.d("Success", "Error")
                }
            })
        }
    }

    fun handleNewIntent(intent: Intent) {
        finishPayment()
        ZaloPaySDK.getInstance().onResult(intent)
    }

}