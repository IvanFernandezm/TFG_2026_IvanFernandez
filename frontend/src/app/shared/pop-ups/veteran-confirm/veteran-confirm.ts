import { Component, inject } from '@angular/core';
import { DocentService } from '../../../core/services/api/docent/docent-service';
import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-veteran-confirm',
  imports: [],
  templateUrl: './veteran-confirm.html',
  styleUrl: './veteran-confirm.scss',
})
export class VeteranConfirm {
  private dialogRef = inject(DialogRef);
  private data = inject<string>(DIALOG_DATA);

  constructor(
    private docentService: DocentService,
    private snackbar: MatSnackBar
  ) { }

  confirm() {
    this.docentService.setVeteranStatus(this.data).subscribe(() => {
      this.snackbar.open('Estat veterà del docent actualitzat correctament', 'Tancar', {
        duration: 3000,
      });
    });
    this.dialogRef.close();
  }
  close() {
    this.dialogRef.close();
  }
}
