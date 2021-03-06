package com.ecandle.raykun.adapters

import android.support.v7.view.ActionMode
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback
import com.bignerdranch.android.multiselector.MultiSelector
import com.bignerdranch.android.multiselector.SwappingHolder
import com.ecandle.raykun.activities.MyBaseSimpleActivity
import com.simplemobiletools.commons.R
import com.simplemobiletools.commons.extensions.baseConfig
import com.simplemobiletools.commons.interfaces.MyAdapterListener
import com.simplemobiletools.commons.views.MyRecyclerView
import java.util.*

abstract class MyRecyclerViewAdapter(val activity: MyBaseSimpleActivity, val recyclerView: MyRecyclerView, val itemClick: ((Any) -> Unit)?)
    : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {
    val baseConfig = activity.baseConfig
    val resources = activity.resources!!
    val layoutInflater = activity.layoutInflater
    var primaryColor = baseConfig.primaryColor
    var textColor = baseConfig.textColor
    var itemViews = SparseArray<View>()
    val selectedPositions = HashSet<Int>()

    private val multiSelector = MultiSelector()
    private var actMode: ActionMode? = null

    abstract fun getActionMenuId(): Int

    abstract fun prepareItemSelection(view: View)

    abstract fun markItemSelection(select: Boolean, view: View?)

    abstract fun prepareActionMode(menu: Menu)

    abstract fun actionItemPressed(id: Int)

    abstract fun getSelectableItemCount(): Int

    fun isOneItemSelected() = selectedPositions.size == 1

    fun toggleItemSelection(select: Boolean, pos: Int) {
        if (select) {
            if (itemViews[pos] != null) {
                prepareItemSelection(itemViews[pos])
                selectedPositions.add(pos)
            }
        } else {
            selectedPositions.remove(pos)
        }

        markItemSelection(select, itemViews[pos])

        if (selectedPositions.isEmpty()) {
            finishActMode()
            return
        }

        updateTitle(selectedPositions.size)
    }

    private fun updateTitle(cnt: Int) {
        val selectableItemCount = getSelectableItemCount()
        val selectedCount = Math.min(cnt, selectableItemCount)
        val oldTitle = actMode?.title
        val newTitle = "$selectedCount / $selectableItemCount"
        if (oldTitle != newTitle) {
            actMode?.title = newTitle
            actMode?.invalidate()
        }
    }

    fun selectAll() {
        val cnt = itemCount
        for (i in 0 until cnt) {
            selectedPositions.add(i)
            notifyItemChanged(i)
        }
        updateTitle(cnt)
    }

    fun setupDragListener(enable: Boolean) {
        if (enable) {
            recyclerView.setupDragListener(object : MyRecyclerView.MyDragListener {
                override fun selectItem(position: Int) {
                    selectItemPosition(position)
                }

                override fun selectRange(initialSelection: Int, lastDraggedIndex: Int, minReached: Int, maxReached: Int) {
                    selectItemRange(initialSelection, lastDraggedIndex, minReached, maxReached)
                }
            })
        } else {
            recyclerView.setupDragListener(null)
        }
    }

    fun setupZoomListener(zoomListener: MyRecyclerView.MyZoomListener?) {
        recyclerView.setupZoomListener(zoomListener)
    }

    fun addVerticalDividers(add: Boolean) {
        if (recyclerView.itemDecorationCount > 0) {
            recyclerView.removeItemDecorationAt(0)
        }

        if (add) {
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL).apply {
                setDrawable(resources.getDrawable(R.drawable.divider))
                recyclerView.addItemDecoration(this)
            }
        }
    }

    fun selectItemPosition(pos: Int) {
        toggleItemSelection(true, pos)
    }

    fun selectItemRange(from: Int, to: Int, min: Int, max: Int) {
        if (from == to) {
            (min..max).filter { it != from }.forEach { toggleItemSelection(false, it) }
            return
        }

        if (to < from) {
            for (i in to..from) {
                toggleItemSelection(true, i)
            }

            if (min > -1 && min < to) {
                (min until to).filter { it != from }.forEach { toggleItemSelection(false, it) }
            }

            if (max > -1) {
                for (i in from + 1..max) {
                    toggleItemSelection(false, i)
                }
            }
        } else {
            for (i in from..to) {
                toggleItemSelection(true, i)
            }

            if (max > -1 && max > to) {
                (to + 1..max).filter { it != from }.forEach { toggleItemSelection(false, it) }
            }

            if (min > -1) {
                for (i in min until from) {
                    toggleItemSelection(false, i)
                }
            }
        }
    }

    fun finishActMode() {
        actMode?.finish()
    }

    fun updateTextColor(textColor: Int) {
        this.textColor = textColor
        notifyDataSetChanged()
    }

    private val adapterListener = object : MyAdapterListener {
        override fun toggleItemSelectionAdapter(select: Boolean, position: Int) {
            toggleItemSelection(select, position)
        }

        override fun getSelectedPositions() = selectedPositions

        override fun itemLongClicked(position: Int) {
            recyclerView.setDragSelectActive(position)
        }
    }

    private val multiSelectorMode = object : ModalMultiSelectorCallback(multiSelector) {
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            actionItemPressed(item.itemId)
            return true
        }

        override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
            super.onCreateActionMode(actionMode, menu)
            actMode = actionMode
            activity.menuInflater.inflate(getActionMenuId(), menu)
            return true
        }

        override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu): Boolean {
            prepareActionMode(menu)
            return true
        }

        override fun onDestroyActionMode(actionMode: ActionMode?) {
            super.onDestroyActionMode(actionMode)
            selectedPositions.forEach {
                markItemSelection(false, itemViews[it])
            }
            selectedPositions.clear()
            actMode?.title = ""
            actMode = null
        }
    }

    fun createViewHolder(layoutType: Int, parent: ViewGroup?): ViewHolder {
        val view = layoutInflater.inflate(layoutType, parent, false)
        return ViewHolder(view, adapterListener, activity, multiSelectorMode, multiSelector, itemClick)
    }

    fun bindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int, view: View) {
        itemViews.put(position, view)
        toggleItemSelection(selectedPositions.contains(position), position)
        holder.itemView.tag = holder
    }

    fun removeSelectedItems() {
        selectedPositions.sortedDescending().forEach {
            notifyItemRemoved(it)
            itemViews.put(it, null)
        }

        val newItems = SparseArray<View>()
        (0 until itemViews.size())
                .filter { itemViews[it] != null }
                .forEachIndexed { curIndex, i -> newItems.put(curIndex, itemViews[i]) }

        itemViews = newItems
        finishActMode()
    }

    class ViewHolder(view: View, val adapterListener: MyAdapterListener, val activity: MyBaseSimpleActivity, val multiSelectorCallback: ModalMultiSelectorCallback,
                     val multiSelector: MultiSelector, val itemClick: ((Any) -> Unit)?) : SwappingHolder(view, multiSelector) {

        fun bindView(any: Any, allowLongClick: Boolean = true, callback: (itemView: View, layoutPosition: Int) -> Unit): View {
            return itemView.apply {
                callback(this, layoutPosition)

                if (isClickable) {
                    setOnClickListener { viewClicked(any) }
                    setOnLongClickListener { if (allowLongClick) viewLongClicked() else viewClicked(any); true }
                } else {
                    setOnClickListener(null)
                    setOnLongClickListener(null)
                }
            }
        }

        private fun viewClicked(any: Any) {
            if (multiSelector.isSelectable) {
                val isSelected = adapterListener.getSelectedPositions().contains(adapterPosition)
                adapterListener.toggleItemSelectionAdapter(!isSelected, adapterPosition)
            } else {
                itemClick?.invoke(any)
            }
        }

        private fun viewLongClicked() {
            if (!multiSelector.isSelectable) {
                activity.startSupportActionMode(multiSelectorCallback)
                adapterListener.toggleItemSelectionAdapter(true, adapterPosition)
            }

            adapterListener.itemLongClicked(adapterPosition)
        }
    }
}
