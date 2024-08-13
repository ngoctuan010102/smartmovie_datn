package com.tuanhn.smartmovie.screen.userscreen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.databinding.FragmentAccountInformationBinding
import com.tuanhn.smartmovie.databinding.FragmentUserBinding


class AccountInformationFragment : Fragment() {

    private var binding: FragmentAccountInformationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAccountInformationBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = context?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

        val savedString = sharedPreferences?.getString("current_user", "default_value")

        binding?.tvName?.text = savedString

        binding?.tvPasss?.text = "*********"

        binding?.layoutChangePass?.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_accountInformationFragment_to_changePasswordFragment)
        }
    }
}