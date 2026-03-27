package org.example.tribunalsbackend.Domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.example.tribunalsbackend.Domain.Abstracts.AppUser;

import java.util.List;

@Entity
@Getter
@Setter
public class Docent extends AppUser {
    private boolean veteran;

    @OneToMany(mappedBy = "docent")
    private List<Disponibilitat> availability;

    public Docent() {}

        public Docent(String email, String name, boolean veteran) {
            super(email, name);
            this.veteran = veteran;
        }

    public void addDisponibilitat(Disponibilitat disponibilitat) {
            this.availability.add(disponibilitat);
        }
}

