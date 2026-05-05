package com.university.studentcoursemanager

import android.app.AlertDialog
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.university.studentcoursemanager.databinding.ActivityAddCourseBinding
import com.university.studentcoursemanager.model.Course

class EditCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCourseBinding
    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val receivedCourse = intent.getSerializableExtra(MainActivity.EXTRA_COURSE) as? Course
        if (receivedCourse == null) { finish(); return }
        course = receivedCourse

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.edit_course)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupDropdowns()
        prefillFields()
        setupButtons()
    }

    private fun setupDropdowns() {
        val creditItems = resources.getStringArray(R.array.credit_hours)
        binding.spinnerCreditHours.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, creditItems)
        )
        val semesterItems = resources.getStringArray(R.array.semesters)
        binding.spinnerSemester.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, semesterItems)
        )
    }

    private fun prefillFields() {
        val creditItems = resources.getStringArray(R.array.credit_hours)
        val semesterItems = resources.getStringArray(R.array.semesters)

        binding.etCourseName.setText(course.courseName)
        binding.etCourseCode.setText(course.courseCode)
        binding.etInstructor.setText(course.instructor)
        binding.etSchedule.setText(course.schedule)
        binding.etRoom.setText(course.room)

        val creditIndex = (course.creditHours - 1).coerceIn(0, creditItems.size - 1)
        binding.spinnerCreditHours.setText(creditItems[creditIndex], false)

        val semIndex = semesterItems.indexOfFirst { it == course.semester }
        binding.spinnerSemester.setText(
            semesterItems[if (semIndex >= 0) semIndex else 0], false
        )

        // Adapt UI for Edit mode
        binding.btnSave.text = getString(R.string.update_course)
        binding.btnCancel.text = getString(R.string.delete)
        binding.btnCancel.setTextColor(getColor(R.color.red_delete))
        binding.btnCancel.background = resources.getDrawable(R.drawable.bg_button_delete, theme)
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener { updateCourse() }
        binding.btnCancel.setOnClickListener { showDeleteDialog() }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        if (binding.etCourseName.text.isNullOrBlank()) {
            binding.tilCourseName.error = getString(R.string.field_required)
            isValid = false
        } else binding.tilCourseName.error = null

        if (binding.etCourseCode.text.isNullOrBlank()) {
            binding.tilCourseCode.error = getString(R.string.field_required)
            isValid = false
        } else binding.tilCourseCode.error = null

        if (binding.etInstructor.text.isNullOrBlank()) {
            binding.tilInstructor.error = getString(R.string.field_required)
            isValid = false
        } else binding.tilInstructor.error = null

        return isValid
    }

    private fun updateCourse() {
        if (!validateFields()) return

        val creditText = binding.spinnerCreditHours.text.toString()
        val creditHours = creditText.firstOrNull()?.digitToIntOrNull() ?: 3

        val updatedCourse = course.copy(
            courseName = binding.etCourseName.text.toString().trim(),
            courseCode = binding.etCourseCode.text.toString().trim().uppercase(),
            instructor = binding.etInstructor.text.toString().trim(),
            creditHours = creditHours,
            schedule = binding.etSchedule.text.toString().trim(),
            room = binding.etRoom.text.toString().trim(),
            semester = binding.spinnerSemester.text.toString()
        )

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false

        FirebaseDatabase.getInstance()
            .getReference("courses")
            .child(course.id)
            .setValue(updatedCourse)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Snackbar.make(binding.root, R.string.course_updated, Snackbar.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.btnSave.isEnabled = true
                Snackbar.make(binding.root, "Update failed: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_confirm_title))
            .setMessage(getString(R.string.delete_confirm_msg))
            .setPositiveButton(getString(R.string.delete)) { _, _ -> deleteCourse() }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun deleteCourse() {
        FirebaseDatabase.getInstance()
            .getReference("courses")
            .child(course.id)
            .removeValue()
            .addOnSuccessListener {
                Snackbar.make(binding.root, R.string.course_deleted, Snackbar.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "Delete failed: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
    }
}