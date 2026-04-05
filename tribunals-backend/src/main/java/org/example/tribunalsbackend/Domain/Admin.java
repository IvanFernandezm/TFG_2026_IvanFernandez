package org.example.tribunalsbackend.Domain;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import org.example.tribunalsbackend.Domain.Abstracts.AppUser;

//USUARI ADMINISTRADOR
@Entity
public class Admin extends AppUser {
    public Admin() {
    }

    public Admin(String email, String name) {
        super(email, name);
    }
}
