package org.example.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "courzina")
public class Courzina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоматическая генерация идентификатора
    private Long id;

    @Column(name = "book_name", nullable = false) // Поле не может быть пустым
    private String bookName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status; // Предположим, что Status - это перечисление

    @Column(name = "rating", nullable = false, columnDefinition = "FLOAT default 0") // По умолчанию 0
    private float rating = 0;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;
    private String email;
    private String uniqueCode;
}
