package org.example.tribunalsbackend.Domain;


import lombok.Getter;
import lombok.Setter;
import org.example.tribunalsbackend.Domain.Abstractes.AppUser;

import java.util.List;

@Getter
@Setter
public class Docent extends AppUser {
    private boolean veteran;
    private List<Disponibilitat> availability;

        public Docent(String email, String name, boolean veteran) {
            super(email, name);
            this.veteran = veteran;
        }
        public void addDisponibilitat(Disponibilitat disponibilitat) {
            this.availability.add(disponibilitat);
        }
}

