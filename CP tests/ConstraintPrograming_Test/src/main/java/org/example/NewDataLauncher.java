package org.example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.example.Data.Domain.Expertesa;
import org.example.Data.NewExampleData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewDataLauncher {

    public static void main(String[] args) {

        NewExampleData data = NewExampleData.buildExampleData();

        Model model = new Model("Adjudicació TFG OPTIMITZADA");

        int P = data.getNumProfessors();
        int T = data.getTfgCount();
        int S = data.getSlotsCount();

        // ─────────────────────────────
        // 1. PRECOMPUTACIÓ
        // ─────────────────────────────

        // Veterans
        List<Integer> veteranList = new ArrayList<>();
        for (int i = 0; i < data.docents.size(); i++) {
            if (data.docents.get(i).isVeteran()) {
                veteranList.add(i + 1);
            }
        }

        int[] veteranIds = veteranList.stream().mapToInt(Integer::intValue).toArray();

        if (veteranIds.length == 0) {
            System.out.println("❌ No hi ha veterans.");
            return;
        }

        // Disponibilitat (GLOBAL)
        Tuples availability = new Tuples(true);
        for (int p = 1; p <= P; p++) {
            for (int s = 1; s <= S; s++) {
                if (data.isProfAvailableAt(p, s)) {
                    availability.add(p, s);
                }
            }
        }

        // Expertesa (GLOBAL PER TFG)
        Map<Expertesa, Tuples> expertiseMap = new HashMap<>();

        for (Expertesa exp : data.experteses) {
            Tuples t = new Tuples(true);
            for (int p1 = 1; p1 <= P; p1++) {
                boolean m1 = data.getDocentBySolverId(p1).getExperteses().contains(exp);
                for (int p2 = 1; p2 <= P; p2++) {
                    boolean m2 = data.getDocentBySolverId(p2).getExperteses().contains(exp);
                    if (m1 || m2) {
                        t.add(p1, p2);
                    }
                }
            }
            expertiseMap.put(exp, t);
        }

        // TFGs tutoritzats per professor
        int[] tutorCount = new int[P + 1];
        for (int t = 0; t < T; t++) {
            int tutor = data.getTutorSolverIdOfTFG(t);
            tutorCount[tutor]++;
        }

        // ─────────────────────────────
        // 2. VARIABLES
        // ─────────────────────────────

        IntVar[] profA = new IntVar[T];
        IntVar[] profB = new IntVar[T];
        IntVar[] slot  = new IntVar[T];

        for (int t = 0; t < T; t++) {
            profA[t] = model.intVar("A_" + t, 1, P);
            profB[t] = model.intVar("B_" + t, 1, P);
            slot[t]  = model.intVar("S_" + t, 1, S);
        }

        // ─────────────────────────────
        // 3. RESTRICCIONS LOCALS
        // ─────────────────────────────

        for (int t = 0; t < T; t++) {

            int tutor = data.getTutorSolverIdOfTFG(t);

            // President veterà
            model.member(profA[t], veteranIds).post();

            // Tutor fora
            model.arithm(profA[t], "!=", tutor).post();
            model.arithm(profB[t], "!=", tutor).post();

            // Diferents
            model.arithm(profA[t], "!=", profB[t]).post();

            // Disponibilitat
            model.table(new IntVar[]{profA[t], slot[t]}, availability).post();
            model.table(new IntVar[]{profB[t], slot[t]}, availability).post();

            // Expertesa
            Expertesa exp = data.getTreballByIndex(t).getExpertesa();
            model.table(new IntVar[]{profA[t], profB[t]}, expertiseMap.get(exp)).post();
        }

        // ─────────────────────────────
        // 4. GLOBAL CARDINALITY
        // ─────────────────────────────

        IntVar[] allProfs = new IntVar[2 * T];
        for (int t = 0; t < T; t++) {
            allProfs[2*t] = profA[t];
            allProfs[2*t+1] = profB[t];
        }

        IntVar[] counts = new IntVar[P];
        int[] values = new int[P];

        //TODO mirar
        for (int p = 0; p < P; p++) {
            int maxAllowed = Math.min(
                    data.maxTribunalsPerProfessor,
                    2 * tutorCount[p+1]
            );

            counts[p] = model.intVar("count_" + (p+1), 0, maxAllowed);
            values[p] = p + 1;
        }

        model.globalCardinality(allProfs, values, counts, false).post();

        // ─────────────────────────────
        // 5. LIMIT PER SLOT
        // ─────────────────────────────

        IntVar[] slotCounts = new IntVar[S];
        int[] slotValues = new int[S];

        for (int s = 0; s < S; s++) {
            slotCounts[s] = model.intVar(0, data.maxDefensesPerSlot);
            slotValues[s] = s + 1;
        }

        model.globalCardinality(slot, slotValues, slotCounts, false).post();

        // ─────────────────────────────
        // 6. NO SOLAPAMENT (OPTIMITZAT)
        // ─────────────────────────────

        for (int p = 1; p <= P; p++) {

            List<BoolVar> presence = new ArrayList<>();

            for (int t = 0; t < T; t++) {
                BoolVar isIn = model.boolVar();

                model.or(
                        model.arithm(profA[t], "=", p),
                        model.arithm(profB[t], "=", p)
                ).reifyWith(isIn);

                presence.add(isIn);
            }

            // Per cada slot, màxim 1 aparició
            for (int s = 1; s <= S; s++) {

                List<BoolVar> inSlot = new ArrayList<>();

                for (int t = 0; t < T; t++) {
                    BoolVar b = model.boolVar();
                    BoolVar slotIsS = model.boolVar();

                    model.arithm(slot[t], "=", s).reifyWith(slotIsS);

                    model.and(presence.get(t), slotIsS).reifyWith(b);

                    inSlot.add(b);
                }

                model.sum(inSlot.toArray(new BoolVar[0]), "<=", 1).post();
            }
        }
        
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

        List<?> tribunalsGuardats = data.guardarSolucioEnTribunals(
                presidenciesPerTFG,
                vocalsPerTFG,
                slotsPerTFG
        );

        // ─────────────────────────────
        // Resultats
        System.out.println("✅ Solució trobada:\n");
        System.out.printf("📌 S'han guardat %d tribunals.%n", tribunalsGuardats.size());

        System.out.println("\n📊 Càrrega per professor:");
        for (int p = 0; p < P; p++) {
            System.out.printf(
                    "%-15s -> %d tribunals%n",
                    data.getDocentBySolverId(p + 1).getName(),
                    solution.getIntVal(counts[p])
            );
        }

        System.out.println("\n🕒 Defenses per data i hora:");
        for (int s = 0; s < S; s++) {
            System.out.printf(
                    "%s -> %d defenses%n",
                    data.slots.get(s).getDataDis(),
                    solution.getIntVal(slotCounts[s])
            );
        }

        // Vista detallada: tribunals celebrats a cada franja
        Map<Integer, List<String>> tribunalsPerSlot = new HashMap<>();
        for (int s = 1; s <= S; s++) {
            tribunalsPerSlot.put(s, new ArrayList<>());
        }

        for (int t = 0; t < T; t++) {
            int assignedSlot = solution.getIntVal(slot[t]);
            var presidencia = data.getDocentBySolverId(solution.getIntVal(profA[t]));
            var vocal = data.getDocentBySolverId(solution.getIntVal(profB[t]));
            String tribunalInfo = String.format(
                    "TFG %d (%s) -> (%s , %s)",
                    t,
                    data.getTreballByIndex(t).getStudent().getName(),
                    presidencia.getName(),
                    vocal.getName()

            );
            tribunalsPerSlot.get(assignedSlot).add(tribunalInfo);
        }

        System.out.println("\n📅 Tribunals per data i hora:");
        for (int s = 1; s <= S; s++) {
            System.out.printf("%s:%n", data.slots.get(s - 1).getDataDis());
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
