package com.example.beethozart.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "sign_in_user")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "username")
    var username : String = "",

    @ColumnInfo(name = "password")
    var password : String = ""
)