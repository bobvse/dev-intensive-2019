package ru.skillbranch.devintensive.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.ui.custom.AvatarImageView
import ru.skillbranch.devintensive.utils.Utils

class ChatAdapter(private val listener: (ChatItem) -> Unit) :
    RecyclerView.Adapter<ChatAdapter.ChatItemViewHolder>() {

    companion object {
        private const val ARCHIVE_TYPE = 0
        private const val SINGLE_TYPE = 1
        private const val GROUP_TYPE = 2
    }

    var items = listOf<ChatItem>()

    override fun getItemViewType(position: Int): Int = when (items[position].chatType) {
        ChatType.ARCHIVE -> ARCHIVE_TYPE
        ChatType.SINGLE -> SINGLE_TYPE
        ChatType.GROUP -> GROUP_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SINGLE_TYPE -> SingleViewHolder(
                inflater.inflate(
                    R.layout.item_chat_single,
                    parent,
                    false
                )
            )
            GROUP_TYPE -> GroupViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false))
            else -> ArchiveViewHolder(inflater.inflate(R.layout.item_chat_archive, parent, false))
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    fun updateData(data: List<ChatItem>) {
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

    abstract inner class ChatItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        LayoutContainer {
        override val containerView: View?
            get() = itemView

        abstract fun bind(item: ChatItem, listener: (ChatItem) -> Unit)
    }

    inner class ArchiveViewHolder(itemView: View) : ChatItemViewHolder(itemView) {

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            itemView.findViewById<TextView>(R.id.tv_date_archive).apply {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }
            itemView.findViewById<TextView>(R.id.tv_counter_archive).apply {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }
            itemView.findViewById<TextView>(R.id.tv_title_archive).text = item.title
            itemView.findViewById<TextView>(R.id.tv_message_archive).text = item.shortDescription
            itemView.setOnClickListener {
                listener.invoke(item)
            }
            itemView.findViewById<TextView>(R.id.tv_message_author_archive).apply {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = "@${item.author}"
            }
        }
    }

    inner class SingleViewHolder(itemView: View) : ChatItemViewHolder(itemView),
        ItemTouchViewHolder {

        override fun onItemSelected() {
            itemView.setBackgroundColor(
                Utils.getCurrntModeColor(
                    itemView.context,
                    R.attr.colorSelected
                )
            )
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(
                Utils.getCurrntModeColor(
                    itemView.context,
                    R.attr.colorItemView
                )
            )
        }

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            val ivAvatarSingle = itemView.findViewById<AvatarImageView>(R.id.iv_avatar_single)
            if (item.avatar == null) {
                Glide.with(itemView).clear(ivAvatarSingle)
                ivAvatarSingle.setInitials(item.initials)
            } else {
                Glide.with(itemView)
                    .load(item.avatar)
                    .into(ivAvatarSingle)
            }
            itemView.findViewById<View>(R.id.sv_indicator).visibility =
                if (item.isOnline) View.VISIBLE else View.GONE
            itemView.findViewById<TextView>(R.id.tv_date_single).apply {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }
            itemView.findViewById<TextView>(R.id.tv_counter_single).apply {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }
            itemView.findViewById<TextView>(R.id.tv_title_single).text = item.title
            itemView.findViewById<TextView>(R.id.tv_message_single).text = item.shortDescription
            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }
    }

    inner class GroupViewHolder(itemView: View) : ChatItemViewHolder(itemView),
        ItemTouchViewHolder {

        override fun onItemSelected() {
            itemView.setBackgroundColor(
                Utils.getCurrntModeColor(
                    itemView.context,
                    R.attr.colorSelected
                )
            )
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(
                Utils.getCurrntModeColor(
                    itemView.context,
                    R.attr.colorItemView
                )
            )
        }

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            itemView.findViewById<AvatarImageView>(R.id.iv_avatar_group).setInitials(item.initials)
            itemView.findViewById<TextView>(R.id.tv_date_group).apply {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }
            itemView.findViewById<TextView>(R.id.tv_counter_group).apply {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }
            itemView.findViewById<TextView>(R.id.tv_title_group).text = item.title
                    itemView.findViewById<TextView>(R.id.tv_message_group).text = item.shortDescription
                    itemView.findViewById<TextView>(R.id.tv_message_author).apply {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = "@${item.author}"
            }
            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }
    }
}
