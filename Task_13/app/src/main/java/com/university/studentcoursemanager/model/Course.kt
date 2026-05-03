package com.university.studentcoursemanager.model

import java.io.Serializable

data class Course(
    var id: String = "",
    var courseName: String = "",
    var courseCode: String = "",
    var instructor: String = "",
    var creditHours: Int = 3,
    var schedule: String = "",
    var room: String = "",
    var semester: String = ""
) : Serializable