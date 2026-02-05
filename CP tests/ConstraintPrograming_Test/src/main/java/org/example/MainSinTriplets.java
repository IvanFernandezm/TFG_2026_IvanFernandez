package org.example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.example.Data.ExampleData;

public class MainSinTriplets {

    public static void main(String[] args) {

        // Les dades venen d'una classe externa
        ExampleData data = ExampleData.buildExampleData();

        Model model = new Model("Adjudicació tribunals TFG (sense trios)");

        int P = data.numProfessors;
        int T = data.tfgCount;
        int S = data.slotsCount;

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
            int tutorId = data.tutorOfTFG[t];
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

        // ─────────────────────────────
        // Resultats
        System.out.println("✅ Solució trobada:\n");
        for (int t = 0; t < T; t++) {
            System.out.printf(
                    "TFG %d: Tutor = %-15s | Tribunal = (%-15s, %-15s) | Slot = %d%n",
                    t,
                    data.professorNames.get(data.tutorOfTFG[t] - 1),
                    data.professorNames.get(solution.getIntVal(profA[t]) - 1),
                    data.professorNames.get(solution.getIntVal(profB[t]) - 1),
                    solution.getIntVal(slot[t])
            );
        }

        System.out.println("\n📊 Càrrega per professor:");
        for (int p = 0; p < P; p++) {
            System.out.printf(
                    "%-15s -> %d tribunals%n",
                    data.professorNames.get(p),
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
    }
}
