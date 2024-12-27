    package org.example.library.dto;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class CourzinaDto {

        private String bookName;
        private String description;
        private String status; // String representation of the Status enum
        private float rating;
        private String email; // Email пользователя
    }
