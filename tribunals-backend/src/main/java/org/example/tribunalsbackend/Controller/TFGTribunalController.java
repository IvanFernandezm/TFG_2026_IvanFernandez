package org.example.tribunalsbackend.Controller;

import org.example.tribunalsbackend.Api.DTO.TribunalDTO;
import org.example.tribunalsbackend.Config.Exceptions.EntityNotFoundException;
import org.example.tribunalsbackend.Domain.*;
import org.example.tribunalsbackend.Persistence.TribunalRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TFGTribunalController {
    TribunalRepository tribunalRepository;

    public TFGTribunalController(TribunalRepository tribunalRepository) {
        this.tribunalRepository = tribunalRepository;
    }

    public List<TribunalDTO> getAllTribunals() {
        List<Tribunal> tribunals = tribunalRepository.findAll();
        List<TribunalDTO> tribunalDTOs = new ArrayList<>();
        if (tribunals.isEmpty()) {
            return null;
        }
        for (Tribunal tribunal : tribunals) {
            TribunalDTO t = TribunalToDTO(tribunal);
            tribunalDTOs.add(t);
        }
        return tribunalDTOs;
    }

    private TribunalDTO TribunalToDTO (Tribunal tribunal) {
        Docent presidencia = tribunal.getPresidencia();
        Docent vocal = tribunal.getVocal();
        Treball treball = tribunal.getTreball();
        Estudiant estudiant = treball.getStudent();
        Docent tutor = treball.getTutor();
        Expertesa exp = treball.getExpertesa();
        return new TribunalDTO(tribunal.getRoom(),presidencia.getName(), vocal.getName(),
                tribunal.getAdjudicacio(), treball.getTitle(), estudiant.getName(), tutor.getName(), exp.getId());
    }
}
