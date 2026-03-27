package org.example.tribunalsbackend.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Disponibilitat {

    @Id
    LocalDateTime availability;

    public Disponibilitat() {
    }

    public Disponibilitat(LocalDateTime availability) {
        this.availability = availability;
    }
}
