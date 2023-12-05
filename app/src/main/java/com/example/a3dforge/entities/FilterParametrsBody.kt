package com.example.a3dforge.entities

data class FilterParametersBody(
    var q: String? = "",
    var categories: Array<String>? = emptyArray<String>(),
    var keywords: Array<String>? = emptyArray<String>(),
    var sortParameter: String? = "",
    var sortDirection: String? = "",
    var minPrice: Double? = 0.0,
    var maxPrice: Double? = 20000.0,
    var rating: Array<String>? = emptyArray<String>(),
    var author: String? = "",
    var page: Int? = 0,
    var pageSize: Int? = 10
)