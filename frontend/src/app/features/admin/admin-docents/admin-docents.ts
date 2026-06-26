import { Dialog } from '@angular/cdk/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AddDocent } from '../../../shared/pop-ups/add-docent/add-docent';
import { Docent } from '../../../core/model/docent-model';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { DocentService } from '../../../core/services/api/docent/docent-service';
import { DocentDetails } from '../../../core/model/docent-details';


@Component({
  selector: 'app-admin-docents',
  imports: [CommonModule],
  templateUrl: './admin-docents.html',
  styleUrl: './admin-docents.scss',
})
export class AdminDocents implements OnInit {
  currentDocent!: DocentDetails | null;
  docents!: Observable<Docent[]>;
  exptToggle: boolean = true;

  private dialog = inject(Dialog);

  constructor(private docentService: DocentService) { }

  protected openModal() {
    this.dialog.open(AddDocent, { disableClose: true });
  }

  ngOnInit(): void {
    this.loadDocents();
  }

  deleteDocent() {
    throw new Error('Method not implemented.');
  }

  toggleExpt() {
    this.exptToggle = !this.exptToggle;
  }

  addDocent() {
    this.openModal();
  }

  selectDocent(Docent: Docent): void {
    this.docentService.getDocentByEmail(Docent.email).pipe().subscribe(
      (docentDetails: DocentDetails) => {
        this.currentDocent = docentDetails;
      },
      (error) => {
        console.error('Error al obtenir detalls del docent: ' + Docent.name, error);
      }
    );
  }

  loadDocents() {
    this.docents = this.docentService.getDocents();

  }
}
