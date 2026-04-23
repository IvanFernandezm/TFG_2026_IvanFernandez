package org.example.Data.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//ENTITAT QUE REPRESENTA EL TRIBUNAL QUE JUTJARÀ UN TREBALL DE FI DE GRAU
@Getter
@Setter
@Entity
public class Tribunal { //ENTITAT DE SORTIDA DE L'ALGORITME
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String room;

    @ManyToOne
    @JoinColumn(name = "presidencia_mail", referencedColumnName = "mail")
    private Docent presidencia;

    @ManyToOne
    @JoinColumn(name = "vocal_mail", referencedColumnName = "mail")
    private Docent vocal;

    private LocalDateTime adjudicacio; //NOMÉS DIA , HORA

    @OneToOne
    @JoinColumn(name = "treball_id", referencedColumnName = "id")
    private Treball treball;

    public Tribunal() {
    }

    public Tribunal(String room, Docent presidency, Docent vocal, Treball treball) {
        this.room = room;
        this.presidencia = presidency;
        this.vocal = vocal;
        this.treball = treball;
    }

    public void intercambiarPresidenciaVocal() {
        Docent temp = this.presidencia;
        this.presidencia = this.vocal;
        this.vocal = temp;
    }

}
