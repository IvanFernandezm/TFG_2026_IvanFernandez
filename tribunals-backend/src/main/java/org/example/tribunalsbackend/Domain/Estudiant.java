package org.example.tribunalsbackend.Domain;

import lombok.NoArgsConstructor;
import org.example.tribunalsbackend.Domain.Abstractes.AppUser;

@NoArgsConstructor
public class Estudiant extends AppUser {

    public Estudiant(String email, String name) {
        super(email, name);
    }
}
