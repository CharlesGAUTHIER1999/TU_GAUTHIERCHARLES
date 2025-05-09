package com.example.tpgestionlivre.infrastructure.application

import com.example.tpgestionlivre.domain.usecase.BookUseCase
import com.example.tpgestionlivre.infrastructure.driven.adapter.BookDAO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCasesConfiguration {
    @Bean
    fun bookUseCase(bookDAO: BookDAO): BookUseCase {
        return BookUseCase(bookDAO)
    }
}