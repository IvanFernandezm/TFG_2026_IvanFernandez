package org.example.tribunalsbackend.Controller;

import org.example.tribunalsbackend.Api.DTO.EstudiantDTO;
import org.example.tribunalsbackend.Api.DTO.TFGEstudiantDTO;
import org.example.tribunalsbackend.Domain.Docent;
import org.example.tribunalsbackend.Domain.Estudiant;
import org.example.tribunalsbackend.Domain.Expertesa;
import org.example.tribunalsbackend.Domain.Treball;
import org.example.tribunalsbackend.Persistence.EstudiantRepository;
import org.example.tribunalsbackend.Persistence.TreballRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TFGEstudiantsController {
    EstudiantRepository estudiantRepository;
    TreballRepository treballRepository;

    public TFGEstudiantsController(EstudiantRepository estudiantRepository, TreballRepository treballRepository) {
        this.estudiantRepository = estudiantRepository;
        this.treballRepository = treballRepository;
    }

    public TFGEstudiantDTO getTFGEstudiant(String mail) {
        Estudiant student = estudiantRepository.findById(mail).orElseThrow(() -> new RuntimeException("Estudiant amb mail: " + mail + " no registrat!"));
        Treball treb = treballRepository.findTreballByStudent(student).orElseThrow(() -> new RuntimeException("L'estudiant amb mail: " + mail + " no té un treball associat!"));
        Docent tutor = treb.getTutor();
        Expertesa exp = treb.getExpertesa();
        if (tutor == null)
            return new TFGEstudiantDTO(student.getMail(), student.getName(), "SENSE TUTOR ASSIGNAT!", "-", treb.getTitle(), exp.getId());
        else
            return new TFGEstudiantDTO(student.getMail(), student.getName(), tutor.getName(), tutor.getMail(), treb.getTitle(), exp.getId());

    }

    public List<EstudiantDTO> getAllEstudiants() {
        List<Estudiant> estudiants = estudiantRepository.findAll();
        return estudiants.stream().map(estudiant -> new EstudiantDTO(estudiant.getMail(),estudiant.getName())).toList();
    }
}
