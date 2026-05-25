package org.example.tribunalsbackend.Api.DTO;

import java.time.LocalDateTime;

public record TribunalDTO(String clase, String president, String vocal, LocalDateTime data, String TFGTitol,String estudiant , String tutor, String expertesa) {

}
