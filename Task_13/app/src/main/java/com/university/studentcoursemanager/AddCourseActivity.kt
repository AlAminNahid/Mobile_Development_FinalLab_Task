package com.university.studentcoursemanager

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.university.studentcoursemanager.databinding.ActivityAddCourseBinding
import com.university.studentcoursemanager.model.Course

class AddCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCourseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.add_course)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupDropdowns()

        binding.btnSave.setOnClickListener { saveCourse() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun setupDropdowns() {
        val creditItems = resources.getStringArray(R.array.credit_hours)
        val creditAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, creditItems)
        binding.spinnerCreditHours.setAdapter(creditAdapter)
        binding.spinnerCreditHours.setText(creditItems[2], false)

        val semesterItems = resources.getStringArray(R.array.semesters)
        val semesterAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, semesterItems)
        binding.spinnerSemester.setAdapter(semesterAdapter)
        binding.spinnerSemester.setText(semesterItems[0], false)
    }

    private fun validateFields(): Boolean {
        var isValid = true

        if (binding.etCourseName.text.isNullOrBlank()) {
            binding.tilCourseName.error = getString(R.string.field_required)
            isValid = false
        } else {
            binding.tilCourseName.error = null
        }

        if (binding.etCourseCode.text.isNullOrBlank()) {
            binding.tilCourseCode.error = getString(R.string.field_required)
            isValid = false
        } else {
            binding.tilCourseCode.error = null
        }

        if (binding.etInstructor.text.isNullOrBlank()) {
            binding.tilInstructor.error = getString(R.string.field_required)
            isValid = false
        } else {
            binding.tilInstructor.error = null
        }

        return isValid
    }

    private fun saveCourse() {
        if (!validateFields()) return

        val creditText = binding.spinnerCreditHours.text.toString()
        val creditHours = creditText.firstOrNull()?.digitToIntOrNull() ?: 3

        val dbRef = FirebaseDatabase.getInstance().getReference("courses")
        val newId = dbRef.push().key ?: return

        val course = Course(
            id = newId,
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

        dbRef.child(newId).setValue(course)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                binding.btnSave.isEnabled = true
                Snackbar.make(binding.root, R.string.course_saved, Snackbar.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.btnSave.isEnabled = true
                Snackbar.make(binding.root, "Error: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }
}