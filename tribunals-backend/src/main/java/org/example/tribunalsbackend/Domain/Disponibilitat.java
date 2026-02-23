package org.example.tribunalsbackend.Domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Disponibilitat {
    LocalDateTime availability;

        public Disponibilitat(LocalDateTime availability) {
            this.availability = availability;
        }
}
