package org.example.Data;

import org.example.Data.Domain.Adjudicacio;
import org.example.Data.Domain.Disponibilitat;
import org.example.Data.Domain.Docent;
import org.example.Data.Domain.Estudiant;
import org.example.Data.Domain.Treball;
import org.example.Data.Domain.Tribunal;

import java.time.LocalDateTime;
import java.util.*;

public class NewExampleData {
    public List<Docent> docents = new ArrayList<>();
    public List<Estudiant> estudiants = new ArrayList<>();
    public List<Treball> treballs = new ArrayList<>();
    public List<Disponibilitat> slots = new ArrayList<>();
    public List<Adjudicacio> adjudicacions = new ArrayList<>();
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

    public Adjudicacio guardarAdjudicacio(LocalDateTime dataAdj, List<Tribunal> tribunals) {
        Adjudicacio adjudicacio = new Adjudicacio(dataAdj);
        for (Tribunal tribunal : tribunals) {
            adjudicacio.addTribunal(tribunal);
        }
        adjudicacions.add(adjudicacio);
        return adjudicacio;
    }

    public Adjudicacio guardarSolucioEnAdjudicacions(
            int[] presidenciesPerTFG,
            int[] vocalsPerTFG,
            int[] slotsPerTFG,
            LocalDateTime dataAdj
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

            Tribunal tribunal = new Tribunal(
                    "TRB-" + (t + 1),
                    "SLOT-" + slotsPerTFG[t],
                    presidencia,
                    vocal,
                    treball
            );
            tribunals.add(tribunal);
        }

        return guardarAdjudicacio(dataAdj, tribunals);
    }

    public static NewExampleData buildExampleData() {
        NewExampleData d = new NewExampleData();

        d.docents = List.of(
                new Docent("anna@uni.cat", "Anna Serra", true),
                new Docent("marc@uni.cat", "Marc Vidal", true),
                new Docent("jordi@uni.cat", "Jordi Puig", true),
                new Docent("laura@uni.cat", "Laura Soler", false),
                new Docent("pere@uni.cat", "Pere Costa", true),
                new Docent("marta@uni.cat", "Marta Roca", false),
                new Docent("joan@uni.cat", "Joan Ferrer", false)
        );

        d.estudiants = List.of(
                new Estudiant("alumne1@uni.cat", "Nora Camps"),
                new Estudiant("alumne2@uni.cat", "Pol Vives"),
                new Estudiant("alumne3@uni.cat", "Ivet Grau"),
                new Estudiant("alumne4@uni.cat", "Eric Mas"),
                new Estudiant("alumne5@uni.cat", "Jana Ferrer"),
                new Estudiant("alumne6@uni.cat", "Nil Serra"),
                new Estudiant("alumne7@uni.cat", "Aina Pons")
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

        int[] tutorByTreball = {1, 2, 3, 4, 5, 6, 7};
        for (int t = 0; t < tutorByTreball.length; t++) {
            Treball treball = new Treball();
            treball.setId("TFG-" + (t + 1));
            treball.setTitle("TFG " + (t + 1));
            treball.setDescription("Treball de prova " + (t + 1));
            treball.setStudent(d.estudiants.get(t));
            treball.setTutor(d.docents.get(tutorByTreball[t] - 1));
            d.treballs.add(treball);
        }

        d.maxTribunalsPerProfessor = 2;
        d.maxDefensesPerSlot = 2;

        return d;
    }
}
