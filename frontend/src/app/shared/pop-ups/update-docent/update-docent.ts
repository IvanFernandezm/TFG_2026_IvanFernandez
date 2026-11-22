import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
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
export class UpdateDocent implements OnInit {

  private dialogRef = inject(DialogRef);
  docentUpdate = signal<DocentDetails>((() => {
    const data = inject<DocentDetails>(DIALOG_DATA);
    return {
      ...data,
      experteses: [...(data.experteses ?? [])],
      disponibilitat: [...(data.disponibilitat ?? [])]
    };
  })());
  specOptions = signal<Expertesa[]>([]);
  dispOptions = signal<Disponibilitat[]>([]);

  constructor(
    private docentService: DocentService,
    private tribunalService: TribunalService
  ) { }

  ngOnInit() {

    forkJoin({
      experteses: this.tribunalService.getExperteses(),
      disponibilitats: this.tribunalService.getDisponibilitats()
    }).subscribe(({ experteses, disponibilitats }) => {

      this.specOptions.set(experteses);
      this.dispOptions.set(disponibilitats);

    });

  }

  toggleDisp(data: string) {
    const docent = this.docentUpdate();
    const index = docent.disponibilitat.indexOf(data);
    const disponibilitat = [...docent.disponibilitat];

    if (index > -1) {
      disponibilitat.splice(index, 1);
    } else {
      disponibilitat.push(data);
    }

    this.docentUpdate.set({ ...docent, disponibilitat });
  }

  isDispSelected(data: string): boolean {
    return (this.docentUpdate().disponibilitat ?? []).some(
      (disp) => disp === data
    );
  }

  toggleSpec(exp: Expertesa) {
    const docent = this.docentUpdate();
    const index = docent.experteses.indexOf(exp.id);
    const experteses = [...docent.experteses];

    if (index > -1) {
      experteses.splice(index, 1);
    } else {
      experteses.push(exp.id);
    }

    this.docentUpdate.set({ ...docent, experteses });
  }

  isSpecSelected(exp: Expertesa): boolean {
    return this.docentUpdate().experteses.some((expertesa) => expertesa === exp.id);
  }

  updateName(name: string) {
    this.docentUpdate.update(docent => ({ ...docent, name }));
  }

  close() {
    this.dialogRef.close();
  }

  submit() {
    this.docentService.updateDocent(this.docentUpdate()).subscribe((updatedDocent) => {
      console.log('Docent updated:', updatedDocent);
      this.dialogRef.close(updatedDocent);
    });
  }
}
