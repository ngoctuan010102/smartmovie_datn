package com.tuanhn.smartmovie.screen.loginscreen

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.databinding.FragmentLoginBinding
import com.tuanhn.smartmovie.screen.homescreen.MainActivity
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null

    private val handler = Handler(Looper.getMainLooper())

    private var isButtonClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnRegister?.setOnClickListener {

            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding?.let { bind ->

            bind.btnLogin?.setOnClickListener {

                if (isButtonClicked) return@setOnClickListener
                isButtonClicked = true

                handler.postDelayed({
                    isButtonClicked = false
                }, 1000)

                bind.loginProgressBar.visibility = View.VISIBLE

                val userName = bind.edtUserName.text.toString()

                val password = bind.edtPassword.text.toString()

                if (userName.isEmpty() || password.isEmpty()) {

                    Toast.makeText(context, "Please fill in the blank field!", Toast.LENGTH_SHORT)
                        .show()

                    isButtonClicked = false
                } else {
                    checkAccount(view, userName, password)
                }
            }

        }
    }

    private fun checkAccount(view: View, userName: String, passWord1: String) {

        val user = User(userName, passWord1)

        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->

                var userExists = false

                for (document in result) {

                    val passWord = document.getString("passWord")

                    val userName = document.getString("userName")

                    val key = getKey(requireContext())

                    var existingPassword = ""

                    key?.let {
                        passWord?.let {
                            existingPassword = decryptAES(passWord, key)
                        }
                    }

                    if (userName == user.userName && existingPassword == user.passWord) {
                        userExists = true
                        break
                    }
                }
                if (userExists) {

                    val sharedPreferences =
                        context?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

                    val editor = sharedPreferences?.edit()

                    editor?.putString("current_user", userName) // Ví dụ lưu một chuỗi

                    editor?.apply()

                    val activity = requireActivity()

                    val intent = Intent(activity.applicationContext, MainActivity::class.java)

                    startActivity(intent)

                    activity.finish()

                } else {
                    Toast.makeText(
                        context,
                        "Login failed, the user name doesn't exist",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                binding?.loginProgressBar?.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun decryptAES(encryptedData: String, secretKey: SecretKey): String {

        val cipher = Cipher.getInstance("AES")

        cipher.init(Cipher.DECRYPT_MODE, secretKey)

        val decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT)

        val decryptedBytes = cipher.doFinal(decodedBytes)

        return String(decryptedBytes)
    }

    private fun getKey(context: Context): SecretKey? {

        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)

        val encodedKey = prefs.getString("aes_key", null) ?: return null

        val decodedKey = Base64.decode(encodedKey, Base64.DEFAULT)

        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }
}