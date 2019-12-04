package es.iessaladillo.pedrojoya.pr04.ui.main

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.base.Event
import es.iessaladillo.pedrojoya.pr04.data.Repository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task

class TasksActivityViewModel(private val repository: Repository,
                             private val application: Application) : ViewModel() {

    // Estado de la interfaz

    private val _tasks: MutableLiveData<List<Task>> = MutableLiveData()
    val tasks: LiveData<List<Task>>
        get() = _tasks

    private val _currentFilter: MutableLiveData<TasksActivityFilter> =
        MutableLiveData(TasksActivityFilter.ALL)
    private val _currentFilterMenuItemId: MutableLiveData<Int> =
        MutableLiveData(R.id.mnuFilterAll)

    val currentFilterMenuItemId: LiveData<Int>
        get() = _currentFilterMenuItemId

    private val _activityTitle: MutableLiveData<String> =
        MutableLiveData(application.getString(R.string.tasks_title_all))
    val activityTitle: LiveData<String>
        get() = _activityTitle

    private val _lblEmptyViewText: MutableLiveData<String> =
        MutableLiveData(application.getString(R.string.tasks_no_tasks_yet))
    val lblEmptyViewText: LiveData<String>
        get() = _lblEmptyViewText

    private val taskIdList: MutableList<Long> = mutableListOf()


    // Eventos de comunicación con la actividad

    private val _onStartActivity: MutableLiveData<Event<Intent>> = MutableLiveData()
    val onStartActivity: LiveData<Event<Intent>>
        get() = _onStartActivity

    private val _onShowMessage: MutableLiveData<Event<String>> = MutableLiveData()
    val onShowMessage: LiveData<Event<String>>
        get() = _onShowMessage

    private val _onShowTaskDeleted: MutableLiveData<Event<Task>> = MutableLiveData()
    val onShowTaskDeleted: LiveData<Event<Task>>
        get() = _onShowTaskDeleted


    init {
        refreshLists(repository.queryAllTasks())
    }

    // ACTION METHODS

    // Hace que se muestre en el RecyclerView todas las tareas.
    fun filterAll() {
        _currentFilterMenuItemId.value = R.id.mnuFilterAll
        _activityTitle.value = application.getString(R.string.tasks_title_all)
        _currentFilter.value = TasksActivityFilter.ALL
        queryTasks(_currentFilter.value!!)
    }

    // Hace que se muestre en el RecyclerView sólo las tareas completadas.
    fun filterCompleted() {
        _currentFilterMenuItemId.value = R.id.mnuFilterCompleted
        _activityTitle.value = application.getString(R.string.tasks_title_completed)
        _currentFilter.value = TasksActivityFilter.COMPLETED
        queryTasks(_currentFilter.value!!)
    }

    // Hace que se muestre en el RecyclerView sólo las tareas pendientes.
    fun filterPending() {
        _currentFilterMenuItemId.value = R.id.mnuFilterPending
        _activityTitle.value = application.getString(R.string.tasks_title_pending)
        _currentFilter.value = TasksActivityFilter.PENDING
        queryTasks(_currentFilter.value!!)
    }

    // Agrega una nueva tarea con dicho concepto. Si la se estaba mostrando
    // la lista de solo las tareas completadas, una vez agregada se debe
    // mostrar en el RecyclerView la lista con todas las tareas, no sólo
    // las completadas.

    fun addTask(concept: String) {
        if (isValidConcept(concept)){
            repository.addTask(concept)
            queryTasks(_currentFilter.value!!)
        }
    }

    // Agrega la tarea
    fun insertTask(task: Task) {
        repository.insertTask(task)
        queryTasks(_currentFilter.value!!)
    }

    // Borra la tarea
    fun deleteTask(task: Task) {
        repository.deleteTask(task.id)
        queryTasks(_currentFilter.value!!)
   }

    // Borra todas las tareas mostradas actualmente en el RecyclerView.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que borrar.
    fun deleteTasks() {
        if(_tasks.value?.isNotEmpty() == true){
            repository.deleteTasks(taskIdList)
        }else{
            _onShowMessage.value = Event(application.getString(R.string.tasks_task_deleted))
        queryTasks(_currentFilter.value!!)
        }
    }

    // Marca como completadas todas las tareas mostradas actualmente en el RecyclerView,
    // incluso si ya estaban completadas.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que marcar como completadas.
    fun markTasksAsCompleted() {
        if(_tasks.value?.isEmpty() == true){
            _onShowMessage.value = Event(application.getString(R.string.tasks_task_completed))
        }else{
            repository.markTasksAsCompleted(taskIdList)
            queryTasks(_currentFilter.value!!)
        }
    }

    // Marca como pendientes todas las tareas mostradas actualmente en el RecyclerView,
    // incluso si ya estaban pendientes.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que marcar como pendientes.
    fun markTasksAsPending() {
        if(_tasks.value?.isEmpty() == true){
            _onShowMessage.value = Event(application.getString(R.string.tasks_task_completed))
        }else{
            repository.markTasksAsPending(taskIdList)
            queryTasks(_currentFilter.value!!)
        }
    }

    // Hace que se envíe un Intent con la lista de tareas mostradas actualmente
    // en el RecyclerView.
    // Si no se estaba mostrando ninguna tarea, se muestra un Snackbar indicando
    // que no hay tareas que compartir.
    fun shareTasks() {
        if (_tasks.value?.isNotEmpty() == true) {
            val intent = sendIntents()
            _onStartActivity.value = Event(intent)
            if (isActivityAvailable(application, intent)) {
                application.startActivity(intent)
            }
        } else {
            _onShowMessage.value = Event(application.getString(R.string.tasks_no_tasks_to_share))
        }
    }

    // Actualiza el estado de completitud de la tarea recibida, atendiendo al
    // valor de isCompleted. Si es true la tarea es marcada como completada y
    // en caso contrario es marcada como pendiente.
    fun updateTaskCompletedState(task: Task, isCompleted: Boolean) {
        if(!isCompleted){
            repository.markTaskAsCompleted(task.id)
        }else{
            repository.markTaskAsPending(task.id)
        }
            queryTasks(_currentFilter.value!!)
    }

    // Retorna si el concepto recibido es válido (no es una cadena vacía o en blanco)
    private fun isValidConcept(concept: String): Boolean = concept.isNotBlank()


    // Pide las tareas al repositorio, atendiendo al filtro recibido
    private fun queryTasks(filter: TasksActivityFilter) {
        when (filter) {
            TasksActivityFilter.ALL ->
                refreshLists(repository.queryAllTasks())
            TasksActivityFilter.COMPLETED ->
                refreshLists(repository.queryCompletedTasks())
            TasksActivityFilter.PENDING ->
                refreshLists(repository.queryPendingTasks())
        }
    }

    private fun refreshLists(newList: List<Task>) {
        _tasks.value = newList.sortedByDescending { it.id }
        taskIdList.clear()
        tasks.value?.forEach {
            taskIdList.add(it.id)
        }
    }

    /*INTENTS*/
    fun isActivityAvailable(ctx: Context, intent: Intent): Boolean {
        val packageManager = ctx.applicationContext.packageManager
        val appList = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return appList.size > 0
    }

        fun sendIntents() : Intent {
            var listToText = ""
            tasks.value?.forEach{
                listToText += it.concept + " "
                listToText += if (it.completed) "Completado \n" else " Pendiente \n"
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_SUBJECT,"Tasks")
                putExtra(Intent.EXTRA_TEXT, listToText)
                type = "text/plain"
            }

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }

}



