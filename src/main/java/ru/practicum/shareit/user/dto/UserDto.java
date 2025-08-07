package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.utils.Marker;

@Data
@Builder
public class UserDto {

    Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым", groups = {Marker.OnCreate.class})
    String name;
    @Email(message = "Невалидный email", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotBlank(message = "Поле email должно быть заполнено", groups = {Marker.OnCreate.class})
    String email;
}
