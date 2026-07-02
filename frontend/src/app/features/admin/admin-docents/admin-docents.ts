import { Dialog } from '@angular/cdk/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AddDocent } from '../../../shared/pop-ups/add-docent/add-docent';
import { Docent } from '../../../core/model/docent-model';
import { debounceTime, Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { DocentService } from '../../../core/services/api/docent/docent-service';
import { DocentDetails } from '../../../core/model/docent-details';
import { FormControl, ReactiveFormsModule } from '@angular/forms';


@Component({
  selector: 'app-admin-docents',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-docents.html',
  styleUrl: './admin-docents.scss',
})
export class AdminDocents implements OnInit {

  searchControl = new FormControl('');
  filteredDocents: Docent[] = [];

  currentDocent!: DocentDetails | null;
  docents: Docent[] = [];
  exptToggle: boolean = true;

  private dialog = inject(Dialog);

  constructor(private docentService: DocentService) { }  

  ngOnInit(): void {
    this.loadDocents();
        this.searchControl.valueChanges.pipe(
          debounceTime(300)
        ).subscribe(searchTerm => {
          const term = (searchTerm ?? '').toLowerCase();
          this.filteredDocents = this.docents.filter(docent =>
            docent.name.toLowerCase().includes(term) ||
            docent.email.toLowerCase().includes(term)
          );
        });
  }

  deleteDocent() {
    throw new Error('Method not implemented.');
  }

  toggleExpt() {
    this.exptToggle = !this.exptToggle;
  }

  addDocent() {
        this.dialog.open(AddDocent, { disableClose: true });
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
    this.docentService.getDocents().subscribe(
      (docents: Docent[]) => {
        this.docents = docents;
        this.filteredDocents = docents;
      }
    );
  }
}
