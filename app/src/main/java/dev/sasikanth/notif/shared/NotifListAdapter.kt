package dev.sasikanth.notif.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.CircularRevealFrameLayout
import dev.sasikanth.notif.R
import dev.sasikanth.notif.data.NotifItem
import dev.sasikanth.notif.databinding.NotifItemBinding

class NotifListAdapter(private val notifAdapterListener: NotifAdapterListener) :
    ListAdapter<NotifItem, RecyclerView.ViewHolder>(NotifDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotifItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NotifItemViewHolder) {
            holder.bind(
                notifItem = getItem(position),
                notifAdapterListener = notifAdapterListener,
                lastItem = position == currentList.lastIndex
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    class NotifItemViewHolder private constructor(private val binding: NotifItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): NotifItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NotifItemBinding.inflate(layoutInflater, parent, false)
                return NotifItemViewHolder(binding)
            }
        }

        val touchCoordinates = floatArrayOf(0f, 0f)

        init {
            binding.notifCard.setOnTouchListener { _, motionEvent ->
                if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                    touchCoordinates[0] = motionEvent.x
                    touchCoordinates[1] = motionEvent.y
                }
                return@setOnTouchListener false
            }

            binding.notifPin.setOnTouchListener { _, motionEvent ->
                if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                    touchCoordinates[0] = (itemView.width - 28.px).toFloat()
                    touchCoordinates[1] = 28.px.toFloat()
                }
                return@setOnTouchListener false
            }
        }

        fun bind(
            notifItem: NotifItem,
            notifAdapterListener: NotifAdapterListener,
            lastItem: Boolean
        ) {
            binding.notifItem = notifItem
            binding.notifAdapterListener = notifAdapterListener
            binding.notifPinnedContent.tag = notifItem.isPinned
            binding.executePendingBindings()

            if (lastItem) {
                binding.notifContent.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 4.px
                }
            } else {
                binding.notifContent.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 0
                }
            }

            if (notifItem.isPinned) {
                binding.notifPinnedContent.isVisible = true
                binding.notifOriginalContent.isGone = true
                binding.notifPin.setImageResource(R.drawable.ic_notif_pinned)
            } else {
                binding.notifPinnedContent.isGone = true
                binding.notifOriginalContent.isVisible = true
                binding.notifPin.setImageResource(R.drawable.ic_notif_pin)
            }
        }

        fun notifItem(): NotifItem? {
            return binding.notifItem
        }

        fun isPinned(): Boolean {
            return binding.notifItem?.isPinned ?: false
        }

        fun getNotifContentView(): ConstraintLayout {
            return binding.notifContent
        }

        fun getPinnedContent(): CircularRevealFrameLayout {
            return binding.notifPinnedContent
        }

        fun getOriginalContent(): ConstraintLayout {
            return binding.notifOriginalContent
        }

        fun getNotifPinView(): ImageView {
            return binding.notifPin
        }
    }
}

object NotifDiffCallback : DiffUtil.ItemCallback<NotifItem>() {
    override fun areItemsTheSame(oldItem: NotifItem, newItem: NotifItem): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(oldItem: NotifItem, newItem: NotifItem): Boolean {
        return oldItem == newItem
    }
}

class NotifAdapterListener(
    private val pinNote: (notifId: Long, isPinned: Boolean) -> Unit,
    private val onNotifClick: (notifItem: NotifItem) -> Unit
) {
    fun onNotifItemClick(notifItem: NotifItem) {
        onNotifClick(notifItem)
    }

    fun pinNotifItem(notifItem: NotifItem): Boolean {
        pinNote(notifItem._id, !notifItem.isPinned)
        return true
    }
}
