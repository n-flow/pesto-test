package com.parth.pestotest.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TaskStatusModel(

    @SerializedName("id")
    var id: String = "",

    @SerializedName("status")
    var status: String? = null
) : Serializable

val otherStatus = TaskStatusModel("-1", "Other")