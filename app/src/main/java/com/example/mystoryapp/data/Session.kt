package com.example.mystoryapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Session (
    var token: String,
    var isLogin: Boolean
    ):Parcelable