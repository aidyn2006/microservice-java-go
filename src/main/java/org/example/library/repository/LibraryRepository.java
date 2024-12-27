package org.example.library.repository;

import org.example.library.model.Courzina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface LibraryRepository extends JpaRepository<Courzina, Long> {

    Optional<Courzina> findByBookName(String bookName);

    @Query("SELECT c FROM Courzina c WHERE c.email = :email AND c.createdAt LIKE :date%")
    List<Courzina> findByEmailAndCreatedAt(@Param("email") String email, @Param("date") String date);
}
