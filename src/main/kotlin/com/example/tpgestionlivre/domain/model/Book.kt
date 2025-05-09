package com.example.tpgestionlivre.domain.model

data class Book (
    val id: Long,
    val title: String,
    val author: String,
    var isReserved: Boolean = false
) {
    init {
        require(title.isNotBlank()) { "Name must not be blank" }
        require(author.isNotBlank()) { "Author must not be blank" }
    }

    fun reserve() {
        if (isReserved) {
            throw IllegalStateException("Book already reserved")
        }
        isReserved = true
    }
}