import { Dialog } from '@angular/cdk/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AddDocent } from '../../../shared/pop-ups/add-docent/add-docent';
import { Docent } from '../../../core/model/docent-model';
import { debounceTime, startWith } from 'rxjs';
import { CommonModule } from '@angular/common';
import { DocentService } from '../../../core/services/api/docent/docent-service';
import { DocentDetails } from '../../../core/model/docent-details';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { UpdateDocent } from '../../../shared/pop-ups/update-docent/update-docent';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { VeteranConfirm } from '../../../shared/pop-ups/veteran-confirm/veteran-confirm';
import { Tribunal } from '../../../core/model/tribunal-model';
import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';

export interface GroupedDisponibilitat {
  day: string;
  slots: Date[];
}


@Component({
  selector: 'app-admin-docents',
  imports: [CommonModule, ReactiveFormsModule, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle],
  templateUrl: './admin-docents.html',
  styleUrl: './admin-docents.scss',
})
export class AdminDocents implements OnInit {
  searchControl = new FormControl('');
  filteredDocents = signal<Docent[]>([]);
  groupedDisponibilitat = signal<GroupedDisponibilitat[]>([]);

  currentDocent = signal<DocentDetails | null>(null);
  currentDocentTribunals = signal<Tribunal[]>([]);
  private docents = signal<Docent[]>([]);
  selectedDocentMail = signal<string | null>(null);
  dispToggle = signal(true);

  private dialog = inject(Dialog);

  constructor(
    private docentService: DocentService,
    private tribunalService: TribunalService
  ) { }

  ngOnInit(): void {
    this.loadDocents();
    this.searchControl.valueChanges.pipe(
      startWith(this.searchControl.value ?? ''),
      debounceTime(300),
    ).subscribe(searchTerm => {
      const term = (searchTerm ?? '').toLowerCase();
      this.filteredDocents.set(this.docents().filter(docent =>
        docent.name.toLowerCase().includes(term) ||
        docent.email.toLowerCase().includes(term)
      ));
    });
  }

  deleteDocent() {
    throw new Error('Method not implemented.');
  }

  toggleExpt() {
    this.dispToggle.update(value => !value);
  }

  addDocent() {
    this.dialog.open(AddDocent, { disableClose: true });
  }

  updateDocent(Docent: DocentDetails): void {
    this.dialog.open(UpdateDocent, { disableClose: true, data: Docent });
  }

  selectDocent(Docent: Docent): void {
    this.selectedDocentMail.set(Docent.email);
    this.docentService.getDocentByEmail(Docent.email).pipe().subscribe(
      (docentDetails: DocentDetails) => {
        this.currentDocent.set(docentDetails);
        this.groupedDisponibilitat.set(
          this.groupDisponibilitat(docentDetails.disponibilitat ?? [])
        );
      },
      (error) => {
        console.error('Error al obtenir detalls del docent: ' + Docent.name, error.message);
      }
    );
    this.tribunalService.getTribunalsByDocentEmail(Docent.email).subscribe(
      (tribunals: Tribunal[]) => {
        this.currentDocentTribunals.set(tribunals);
      },
      (error) => {
        console.error('Error al obtenir tribunals del docent: ' + Docent.name, error.message);
      }
    );
  }

  private groupDisponibilitat(disponibilitat: string[]): GroupedDisponibilitat[] {
    const grouped = new Map<string, Date[]>();

    disponibilitat
      .map(slot => new Date(slot))
      .sort((a, b) => a.getTime() - b.getTime())
      .forEach(slot => {
        const day = this.formatDay(slot);
        const group = grouped.get(day) ?? [];

        group.push(slot);
        grouped.set(day, group);
      });

    return Array.from(grouped.entries()).map(([day, slots]) => ({
      day,
      slots,
    }));
  }

  private formatDay(date: Date): string {
    const year = date.getFullYear();
    const month = `${date.getMonth() + 1}`.padStart(2, '0');
    const day = `${date.getDate()}`.padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  loadDocents() {
    this.docentService.getDocents().subscribe(
      (docents: Docent[]) => {
        this.docents.set(docents);

        const term = (this.searchControl.value ?? '').toLowerCase();
        this.filteredDocents.set(docents.filter(docent =>
          docent.name.toLowerCase().includes(term) ||
          docent.email.toLowerCase().includes(term)
        ));
      }
    );
  }
  setVeteranStatus(mail: string) {
    this.dialog.open(VeteranConfirm, { disableClose: true, data: mail }).closed.subscribe(() => {
      this.loadDocents();
      const docent = this.docents().find(d => d.email === mail);
      if (docent) {
        this.selectDocent(docent);
      }
    });
  }
}
