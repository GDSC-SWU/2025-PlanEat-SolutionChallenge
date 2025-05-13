package com.example.planeat.data.remote.dto

data class GoogleSearchResponse(
    val items: List<Item>
)

data class Item(
    val link: String
)