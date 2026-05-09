package org.example.Data;

import org.example.Data.Domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewExampleData2 {
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

    public static NewExampleData2 buildExampleData() {
        NewExampleData2 d = new NewExampleData2();

        d.docents = List.of(
                new Docent("jboix@tecnocampus.cat", "Jordi Boix"),
                new Docent("rherrero@tecnocampus.cat", "Rosa Herrero"),
                new Docent("sesa@tecnocampus.cat", "Enric Sesa"),
                new Docent("barberan@tecnocampus.cat", "Pere Barberán"),
                new Docent("ptuset@tecnocampus.cat", "Pere Tuset"),
                new Docent("imorenoa@tecnocampus.cat", "Immaculada Moreno"),
                new Docent("cbonet@tecnocampus.cat", "Carles Bonet"),
                new Docent("alopez@tecnocampus.cat", "Anna López"),
                new Docent("dgarcia@tecnocampus.cat", "David García"),
                new Docent("mserra@tecnocampus.cat", "Marta Serra")
        );

        d.estudiants = List.of(
                new Estudiant("ifernandezm@edu.tecnocampus.cat", "Fernández Muñoz, Iván"),
                new Estudiant("amarting@edu.tecnocampus.cat", "Martín Giol, Arnau"),
                new Estudiant("mpuiga@edu.tecnocampus.cat", "Puig Alcaide, Marc"),
                new Estudiant("ftorrus@edu.tecnocampus.cat", "Torrus Mora, Francesc"),
                new Estudiant("xxiangc@edu.tecnocampus.cat", "Xiang Chen, Xinyang"),
                new Estudiant("jrodriguezmi@edu.tecnocampus.cat", "Pujol Rodríguez, David"),
                new Estudiant("cguinovarti@edu.tecnocampus.cat", "Guinovart i Galofre, Carlos"),
                new Estudiant("lmartinez@edu.tecnocampus.cat", "Martínez López, Laura"),
                new Estudiant("pgomez@edu.tecnocampus.cat", "Gómez Ruiz, Pablo"),
                new Estudiant("nruiz@edu.tecnocampus.cat", "Ruiz Torres, Núria")
        );

        d.experteses = List.of(
                new Expertesa("Seguretat i comunicacions"),
                new Expertesa("Internet of Things"),
                new Expertesa("Signal Processing"),
                new Expertesa("Big data i machine Learning"),
                new Expertesa("Solucions Web / Mòbil"),
                new Expertesa("Human Computer Interaction")
        );

        d.slots = List.of(
                new Disponibilitat(LocalDateTime.of(2026, 6, 10, 9, 0)),
                new Disponibilitat(LocalDateTime.of(2026, 6, 10, 11, 0)),
                new Disponibilitat(LocalDateTime.of(2026, 6, 10, 15, 0)),
                new Disponibilitat(LocalDateTime.of(2026, 6, 10, 17, 0)),
                new Disponibilitat(LocalDateTime.of(2026, 6, 11, 9, 0))
        );

        for (Docent docent : d.docents) {
            docent.setAvailability(new ArrayList<>(d.slots));
        }

        int[][] expertesesByDocent = {
                {1, 2},      // Boix
                {1, 3, 6},   // Herrero
                {2, 4, 6},   // Sesa
                {2, 4, 5},   // Barberán
                {4, 5, 6},   // Tuset
                {1, 5},      // Moreno
                {6},         // Bonet
                {5, 6},      // López
                {3, 4},      // García
                {1, 2}       // Serra
        };

        for (int i = 0; i < d.docents.size(); i++) {
            Docent docent = d.docents.get(i);
            for (int expId : expertesesByDocent[i]) {
                docent.addExpertesa(d.experteses.get(expId - 1));
            }
        }

        String[] titols = {
                "Sistema IoT per monitorització ambiental",
                "Aplicació web per gestió de reserves",
                "Anàlisi de dades amb machine learning",
                "Sistema de seguretat en xarxes",
                "App mòbil per seguiment esportiu",
                "Interfície HCI per persones grans",
                "Processament de senyal en temps real",
                "Plataforma Big Data per IoT",
                "Web intel·ligent amb recomanador",
                "Sistema distribuït segur"
        };

        int[] tutors = {1,2,3,4,5,6,7,8,9,10};
        int[] expertesesTFG = {2,5,4,1,5,6,3,4,5,1};

        for (int i = 0; i < titols.length; i++) {
            Treball t = new Treball();
            t.setTitle(titols[i]);
            t.setDescription("TFG real " + (i + 1));
            t.setStudent(d.estudiants.get(i));

            Docent tutor = d.docents.get(tutors[i] - 1);
            t.setTutor(tutor);

            t.setExpertesa(d.experteses.get(expertesesTFG[i] - 1));

            d.treballs.add(t);
        }

        d.maxTribunalsPerProfessor = 2;
        d.maxDefensesPerSlot = 2;

        return d;
    }
}
