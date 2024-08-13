package com.tuanhn.smartmovie.screen.homescreen.bookticket

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
import com.google.firebase.database.FirebaseDatabase

import com.tuanhn.smartmovie.databinding.FragmentBookSeatsInformationBinding
import org.json.JSONArray
import org.json.JSONObject


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

    private fun setDataRealTime(edtEmail: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("bookedSeats").child(args.cinemaName)
            .child(args.time)
        for (item in args.listSeat) {
            myRef.child(item).setValue(edtEmail)
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

                setDataRealTime(edtEmail)

                val requestQueue = Volley.newRequestQueue(context)

                sendEmailDirectly(requestQueue, seats)

                val action =
                    BookSeatsInformationFragmentDirections.actionBookSeatsInformationToDetailFilm(
                        args.listFilm
                    )
                Navigation.findNavController(view).navigate(action)
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