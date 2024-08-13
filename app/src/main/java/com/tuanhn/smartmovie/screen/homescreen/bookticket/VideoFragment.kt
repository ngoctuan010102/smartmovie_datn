package com.tuanhn.smartmovie.screen.homescreen.bookticket

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.tuanhn.smartmovie.databinding.FragmentVideoBinding



class VideoFragment : Fragment() {
    private val args: VideoFragmentArgs by navArgs()
    private var binding: FragmentVideoBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val videoView = binding?.videoView
        val videoUri: Uri = Uri.parse(args.videoURL)
        videoView?.setVideoURI(videoUri)

        // Thêm MediaController để điều khiển video (tùy chọn)
        val mediaController = MediaController(binding?.videoView?.context)
        mediaController.setAnchorView(videoView)
        videoView?.setMediaController(mediaController)
        videoView?.start()

        videoView?.setOnCompletionListener {
            Navigation.findNavController(view).popBackStack()
        }
    }
}