package ru.skillbranch.devintensive.ui.adapters

import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_archive.*
import kotlinx.android.synthetic.main.item_chat_group.*
import kotlinx.android.synthetic.main.item_chat_single.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback.ItemTouchViewHolder
import ru.skillbranch.devintensive.utils.Utils

class ChatAdapter(val listener : (ChatItem) -> Unit) : RecyclerView.Adapter<ChatAdapter.ChatItemViewHolder>() {

    companion object {
        private const val ARCHIVE_TYPE = 0
        private const val SINGLE_TYPE = 1
        private const val GROUP_TYPE = 2
    }

    var items: List<ChatItem> = listOf()

    override fun getItemViewType(position: Int): Int = when(items[position].chatType) {
        ChatType.ARCHIVE -> ARCHIVE_TYPE
        ChatType.SINGLE -> SINGLE_TYPE
        ChatType.GROUP -> GROUP_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            SINGLE_TYPE -> SingleChatViewHolder(inflater.inflate(R.layout.item_chat_single, parent, false))
            GROUP_TYPE -> GroupChatViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false))
            ARCHIVE_TYPE -> ArchiveChatViewHolder(inflater.inflate(R.layout.item_chat_archive, parent, false))
            else -> SingleChatViewHolder(inflater.inflate(R.layout.item_chat_single, parent, false))
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        Log.d("M_ChatAdapter", "onBindViewHolder $position")
        holder.bind(items[position], listener)
    }

    fun updateData(data: List<ChatItem>) {
        Log.d("M_ChatAdapter", "update data adapter - new data ${data.size} hash: ${data.hashCode()}" +
                "old data ${items.size} hash: ${items.hashCode()}")

        val diffCallback = object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean =
                items[oldPosition].id == data[newPosition].id

            override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean =
                items[oldPosition].hashCode() == data[newPosition].hashCode()

            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = data.size

        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    abstract inner class ChatItemViewHolder(convertView: View) : RecyclerView.ViewHolder(convertView), LayoutContainer {
        override val containerView: View?
            get() = itemView

        abstract fun bind(item: ChatItem, listener: (ChatItem) -> Unit)
    }

    inner class SingleChatViewHolder(convertView: View) : ChatItemViewHolder(convertView), ItemTouchViewHolder {
        //val iv_avatar = convertView.findViewById<AvatarImageView>(R.id.iv_avatar_single)
        //val tv_title = convertView.findViewById<TextView>(R.id.tv_title_single)

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            //iv_avatar.setInitials(item.initials)
            //tv_title.text = item.shortDescription
            if (item.avatar == null) {
                Glide.with(itemView)
                    .clear(iv_avatar_single)
                //iv_avatar_single.setInitials(item.initials)
                iv_avatar_single.generateAvatar(item.initials, Utils.convertSpToPx(18))
            } else {
                Glide.with(itemView)
                    .load(item.avatar)
                    .into(iv_avatar_single)
            }

            sv_indicator.visibility = if (item.isOnline) View.VISIBLE else View.GONE

            with(tv_date_single) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }

            with(tv_counter_single) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }

            tv_title_single.text = item.title
            tv_message_single.text = item.shortDescription ?: "Сообщений ещё нет"
            itemView.setOnClickListener{
                listener.invoke(item)
            }
        }

        override fun onItemSelected() {
            val color = TypedValue()
            val theme = itemView.context.theme
            theme.resolveAttribute(R.attr.colorItemSelected, color, true)
            itemView.setBackgroundColor(color.data)
        }

        override fun onItemCleared() {
            val color = TypedValue()
            val theme = itemView.context.theme
            theme.resolveAttribute(R.attr.colorItemCleared, color, true)
            itemView.setBackgroundColor(color.data)
        }
    }

    inner class GroupChatViewHolder(convertView: View) : ChatItemViewHolder(convertView), ItemTouchViewHolder {

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            //iv_avatar_group.setInitials(item.initials)
            iv_avatar_group.generateAvatar(item.initials, Utils.convertSpToPx(18))

            with(tv_date_group) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }

            with(tv_counter_group) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }

            tv_title_group.text = item.title
            tv_message_group.text = item.shortDescription ?: "Сообщений ещё нет"
            val author = "@${item.author}"
            with(tv_message_author) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = author
            }
            itemView.setOnClickListener{
                listener.invoke(item)
            }
        }

        override fun onItemSelected() {
            val color = TypedValue()
            val theme = itemView.context.theme
            theme.resolveAttribute(R.attr.colorItemSelected, color, true)
            itemView.setBackgroundColor(color.data)
        }

        override fun onItemCleared() {
            val color = TypedValue()
            val theme = itemView.context.theme
            theme.resolveAttribute(R.attr.colorItemCleared, color, true)
            itemView.setBackgroundColor(color.data)
        }
    }

    inner class ArchiveChatViewHolder(convertView: View) : ChatItemViewHolder(convertView) {

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {

            with(tv_date_archive) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }

            with(tv_counter_archive) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }

            tv_message_archive.text = item.shortDescription
            val author = "@${item.author}"
            with(tv_message_author_archive) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = author
            }

            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }
    }
}