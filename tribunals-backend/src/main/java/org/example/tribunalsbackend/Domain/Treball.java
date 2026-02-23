package org.example.tribunalsbackend.Domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Treball {
    private String id;
    private String title;
    private String description;
    private Estudiant student;
    private Docent tutor;
    private List<Expertesa> expertness;

    public Treball(String id, String title, String description, Estudiant student, Docent tutor, List<Expertesa> expertness) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.student = student;
        this.tutor = tutor;
        this.expertness = expertness;
    }
    public void addExpertesa(Expertesa expertesa) {
        this.expertness.add(expertesa);
    }
}
