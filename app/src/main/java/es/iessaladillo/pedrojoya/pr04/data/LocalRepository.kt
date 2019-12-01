package es.iessaladillo.pedrojoya.pr04.data

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


object LocalRepository : Repository {


    private val tasks : MutableList<Task> = mutableListOf()
    private var id: Long = 1





    override fun queryAllTasks(): List<Task> {
        return tasks
    }

    override fun queryCompletedTasks(): List<Task> {
        return ArrayList<Task>(tasks).filter {
            it.completed
        }
    }

    override fun queryPendingTasks(): List<Task> {
        return ArrayList<Task>(tasks).filter {
            !it.completed
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun addTask(concept: String) {
        createTask(concept)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTask(concept: String) {
        if(concept.isNotBlank()){
            val dateTime = LocalDateTime.now()
            val formatTime: String = dateTime.format(
                DateTimeFormatter.ofPattern("M/d/y , HH:mm:ss")
            )
            id = (tasks.size + 1).toLong()
            val task = Task(id, concept, "Created at: $formatTime", false, "No completed")
            insertTask(task)
        }
    }

    override fun insertTask(task: Task) {
        tasks.add(task)
    }

    override fun deleteTask(taskId: Long) {
        var position = tasks.indexOfFirst { it.id == taskId }
        if (position >= 0){
            tasks.removeAt(position)
        }
    }

    override fun deleteTasks(taskIdList: List<Long>) {
        if(taskIdList.isNotEmpty()){
            taskIdList.forEach {
                deleteTask(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markTaskAsCompleted(taskId: Long) {
        val dateTime = LocalDateTime.now()
        tasks.forEach {
            if (it.id == taskId) {
                it.completed = true
                it.completedAt = "Completed at: $dateTime"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markTasksAsCompleted(taskIdList: List<Long>) {
        tasks.forEach {
            if (taskIdList.contains(it.id)) {
                markTaskAsCompleted(it.id)
            }
        }
    }

    override fun markTaskAsPending(taskId: Long) {
        tasks.forEach {
            if (it.id == taskId) {
                it.completed = false
            }
        }
    }


    override fun markTasksAsPending(taskIdList: List<Long>) {
        tasks.forEach {
            if (taskIdList.contains(it.id)) {
                markTaskAsPending(it.id)
            }
        }
    }

}


