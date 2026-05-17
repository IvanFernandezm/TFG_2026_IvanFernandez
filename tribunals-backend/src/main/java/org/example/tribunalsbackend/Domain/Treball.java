package org.example.tribunalsbackend.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//ENTITAT QUE REPRESENTA EL TREBALL DE FI DE GRAU, ON ES GUARDARAN LES SEVES CARACTERÍSTIQUES I LES RELACIONS AMB ELS ESTUDIANTS, DOCENTS I EXPERTESES
@Entity
@Getter
@Setter
public class Treball {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //id autogenerat per gestionar internament

    private String title;
    private String description;

    @OneToOne
    @JoinColumn(name = "estudiant_mail")
    private Estudiant student;

    @ManyToOne
    @JoinColumn(name = "tutor_mail")
    private Docent tutor;


    @ManyToOne
    @JoinColumn(name = "expertesa_id")
    private Expertesa expertesa;

    public Treball() {}

    public Treball(String title, String description, Estudiant student, Docent tutor, Expertesa expertesa) {
        this.title = title;
        this.description = description;
        this.student = student;
        this.tutor = tutor;
        this.expertesa = expertesa;
    }
}
