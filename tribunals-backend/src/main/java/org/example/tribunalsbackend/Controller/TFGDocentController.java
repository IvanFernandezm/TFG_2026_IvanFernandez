package org.example.tribunalsbackend.Controller;

import org.example.tribunalsbackend.Api.DTO.DisponibilitatDTO;
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
import java.util.Set;
import java.util.stream.Collectors;

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
        for (Expertesa ex : d.getExperteses()) {
            exNames.add(ex.getDescription());
        }
        for (Disponibilitat dispo : disponibilitats) {
            dispoDates.add(dispo.getDataDis());
        }
        return new DocentDetailsDTO(d.getName(), d.getMail(), d.isVeteran(), exNames, dispoDates);
    }

    //A l'actualitzar l'horari per als tribunals es reseteja la disponibilitat dels docents
    public void updateDisponibilitat(List<DisponibilitatDTO> dto) {
        List<Docent> docents = docentRepository.findAll();

        List<LocalDateTime> newDates = dto.stream().map(DisponibilitatDTO::timestamp).toList();

        List<Disponibilitat> existents = disponibilitatRepository.findAllByDataDisIn(newDates);
        Set<LocalDateTime> existentesSet = existents.stream()
                .map(Disponibilitat::getDataDis)
                .collect(Collectors.toSet());

        List<Disponibilitat>novesDisp = newDates.stream()
                .filter(date -> !existentesSet.contains(date))
                .map(Disponibilitat::new)
                .toList();

        disponibilitatRepository.saveAll(novesDisp);

        List<Disponibilitat> totes = new ArrayList<>();
        totes.addAll(existents);
        totes.addAll(novesDisp);
        for (Docent docent : docents) {
            docent.getAvailability().clear();
            docent.getAvailability().addAll(totes);
        }

        docentRepository.saveAll(docents);
    }
}
