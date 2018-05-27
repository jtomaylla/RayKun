package com.ecandle.raykun.activities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.ecandle.raykun.R
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.extensions.launchNewTaskIntent
import com.ecandle.raykun.fragments.TaskListFragment
import com.ecandle.raykun.helpers.ConnectionDetector
import com.ecandle.raykun.helpers.M
import com.ecandle.raykun.helpers.USER_ID
import com.ecandle.raykun.models.DataEvent
import com.ecandle.raykun.models.Task
import com.ecandle.raykun.tasks.loadEventDataTask
import com.simplemobiletools.commons.extensions.beVisible
import com.simplemobiletools.commons.extensions.toast
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : SimpleActivity() {
    private val LOG_TAG = TaskListActivity::class.java.simpleName
    private var mUserId: String? = null
    lateinit var mTask: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        var connectionDetector = ConnectionDetector(this)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_cross)

        supportActionBar?.title = resources.getString(R.string.tasks)

        task_fab.setOnClickListener { launchNewTaskIntent() }
//TODO chequear si userr id esta logeado OJO!!
//
//        val savedSettings = SavedSettings(applicationContext)
//
//        mUserId = savedSettings.getLoggedUserId() ?: return
        mUserId = intent.getStringExtra(USER_ID)
        val intent = intent ?: return

        if (connectionDetector!!.isConnectingToInternet) {
            // JT: Loading Progress Bar
            var dialog = M.setProgressDialog(this)
            dialog.show()
            Handler().postDelayed({dialog.dismiss()},3000)
            //JT
            loadUserTasks()
        }

        fillTasksList()

    }

    private fun fillTasksList() {
        calendar_task_list_holder.beVisible()
        supportFragmentManager.beginTransaction().replace(R.id.calendar_task_list_holder, TaskListFragment(), "").commit()
    }

    fun loadUserTasks(){
        dbHelper.initTasksTable()
        val url="http://ecandlemobile.com/RayKun/webservice/index.php/admin/tasks/showUserTasks?id="+mUserId
        //val url="http://ecandlemobile.com/RayKun/webservice/index.php/admin/tasks/showUserTasks?id=1"
        //val eventsData = List<DataEvent>()!
        val loadTaskData = loadEventDataTask(this)

        val tasksData =  loadTaskData.execute(url).get()

        if (tasksData == null){
            toast(getString(R.string.no_calendar_data),Toast.LENGTH_LONG)
            finish()
        } else {
            Log.d("loadTaskDataTask",tasksData.toString())

            for (task in tasksData){
                saveTask(task)
            }
        }

    }

    private fun saveTask(task: DataEvent) {
        val newId = task.id
        val newName = task.name
        val newDescription = task.description
        val newPriority = task.priority

        val newStartDate= task.startdate
        val newDueDate = task.duedate
        val newStatus = task.status

        mTask = Task(id = newId,
                name = newName,
                description = newDescription,
                priority = newPriority,
                startdate = newStartDate,
                duedate = newDueDate,
                status = newStatus
                )
        mTask.id = 0
        storeTask()
    }

    private fun storeTask() {
        if (mTask.id == 0) {
            dbHelper.insertTask(mTask)
                Log.d(LOG_TAG,"task added")
                //finish()
        } else {

            dbHelper.updateTask(mTask)
                Log.d(LOG_TAG,"task updated")
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_task_list, menu)
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
