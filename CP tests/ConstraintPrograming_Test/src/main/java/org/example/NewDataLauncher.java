package org.example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.example.Data.Domain.Adjudicacio;
import org.example.Data.NewExampleData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewDataLauncher {

    public static void main(String[] args) {
        System.out.println("Iniciant adjudicació de tribunals amb les noves dades...\n");

        // Les dades venen d'una classe externa
        NewExampleData data = NewExampleData.buildExampleData();

        Model model = new Model("Adjudicació tribunals TFG (sense trios)");

        int P = data.getNumProfessors();
        int T = data.getTfgCount();
        int S = data.getSlotsCount();

        // ─────────────────────────────
        // Variables
        IntVar[] profA = new IntVar[T];
        IntVar[] profB = new IntVar[T];
        IntVar[] slot  = new IntVar[T];

        for (int t = 0; t < T; t++) {
            profA[t] = model.intVar("profA_" + t, 1, P);
            profB[t] = model.intVar("profB_" + t, 1, P);
            slot[t]  = model.intVar("slot_"  + t, 1, S);

            // 1️⃣ El tutor no pot ser membre del tribunal
            int tutorId = data.getTutorSolverIdOfTFG(t);
            model.arithm(profA[t], "!=", tutorId).post();
            model.arithm(profB[t], "!=", tutorId).post();

            // 2️⃣ Els dos membres han de ser diferents
            model.arithm(profA[t], "!=", profB[t]).post();

            // 3️⃣ Disponibilitat professor / franja
            Tuples allowed = new Tuples(true);
            for (int p = 1; p <= P; p++) {
                for (int s = 1; s <= S; s++) {
                    if (data.isProfAvailableAt(p, s)) {
                        allowed.add(p, s);
                    }
                }
            }
            model.table(new IntVar[]{profA[t], slot[t]}, allowed).post();
            model.table(new IntVar[]{profB[t], slot[t]}, allowed).post();
        }

        // ─────────────────────────────
        // 4️⃣ Màxim tribunals per professor
        IntVar[] allProfs = new IntVar[2 * T];
        for (int t = 0; t < T; t++) {
            allProfs[2 * t]     = profA[t];
            allProfs[2 * t + 1] = profB[t];
        }

        IntVar[] counts = new IntVar[P];
        int[] values = new int[P];
        for (int p = 0; p < P; p++) {
            counts[p] = model.intVar(
                    "count_prof_" + (p + 1),
                    0,
                    data.maxTribunalsPerProfessor
            );
            values[p] = p + 1;
        }
        model.globalCardinality(allProfs, values, counts, false).post();

        // ─────────────────────────────
        // 5️⃣ Màxim defenses per franja
        IntVar[] countsSlot = new IntVar[S];
        int[] slotValues = new int[S];
        for (int s = 0; s < S; s++) {
            countsSlot[s] = model.intVar(
                    "count_slot_" + (s + 1),
                    0,
                    data.maxDefensesPerSlot
            );
            slotValues[s] = s + 1;
        }
        model.globalCardinality(slot, slotValues, countsSlot, false).post();

        // ─────────────────────────────
        // 6️⃣ Un professor no pot estar en dos tribunals al mateix slot
        for (int p = 1; p <= P; p++) {
            for (int i = 0; i < T; i++) {
                for (int j = i + 1; j < T; j++) {

                    BoolVar pInI = model.boolVar("p" + p + "_in_tfg_" + i);
                    BoolVar pInJ = model.boolVar("p" + p + "_in_tfg_" + j);

                    model.or(
                            model.arithm(profA[i], "=", p),
                            model.arithm(profB[i], "=", p)
                    ).reifyWith(pInI);

                    model.or(
                            model.arithm(profA[j], "=", p),
                            model.arithm(profB[j], "=", p)
                    ).reifyWith(pInJ);

                    model.ifThen(
                            model.and(
                                    model.arithm(pInI, "=", 1),
                                    model.arithm(pInJ, "=", 1)
                            ),
                            model.arithm(slot[i], "!=", slot[j])
                    );
                }
            }
        }

        // ─────────────────────────────
        // Resolució
        Solution solution = model.getSolver().findSolution();
        if (solution == null) {
            System.out.println("❌ No hi ha cap solució.");
            return;
        }

        int[] presidenciesPerTFG = new int[T];
        int[] vocalsPerTFG = new int[T];
        int[] slotsPerTFG = new int[T];
        for (int t = 0; t < T; t++) {
            presidenciesPerTFG[t] = solution.getIntVal(profA[t]);
            vocalsPerTFG[t] = solution.getIntVal(profB[t]);
            slotsPerTFG[t] = solution.getIntVal(slot[t]);
        }

        Adjudicacio adjudicacio = data.guardarSolucioEnAdjudicacions(
                presidenciesPerTFG,
                vocalsPerTFG,
                slotsPerTFG,
                LocalDateTime.now()
        );

        // ─────────────────────────────
        // Resultats
        System.out.println("✅ Solució trobada:\n");
        for (int t = 0; t < T; t++) {
            System.out.printf(
                    "TFG %d: Tutor = %-15s | Tribunal = (%-15s, %-15s) | Slot = %d%n",
                    t,
                    data.getTreballByIndex(t).getTutor().getName(),
                    data.getDocentBySolverId(solution.getIntVal(profA[t])).getName(),
                    data.getDocentBySolverId(solution.getIntVal(profB[t])).getName(),
                    solution.getIntVal(slot[t])
            );
        }

        System.out.println("\n📊 Càrrega per professor:");
        for (int p = 0; p < P; p++) {
            System.out.printf(
                    "%-15s -> %d tribunals%n",
                    data.getDocentBySolverId(p + 1).getName(),
                    solution.getIntVal(counts[p])
            );
        }

        System.out.println("\n🕒 Defenses per franja:");
        for (int s = 0; s < S; s++) {
            System.out.printf(
                    "Slot %d -> %d defenses%n",
                    s + 1,
                    solution.getIntVal(countsSlot[s])
            );
        }

        // Vista detallada: tribunals celebrats a cada franja
        Map<Integer, List<String>> tribunalsPerSlot = new HashMap<>();
        for (int s = 1; s <= S; s++) {
            tribunalsPerSlot.put(s, new ArrayList<>());
        }

        for (int t = 0; t < T; t++) {
            int assignedSlot = solution.getIntVal(slot[t]);
            String tribunalInfo = String.format(
                    "TFG %d -> (%s, %s)",
                    t,
                    data.getDocentBySolverId(solution.getIntVal(profA[t])).getName(),
                    data.getDocentBySolverId(solution.getIntVal(profB[t])).getName()
            );
            tribunalsPerSlot.get(assignedSlot).add(tribunalInfo);
        }

        System.out.printf("\n📅 Tribunals per horari ");
        for (int s = 1; s <= S; s++) {
            System.out.printf("Slot %d (%s):%n", s, data.slots.get(s - 1).getDataDis());
            List<String> tribunals = tribunalsPerSlot.get(s);
            if (tribunals.isEmpty()) {
                System.out.println("  - Cap tribunal");
            } else {
                for (String tribunal : tribunals) {
                    System.out.println("  - " + tribunal);
                }
            }
        }
    }
}
