import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { Tribunal } from '../../../core/model/tribunal-model';
import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';
import { Dialog } from '@angular/cdk/dialog';
import { UpdateTribunal } from '../../../shared/pop-ups/update-tribunal/update-tribunal';
import { MatIconModule } from '@angular/material/icon';
import { TribunalDelete } from '../../../shared/pop-ups/tribunal-delete/tribunal-delete';

export interface GroupedTribunals {
  day: string;
  tribunals: Tribunal[];
}

@Component({
  selector: 'app-admin-tribunals',
  imports: [CommonModule, MatIconModule],
  templateUrl: './admin-tribunals.html',
  styleUrl: './admin-tribunals.scss',
})
export class AdminTribunals implements OnInit {

  groupedTribunals = signal<GroupedTribunals[]>([]);
  selected = signal<Tribunal | null>(null);
  theresTribunals = signal(false);
  tribunalsLoaded = signal(false);

  private dialog = inject(Dialog);


  constructor(private tribunalService: TribunalService) { }

  ngOnInit(): void {
    this.loadTribunals();
  }

  loadTribunals() {
    this.tribunalsLoaded.set(false);

    this.tribunalService.getTribunals().subscribe({
      next: tribunals => {
        const groupedTribunals = this.groupTribunalsByDay(tribunals);

        for (const group of groupedTribunals) {
          group.tribunals.sort((a, b) => a.data.localeCompare(b.data));
        }

        this.groupedTribunals.set(groupedTribunals);
        this.theresTribunals.set(groupedTribunals.length > 0);
        this.tribunalsLoaded.set(true);
      },
      error: error => {
        this.groupedTribunals.set([]);
        this.theresTribunals.set(false);
        this.tribunalsLoaded.set(true);
        console.error('Error al obtenir els tribunals', error);
      }
    });
  }

  private groupTribunalsByDay(tribunals: Tribunal[]): GroupedTribunals[] {
    const grouped = new Map<string, Tribunal[]>();

    tribunals.forEach(tribunal => {
      const day = this.formatDay(tribunal.data);
      const group = grouped.get(day) ?? [];

      group.push(tribunal);
      grouped.set(day, group);
    });

    return Array.from(grouped.entries()).map(([day, groupedTribunals]) => ({
      day,
      tribunals: groupedTribunals,
    }));
  }

  private formatDay(timestamp: string): string {
    return timestamp.substring(0, 10);
  }

  editarTribunal(tribunal: Tribunal) {
    this.dialog.open(UpdateTribunal, { disableClose: true, data: tribunal }).closed.subscribe(
      (updated) => {
        if (updated) {
          this.ngOnInit();
        }
      }
    );
  }

  eliminarTribunal(titol: string) {
    this.dialog.open(TribunalDelete, { disableClose: true, data: titol }).closed.subscribe(
      (deleted) => {
        if (deleted) {
          this.ngOnInit();
        }
      }
    );
  }
}
