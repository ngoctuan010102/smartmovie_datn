package com.tuanhn.smartmovie.screen.homescreen.bookticket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FirebaseFirestore
import com.tuanhn.smartmovie.data.model.entities.Bill

import com.tuanhn.smartmovie.databinding.FragmentBookSeatsInformationBinding
import com.tuanhn.smartmovie.screen.homescreen.MainActivity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class BookSeatsInformationFragment : Fragment() {

    private val args: BookSeatsInformationFragmentArgs by navArgs()

    private var binding: FragmentBookSeatsInformationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBookSeatsInformationBinding.inflate(inflater, container, false)

        return binding?.root
    }

    private fun setBillRealTime(
        seats: String,
        totalMoney: Float,
        cinemaName: String,
        currentDate: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("bills")

        documentRef.get().addOnSuccessListener { result ->

            val billId = result.size() + 1

            setUp(db, billId, seats, totalMoney, cinemaName, currentDate)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.tvTotal?.text = "${args.total} VND"

        val seats = StringBuilder()
        for (item in args.listSeat.indices) {
            if (item == 0)
                seats.append(args.listSeat[item])
            else
                seats.append(" - ${args.listSeat[item]}")
        }

        binding?.tvSeat?.text = "$seats"

        binding?.btnLastConfirm?.setOnClickListener {

            val edtEmail = binding?.edtEmail?.text.toString()

            val edtNumber = binding?.edtNumber?.text.toString()

            if (edtEmail.isNullOrEmpty() || edtNumber.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Please fill in the blank field before submit",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

                val currentDate = dateFormat.format(Date())

                setBillRealTime(seats.toString(), args.total, args.cinemaName, currentDate)

                setDataRealTime(edtEmail, currentDate)

                val requestQueue = Volley.newRequestQueue(context)

                sendEmailDirectly(requestQueue, seats)

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                /*val action =
                    BookSeatsInformationFragmentDirections.actionBookSeatsInformationToDetailFilm(
                        args.listFilm
                    )
                Navigation.findNavController(view).navigate(action)*/
            }
        }
    }

    private fun sendEmailDirectly(requestQueue: RequestQueue, seats: StringBuilder) {

        val recipient = binding?.edtEmail?.text.toString()

        val subject = "${args.cinemaName} sent you ticket"

        val message = "Your Total Cost you paid: ${args.total} VND with seats: $seats"

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
}