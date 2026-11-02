import { Component, inject } from '@angular/core';
import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';

@Component({
  selector: 'app-tribunal-delete',
  imports: [],
  templateUrl: './tribunal-delete.html',
  styleUrl: './tribunal-delete.scss',
})
export class TribunalDelete {
  private dialogRef = inject<DialogRef<boolean>>(DialogRef);
  private data = inject<string>(DIALOG_DATA);

  constructor(
    private tribunalService: TribunalService,
    private snackbar: MatSnackBar
  ) { }

  confirm() {
    this.tribunalService.deleteTribunal(this.data).subscribe({
      next: () => {
        this.snackbar.open('Tribunal eliminat correctament', 'Tancar', {
          duration: 3000,
        });
        this.dialogRef.close(true);
      },
      error: (error) => {
        this.snackbar.open(
          'Error al eliminar el tribunal: ' + (error.message ?? 'Error desconegut'),
          'Tancar',
          { duration: 3000 }
        );
        this.dialogRef.close(false);
      },
    });
  }

  close() {
    this.dialogRef.close(false);
  }
}

