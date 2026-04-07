package org.example.Data.Domain;

import jakarta.persistence.Entity;
import org.example.Data.Domain.Abstracts.AppUser;

//USUARI ADMINISTRADOR
@Entity
public class Admin extends AppUser {
    public Admin() {
    }

    public Admin(String email, String name) {
        super(email, name);
    }
}
