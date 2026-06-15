import { Dialog } from '@angular/cdk/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AddDocent } from '../../../shared/pop-ups/add-docent/add-docent';
import { Docent } from '../../../core/model/docent-model';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { DocentService } from '../../../core/services/api/docent/docent-service';


@Component({
  selector: 'app-admin-docents',
  imports: [CommonModule],
  templateUrl: './admin-docents.html',
  styleUrl: './admin-docents.scss',
})
export class AdminDocents implements OnInit {
  currentDocent = signal<Docent | null>(null);
  docents!: Observable<Docent[]>;

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
  addDocent() {
    this.openModal();
  }
  selectDocent(Docent: Docent) {
    this.currentDocent.set(Docent);
  }
  loadDocents() {
    this.docents = this.docentService.getDocents();

  }
}
