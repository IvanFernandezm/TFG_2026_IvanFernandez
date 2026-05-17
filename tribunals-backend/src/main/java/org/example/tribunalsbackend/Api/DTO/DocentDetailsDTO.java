package org.example.tribunalsbackend.Api.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record DocentDetailsDTO(String name, String mail,Boolean veteran, List<String> experteses, List<LocalDateTime> disponibilitat) {
}
