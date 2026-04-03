package org.example.tribunalsbackend.Domain;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import org.example.tribunalsbackend.Domain.Abstracts.AppUser;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Docent extends AppUser {
    private boolean veteran;

    @ManyToMany
    @JoinTable(
        name = "docent_disponibilitat",
        joinColumns = @JoinColumn(name = "docent_mail", referencedColumnName = "mail"),
        inverseJoinColumns = @JoinColumn(name = "disponibilitat_id", referencedColumnName = "id")
    )
    private List<Disponibilitat> availability = new ArrayList<>();

    public Docent() {}

        public Docent(String email, String name, boolean veteran) {
            super(email, name);
            this.veteran = veteran;
        }

    public void addDisponibilitat(Disponibilitat disponibilitat) {
            if (this.availability == null) this.availability = new ArrayList<>();
            if (!this.availability.contains(disponibilitat)) {
                this.availability.add(disponibilitat);
                disponibilitat.getDocents().add(this);
            }
        }

    public void removeDisponibilitat(Disponibilitat disponibilitat) {
        if (this.availability != null && this.availability.contains(disponibilitat)) {
            this.availability.remove(disponibilitat);
            disponibilitat.getDocents().remove(this);
        }
    }
}
