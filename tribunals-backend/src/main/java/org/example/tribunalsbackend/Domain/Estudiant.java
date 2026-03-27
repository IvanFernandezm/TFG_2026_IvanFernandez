package org.example.tribunalsbackend.Domain;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import org.example.tribunalsbackend.Domain.Abstracts.AppUser;

@Entity
public class Estudiant extends AppUser {

    public Estudiant() {
    }

    public Estudiant(String email, String name) {
        super(email, name);
    }
}
