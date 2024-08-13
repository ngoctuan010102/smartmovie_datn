package com.tuanhn.smartmovie.screen.loginscreen

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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.databinding.FragmentRegisterBinding
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let { bind ->
            bind.btnRegister?.setOnClickListener {

                val userName = bind.edtUserName.text.toString()

                val passWord1 = bind.edtPassword1.text.toString()

                val passWord2 = bind.edtPassword2.text.toString()

                val email = bind.edtEmail.text.toString()

                if (userName.isEmpty() || passWord1.isEmpty() || passWord2.isEmpty() || email.isEmpty()) {

                    Toast.makeText(context, "Please fill in the blank field!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (passWord1 == passWord2) {

                        insertNewUser(view, userName, passWord1, email)

                    } else
                        Toast.makeText(context, "Password didn't match!", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun generateAESKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")

        keyGen.init(256) // Kích thước khóa (128, 192, 256)

        return keyGen.generateKey()
    }

    fun encryptAES(data: String, secretKey: SecretKey): String {

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

    // Lấy khóa AES từ SharedPreferences
    private fun getKey(context: Context): SecretKey? {

        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)

        val encodedKey = prefs.getString("aes_key", null) ?: return null

        // Giải mã khóa từ Base64
        val decodedKey = Base64.decode(encodedKey, Base64.DEFAULT)

        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }

    fun securityPassword(): SecretKey {

        var secretKey = getKey(requireContext())

        if (secretKey == null) {

            secretKey = generateAESKey()

            saveKey(secretKey)
        }
        return secretKey
    }

    private fun isValidEmail(email: String): Boolean {

        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"

        return email.matches(emailRegex.toRegex())
    }

    private fun insertNewUser(view: View, userName: String, passWord1: String, email: String) {

        val user = User(userName, passWord1, email)

        val database = FirebaseDatabase.getInstance()

        val myRef = database.getReference("users")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var userExists = false

                for (snapshot in dataSnapshot.children) {

                    val existingUser = snapshot.getValue(User::class.java)

                    if (existingUser?.userName == user.userName) {
                        userExists = true
                        break
                    }
                }

                if (userExists) {

                    Toast.makeText(context, "Tên người dùng đã tồn tại!", Toast.LENGTH_SHORT).show()
                } else {


                    val key = securityPassword()

                    val encryptString = encryptAES(user.passWord, key)

                    user.passWord = encryptString

                    if (isValidEmail(user.email)) {

                        myRef.child(user.userName).setValue(user)

                        Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

                        Navigation.findNavController(view)
                            .navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Lỗi kết nối database!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}