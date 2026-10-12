import { Component, inject } from '@angular/core';
import { Disponibilitat } from '../../../core/model/disponibilitat-model';
import { DialogRef } from '@angular/cdk/dialog';
import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';
import { DocentService } from '../../../core/services/api/docent/docent-service';

@Component({
  selector: 'app-update-tribunal',
  imports: [],
  templateUrl: './update-tribunal.html',
  styleUrl: './update-tribunal.scss',
})
export class UpdateTribunal {
  private dialogRef = inject(DialogRef);
  dispOptions: Disponibilitat[] = [];

  constructor(
    private docentService: DocentService,
    private tribunalService: TribunalService
  ) { }

  closeDialog() {
    this.dialogRef.close();
  }

}
