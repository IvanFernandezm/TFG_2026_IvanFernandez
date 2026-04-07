package org.example.Data;

import org.example.Main;

import java.util.*;

//DADES D'EXEMPLE PRIMITIVES PER A LES PRIMERES PROVES
public class ExampleData {
    public int numProfessors;
    public int tfgCount;
    public int slotsCount;
    public List<String> professorNames = new ArrayList<>();
    public Map<Integer, Set<Integer>> profAvailability;
    public int[] tutorOfTFG;
    public int maxTribunalsPerProfessor = 2;
    public int maxDefensesPerSlot = 2;

    public boolean isProfAvailableAt(int profId, int slotId) {
        Set<Integer> s = profAvailability.get(profId);
        return s != null && s.contains(slotId);
    }

    public static ExampleData buildExampleData() {
        ExampleData d = new ExampleData();

        // ─────────────────────────────
        // Professors
        d.numProfessors = 7;
        d.professorNames = List.of(
                "Anna Serra",    // 1
                "Marc Vidal",    // 2
                "Jordi Puig",    // 3
                "Laura Soler",   // 4
                "Pere Costa",    // 5
                "Marta Roca",    // 6
                "Joan Ferrer"    // 7
        );

        // ─────────────────────────────
        // TFGs i franges
        d.tfgCount = 7;    // més TFGs → més combinacions
        d.slotsCount = 4;  // matí1, matí2, tarda, vespre

        // ─────────────────────────────
        // Disponibilitats
        d.profAvailability = new HashMap<>();
        d.profAvailability.put(1, new HashSet<>(Arrays.asList(1, 2, 3, 4))); // Anna
        d.profAvailability.put(2, new HashSet<>(Arrays.asList(1, 2, 3)));   // Marc
        d.profAvailability.put(3, new HashSet<>(Arrays.asList(2, 3, 4)));   // Jordi
        d.profAvailability.put(4, new HashSet<>(Arrays.asList(1, 3, 4)));   // Laura
        d.profAvailability.put(5, new HashSet<>(Arrays.asList(1, 2, 4)));   // Pere
        d.profAvailability.put(6, new HashSet<>(Arrays.asList(2, 3)));     // Marta
        d.profAvailability.put(7, new HashSet<>(Arrays.asList(1, 4)));     // Joan

        // ─────────────────────────────
        // Tutors per TFG (pensat per crear reciprocitats)
        d.tutorOfTFG = new int[]{
                1, // TFG 0 → Anna
                2, // TFG 1 → Marc
                3, // TFG 2 → Jordi
                1, // TFG 3 → Anna
                4, // TFG 4 → Laura
                2, // TFG 5 → Marc
                5  // TFG 6 → Pere
        };

        // ─────────────────────────────
        // Paràmetres
        d.maxTribunalsPerProfessor = 2;
        d.maxDefensesPerSlot = 2;

        return d;
    }
}
