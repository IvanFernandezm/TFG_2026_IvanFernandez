package org.example.tribunalsbackend.Domain;

import lombok.NoArgsConstructor;
import org.example.tribunalsbackend.Domain.Abstractes.AppUser;

@NoArgsConstructor
public class Admin extends AppUser {

    public Admin(String email, String name) {
        super(email, name);
    }
}
