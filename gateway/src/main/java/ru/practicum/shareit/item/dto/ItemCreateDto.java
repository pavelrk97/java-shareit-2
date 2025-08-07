package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCreateDto {
    @NotBlank(message = "Название товара не может быть пустым или содержать только пробелы")
    String name;

    @NotBlank(message = "Описание товара не может быть пустым или содержать только пробелы")
    String description;

    @NotNull(message = "Статус о том, доступна или нет вещь для аренды обязателен")
    Boolean available;

    Long requestId;
}