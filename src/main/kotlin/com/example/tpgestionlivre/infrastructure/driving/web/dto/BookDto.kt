package com.example.tpgestionlivre.infrastructure.driving.web.dto

import com.example.tpgestionlivre.domain.model.Book

data class BookDto(
    val id: Long?,
    val title: String,
    val author: String,
    val isReserved: Boolean = false,
) {
    fun toDomain(): Book {
        return Book(
            id = this.id ?: 0L,
            title = this.title,
            author = this.author,
            isReserved = this.isReserved
        )
    }
}

fun Book.toDto() = BookDto (
    id = this.id,
    title = this.title,
    author = this.author,
    isReserved = this.isReserved
)