import { Component, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Disponibilitat } from '../../../core/model/disponibilitat-model';
import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';
import { DocentService } from '../../../core/services/api/docent/docent-service';
import { Docent } from '../../../core/model/docent-model';
import { Tribunal } from '../../../core/model/tribunal-model';
import { FormsModule } from "@angular/forms";
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSelectModule, MatSelect, MatOption } from '@angular/material/select';

@Component({
  selector: 'app-update-tribunal',
  imports: [FormsModule, MatSelect, MatOption],
  templateUrl: './update-tribunal.html',
  styleUrl: './update-tribunal.scss',
})
export class UpdateTribunal {

  private dialogRef = inject(DialogRef);
  private dialogData = inject<Tribunal>(DIALOG_DATA);
  private snackBar = inject(MatSnackBar);

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
    this.tribunalService.comprovarTribunal(this.tribUpdate).subscribe(
      (result: string) => {
        alert(result);
      },
      (error: HttpErrorResponse) => {
        const message = typeof error.error === 'string' ? error.error : error.message;
        this.snackBar.open(message, 'Tancar', {
          duration: 3000,
        });
      }
    );
  }

  onSubmit() {
    this.tribunalService.updateTribunal(this.tribUpdate).subscribe(
      (result: Tribunal) => {
        this.snackBar.open('Tribunal actualitzat correctament', 'Tancar', {
          duration: 3000,
        });
        this.closeDialog();
      },
      (error: HttpErrorResponse) => {
        this.snackBar.open('Error al actualitzar el tribunal: ' + error.error, 'Tancar', {
          duration: 3000,
        });
      }
    );
  }

}
