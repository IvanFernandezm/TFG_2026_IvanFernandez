import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
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

  groupedTribunals: GroupedTribunals[] = [];
  selected: Tribunal | null = null;

  private dialog = inject(Dialog);


  constructor(private tribunalService: TribunalService) { }

  ngOnInit(): void {
    this.loadTribunals();
  }

  loadTribunals() {
    this.tribunalService.getTribunals().subscribe(tribunals => {
      this.groupedTribunals = this.groupTribunalsByDay(tribunals);
      for (const group of this.groupedTribunals) {
        group.tribunals.sort((a, b) => a.data.getTime() - b.data.getTime());
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

  private formatDay(date: Date): string {
    const year = date.getFullYear();
    const month = `${date.getMonth() + 1}`.padStart(2, '0');
    const day = `${date.getDate()}`.padStart(2, '0');

    return `${year}-${month}-${day}`;
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
