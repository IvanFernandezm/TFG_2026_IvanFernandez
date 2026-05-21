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

        System.out.println("[DEBUG] Iniciando NewDataLauncher...");

        NewExampleData data = NewExampleData.buildExampleData();

        System.out.printf(
                "[DEBUG] Datos cargados: %d profes, %d TFGs, %d slots, %d experteses.%n",
                data.getNumProfessors(),
                data.getTfgCount(),
                data.getSlotsCount(),
                data.experteses.size()
        );

        Model model = new Model("Adjudicació TFG OPTIMITZADA");

        int P = data.getNumProfessors();
        int T = data.getTfgCount();
        int S = data.getSlotsCount();

        // ─────────────────────────────
        // 1. PRECOMPUTACIÓ
        // ─────────────────────────────

        System.out.println("[DEBUG] Preparant precomputacions...");

        // Veterans
        List<Integer> veteranList = new ArrayList<>();

        for (int i = 0; i < data.docents.size(); i++) {
            if (data.docents.get(i).isVeteran()) {
                veteranList.add(i + 1);
            }
        }

        int[] veteranIds = veteranList.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        if (veteranIds.length == 0) {
            System.out.println("❌ No hi ha veterans.");
            return;
        }

        System.out.printf(
                "[DEBUG] Veterans detectats: %d%n",
                veteranIds.length
        );

        // Disponibilitat
        Tuples availability = new Tuples(true);

        for (int p = 1; p <= P; p++) {
            for (int s = 1; s <= S; s++) {

                if (data.isProfAvailableAt(p, s)) {
                    availability.add(p, s);
                }
            }
        }

        System.out.println("[DEBUG] Taula disponibilitat creada.");

        // Expertesa
        Map<Expertesa, Tuples> expertiseMap = new HashMap<>();

        for (Expertesa exp : data.experteses) {

            Tuples tuples = new Tuples(true);

            for (int p1 = 1; p1 <= P; p1++) {

                boolean m1 = data.getDocentBySolverId(p1)
                        .getExperteses()
                        .contains(exp);

                for (int p2 = 1; p2 <= P; p2++) {

                    boolean m2 = data.getDocentBySolverId(p2)
                            .getExperteses()
                            .contains(exp);

                    if (m1 || m2) {
                        tuples.add(p1, p2);
                    }
                }
            }

            expertiseMap.put(exp, tuples);
        }

        System.out.printf(
                "[DEBUG] Taules expertesa creades: %d%n",
                expertiseMap.size()
        );

        // Comptatge tutoritzacions
        int[] tutorCount = new int[P + 1];

        for (int t = 0; t < T; t++) {
            int tutor = data.getTutorSolverIdOfTFG(t);
            tutorCount[tutor]++;
        }

        System.out.println("[DEBUG] Comptatge tutoritzacions preparat.");

        // Diagnòstic ràpid de viabilitat abans de modelar-ho tot
        int[] maxAssignmentsPerProfessor = new int[P + 1];
        int totalAssignmentCapacity = 0;
        int veteranPresidencyCapacity = 0;
        for (int p = 1; p <= P; p++) {
            // Capacitat basada només en la quantitat de TFG que tutoritza
            int maxAllowed = 2 * tutorCount[p];
            maxAssignmentsPerProfessor[p] = maxAllowed;
            totalAssignmentCapacity += maxAllowed;
            if (data.getDocentBySolverId(p).isVeteran()) {
                veteranPresidencyCapacity += maxAllowed;
            }
        }

        int requiredAssignments = 2 * T;
        int slotCapacity = S * data.maxDefensesPerSlot;

        System.out.printf("[DEBUG] Capacitat professors: %d assignacions | Necessàries: %d%n", totalAssignmentCapacity, requiredAssignments);
        System.out.printf("[DEBUG] Capacitat slots: %d defenses | Necessàries: %d%n", slotCapacity, T);
        System.out.printf("[DEBUG] Capacitat presidències veterans: %d | Necessàries: %d%n", veteranPresidencyCapacity, T);

        if (totalAssignmentCapacity < requiredAssignments) {
            System.out.println("❌ Model inviable: la capacitat màxima de tribunals per professor és massa baixa per cobrir tots els TFG.");
            return;
        }
        if (slotCapacity < T) {
            System.out.println("❌ Model inviable: no hi ha prou capacitat de slots per totes les defenses.");
            return;
        }
        if (veteranPresidencyCapacity < T) {
            System.out.println("❌ Model inviable: no hi ha prou capacitat de veterans per cobrir totes les presidències.");
            return;
        }

        // ─────────────────────────────
        // 2. VARIABLES
        // ─────────────────────────────

        System.out.println("[DEBUG] Creant variables amb dominis reduïts...");

        IntVar[] profA = new IntVar[T];
        IntVar[] profB = new IntVar[T];
        IntVar[] slot = new IntVar[T];

        // Precalcular dominis per TFG per reduir l'espai de cerca
        for (int t = 0; t < T; t++) {
            int tutor = data.getTutorSolverIdOfTFG(t);

            // ProfA ha de ser veteran i no pot ser tutor; filtrar per disponibilitat en algun slot
            List<Integer> candidatesA = new ArrayList<>();
            List<Integer> candidatesB = new ArrayList<>();

            for (int p = 1; p <= P; p++) {
                if (p == tutor) continue; // excloure tutor

                boolean availableSomeSlot = false;
                for (int s = 1; s <= S; s++) {
                    if (data.isProfAvailableAt(p, s)) { availableSomeSlot = true; break; }
                }
                if (!availableSomeSlot) continue;

                // A: només veterans
                if (data.getDocentBySolverId(p).isVeteran()) candidatesA.add(p);

                // B: qualsevol disponible (i no tutor)
                candidatesB.add(p);
            }

            if (candidatesA.isEmpty()) {
                System.out.printf("[DEBUG] Avís: cap candidat veterà disponible per TFG %d\n", t + 1);
                // fallback: permetre qualsevol professor menys el tutor
                for (int p = 1; p <= P; p++) if (p != tutor) candidatesA.add(p);
            }

            if (candidatesB.isEmpty()) {
                System.out.printf("[DEBUG] Avís: cap candidat disponible per vocal en TFG %d\n", t + 1);
                for (int p = 1; p <= P; p++) if (p != tutor) candidatesB.add(p);
            }

            int[] domA = candidatesA.stream().mapToInt(Integer::intValue).toArray();
            int[] domB = candidatesB.stream().mapToInt(Integer::intValue).toArray();

            profA[t] = model.intVar("A_" + t, domA);
            profB[t] = model.intVar("B_" + t, domB);
            slot[t] = model.intVar("S_" + t, 1, S);
        }

        System.out.printf("[DEBUG] Variables creades amb dominis reduïts: %d A, %d B, %d slots.%n", profA.length, profB.length, slot.length);

        // ─────────────────────────────
        // 3. RESTRICCIONS LOCALS
        // ─────────────────────────────

        System.out.println("[DEBUG] Aplicant restriccions locals...");

        BoolVar[] hasAllExpertise = new BoolVar[T];

        for (int t = 0; t < T; t++) {

            int tutor = data.getTutorSolverIdOfTFG(t);

            // President veterà
            model.member(profA[t], veteranIds).post();

            // Tutor exclòs
            model.arithm(profA[t], "!=", tutor).post();
            model.arithm(profB[t], "!=", tutor).post();

            // Diferents
            model.arithm(profA[t], "!=", profB[t]).post();

            // Disponibilitat
            model.table(
                    new IntVar[]{profA[t], slot[t]},
                    availability
            ).post();

            model.table(
                    new IntVar[]{profB[t], slot[t]},
                    availability
            ).post();

            // Expertesa optimitzable (almenys un docent expert)
            Expertesa exp = data.getTreballByIndex(t).getExpertesa();

            hasAllExpertise[t] = model.boolVar("expert_" + t);

            model.table(
                    new IntVar[]{profA[t], profB[t]},
                    expertiseMap.get(exp)
            ).reifyWith(hasAllExpertise[t]);

            if (t < 3) {
                System.out.printf(
                        "[DEBUG] Restriccions TFG %d aplicades.%n",
                        t + 1
                );
            }
        }

        System.out.println("[DEBUG] Restriccions locals completades.");

        // ─────────────────────────────
        // 4. GLOBAL CARDINALITY
        // ─────────────────────────────

        System.out.println("[DEBUG] Aplicant càrrega professors...");

        IntVar[] allProfs = new IntVar[2 * T];

        for (int t = 0; t < T; t++) {
            allProfs[2 * t] = profA[t];
            allProfs[2 * t + 1] = profB[t];
        }

        IntVar[] counts = new IntVar[P];
        int[] values = new int[P];

        for (int p = 0; p < P; p++) {

            int maxAllowed = maxAssignmentsPerProfessor[p + 1];

            counts[p] = model.intVar(
                    "count_" + (p + 1),
                    0,
                    maxAllowed
            );

            values[p] = p + 1;

            System.out.printf(
                    "[DEBUG] %s -> max %d tribunals%n",
                    data.getDocentBySolverId(p + 1).getName(),
                    maxAllowed
            );
        }

        model.globalCardinality(
                allProfs,
                values,
                counts,
                false
        ).post();

        System.out.println("[DEBUG] Global cardinality professors aplicada.");

        // ─────────────────────────────
        // 5. LIMIT PER SLOT
        // ─────────────────────────────

        System.out.println("[DEBUG] Aplicant límit slots...");

        IntVar[] slotCounts = new IntVar[S];
        int[] slotValues = new int[S];

        for (int s = 0; s < S; s++) {

            slotCounts[s] = model.intVar(
                    "slotCount_" + s,
                    0,
                    data.maxDefensesPerSlot
            );

            slotValues[s] = s + 1;
        }

        model.globalCardinality(
                slot,
                slotValues,
                slotCounts,
                false
        ).post();

        System.out.println("[DEBUG] Global cardinality slots aplicada.");

        // ─────────────────────────────
        // 6. NO SOLAPAMENT (VERSIÓN SIMPLIFICADA)
        // ─────────────────────────────
        System.out.println("[DEBUG] Aplicant restriccions de no solapament optimitzades...");

        for (int t1 = 0; t1 < T; t1++) {

            for (int t2 = t1 + 1; t2 < T; t2++) {

                // Si comparteixen slot:
                BoolVar sameSlot =
                        model.arithm(slot[t1], "=", slot[t2]).reify();

                // No poden compartir cap docent
                model.ifThen(
                        sameSlot,

                        model.and(
                                model.arithm(profA[t1], "!=", profA[t2]),
                                model.arithm(profA[t1], "!=", profB[t2]),
                                model.arithm(profB[t1], "!=", profA[t2]),
                                model.arithm(profB[t1], "!=", profB[t2])
                        )
                );
            }
        }

        System.out.println("[DEBUG] Restriccions no-solapament aplicades.");
        // ─────────────────────────────
        // 7. OBJECTIU
        // ─────────────────────────────

        IntVar expertCount = model.intVar(
                "expertCount",
                0,
                T
        );

        model.sum(
                hasAllExpertise,
                "=",
                expertCount
        ).post();

        System.out.println("[DEBUG] Funció objectiu preparada.");

        // ─────────────────────────────
        // 8. ESTRATÈGIA I RESTRICCIONS DE TEMPS
        // ─────────────────────────────

        // Configurar estratègia: treballar en l'ordre (profA, profB, slot) amb heurística minDomLB
        IntVar[] searchVars = new IntVar[profA.length + profB.length + slot.length];
        int idx = 0;
        for (IntVar v : profA) searchVars[idx++] = v;
        for (IntVar v : profB) searchVars[idx++] = v;
        for (IntVar v : slot)   searchVars[idx++] = v;

        try {
            model.getSolver().setSearch(org.chocosolver.solver.search.strategy.Search.minDomLBSearch(searchVars));
            System.out.println("[DEBUG] Estratègia de cerca establerta: minDomLBSearch sobre (A,B,S).");
        } catch (Exception ex) {
            System.out.println("[DEBUG] No s'ha pogut establir l'estratègia minDomLBSearch: " + ex.getMessage());
        }

        // Sense límit de temps explícit

        // ─────────────────────────────
        // 9. RESOLUCIÓ (ÓPTIMA dins del límit)
        // ─────────────────────────────

        System.out.println("[DEBUG] Començant cerca de solució òptima (dins del límit)...");

        long start = System.currentTimeMillis();

        //Solution solution = model.getSolver().findOptimalSolution(expertCount, true);
        Solution solution = model.getSolver().findSolution();

        long end = System.currentTimeMillis();

        System.out.printf(
                "[DEBUG] Temps resolució: %.2f segons%n",
                (end - start) / 1000.0
        );

        if (solution == null) {
            System.out.println("❌ No hi ha cap solució.");
            return;
        }

        System.out.printf(
                "[DEBUG] Solució trobada. expertCount=%d%n",
                solution.getIntVal(expertCount)
        );

        // ─────────────────────────────
        // RESULTATS
        // ─────────────────────────────

        int[] presidenciesPerTFG = new int[T];
        int[] vocalsPerTFG = new int[T];
        int[] slotsPerTFG = new int[T];

        for (int t = 0; t < T; t++) {

            presidenciesPerTFG[t] =
                    solution.getIntVal(profA[t]);

            vocalsPerTFG[t] =
                    solution.getIntVal(profB[t]);

            slotsPerTFG[t] =
                    solution.getIntVal(slot[t]);
        }

        List<?> tribunalsGuardats =
                data.guardarSolucioEnTribunals(
                        presidenciesPerTFG,
                        vocalsPerTFG,
                        slotsPerTFG
                );

        System.out.println("\n✅ Solució trobada:\n");

        System.out.printf(
                "📌 S'han guardat %d tribunals.%n",
                tribunalsGuardats.size()
        );

        int tribunalsAmbExperts = 0;

        for (BoolVar expert : hasAllExpertise) {

            if (solution.getIntVal(expert) == 1) {
                tribunalsAmbExperts++;
            }
        }

        System.out.printf(
                "🎓 Tribunals amb almenys un docent expert: %d/%d%n",
                tribunalsAmbExperts,
                T
        );

        System.out.println("\n📊 Càrrega per professor:");

        for (int p = 0; p < P; p++) {

            System.out.printf(
                    "%-20s -> %d tribunals%n",
                    data.getDocentBySolverId(p + 1).getName(),
                    solution.getIntVal(counts[p])
            );
        }

        System.out.println("\n🕒 Defenses per slot:");

        for (int s = 0; s < S; s++) {

            System.out.printf(
                    "%s -> %d defenses%n",
                    data.slots.get(s).getDataDis(),
                    solution.getIntVal(slotCounts[s])
            );
        }

        System.out.println("\n📅 Tribunals per data i hora:");

        Map<Integer, List<String>> tribunalsPerSlot = new HashMap<>();

        for (int s = 1; s <= S; s++) {
            tribunalsPerSlot.put(s, new ArrayList<>());
        }

        for (int t = 0; t < T; t++) {

            int assignedSlot = solution.getIntVal(slot[t]);

            var president =
                    data.getDocentBySolverId(
                            solution.getIntVal(profA[t])
                    );

            var vocal =
                    data.getDocentBySolverId(
                            solution.getIntVal(profB[t])
                    );

            var treball =
                    data.getTreballByIndex(t);

            String expertMark =
                    solution.getIntVal(hasAllExpertise[t]) == 1
                            ? "✓"
                            : "⚠";

            String tribunalInfo = String.format(
                    "%s TFG %d (%s) - %s -> (%s, %s)",
                    expertMark,
                    t + 1,
                    treball.getStudent().getName(),
                    treball.getTitle(),
                    president.getName(),
                    vocal.getName()
            );

            tribunalsPerSlot.get(assignedSlot).add(tribunalInfo);
        }

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