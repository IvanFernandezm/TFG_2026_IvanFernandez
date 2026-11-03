package org.example.tribunalsbackend.Api;

import org.example.tribunalsbackend.Api.DTO.ResumDTO;
import org.example.tribunalsbackend.Controller.DataTreatmentController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/general")
public class TFGDashboardRestController {
    private final DataTreatmentController dataTreatmentController;

    public TFGDashboardRestController(DataTreatmentController dataTreatmentController) {
        this.dataTreatmentController = dataTreatmentController;
    }

    @GetMapping("/info")
    public ResponseEntity<ResumDTO> getGeneralInfo() throws Exception{
        ResumDTO info = this.dataTreatmentController.getGeneralInfo();
        return ResponseEntity.ok(info);
    }
}
