package com.ecandle.raykun.activities
import android.os.Bundle
import android.util.Log
import com.ecandle.raykun.R
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.extensions.launchNewItemIntent
import com.ecandle.raykun.fragments.ProductListFragment
import com.ecandle.raykun.helpers.ConnectionDetector
import com.ecandle.raykun.helpers.USER_ID
import com.ecandle.raykun.models.Item
import com.ecandle.raykun.tasks.loadItemDataTask
import com.simplemobiletools.commons.extensions.beVisible
import kotlinx.android.synthetic.main.activity_product_list.*

class ProductListActivity : SimpleActivity() {
    private val LOG_TAG = ProductListActivity::class.java.simpleName
    private var mUserId: String? = null
    lateinit var mItem: Item
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        var connectionDetector = ConnectionDetector(this)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_cross)

        supportActionBar?.title = resources.getString(R.string.items)

        product_fab.setOnClickListener { launchNewItemIntent() }

        mUserId = intent.getStringExtra(USER_ID)
        val intent = intent ?: return

        if (connectionDetector!!.isConnectingToInternet) {
            loadProductItems()
        }

        fillItemsList()

    }

    private fun fillItemsList() {
        product_list_holder.beVisible()
        supportFragmentManager.beginTransaction().replace(R.id.product_list_holder, ProductListFragment(), "").commit()
    }

    fun loadProductItems(){

        if (dbHelper.isTableExists(dbHelper.ITEMS_TABLE_NAME)) {
            dbHelper.initItemsTable()
        }else{
            dbHelper.createItemsTable()
        }

        val url="http://ecandlemobile.com/RayKun/webservice/index.php/admin/invoice_items/showInvoiceItems"
        val loadItemData = loadItemDataTask(this)

        val itemsData =  loadItemData.execute(url).get()

        Log.d("loadItemDataItem",itemsData.toString())

        for (item in itemsData){
            saveItem(item)
        }
    }

    private fun saveItem(item: Item) {
        val newItemId = item.itemid
        val newDescription = item.description
        val newLong_Description = item.long_description
        val newRate = item.rate
        val newTaxRate= item.taxrate
        val newTaxRate_2 = item.taxrate_2
        val newGroup_id = item.group_id
        val newUnit = item.unit

        mItem = Item(itemid =  newItemId,
                description = newDescription,
                long_description = newLong_Description,
                rate = newRate,
                taxrate = newTaxRate,
                taxrate_2 = newTaxRate_2,
                group_id = newGroup_id,
                unit = newUnit
                )
        //mItem.itemid = 0
        storeItem()
    }

    private fun storeItem() {
//        if (mItem.itemid == 0) {
            dbHelper.insertItem(mItem)
                Log.d(LOG_TAG,"item added")
                //finish()
//        } else {
//
//            dbHelper.updateItem(mItem)
//                Log.d(LOG_TAG,"item updated")
//        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_product_list, menu)
//        menu.findItem(R.id.exit).isVisible = true
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.exit -> finish()
//            else -> return super.onOptionsItemSelected(item)
//        }
//        return true
//    }
}
