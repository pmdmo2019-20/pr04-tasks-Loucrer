package es.iessaladillo.pedrojoya.pr04.ui.main

import android.annotation.TargetApi
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.strikeThrough
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.tasks_activity.*
import kotlinx.android.synthetic.main.tasks_activity_item.*
import kotlinx.android.synthetic.main.tasks_activity_item.view.*


@Suppress("DEPRECATION")
class TasksActivityAdapter : RecyclerView.Adapter<TasksActivityAdapter.ViewHolder>() {

    private var data: List<Task> = emptyList()
    var onItemClickListener: ((Int) -> Unit)? = null

    init {
        setHasStableIds(true)
    }


    override fun getItemId(position: Int): Long {
        return data[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.tasks_activity_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task: Task = data[position]
        holder.bind(task)


    }

    fun submitList(newList: List<Task>) {
        data = newList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Task = data[position]


    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            containerView.setOnClickListener { onItemClickListener?.invoke(adapterPosition) }
            chkCompleted.setOnClickListener { onItemClickListener?.invoke(adapterPosition) }
        }


        fun bind(task: Task) {
            task.run {
                containerView.lblConcept.text = concept
                containerView.chkCompleted.isChecked = completed
                if (!completed) {
                    containerView.lblConcept.strikeThrough(false)
                    containerView.lblCompleted.text = createdAt
                    containerView.viewBar.run {
                        background = resources.getDrawable(R.color.colorPendingTask)
                    }
                } else {
                    containerView.lblConcept.strikeThrough(true)
                    containerView.viewBar.run {
                        background = resources.getDrawable(R.color.colorCompletedTask)
                        containerView.lblCompleted.text = completedAt
                    }
                }
            }
        }
    }
}


