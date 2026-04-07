package org.example.Data.Domain;

import jakarta.persistence.Entity;
import org.example.Data.Domain.Abstracts.AppUser;

//USUARI ESTUDIANT
@Entity
public class Estudiant extends AppUser {

    public Estudiant() {
    }

    public Estudiant(String email, String name) {
        super(email, name);
    }
}
