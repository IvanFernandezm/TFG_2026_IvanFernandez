import { Component, inject } from '@angular/core';
import { Disponibilitat } from '../../../core/model/disponibilitat-model';
import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';
import { DocentService } from '../../../core/services/api/docent/docent-service';
import { Docent } from '../../../core/model/docent-model';
import { Tribunal } from '../../../core/model/tribunal-model';
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-update-tribunal',
  imports: [FormsModule],
  templateUrl: './update-tribunal.html',
  styleUrl: './update-tribunal.scss',
})
export class UpdateTribunal {

  private dialogRef = inject(DialogRef);
  private dialogData = inject<Tribunal>(DIALOG_DATA);
  dispOptions: Disponibilitat[] = [];
  docOptions: Docent[] = [];
  tribUpdate!: Tribunal;

  constructor(
    private docentService: DocentService,
    private tribunalService: TribunalService
  ) { }

  ngOnInit() {
    this.tribunalService.getDisponibilitats().subscribe(disponibilitats => {
      this.dispOptions = disponibilitats;
    });

    this.docentService.getDocents().subscribe(
      (docents: Docent[]) => {
        this.docOptions = docents;
      }
    );

    this.tribUpdate = this.dialogData;
  }

  closeDialog() {
    this.dialogRef.close();
  }

  comprovar() {
    
  }

}
