package org.example.tribunalsbackend.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//REPRESENTA UNA FRANJA HORARIA EN LA QUE UN USUARI POT ESTAR DISPONIBLE
@Entity
@Getter
@Setter
public class Disponibilitat { //L'ADMINISTRADOR POT ACOTAR QUINS DIES I HORES SÓN VALIDES

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataDis;

    @ManyToMany(mappedBy = "availability")
    private List<Docent> docents = new ArrayList<>(); //DOCENTS DISPONIBLES EN AQUESTA DATA I HORA

    public Disponibilitat() {}

    public Disponibilitat(LocalDateTime dataDis) {
        this.dataDis = dataDis;
    }

    public Disponibilitat(Long id, LocalDateTime dataDis, List<Docent> docents) {
        this.id = id;
        this.dataDis = dataDis;
        this.docents = docents;
    }

    public void addDocent(Docent docent) {
        if (!this.docents.contains(docent)) {
            this.docents.add(docent);
            docent.getAvailability().add(this);
        }
    }

    public void removeDocent(Docent docent) {
        if (this.docents.contains(docent)) {
            this.docents.remove(docent);
            docent.getAvailability().remove(this);
        }
    }
}
