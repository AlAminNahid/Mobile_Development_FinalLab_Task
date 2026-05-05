package com.university.studentcoursemanager

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.university.studentcoursemanager.adapter.CourseAdapter
import com.university.studentcoursemanager.databinding.ActivityMainBinding
import com.university.studentcoursemanager.model.Course

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupFirebaseListener()
        setupFab()
        setupSearch()
    }

    private fun setupRecyclerView() {
        adapter = CourseAdapter(
            onEditClick = { course ->
                val intent = Intent(this, EditCourseActivity::class.java)
                intent.putExtra(EXTRA_COURSE, course)
                startActivity(intent)
            },
            onDeleteClick = { course ->
                showDeleteDialog(course)
            },
            onItemClick = { course ->
                val intent = Intent(this, CourseDetailActivity::class.java)
                intent.putExtra(EXTRA_COURSE, course)
                startActivity(intent)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupFirebaseListener() {
        val dbRef = FirebaseDatabase.getInstance().getReference("courses")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Course>()

                for(child in snapshot.children) {
                    val course = child.getValue(Course::class.java)

                    if(course != null) {
                        list.add(course)
                    }
                    adapter.updateData(list)
                    updateEmptyState(list.isEmpty())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(
                    binding.root, "Failed to load courses: ${error.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setupFab(){
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddCourseActivity::class.java))
        }
    }

    private fun setupSearch() {
        val searchItem = binding.toolbar.menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = getString(R.string.search_hint)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText.orEmpty())
                return true
            }
        })
    }

    private fun showDeleteDialog(course: Course) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_confirm_title))
            .setMessage(getString(R.string.delete_confirm_msg))
            .setPositiveButton(getString(R.string.delete)) {
                _, _ ->
                    deleteCourse(course)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun deleteCourse(course: Course) {
        FirebaseDatabase.getInstance()
            .getReference("courses")
            .child(course.id)
            .removeValue()
            .addOnSuccessListener {
                Snackbar.make(binding.root, R.string.course_deleted, Snackbar.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "Delete failed: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateLayout.visibility = if(isEmpty) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if(isEmpty) View.GONE else View.VISIBLE
    }

    companion object {
        const val EXTRA_COURSE = "extra_course"
    }
}