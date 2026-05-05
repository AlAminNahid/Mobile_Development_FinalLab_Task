package com.university.studentcoursemanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.university.studentcoursemanager.databinding.ActivityCourseDetailBinding
import com.university.studentcoursemanager.model.Course

class CourseDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val course = intent.getSerializableExtra(MainActivity.EXTRA_COURSE) as? Course
        if (course == null) { finish(); return }

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        bindCourseDetail(course)

        binding.fabEdit.setOnClickListener {
            val intent = Intent(this, EditCourseActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_COURSE, course)
            startActivity(intent)
        }
    }

    private fun bindCourseDetail(course: Course) {
        binding.tvDetailCourseName.text = course.courseName
        binding.tvDetailCourseCode.text = course.courseCode
        binding.tvDetailSemester.text = course.semester

        binding.tvInstructorValue.text = course.instructor.ifEmpty { getString(R.string.not_specified) }
        binding.tvCreditsValue.text = "${course.creditHours} Credits"
        binding.tvScheduleValue.text = course.schedule.ifEmpty { getString(R.string.not_specified) }
        binding.tvRoomValue.text = course.room.ifEmpty { getString(R.string.not_specified) }
    }
}