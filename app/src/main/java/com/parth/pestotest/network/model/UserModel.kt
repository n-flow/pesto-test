package com.parth.pestotest.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserModel(

    @SerializedName("id")
    val id: String = "",

    @SerializedName("personName")
    val personName: String = "",

    @SerializedName("personGivenName")
    val personGivenName: String = "",

    @SerializedName("personFamilyName")
    val personFamilyName: String = "",

    @SerializedName("personEmail")
    var personEmail: String = "",

    @SerializedName("personId")
    val personId: String = "",

    @SerializedName("personPhoto")
    val personPhoto: String? = null,

    ) : Serializable

val unassigned = UserModel("-1", "Unassigned", "Unassigned", "Unassigned", "", "", "")