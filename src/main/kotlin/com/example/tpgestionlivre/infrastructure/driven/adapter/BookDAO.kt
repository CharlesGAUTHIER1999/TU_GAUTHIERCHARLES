package com.example.tpgestionlivre.infrastructure.driven.adapter

import com.example.tpgestionlivre.domain.model.Book
import com.example.tpgestionlivre.domain.port.BookPort
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class BookDAO(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : BookPort {

    private val rowMapper = RowMapper { rs, _ ->
        Book(
            id = rs.getLong("id"),
            title = rs.getString("title"),
            author = rs.getString("author"),
            isReserved = rs.getBoolean("is_reserved")
        )
    }

    override fun getAllBooks(): List<Book> {
        val sql = "SELECT * FROM book"
        return jdbcTemplate.query(sql, rowMapper)
    }

    override fun createBook(book: Book) {
        val sql = """
            INSERT INTO book (id, title, author, is_reserved) 
            VALUES (:id, :title, :author, :isReserved)
        """.trimIndent()
        val params = mapOf(
            "id" to book.id,
            "title" to book.title,
            "author" to book.author,
            "isReserved" to book.isReserved
        )
        jdbcTemplate.update(sql, params)
    }

    override fun findById(id: Long): Book? {
        val sql = "SELECT * FROM book WHERE id = :id"
        val params = mapOf("id" to id)
        return jdbcTemplate.query(sql, params, rowMapper).firstOrNull()
    }

    override fun saveBook(book: Book): Book {
        val sql = """
            UPDATE book 
            SET title = :title, author = :author, is_reserved = :isReserved 
            WHERE id = :id
        """.trimIndent()
        val params = mapOf(
            "id" to book.id,
            "title" to book.title,
            "author" to book.author,
            "isReserved" to book.isReserved
        )
        jdbcTemplate.update(sql, params)
        return book
    }
}