package org.example.tribunalsbackend.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//REPRESENTA LA DATA EN LA QUE ES CELEBREN UNES DEFENSES
@Entity
@Getter
@Setter
public class Adjudicacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataAdj;

    @OneToMany(mappedBy = "adjudicacio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tribunal> adjudicats; //TRIBUNALS ADJUDICATS A AQUESTA DATA

    public Adjudicacio() {
        this.adjudicats = new ArrayList<>();
    }

    public Adjudicacio(LocalDateTime dataAdj) {
        this.adjudicats = new ArrayList<>();
        this.dataAdj = dataAdj;
    }

    public void addTribunal(Tribunal tribunal) {
        if (!this.adjudicats.contains(tribunal)) {
            this.adjudicats.add(tribunal);
            tribunal.setAdjudicacio(this);
        }
    }
    public void removeTribunal(Tribunal tribunal) {
        if (this.adjudicats.contains(tribunal)) {
            this.adjudicats.remove(tribunal);
            tribunal.setAdjudicacio(null);
        }
        //llençarà excepció si el tribunal no està associat a aquesta adjudicació
    }
}