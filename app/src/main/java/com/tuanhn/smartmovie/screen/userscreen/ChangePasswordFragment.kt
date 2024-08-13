package com.tuanhn.smartmovie.screen.userscreen

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.databinding.FragmentChangePasswordBinding
import com.tuanhn.smartmovie.screen.loginscreen.User
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class ChangePasswordFragment : Fragment() {
    private var binding: FragmentChangePasswordBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = context?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

        val savedString = sharedPreferences?.getString("current_user", "default_value")

        binding?.btnSubmit?.setOnClickListener {
            savedString?.let {
                checkAccount(view, savedString, binding?.edtOldPass?.text.toString())
            }

        }
    }

    private fun checkAccount(view: View, userName: String, passWord: String) {

        val user = User(userName, passWord)

        val database = FirebaseDatabase.getInstance()

        val myRef = database.getReference("users")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var userExists = false

                var email = ""

                for (snapshot in dataSnapshot.children) {

                    val existingUser = snapshot.getValue(User::class.java)

                    val key = getKey(requireContext())

                    var existingPassword = ""

                    key?.let {
                        existingUser?.let {
                            existingPassword = decryptAES(existingUser.passWord, key)
                        }
                    }

                    if (existingUser?.userName == user.userName && existingPassword == user.passWord) {

                        userExists = true

                        email = existingUser.email

                        break
                    }
                }

                if (userExists) {

                    if (binding?.edtNewPass1?.text.toString() == binding?.edtNewPass2?.text.toString()) {

                        updateUser(User(userName, binding?.edtNewPass1?.text.toString(), email), myRef)

                        Navigation.findNavController(view).navigate(R.id.accountInformationFragment)
                    }

                } else {
                    Toast.makeText(
                        context,
                        "Login failed, the user name doesn't exist $userName ${user.passWord}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Lỗi kết nối database!", Toast.LENGTH_SHORT).show()
            }
        })
    }

   private fun securityPassword(): SecretKey {

        var secretKey = getKey(requireContext())

        if (secretKey == null) {

            secretKey = generateAESKey()

            saveKey(secretKey)
        }
        return secretKey
    }

    private fun generateAESKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")

        keyGen.init(256) // Kích thước khóa (128, 192, 256)

        return keyGen.generateKey()
    }

    private fun encryptAES(data: String, secretKey: SecretKey): String {

        val cipher = Cipher.getInstance("AES")

        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val encryptedBytes = cipher.doFinal(data.toByteArray())

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }


    // Lưu khóa AES vào SharedPreferences (với mã hóa)
    private fun saveKey(secretKey: SecretKey) {

        val prefs = requireContext().getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)

        val editor = prefs.edit()

        // Mã hóa khóa thành Base64
        val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)

        editor.putString("aes_key", encodedKey)

        editor.apply()
    }

    fun updateUser(user: User, myRef: DatabaseReference) {
        val key = securityPassword()

        val encryptString = encryptAES(user.passWord, key)

        user.passWord = encryptString

        if (isValidEmail(user.email)) {
            myRef.child(user.userName).setValue(user)
        }
    }

    private fun isValidEmail(email: String): Boolean {

        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"

        return email.matches(emailRegex.toRegex())
    }

    fun decryptAES(encryptedData: String, secretKey: SecretKey): String {

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