package org.example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.example.Data.ExampleData;

import java.util.ArrayList;
import java.util.List;

import static org.example.Data.ExampleData.buildExampleData;

public class MainSinTriplets {
    public static void main(String[] args) {
        System.out.println("Aquest codi no utilitza taules de tuples per a les restriccions.");
        ExampleData data = buildExampleData();

        Model model = new Model("Adjudicació tribunals TFG");

        int P = data.numProfessors;
        int T = data.tfgCount;
        int S = data.slotsCount;

        // Variables, professor 1 i professor 2, slot es franja horaria
        IntVar[] profA = new IntVar[T];
        IntVar[] profB = new IntVar[T];
        IntVar[] slot = new IntVar[T];

        for (int t = 0; t < T; t++) {
            profA[t] = model.intVar("profA_" + t, 1, P, true);
            profB[t] = model.intVar("profB_" + t, 1, P, true);
            slot[t] = model.intVar("slot_" + t, 1, S, true);

            // 1️⃣ El tutor no pot ser membre del tribunal
            int tutorId = data.tutorOfTFG[t];
            model.arithm(profA[t], "!=", tutorId).post();
            model.arithm(profB[t], "!=", tutorId).post();

            // 2️⃣ Els dos membres han de ser diferents
            model.arithm(profA[t], "!=", profB[t]).post();

            // 3️⃣ Disponibilitat dels professors
            Tuples allowed = new Tuples(true);
            for (int pId = 1; pId <= P; pId++) {
                for (int sId = 1; sId <= S; sId++) {
                    if (data.isProfAvailableAt(pId, sId)) {
                        allowed.add(pId, sId);
                    }
                }
            }
            model.table(new IntVar[]{profA[t], slot[t]}, allowed).post();
            model.table(new IntVar[]{profB[t], slot[t]}, allowed).post();
        }

        // 4️⃣ Un professor no pot estar en més de 2 tribunals
        IntVar[] allProfs = new IntVar[2 * T];
        for (int i = 0; i < T; i++) {
            allProfs[2 * i] = profA[i];
            allProfs[2 * i + 1] = profB[i];
        }

        IntVar[] counts = new IntVar[P];
        for (int p = 0; p < P; p++) {
            counts[p] = model.intVar("count_prof_" + (p + 1), 0, data.maxTribunalsPerProfessor, true);
        }
        int[] values = new int[P];
        for (int i = 0; i < P; i++) values[i] = i + 1;
        model.globalCardinality(allProfs, values, counts, false).post();

        // 5️⃣ Màxim 2 defenses al mateix slot
        IntVar[] countsSlot = new IntVar[S];
        for (int s = 0; s < S; s++) {
            countsSlot[s] = model.intVar("count_slot_" + (s + 1), 0, data.maxDefensesPerSlot, true);
        }
        int[] slotValues = new int[S];
        for (int s = 0; s < S; s++) slotValues[s] = s + 1;
        model.globalCardinality(slot, slotValues, countsSlot, false).post();

        //per un slot/horari una persona no pot estar dos slots

        // 6️⃣ Relació "professor jutja TFG tutoritzat per un altre"
        BoolVar[][] judgesTutor = new BoolVar[P + 1][P + 1];
        // judgesTutor[a][b] = 1 si el professor a jutja algun TFG tutoritzat per b

        for (int a = 1; a <= P; a++) {
            for (int b = 1; b <= P; b++) {
                judgesTutor[a][b] = model.boolVar("judges_" + a + "_tutor_" + b);
            }
        }

        for (int t = 0; t < T; t++) {
            int tutor = data.tutorOfTFG[t];

            for (int p = 1; p <= P; p++) {

                // p és jutge del TFG t?
                BoolVar isJudge = model.boolVar("isJudge_p" + p + "_tfg" + t);

                model.or(
                        model.arithm(profA[t], "=", p),
                        model.arithm(profB[t], "=", p)
                ).reifyWith(isJudge);

                // Si p jutja un TFG tutoritzat per 'tutor'
                model.ifThen(
                        model.arithm(isJudge, "=", 1),
                        model.arithm(judgesTutor[p][tutor], "=", 1)
                );
            }
        }

        // 🔍 Buscar solució
        Solution solution = model.getSolver().findSolution();
        if (solution == null) {
            System.out.println("❌ No hi ha cap solució que satisfaci totes les restriccions.");
            return;
        }

        System.out.println("✅ Solució trobada:\n");
        for (int t = 0; t < T; t++) {
            int a = solution.getIntVal(profA[t]);
            int b = solution.getIntVal(profB[t]);
            int s = solution.getIntVal(slot[t]);
            int tutorId = data.tutorOfTFG[t];
            System.out.printf("TFG %d: Tutor = %-15s | Tribunal = (%-15s, %-15s) | Slot = %d%n",
                    t,
                    data.professorNames.get(tutorId - 1),
                    data.professorNames.get(a - 1),
                    data.professorNames.get(b - 1),
                    s);
        }

        System.out.println("\n📊 Càrrega per professor:");
        for (int p = 1; p <= P; p++) {
            int cnt = solution.getIntVal(counts[p - 1]);
            System.out.printf("%-15s -> %d tribunals%n", data.professorNames.get(p - 1), cnt);
        }

        System.out.println("\n🕒 Defenses per franja horària:");
        for (int s = 1; s <= S; s++) {
            int cnt = solution.getIntVal(countsSlot[s - 1]);
            System.out.printf("Slot %d -> %d defenses%n", s, cnt);
        }

    }


}

