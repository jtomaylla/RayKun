package com.ecandle.raykun.activities

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.ecandle.raykun.R
import com.ecandle.raykun.dialogs.DeleteProductDialog
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.ITEM_ID
import com.ecandle.raykun.helpers.SavedSettings
import com.ecandle.raykun.models.Item
import com.ecandle.raykun.models.ProductGroup
import com.simplemobiletools.commons.dialogs.RadioGroupDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.models.RadioItem
import kotlinx.android.synthetic.main.activity_product.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.TreeSet
import kotlin.collections.ArrayList

class LeadActivity : SimpleActivity() {
    private val LOG_TAG = LeadActivity::class.java.simpleName
    private var mDialogTheme = 0

    private var wasActivityInitialized = false
    
    
    lateinit var mItem: Item
    private var mItemDescription = ""
    private var mItemLongDescription = ""
    private var mRate  = ""
    private var mTaxRate = ""
    private var mTaxRate2 =  ""
    private var mGroupId = 0
    private var mUnit = ""

    val groups = TreeSet<Int>()
    val data = java.util.ArrayList<ProductGroup>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_cross)

        val intent = intent ?: return
        mDialogTheme = getDialogTheme()

        // Load Item Groups from Shared Preferences
        loadProductGroups()

        val itemId = intent.getIntExtra(ITEM_ID, 0)
        val item = dbHelper.getItemWithId(itemId)
        if (itemId != 0 && item == null) {
            finish()
            return
        }

        if (item != null) {
            mItem = item
            setupEditItem()
            mGroupId = mItem.group_id.toInt()
        } else {
            mItem = Item(0,"","","","","","","")

            setupNewItem()
        }

        updateDescription()
        updateLongDescription()
        updateRate()
        updateTaxRate()
        updateTaxRate2()
        updateGroupIdText()
        updateUnit()

        product_group_id.setOnClickListener { showGroupIdDialog() }
        
        updateTextColors(product_scrollview)

        wasActivityInitialized = true
    }

    private fun setupEditItem() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        supportActionBar?.title = resources.getString(R.string.edit_item)
        product_description.setText(mItem.description)
        product_long_description.setText(mItem.long_description)
        product_rate.setText(mItem.rate)
        product_taxrate.setText(mItem.taxrate)
        product_taxrate_2.setText(mItem.taxrate_2)
        product_description.movementMethod = LinkMovementMethod.getInstance()
        product_unit.setText(mItem.unit)
        product_group_id.setText(mItem.group_id)
    }

    private fun setupNewItem() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        supportActionBar?.title = resources.getString(R.string.new_item)
    }


    private fun updateDescription() {
        product_description.setText(mItem.description)
    }

    private fun updateLongDescription() {
        product_long_description.setText(mItem.long_description)
    }

    private fun updateRate() {
        product_rate.setText(mItem.rate)
    }

    private fun updateTaxRate() {
        product_taxrate.setText(mItem.taxrate)
    }

    private fun updateTaxRate2() {
        product_taxrate_2.setText(mItem.taxrate_2)
    }

    private fun updateGroupIdText() {
        product_group_id.setText(getGroupIdText(mGroupId))
    }

    private fun updateUnit() {
        product_unit.setText(mItem.unit)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        if (wasActivityInitialized) {
            menu.findItem(R.id.delete).isVisible = mItem.itemid != 0
            menu.findItem(R.id.exit).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> saveItem()
            R.id.delete -> deleteItem()
            R.id.exit -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun deleteItem() {
        DeleteProductDialog(this, arrayListOf(mItem.itemid)) {
            if (it) {
                dbHelper.deleteItems(arrayOf(mItem.itemid.toString()))
            }
            finish()
        }
    }

    private fun saveItem() {
        val newId = mItem.itemid
        val newDescription  = product_description.value
        if (newDescription.isEmpty()) {
            toast(R.string.title_empty)
            product_description.requestFocus()
            return
        }
        val newLongDescription = product_long_description.value
        val newRate = product_rate.value
        val newTaxRate= product_taxrate.value
        val newTaxRate2 = product_taxrate_2.value
        val newGroupId = getGroupIdValue(mGroupId)//mGroupId.toString() //product_group_id.value
        val newUnit = product_unit.value

        mItem = Item(itemid = newId,
                description = newDescription,
                long_description =  newLongDescription,
                rate = newRate,
                taxrate = newTaxRate,
                taxrate_2 = newTaxRate2,
                group_id = newGroupId,
                unit = newUnit
        )
        //mItem.id = 0
        Log.d(LOG_TAG,"mItem.itemid = $mItem.itemid")
        storeItem()
    }

    private fun storeItem() {
        if (mItem.itemid == 0) {
            // Add last Item value ID plus one for new item
            val items = dbHelper.fetchItems()
            mItem.itemid = items[items.size-1].itemid + 1
            //
            dbHelper.insertItem(mItem)
            Log.d(LOG_TAG,"item added")
            finish()
        } else {
            dbHelper.updateItem(mItem)
            Log.d(LOG_TAG,"item updated")
           itemUpdated()
        }
    }

    private fun itemUpdated() {
        toast(R.string.item_updated)
        finish()
    }


    private fun showGroupIdDialog() {
        showProductGroupIdDialog(mGroupId) {
            setGroupId(it)
        }
    }

    private fun setGroupId(groupid: Int) {
        mGroupId = groupid
        updateGroupIdText()
    }

    protected fun showProductGroupIdDialog(curGroupId: Int, callback: (minutes: Int) -> Unit) {
        hideKeyboard()

        val items = ArrayList<RadioItem>(groups.size + 1)

        groups.mapIndexedTo(items, { index, value ->
            RadioItem(index, getGroupIdText(value))
            RadioItem(index, getGroupIdText(value), value)
        })

        var selectedIndex = 0

        groups.forEachIndexed { index, value ->
            if (value == curGroupId)
                selectedIndex = index
        }

        RadioGroupDialog(this, items, selectedIndex) {
            callback(it as Int)
        }
    }


    fun loadProductGroups(){
        val savedSettings = SavedSettings(applicationContext)

        val jArray = JSONArray(savedSettings.getJsonInvoiceDataItem("items_groups"))
        val itemData1 = ProductGroup(
                0,
                getString(R.string.empty_name)
        )
        data.add(itemData1)
        groups.add(itemData1.id)
        // Extract data from json and store into ArrayList as class objects
        for (i in 0 until jArray.length()) {
            val jsondata: JSONObject = jArray.getJSONObject(i)
            val itemData = ProductGroup(
                    jsondata.getInt("id"),
                    jsondata.getString("name")
            )
            data.add(itemData)
            groups.add(itemData.id)
        }

    }

    fun getGroupIdText(groupid: Int) : String {
        return data.get(groupid).name
    }

    private fun getGroupIdValue(groupid: Int): String {
        return data.get(groupid).id.toString()
    }
}
