package org.example.Data;

import org.example.Data.Domain.*;

import java.time.LocalDateTime;
import java.util.*;

public class NewExampleData {
    public List<Docent> docents = new ArrayList<>();
    public List<Estudiant> estudiants = new ArrayList<>();
    public List<Treball> treballs = new ArrayList<>();
    public List<Disponibilitat> slots = new ArrayList<>();
    public List<Tribunal> tribunals = new ArrayList<>();
    public List<Expertesa> experteses = new ArrayList<>();
    public int maxTribunalsPerProfessor = 2;
    public int maxDefensesPerSlot = 2;

    public int getNumProfessors() {
        return docents.size();
    }

    public int getTfgCount() {
        return treballs.size();
    }

    public int getSlotsCount() {
        return slots.size();
    }

    public Docent getDocentBySolverId(int solverId) {
        if (solverId < 1 || solverId > docents.size()) {
            return null;
        }
        return docents.get(solverId - 1);
    }

    public Treball getTreballByIndex(int tfgIndex) {
        return treballs.get(tfgIndex);
    }

    public int getTutorSolverIdOfTFG(int tfgIndex) {
        Docent tutor = treballs.get(tfgIndex).getTutor();
        int idx = docents.indexOf(tutor);
        return idx + 1;
    }

    public boolean isProfAvailableAt(int profSolverId, int slotSolverId) {
        Docent docent = getDocentBySolverId(profSolverId);
        if (docent == null || slotSolverId < 1 || slotSolverId > slots.size()) {
            return false;
        }

        List<Disponibilitat> availability = docent.getAvailability();
        if (availability == null) {
            return false;
        }

        Disponibilitat slot = slots.get(slotSolverId - 1);
        return availability.contains(slot);
    }

    public List<Tribunal> guardarSolucioEnTribunals(
            int[] presidenciesPerTFG,
            int[] vocalsPerTFG,
            int[] slotsPerTFG
    ) {
        if (presidenciesPerTFG.length != treballs.size()
                || vocalsPerTFG.length != treballs.size()
                || slotsPerTFG.length != treballs.size()) {
            throw new IllegalArgumentException("Les dimensions de la solucio no coincideixen amb els TFGs");
        }

        List<Tribunal> tribunals = new ArrayList<>();
        for (int t = 0; t < treballs.size(); t++) {
            Docent presidencia = getDocentBySolverId(presidenciesPerTFG[t]);
            Docent vocal = getDocentBySolverId(vocalsPerTFG[t]);
            Treball treball = treballs.get(t);
            int slotSolverId = slotsPerTFG[t];

            if (slotSolverId < 1 || slotSolverId > slots.size()) {
                throw new IllegalArgumentException("Slot invalid per al TFG index " + t + ": " + slotSolverId);
            }

            Tribunal tribunal = new Tribunal(
                    "SLOT-" + slotSolverId,
                    presidencia,
                    vocal,
                    treball
            );
            tribunal.setAdjudicacio(slots.get(slotSolverId - 1).getDataDis());
            tribunals.add(tribunal);
        }

        this.tribunals.addAll(tribunals);
        return tribunals;
    }

    public static NewExampleData buildExampleData() {
        NewExampleData d = new NewExampleData();

        d.docents = List.of(
                new Docent("jboix@tecnocampus.cat", "Jordi Boix"),
                new Docent("rherrero@tecnocampus.cat", "Rosa Herrero"),
                new Docent("sesa@tecnocampus.cat", "Enric Sesa"),
                new Docent("barberan@tecnocampus.cat", "Pere Barberán"),
                new Docent("ptuset@tecnocampus.cat", "Pere Tuset"),
                new Docent("imorenoa@tecnocampus.cat", "Immaculada Moreno"),
                new Docent("cbonet@tecnocampus.cat", "Carles Bonet")
        );

        d.estudiants = List.of(
                new Estudiant("ifernandezm@edu.tecnocampus.cat", "Fernández Muñoz, Iván"),
                new Estudiant("amarting@edu.tecnocampus.cat", "Martín Giol, Arnau"),
                new Estudiant("mpuiga@edu.tecnocampus.cat", "Puig Alcaide, Marc"),
                new Estudiant("ftorrus@edu.tecnocampus.cat", "Torrus Mora, Francesc"),
                new Estudiant("xxiangc@edu.tecnocampus.cat", "Xiang Chen, Xinyang"),
                new Estudiant("jrodriguezmi@edu.tecnocampus.cat", "Pujol Rodríguez, David"),
                new Estudiant("cguinovarti@edu.tecnocampus.cat", "Guinovart i Galofre, Carlos")
        );
        d.experteses = List.of(
                new Expertesa("Seguretat i comunicacions"),
                new Expertesa("Internet of Things"),
                new Expertesa("Signal Processing"),
                new Expertesa("Big data i machine Learning"),
                new Expertesa("Solucions Web / Móbil"),
                new Expertesa("Human Computer Interaction")
        );

        d.slots = List.of(
                new Disponibilitat(LocalDateTime.of(2026, 6, 10, 9, 0)),
                new Disponibilitat(LocalDateTime.of(2026, 6, 10, 11, 0)),
                new Disponibilitat(LocalDateTime.of(2026, 6, 10, 15, 0)),
                new Disponibilitat(LocalDateTime.of(2026, 6, 10, 17, 0))
        );

        int[][] availabilityByDocent = {
                {1, 2, 3, 4},
                {1, 2, 3},
                {2, 3, 4},
                {1, 3, 4},
                {1, 2, 4},
                {2, 3},
                {1, 4}
        };

        for (int i = 0; i < d.docents.size(); i++) {
            Docent docent = d.docents.get(i);
            docent.setAvailability(new ArrayList<>());
            for (int slotId : availabilityByDocent[i]) {
                docent.getAvailability().add(d.slots.get(slotId - 1));
            }
        }

        // Cada docent rep almenys una expertesa, amb solapaments pensats per fer viable
        // la constraint del tribunal: presidència o vocal han de compartir expertesa amb el TFG.
        int[][] expertesesByDocent = {
                {1, 2},      // Jordi Boix
                {6, 1, 3},   // Rosa Herrero
                {2, 4, 6},   // Enric Sesa
                {5, 2, 4},   // Pere Barberán
                {4, 5, 6},   // Pere Tuset
                {1, 5},      // Immaculada Moreno
                {6}          // Carles Bonet
        };
        for (int i = 0; i < d.docents.size(); i++) {
            Docent docent = d.docents.get(i);
            for (int expertesaId : expertesesByDocent[i]) {
                docent.addExpertesa(d.experteses.get(expertesaId - 1));
            }
        }

        int[] tutorByTreball = {1, 2, 3, 4, 5, 6, 7};
        for (int t = 0; t < tutorByTreball.length; t++) {
            Treball treball = new Treball();
            treball.setTitle("TFG " + (t + 1));
            treball.setDescription("Treball de prova " + (t + 1));
            treball.setStudent(d.estudiants.get(t));
            Docent tutor = d.docents.get(tutorByTreball[t] - 1);
            treball.setTutor(tutor);
            treball.setExpertesa(tutor.getExperteses().get(0));
            d.treballs.add(treball);
        }

        d.maxTribunalsPerProfessor = 2;
        d.maxDefensesPerSlot = 2;

        return d;
    }
}
