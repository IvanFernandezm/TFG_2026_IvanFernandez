package org.example.tribunalsbackend.Controller;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.example.tribunalsbackend.Api.DTO.TribunalDTO;
import org.example.tribunalsbackend.Config.Exceptions.TribunalsAutomatedSolutionException;
import org.example.tribunalsbackend.Domain.*;
import org.example.tribunalsbackend.Persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataImportController {
    DocentRepository docentRepository;
    EstudiantRepository estudiantRepository;
    TreballRepository treballRepository;
    ExpertesaRepository expertesaRepository;
    DisponibilitatRepository  disponibilitatRepository;
    TribunalRepository tribunalRepository;

    public DataImportController(DocentRepository docentRepository, EstudiantRepository estudiantRepository,
                                TreballRepository treballRepository, ExpertesaRepository expertesaRepository,
                                DisponibilitatRepository disponibilitatRepository, TribunalRepository tribunalRepository) {
        this.docentRepository = docentRepository;
        this.estudiantRepository = estudiantRepository;
        this.treballRepository = treballRepository;
        this.expertesaRepository = expertesaRepository;
        this.disponibilitatRepository = disponibilitatRepository;
        this.tribunalRepository = tribunalRepository;
    }

    public void importExcel(MultipartFile excelFile) throws Exception {
        List<Disponibilitat> disponibilitats = disponibilitatRepository.findAll();
        Workbook workbook = new XSSFWorkbook(excelFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        boolean firstRow = true;

        for (Row row : sheet) {
            if (firstRow) {firstRow = false; continue;} // Salta la primera fila (capçaleres)

            Cell c0 = row.getCell(0); // nom estudiant
            Cell c1 = row.getCell(1); // mail estudiant
            Cell c2 = row.getCell(2); // nom tutor (docent)
            Cell c3 = row.getCell(3); // mail tutor (docent)
            Cell c4 = row.getCell(4); // descripció treball
            Cell c5 = row.getCell(5); // titol treball
            Cell c6 = row.getCell(6); // expertesa id

            String studentName = getCellString(c0);
            String studentEmail = getCellString(c1);
            String tutorName = getCellString(c2);
            String tutorEmail = getCellString(c3);
            String description = getCellString(c4);
            String title = getCellString(c5);
            String expertesaId = getCellString(c6);

            if(studentEmail == null || tutorName == null || description == null || expertesaId == null || title == null){
                throw new IllegalArgumentException("Alguna de les dades obligatòries és nula a la fila " + row.getRowNum());
            }

            Estudiant student = new Estudiant(studentEmail, studentName);
            Docent tutor = docentRepository.findById(tutorEmail).orElse(new Docent(tutorEmail, tutorName));

            Expertesa expertesa = expertesaRepository.findById(expertesaId)
                    .orElse(expertesaRepository.save(new Expertesa(expertesaId, "Expertesa " + expertesaId)));

            tutor.addExpertesa(expertesa);
            tutor.setAvailability(disponibilitats);

            estudiantRepository.save(student);
            docentRepository.save(tutor);

            Treball tfg =  new Treball(title, description,student,tutor,expertesa);

            treballRepository.save(tfg);
        }
    }
    public List<TribunalDTO> organitzarTribunals(int maxDefensesPerSlot) {
        List<Docent> docents = this.docentRepository.findAll();
        List<Estudiant> estudiants = this.estudiantRepository.findAll();
        List<Treball> treballs = this.treballRepository.findAll();
        List<Disponibilitat> slots = this.disponibilitatRepository.findAll();
        List<Expertesa> experteses = this.expertesaRepository.findAll();

        System.out.println("[DEBUG] Iniciando NewDataLauncher...");

        System.out.printf(
                "[DEBUG] Dades carregades: %d profes, %d TFGs, %d slots, %d experteses.%n",
                docents.size(),
                treballs.size(),
                slots.size(),
                experteses.size()
        );

        Model model = new Model("Adjudicació TFG OPTIMITZADA");

        int P = docents.size();
        int T = treballs.size();
        int S = slots.size();

        // ─────────────────────────────
        // 1. PRECOMPUTACIÓ
        // ─────────────────────────────

        System.out.println("[DEBUG] Preparant precomputacions...");

        // Veterans
        List<Integer> veteranList = new ArrayList<>();

        for (int i = 0; i < docents.size(); i++) {
            if (docents.get(i).isVeteran()) {
                veteranList.add(i + 1);
            }
        }

        int[] veteranIds = veteranList.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        if (veteranIds.length == 0) {
            System.out.println("❌ No hi ha veterans.");
        }

        System.out.printf(
                "[DEBUG] Veterans detectats: %d%n",
                veteranIds.length
        );

        // Disponibilitat
        Tuples availability = new Tuples(true);

        for (int p = 1; p <= P; p++) {
            for (int s = 1; s <= S; s++) {
                Docent doc = docents.get(p - 1);
                Disponibilitat dis = slots.get(s-1);
                if (doc.isAvailableAt(dis)) {
                    availability.add(p, s);
                }
            }
        }

        System.out.println("[DEBUG] Taula disponibilitat creada.");

        // Expertesa
        Map<Expertesa, Tuples> expertiseMap = new HashMap<>();

        for (Expertesa exp : experteses) {

            Tuples tuples = new Tuples(true);

            for (int p1 = 1; p1 <= P; p1++) {

                boolean m1 = docents.get(p1-1)
                        .getExperteses()
                        .contains(exp);

                for (int p2 = 1; p2 <= P; p2++) {

                    boolean m2 = docents.get(p2-1)
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
            Docent doc = treballs.get(t).getTutor();
            int tutor =docents.indexOf(doc);
            tutorCount[tutor]++;
        }

        System.out.println("[DEBUG] Comptatge tutoritzacions preparat.");


        // Diagnòstic ràpid de viabilitat abans de modelar-ho tot
        int[] maxAssignmentsPerProfessor = new int[P + 1];
        int totalAssignmentCapacity = 0;
        int veteranPresidencyCapacity = 0;
        for (int p = 1; p <= P; p++) {
            // Capacitat basada només en la quantitat de TFG que tutoritza
            Docent vet = docents.get(p - 1);
            int maxAllowed = 2 * tutorCount[p];
            maxAssignmentsPerProfessor[p] = maxAllowed;
            totalAssignmentCapacity += maxAllowed;
            if (vet.isVeteran()) {
                veteranPresidencyCapacity += maxAllowed;
            }
        }

        int requiredAssignments = 2 * T;
        int slotCapacity = S * maxDefensesPerSlot;

        System.out.printf("[DEBUG] Capacitat professors: %d assignacions | Necessàries: %d%n", totalAssignmentCapacity, requiredAssignments);
        System.out.printf("[DEBUG] Capacitat slots: %d defenses | Necessàries: %d%n", slotCapacity, T);
        System.out.printf("[DEBUG] Capacitat presidències veterans: %d | Necessàries: %d%n", veteranPresidencyCapacity, T);

        if (totalAssignmentCapacity < requiredAssignments) {
            System.out.println("❌ Model inviable: la capacitat màxima de tribunals per professor és massa baixa per cobrir tots els TFG.");
        }
        if (slotCapacity < T) {
            System.out.println("❌ Model inviable: no hi ha prou capacitat de slots per totes les defenses.");
        }
        if (veteranPresidencyCapacity < T) {
            System.out.println("❌ Model inviable: no hi ha prou capacitat de veterans per cobrir totes les presidències.");
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
            Docent tutor = treballs.get(t).getTutor();
            int tutorPoss = docents.indexOf(tutor); //Posició del tutor a la llista de docents

            // ProfA ha de ser veteran i no pot ser tutor; filtrar per disponibilitat en algun slot
            List<Integer> candidatesA = new ArrayList<>();
            List<Integer> candidatesB = new ArrayList<>();

            for (int p = 1; p <= P; p++) {
                if (p == tutorPoss) continue; // excloure tutor
                Docent doc = docents.get(p - 1);

                boolean availableSomeSlot = false;
                for (int s = 1; s <= S; s++) {
                    Disponibilitat dis = slots.get(s-1);
                    if (doc.isAvailableAt(dis)) { availableSomeSlot = true; break; }
                }
                if (!availableSomeSlot) continue;

                // A: només veterans
                if (doc.isVeteran()) candidatesA.add(p);

                // B: qualsevol disponible (i no tutor)
                candidatesB.add(p);
            }

            if (candidatesA.isEmpty()) {
                System.out.printf("[DEBUG] Avís: cap candidat veterà disponible per TFG %d\n", t + 1);
                // fallback: permetre qualsevol professor menys el tutor
                for (int p = 1; p <= P; p++) if (p != tutorPoss) candidatesA.add(p);
            }

            if (candidatesB.isEmpty()) {
                System.out.printf("[DEBUG] Avís: cap candidat disponible per vocal en TFG %d\n", t + 1);
                for (int p = 1; p <= P; p++) if (p != tutorPoss) candidatesB.add(p);
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
            Docent tutor = treballs.get(t).getTutor();
            int tutorPoss = docents.indexOf(tutor);

            // President veterà
            model.member(profA[t], veteranIds).post();

            // Tutor exclòs
            model.arithm(profA[t], "!=", tutorPoss).post();
            model.arithm(profB[t], "!=", tutorPoss).post();

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
            Expertesa exp = treballs.get(t).getExpertesa();

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
                    docents.get(p).getName(),
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
                    maxDefensesPerSlot
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

        // ─────────────────────────────
        // 9. RESOLUCIÓ (ÓPTIMA dins del límit)
        // ─────────────────────────────

        System.out.println("[DEBUG] Començant cerca de solució òptima (dins del límit)...");

        long start = System.currentTimeMillis();

        //Solution solution = model.getSolver().findOptimalSolution(expertCount, true); -> no arriba a donar una solució
        //Solution solution = model.getSolver().findSolution(); -> no assegura que l'opció donada sigui la més optima
        model.getSolver().limitTime("5s");

        Solution solution = null;

        while(model.getSolver().solve()){

            solution = new Solution(model);
            solution.record();

            System.out.println(
                    "Tribunals amb 2 experts count: "
                            + expertCount.getValue()
            );
        }

        long end = System.currentTimeMillis();

        System.out.printf(
                "[DEBUG] Temps resolució: %.2f segons%n",
                (end - start) / 1000.0
        );

        if (solution == null) {
            throw new TribunalsAutomatedSolutionException("No s'ha pogut trobar cap solució per a l'organització dels tribunals amb les dades i restriccions actuals. Si us plau, revisa les dades d'entrada i les restriccions, i torna-ho a intentar.");
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

        List<Tribunal> tribunals = guardarSolucioEnTribunals(presidenciesPerTFG, vocalsPerTFG, slotsPerTFG);
        List<TribunalDTO> tribunalDTOs = new ArrayList<>();
        for (Tribunal tribunal : tribunals) {
            tribunalDTOs.add(tribunalToDTO(tribunal));
        }
        this.tribunalRepository.saveAll(tribunals);
        return tribunalDTOs;
    }

    public List<Tribunal> guardarSolucioEnTribunals(
            int[] presidenciesPerTFG,
            int[] vocalsPerTFG,
            int[] slotsPerTFG
    ) {
        List<Docent> docents = docentRepository.findAll();
        List<Disponibilitat> slots = disponibilitatRepository.findAll();
        List<Treball> treballs = treballRepository.findAll();
        if (presidenciesPerTFG.length != treballs.size()
                || vocalsPerTFG.length != treballs.size()
                || slotsPerTFG.length != treballs.size()) {
            throw new IllegalArgumentException("Les dimensions de la solucio no coincideixen amb els TFGs");
        }

        List<Tribunal> tribunals = new ArrayList<>();
        for (int t = 0; t < treballs.size(); t++) {
            Docent presidencia = docents.get(presidenciesPerTFG[t]);
            Docent vocal = docents.get(presidenciesPerTFG[t]);
            Treball treball = treballs.get(t);
            int slotSolverId = slotsPerTFG[t];

            if (slotSolverId < 1 || slotSolverId > disponibilitatRepository.findAll().size()) {
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
        return tribunals;
    }

    private static String getCellString(Cell c) {
        if (c == null) return null;
        switch (c.getCellType()) {
            case STRING:
                return c.getStringCellValue();
            case NUMERIC:
                double v = c.getNumericCellValue();
                if (v == (long) v) return String.valueOf((long) v);
                return String.valueOf(v);
            case BOOLEAN:
                return String.valueOf(c.getBooleanCellValue());
            case FORMULA:
                try {
                    return c.getStringCellValue();
                } catch (Exception e) {
                    double dv = c.getNumericCellValue();
                    if (dv == (long) dv) return String.valueOf((long) dv);
                    return String.valueOf(dv);
                }
            default:
                return null;
        }
    }
    private TribunalDTO tribunalToDTO (Tribunal tribunal) {
        Docent presidencia = tribunal.getPresidencia();
        Docent vocal = tribunal.getVocal();
        Treball treball = tribunal.getTreball();
        Estudiant estudiant = treball.getStudent();
        Docent tutor = treball.getTutor();
        Expertesa exp = treball.getExpertesa();
        return new TribunalDTO(tribunal.getRoom(),presidencia.getName(), vocal.getName(),
                tribunal.getAdjudicacio(), treball.getTitle(), estudiant.getName(), tutor.getName(), exp.getId());
    }
}
