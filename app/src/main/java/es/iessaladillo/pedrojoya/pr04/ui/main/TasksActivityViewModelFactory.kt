package es.iessaladillo.pedrojoya.pr04.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.iessaladillo.pedrojoya.pr04.data.LocalRepository


class TasksActivityViewModelFactory(val repository: LocalRepository, val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = TasksActivityViewModel(repository, application) as T
}

