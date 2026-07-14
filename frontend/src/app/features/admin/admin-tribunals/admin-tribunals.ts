import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Tribunal } from '../../../core/model/tribunal-model';
import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';

export interface GroupedTribunals {
  day: string;
  tribunals: Tribunal[];
}

@Component({
  selector: 'app-admin-tribunals',
  imports: [CommonModule],
  templateUrl: './admin-tribunals.html',
  styleUrl: './admin-tribunals.scss',
})
export class AdminTribunals implements OnInit {
  groupedTribunals: GroupedTribunals[] = [];
  selected: Tribunal | null = null;

  constructor(private tribunalService: TribunalService) {}

  ngOnInit(): void {
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
}
