package org.example.tribunalsbackend.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Expertesa {
    @Id
    private String id;
    private String description;

    @ManyToMany(mappedBy = "experteses")
    private List<Docent> docents;

    @ManyToMany(mappedBy = "experteses")
    private List<Treball> treballs;

    public Expertesa() {
    }

    public Expertesa(String id, String description) {
        this.id = id;
        this.description = description;
    }
}
