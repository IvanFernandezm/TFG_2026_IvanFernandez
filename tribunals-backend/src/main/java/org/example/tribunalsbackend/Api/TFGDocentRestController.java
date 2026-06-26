package org.example.tribunalsbackend.Api;

import org.example.tribunalsbackend.Api.DTO.DisponibilitatDTO;
import org.example.tribunalsbackend.Api.DTO.DocentDTO;
import org.example.tribunalsbackend.Api.DTO.DocentDetailsDTO;
import org.example.tribunalsbackend.Controller.TFGDocentController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docent")
public class TFGDocentRestController {
    private final TFGDocentController docentController;

    public TFGDocentRestController(TFGDocentController docentController) {
        this.docentController = docentController;
    }

    @GetMapping("/all")
    public ResponseEntity<List<DocentDTO>> getDocents() {
        List<DocentDTO> docents = docentController.getDocents();
        return ResponseEntity.ok(docents);

    }

    @GetMapping("/details")
    public ResponseEntity<DocentDetailsDTO> getDocentDetails(@RequestParam String mail) throws Exception {
        DocentDetailsDTO details = docentController.getDocentDetails(mail);
        return ResponseEntity.ok(details);
    }

    @PutMapping("/disponibilitat")
    public void updateDisponibilitat(@RequestBody List<DisponibilitatDTO> dto) throws Exception{
        docentController.updateDisponibilitat(dto);
    }
}
