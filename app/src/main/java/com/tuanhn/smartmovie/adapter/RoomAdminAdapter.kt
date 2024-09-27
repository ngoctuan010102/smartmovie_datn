package com.tuanhn.smartmovie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.data.model.entities.Room

class RoomAdminAdapter(
    private var listRoom: List<Room>,
    private val displayRoom: (Room) -> Unit
) : RecyclerView.Adapter<RoomAdminAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val tvRoomID: TextView = view.findViewById(R.id.tvRoomID)

        private val tvRoomName: TextView = view.findViewById(R.id.tvRoomName)

        private val tvRoomCapacity: TextView = view.findViewById(R.id.tvRoomCapacity)

        private val layoutRoom: LinearLayout = view.findViewById(R.id.layoutRoom)

        fun onBind(room: Room) {

            tvRoomID.text = room.room_id.toString()
            tvRoomName.text = room.room_name
            tvRoomCapacity.text = room.capacity.toString()

            layoutRoom.setOnClickListener {
                displayRoom(room)
            }


        }
    }

    fun updateRoom(newList: List<Room>) {
        val diffResult = DiffUtil.calculateDiff(DiffRoomAdmin(listRoom, newList))
        listRoom = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_room_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomAdminAdapter.ViewHolder, position: Int) {
        holder.onBind(listRoom[position])
    }


    override fun getItemCount() = listRoom.size

}