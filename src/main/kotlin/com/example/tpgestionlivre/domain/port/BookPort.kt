package com.example.tpgestionlivre.domain.port

import com.example.tpgestionlivre.domain.model.Book

interface BookPort {
    fun getAllBooks(): List<Book>
    fun createBook(book: Book)
    fun findById(id: Long): Book?
    fun saveBook(book: Book): Book
}
