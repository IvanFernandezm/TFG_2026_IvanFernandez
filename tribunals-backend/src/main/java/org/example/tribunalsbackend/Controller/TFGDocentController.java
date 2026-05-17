package org.example.tribunalsbackend.Controller;

import org.example.tribunalsbackend.Api.DTO.DocentDTO;
import org.example.tribunalsbackend.Api.DTO.DocentDetailsDTO;
import org.example.tribunalsbackend.Domain.Disponibilitat;
import org.example.tribunalsbackend.Domain.Docent;
import org.example.tribunalsbackend.Domain.Expertesa;
import org.example.tribunalsbackend.Persistence.DisponibilitatRepository;
import org.example.tribunalsbackend.Persistence.DocentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

public class TFGDocentController {
    DocentRepository docentRepository;
    DisponibilitatRepository disponibilitatRepository;

    public TFGDocentController(DocentRepository docentRepository, DisponibilitatRepository disponibilitatRepository) {
        this.docentRepository = docentRepository;
        this.disponibilitatRepository = disponibilitatRepository;
    }

    public List<DocentDTO> getDocents() {
        List<Docent> docentList = docentRepository.findAll();
        List<DocentDTO> docentDTOList = new ArrayList<>();
        for (Docent docent : docentList) {
            DocentDTO docentDTO = new DocentDTO(docent.getMail(), docent.getName());
            docentDTOList.add(docentDTO);
        }
        return docentDTOList;
    }

    public DocentDetailsDTO getDocentDetails(String mail) {
        Docent d = docentRepository.getReferenceById(mail);
        List<Disponibilitat> disponibilitats = d.getAvailability();
        List<String> exNames = new ArrayList<>();
        List<LocalDateTime> dispoDates = new ArrayList<>();
        for(Expertesa ex : d.getExperteses()){
            exNames.add(ex.getDescription());
        }
        for(Disponibilitat dispo : disponibilitats){
            dispoDates.add(dispo.getDataDis());
        }
        return new DocentDetailsDTO(d.getName(), d.getMail(),d.isVeteran(), exNames, dispoDates);
    }
}
