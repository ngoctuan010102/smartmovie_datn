package com.tuanhn.smartmovie

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.tuanhn.smartmovie.adapter.AdjustMovieAdapter
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.databinding.FragmentAdjustingMovieAdminBinding

class AdjustingMovieAdminFragment : Fragment() {

    private lateinit var binding: FragmentAdjustingMovieAdminBinding

    private val PICK_IMAGE_REQUEST = 71

    private val PICK_VIDEO_REQUEST = 72

    private var adapter: AdjustMovieAdapter? = null

    private var filePathPoster: Uri? = null

    private var filePathVideo: Uri? = null

    private var fileDownloadVideo: Uri? = null

    private var fileDownloadImage: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdjustingMovieAdminBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEvent()

        initialAdapter()

        observeData()
    }

    private fun observeData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("films").get().addOnSuccessListener { result ->
            val list: MutableList<Film> = mutableListOf()
            for (document in result) {

                val film = document.toObject(Film::class.java)

                list.add(film)
            }
            adapter?.updateMovies(list)
        }
    }

    private fun displayFilm(film: Film) {
        binding?.edtFilmID?.setText(film.film_id.toString())
        binding?.edtFilmName?.setText(film.film_name)
        binding?.edtReleaseDate?.setText(film.releaseDate)
        binding?.edtDescription?.setText(film.synopsis_long)

        film.poster?.let {
            filePathPoster = Uri.parse(it)
        }

        setPoster()

        film.film_trailer?.let {
            filePathVideo = Uri.parse(it)
        }
        setVideo()
    }

    private fun initialAdapter() {
        adapter = AdjustMovieAdapter(listOf(), this@AdjustingMovieAdminFragment::displayFilm)

        binding?.rcvAdjustMovie?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding?.rcvAdjustMovie?.adapter = adapter

    }

    private fun setEvent() {

        binding?.swipeAdjustment?.setOnRefreshListener {
            observeData()
            binding?.swipeAdjustment?.isRefreshing = false
        }

        binding?.btnTrailer?.setOnClickListener {
            chooseVideo()
        }

        binding?.btnPoster?.setOnClickListener {
            chooseImage()
        }
        binding?.videoTrailer?.setOnClickListener {
            setVideo()
        }

        binding?.btnFind?.setOnClickListener {
            val keyWord = binding?.edtFilmID?.text.toString()
            findFilm(keyWord)
        }

        binding?.btnDelete?.setOnClickListener {
            val id = binding?.edtFilmID?.text.toString()
            delete(id.trim().toInt())
            deleteVideo(id.toInt())
            deleteImage(id.toInt())
        }

        binding?.btnUpdate?.setOnClickListener {

            val film_id = binding?.edtFilmID?.text.toString().toInt()

            uploadMediaAndAddFilm(film_id)
        }

        binding?.btnAdd?.setOnClickListener {
            /*var id = 0
            val newId = id!! + 1*//*
            val film = getDataFromUI(0)*/
            val film_id = binding?.edtFilmID?.text.toString().toInt()

            uploadMediaAndAddFilm(film_id)
        }
    }
    private fun deleteVideo(filmId: Int) {
        val storageReference = FirebaseStorage.getInstance().reference
        val videoRef = storageReference.child("videos/${filmId}.mp4")

        videoRef.delete()
            .addOnSuccessListener {
                // Video đã được xóa thành công
                Toast.makeText(requireContext(), "Video deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Xóa không thành công
                Toast.makeText(requireContext(), "Error deleting video: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun deleteImage(filmId: Int) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("images/${filmId}.jpg")

        imageRef.delete()
            .addOnSuccessListener {
                // Ảnh đã được xóa thành công
                Toast.makeText(requireContext(), "Image deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Xóa không thành công
                Toast.makeText(requireContext(), "Error deleting image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun delete(id: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("films")
            .whereEqualTo("film_id", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Delete each document that matches the query
                    document.reference.delete()
                        .addOnSuccessListener {
                            // Document deleted successfully
                            observeData()
                        }
                        .addOnFailureListener { e ->
                            // Error deleting the document
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle error when retrieving documents
                //     Toast.makeText(this, "Error retrieving documents: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addFilm(film: Film) {
        val db = FirebaseFirestore.getInstance()
        db.collection("films").document(film.film_name).set(film).addOnSuccessListener {
            observeData()
        }
    }

    private fun uploadMediaAndAddFilm(film_id: Int) {

        val storageReference = FirebaseStorage.getInstance().reference

        val videoRef = storageReference.child("videos/$film_id.mp4")
        val imageRef = storageReference.child("images/$film_id.jpg")

        val uploadTasks = mutableListOf<Task<Uri>>()

        filePathVideo?.let {
            val uploadTask = videoRef.putFile(it).continueWithTask { videoRef.downloadUrl }
            uploadTasks.add(uploadTask)
        }

        filePathPoster?.let {
            val uploadTask = imageRef.putFile(it).continueWithTask { imageRef.downloadUrl }
            uploadTasks.add(uploadTask)
        }

        Tasks.whenAllSuccess<Uri>(uploadTasks).addOnSuccessListener { results ->
            // Cập nhật các URL download của video và ảnh vào đối tượng film
            if (results.isNotEmpty()) {
                fileDownloadVideo = results.getOrNull(0) // Video URL
                fileDownloadImage = results.getOrNull(1) // Image URL
            }

            val film = getDataFromUI(0)
            film?.let {
                addFilm(film)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to upload media", Toast.LENGTH_SHORT).show()
        }
    }

    /* private fun uploadVideo(film_id: Int) {
         if (filePathVideo != null) {
             val storageReference = FirebaseStorage.getInstance().reference
             val videoRef = storageReference.child("videos/${film_id}.mp4")

             // Upload video lên Firebase Storage
             videoRef.putFile(filePathVideo!!)
                 .addOnSuccessListener {
                     // Sau khi upload thành công, lấy URL download của video
                     videoRef.downloadUrl.addOnSuccessListener { uri ->
                         fileDownloadVideo = uri
                         val film = getDataFromUI(0)
                         film?.let {
                             addFilm(film)
                         }
                     }
                 }
                 .addOnFailureListener {
                     Toast.makeText(requireContext(), "Failed to upload video", Toast.LENGTH_SHORT)
                         .show()
                 }
         } else {
             Toast.makeText(requireContext(), "No video selected", Toast.LENGTH_SHORT).show()
         }
     }

     private fun uploadImage(film_id: Int) {
         if (filePathPoster != null) {
             val storageReference = FirebaseStorage.getInstance().reference
             val imageRef = storageReference.child("images/${film_id}.jpg")

             // Upload ảnh lên Firebase Storage
             imageRef.putFile(filePathPoster!!)
                 .addOnSuccessListener {
                     // Sau khi upload thành công, lấy URL download của ảnh
                     imageRef.downloadUrl.addOnSuccessListener { uri ->
                         fileDownloadImage = uri
                         val film = getDataFromUI(0)
                         film?.let {
                             addFilm(film)
                         }
                     }
                 }
                 .addOnFailureListener {
                     Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT)
                         .show()
                 }
         } else {
             Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
         }
     }
 */
    private fun findFilm(keyWord: String) {
        try {

            val ID = keyWord.trim().toInt()

            val db = FirebaseFirestore.getInstance()

            val listFilm: MutableList<Film> = mutableListOf()

            db.collection("films").get().addOnSuccessListener { result ->
                for (document in result) {

                    val film = document.toObject<Film>()
                    if (film.film_id == ID) {
                        listFilm.add(film)
                        break
                    }
                }
                adapter?.updateMovies(listFilm)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Please enter a valid ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDataFromUI(id: Int): Film? {
        val edtReleaseDate = binding?.edtReleaseDate?.text.toString()
        val edtDescription = binding?.edtDescription?.text.toString()
        val edtFilmName = binding?.edtFilmName?.text.toString()
        val edtID = binding?.edtFilmID?.text.toString()


        return if (edtReleaseDate.isNotEmpty() && edtDescription.isNotEmpty() && edtFilmName.isNotEmpty()) {
            val film = Film(
                id,
                edtID.toInt(),
                0,
                "",
                edtFilmName,
                "",
                edtReleaseDate,
                fileDownloadVideo.toString() ?: "",
                edtDescription,
                fileDownloadImage.toString() ?: "",
                "",
                true
            )

            film
        } else {
            Toast.makeText(requireContext(), "Please fill in the blank", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun setVideo() {
        filePathVideo?.let {
            binding?.videoTrailer?.setVideoURI(filePathVideo)
            binding?.videoTrailer?.start()
        }
    }

    private fun chooseVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST)
    }

    // Hàm để mở album ảnh
    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    // Xử lý khi người dùng chọn ảnh
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {

            filePathPoster = data.data
            setPoster()
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {

            filePathVideo = data.data

            setVideo()
        }
    }

    private fun setPoster() {
        filePathPoster?.let {
            Picasso.get()
                .load(filePathPoster) // Placeholder khi ảnh đang tải // Hình ảnh khi có lỗi
                .into(binding?.imgPoster)
        }
    }
}