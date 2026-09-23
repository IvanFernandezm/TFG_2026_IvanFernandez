import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { Component, inject } from '@angular/core';
import { DocentDetails } from '../../../core/model/docent-details';
import { Expertesa } from '../../../core/model/expertesa-model';
import { MatCheckboxModule } from "@angular/material/checkbox";
import { DocentService } from '../../../core/services/api/docent/docent-service';
import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';
import { DatePipe } from '@angular/common';
import { Disponibilitat } from '../../../core/model/disponibilitat-model';
import { forkJoin } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-update-docent',
  imports: [MatCheckboxModule, DatePipe, FormsModule],
  templateUrl: './update-docent.html',
  styleUrl: './update-docent.scss',
})
export class UpdateDocent {

  private dialogRef = inject(DialogRef);
  docentUpdate =(() => {
    const data = inject<DocentDetails>(DIALOG_DATA);
      return {
    ...data,
    experteses: [...(data.experteses ?? [])],
    disponibilitat: (data.disponibilitat ?? []).map(date => new Date(date))
  };
  })();
  specOptions: Expertesa[] = [];
  dispOptions: Disponibilitat[] = [];

  constructor(
    private docentService: DocentService,
    private tribunalService: TribunalService
  ) { }

  ngOnInit() {

    forkJoin({
      experteses: this.tribunalService.getExperteses(),
      disponibilitats: this.tribunalService.getDisponibilitats()
    }).subscribe(({ experteses, disponibilitats }) => {

      this.specOptions = experteses;
      this.dispOptions = disponibilitats;

    });

  }

  toggleDisp(data: Disponibilitat) {
    const index = this.docentUpdate.disponibilitat.indexOf(data.timestamp);
    if (index > -1) {
      this.docentUpdate.disponibilitat.splice(index, 1);
    } else {
      this.docentUpdate.disponibilitat.push(data.timestamp);
    }
  }

  isDispSelected(data: Disponibilitat): boolean {
  const timestamp = new Date(data.timestamp).getTime();

  return (this.docentUpdate.disponibilitat ?? []).some(
    disp => new Date(disp).getTime() === timestamp
  );
  }

  toggleSpec(exp: Expertesa) {
    const index = this.docentUpdate.experteses.indexOf(exp.id);
    if (index > -1) {
      this.docentUpdate.experteses.splice(index, 1);
    } else {
      this.docentUpdate.experteses.push(exp.id);
    }
  }

  isSpecSelected(exp: Expertesa): boolean {
    return this.docentUpdate.experteses.some((expertesa) => expertesa === exp.id);
  }

  close() {
    this.dialogRef.close();
  }

  submit() {
    this.docentService.updateDocent(this.docentUpdate).subscribe((updatedDocent) => {
      console.log('Docent updated:', updatedDocent);
    });
    this.dialogRef.close();
  }
}
