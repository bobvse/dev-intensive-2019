package ru.skillbranch.devintensive.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.ui.custom.AvatarImageView

class UserAdapter(
    private val listener: (UserItem) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    var items = listOf<UserItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_list, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) =
        holder.bind(items[position], listener)

    fun updateData(data: List<UserItem>) {
        val diffCallBack = object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition].id == data[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition] == data[newItemPosition]

            override fun getOldListSize(): Int = items.size
            override fun getNewListSize(): Int = data.size
        }
        val diffResult = DiffUtil.calculateDiff(diffCallBack)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun bind(user: UserItem, listener: (UserItem) -> Unit) {
            val ivAvatarUser = itemView.findViewById<AvatarImageView>(R.id.iv_avatar_user)
            if (user.avatar != null) {
                Glide.with(itemView)
                    .load(user.avatar)
                    .into(ivAvatarUser)
            } else {
                Glide.with(itemView).clear(ivAvatarUser)
                ivAvatarUser.setInitials(user.initials ?: "??")
            }
            itemView.findViewById<View>(R.id.sv_indicator).visibility = if (user.isOnline) View.VISIBLE else View.GONE
            itemView.findViewById<TextView>(R.id.tv_user_name).text = user.fullName
            itemView.findViewById<TextView>(R.id.tv_last_activity).text = user.lastActivity
            itemView.findViewById<ImageView>(R.id.iv_selected).visibility = if (user.isSelected) View.VISIBLE else View.GONE
            itemView.setOnClickListener { listener.invoke(user) }
        }
    }

}