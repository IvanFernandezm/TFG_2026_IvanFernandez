package org.example.tribunalsbackend.Api;

import org.example.tribunalsbackend.Api.DTO.TribunalDTO;
import org.example.tribunalsbackend.Controller.DataTreatmentController;
import org.example.tribunalsbackend.Controller.TFGTribunalController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tribunal")
public class TFGTribunalRestController {
    private final DataTreatmentController dataTreatmentController;
    private final TFGTribunalController tribunalController;

    public TFGTribunalRestController(DataTreatmentController dataTreatmentController, TFGTribunalController tfgTribunalRestController) {
        this.dataTreatmentController = dataTreatmentController;
        this.tribunalController = tfgTribunalRestController;
    }

    @PostMapping("/import")
    public ResponseEntity<String> importData(@RequestParam("file") MultipartFile excel) throws Exception {
        this.dataTreatmentController.importExcel(excel);
        return ResponseEntity.ok("Importació correcta");
    }

    @GetMapping("/organitzar")
    public ResponseEntity<List<TribunalDTO>> organitzarTribunals(@RequestParam int maxClassrooms) {
        List<TribunalDTO> tribunals = this.dataTreatmentController.organitzarTribunals(maxClassrooms);
        return ResponseEntity.ok(tribunals);
    }

    @GetMapping("/all")public ResponseEntity<List<TribunalDTO>> allTribunals() {
        List<TribunalDTO> tribunals = this.tribunalController.getAllTribunals();
        return ResponseEntity.ok(tribunals);
    }

    @PutMapping("/update") public ResponseEntity<TribunalDTO> updateTribunal(@RequestBody TribunalDTO update) {
        TribunalDTO updated = this.dataTreatmentController.updateTribunal(update);
        return ResponseEntity.ok(updated);

    }
}
