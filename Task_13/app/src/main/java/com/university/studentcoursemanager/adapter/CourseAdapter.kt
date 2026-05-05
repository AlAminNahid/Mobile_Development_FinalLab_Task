package com.university.studentcoursemanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.university.studentcoursemanager.databinding.ItemCourseBinding
import com.university.studentcoursemanager.model.Course

class CourseAdapter (
    private val onEditClick: (Course) -> Unit,
    private val onDeleteClick: (Course) -> Unit,
    private val onItemClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private val courseList = mutableListOf<Course>()
    private val filteredList = mutableListOf<Course>()

    inner class CourseViewHolder(private val binding: ItemCourseBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(course: Course) {
            binding.tvCourseName.text = course.courseName
            binding.tvCourseCode.text = course.courseCode
            binding.tvCredits.text = "${course.creditHours} Credits"
            binding.tvInstructor.text = course.instructor
            binding.tvSchedule.text = course.schedule.ifEmpty { "No schedule set" }

            binding.root.setOnClickListener { onItemClick(course) }
            binding.btnEdit.setOnClickListener { onEditClick(course) }
            binding.btnDelete.setOnClickListener { onDeleteClick(course) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateData(newList: List<Course>) {
        courseList.clear()
        courseList.addAll(newList)
        filteredList.clear()
        filteredList.addAll(newList)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(courseList)
        } else {
            val q = query.lowercase()
            filteredList.addAll(courseList.filter {
                it.courseName.lowercase().contains(q) ||
                        it.courseCode.lowercase().contains(q)
            })
        }
        notifyDataSetChanged()
    }
}