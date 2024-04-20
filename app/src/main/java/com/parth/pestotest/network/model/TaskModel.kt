package com.parth.pestotest.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TaskModel(

    @SerializedName("id")
    var id: String = "",

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("createdAt")
    var createdAt: Long = 0,

    @SerializedName("updatedAt")
    var updatedAt: Long = 0,

    @SerializedName("status")
    var status: TaskStatusModel? = null,

    @SerializedName("user")
    var user: UserModel = unassigned,

    @SerializedName("dueDate")
    var dueDate: Long = 0,
) : Serializable