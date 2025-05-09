package com.example.tpgestionlivre.driven.adapter

import com.example.tpgestionlivre.domain.model.Book
import com.example.tpgestionlivre.infrastructure.driven.adapter.BookDAO
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet

@SpringBootTest
@ActiveProfiles("testIntegration")
class BookDAOIT(
    private val bookDAO: BookDAO
) : FunSpec() {
    init {
        extension(SpringExtension)

        beforeTest {
            performQuery("DELETE FROM book")
        }

        test("get all books from db") {
            // GIVEN
            performQuery(
                // language=sql
                """
                INSERT INTO book (id, title, author, is_reserved)
                VALUES 
                    (1, 'Hamlet', 'Shakespeare', false),
                    (2, 'Les fleurs du mal', 'Beaudelaire', false),
                    (3, 'Harry Potter', 'Rowling', false)
                """.trimIndent()
            )

            // WHEN
            val res = bookDAO.getAllBooks()

            // THEN
            res.shouldContainExactlyInAnyOrder(
                Book(1L, "Hamlet", "Shakespeare", false),
                Book(2L, "Les fleurs du mal", "Beaudelaire", false),
                Book(3L, "Harry Potter", "Rowling", false)
            )
        }

        test("create book in db") {
            val book = Book(1L, "Les Misérables", "Victor Hugo", isReserved = false)
            bookDAO.createBook(book)

            val res = performQuery("SELECT * FROM book")

            res shouldHaveSize 1
            assertSoftly(res.first()) {
                this["id"].shouldNotBeNull().shouldBeInstanceOf<Long>()
                this["title"].shouldBe("Les Misérables")
                this["author"].shouldBe("Victor Hugo")
                this["is_reserved"].shouldBe(false)
            }
        }

        afterSpec {
            container.stop()
        }
    }

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        init {
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }

        private fun ResultSet.toList(): List<Map<String, Any>> {
            val md = this.metaData
            val columns = md.columnCount
            val rows: MutableList<Map<String, Any>> = ArrayList()
            while (this.next()) {
                val row: MutableMap<String, Any> = HashMap(columns)
                for (i in 1..columns) {
                    row[md.getColumnName(i)] = this.getObject(i)
                }
                rows.add(row)
            }
            return rows
        }

        fun performQuery(sql: String): List<Map<String, Any>> {
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = container.jdbcUrl
                username = container.username
                password = container.password
                driverClassName = container.driverClassName
            }

            HikariDataSource(hikariConfig).use { ds ->
                ds.connection.use { conn ->
                    val statement = conn.createStatement()
                    statement.execute(sql)
                    val resultSet = statement.resultSet
                    return resultSet?.toList() ?: emptyList()
                }
            }
        }
    }
}
