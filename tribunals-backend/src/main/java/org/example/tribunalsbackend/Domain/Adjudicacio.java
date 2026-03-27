package org.example.tribunalsbackend.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Adjudicacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataAdj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tribunal_id", referencedColumnName = "id")
    private Tribunal tribunal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiant_mail", referencedColumnName = "mail")
    private Estudiant estudiant;

    public Adjudicacio() {
    }

    public Adjudicacio(LocalDate dataAdj, Tribunal tribunal, Estudiant estudiant) {
        this.dataAdj = dataAdj;
        this.tribunal = tribunal;
        this.estudiant = estudiant;
    }
}