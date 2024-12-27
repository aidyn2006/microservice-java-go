package org.example.library.service;

import lombok.RequiredArgsConstructor;
import org.example.library.dto.CourzinaDto;
import org.example.library.model.Courzina;
import org.example.library.model.Status;
import org.example.library.repository.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public LibraryService(LibraryRepository libraryRepository, JavaMailSender mailSender) {
        this.libraryRepository = libraryRepository;
        this.mailSender = mailSender;
    }

    public Courzina create(CourzinaDto courzinaDto) {
        // Проверяем, не взял ли пользователь больше 5 книг за сегодня
        LocalDate today = LocalDate.now();
        List<Courzina> booksTakenToday = libraryRepository.findByEmailAndCreatedAt(courzinaDto.getEmail(), today.toString());

        if (booksTakenToday.size() >= 5) {
            throw new IllegalArgumentException("You cannot take more than 5 books in a day.");
        }

        // Проверяем, не взята ли эта книга другим пользователем
        Optional<Courzina> existingBook = libraryRepository.findByBookName(courzinaDto.getBookName());
        if (existingBook.isPresent() && existingBook.get().getStatus() == Status.ACCEPTED) {
            // Книга уже занята, отправляем уведомление
            sendBookAlreadyTakenNotification(courzinaDto.getBookName(), courzinaDto.getEmail());
            throw new IllegalArgumentException("This book has already been taken.");
        }

        // Проверка статуса
        Status status;
        try {
            status = Status.valueOf(courzinaDto.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + courzinaDto.getStatus());
        }

        // Создаем уникальный код для книги
        String uniqueCode = UUID.randomUUID().toString();

        // Создаем новую запись о книге
        Courzina courzina = Courzina.builder()
                .bookName(courzinaDto.getBookName())
                .description(courzinaDto.getDescription())
                .status(status)
                .email(courzinaDto.getEmail())
                .rating(courzinaDto.getRating())
                .createdAt(String.valueOf(LocalDateTime.now()))
                .updatedAt(String.valueOf(LocalDateTime.now()))
                .uniqueCode(uniqueCode)
                .build();

        libraryRepository.save(courzina);

        // Рассчитываем дату возврата (например, через 14 дней)
        LocalDate returnDate = today.plusDays(14);

        // Отправляем уведомление на email с подробным чеком
        sendEmailNotification(courzinaDto.getBookName(), courzina.getEmail(), today, returnDate, uniqueCode);

        return courzina;
    }

    public Optional<Courzina> get(Long id) {
        return libraryRepository.findById(id);
    }

    private void sendEmailNotification(String bookName, String email, LocalDate borrowDate, LocalDate returnDate, String uniqueCode) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true);
            helper.setFrom("nurlan.aydin06nnn@mail.ru");
            helper.setTo(email);
            helper.setSubject("Library Book Receipt");

            // Форматируем чек
            String formattedBorrowDate = borrowDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String formattedReturnDate = returnDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String emailText = String.format(
                    "Dear User,\n\n" +
                            "Here are the details of the book you borrowed:\n" +
                            "Book Name: %s\n" +
                            "Borrow Date: %s\n" +
                            "Return Date: %s\n" +
                            "Unique Code: %s\n\n" +
                            "Please ensure to return the book by the specified date to avoid penalties.\n\n" +
                            "Thank you for using our library service!\n\n" +
                            "Best regards,\nLibrary Team",
                    bookName, formattedBorrowDate, formattedReturnDate, uniqueCode
            );

            helper.setText(emailText, false);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private void sendBookAlreadyTakenNotification(String bookName, String email) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true);
            helper.setFrom("nurlan.aydin06nnn@mail.ru");
            helper.setTo(email);
            helper.setSubject("Library Book Status: Book Already Taken");

            // Форматируем сообщение
            String emailText = String.format(
                    "Dear User,\n\n" +
                            "The book you requested, '%s', is already taken and cannot be borrowed at the moment.\n\n" +
                            "Please check for other available books or try again later.\n\n" +
                            "Thank you for using our library service!\n\n" +
                            "Best regards,\nLibrary Team",
                    bookName
            );

            helper.setText(emailText, false);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send 'book already taken' notification", e);
        }
    }
}
