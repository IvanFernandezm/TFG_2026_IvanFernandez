package org.example.tribunalsbackend.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Entity
@Getter
@Setter
public class Treball {

    @Id
    private String id;
    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "estudiant_id")
    private Estudiant student;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Docent tutor;

    @ManyToMany
    @JoinTable(
            name = "expertesa_treball",
            joinColumns = @JoinColumn(name = "treball_id"),
            inverseJoinColumns = @JoinColumn(name = "expertesa_id")
    )
    private List<Expertesa> experteses;

    public Treball() {
    }

    public Treball(String id, String title, String description, Estudiant student, Docent tutor, List<Expertesa> expertness) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.student = student;
        this.tutor = tutor;
        this.experteses = expertness;
    }
    public void addExpertesa(Expertesa expertesa) {
        this.experteses.add(expertesa);
    }
}
