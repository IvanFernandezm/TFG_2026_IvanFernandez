import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { DocentService } from '../../../core/services/api/docent/docent-service';
import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

interface TimeSlot {
  timestamp: string; // ES GUARDARÀ COM A ISO STRING: "2024-09-01T08:00:00"
  selected: boolean;
}

@Component({
  selector: 'app-planificador',
  imports: [CommonModule],
  templateUrl: './planificador.html',
  styleUrl: './planificador.scss',
})

export class Planificador {

  private readonly router = inject(Router);
  private snackBar = inject(MatSnackBar);


  constructor(private docentService: DocentService, private tribunalService: TribunalService) { }

  generateTribunals() {
    this.tribunalService.organitzarTribunals(this.maxClassrooms()).subscribe({
      next: () => {
        this.snackBar.open('Nous Tribunals organitzats', 'Tancar', { duration: 3000 });
        this.router.navigateByUrl('/admin/admin-tribunals');
      },
      error: error => {
        console.error('Error generant tribunals', error.message);
      }
    });

  }

  sendDisponibilitats() {
    const selectedSlots: string[] = this.slots().filter(slot => slot.selected).map(slot => slot.timestamp);
    this.docentService.newDisponibilitat(selectedSlots);
  }

  startDate = signal<Date | null>(null);
  endDate = signal<Date | null>(null);
  maxClassrooms = signal<number>(1);

  slots = signal<TimeSlot[]>([]);
  days = signal<string[]>([]);

  hours = signal<string[]>([
    '08:00', '09:00', '10:00', '11:00', '12:00', '13:00',
    '15:00', '16:00', '17:00', '18:00', '19:00'
  ]);

  generate() {
    if (!this.startDate() || !this.endDate()) return;

    const slots = this.generateSchedule(this.startDate()!, this.endDate()!);
    this.slots.set(slots);

    const uniqueDays = [...new Set(slots.map(s => s.timestamp.split('T')[0]))];
    this.days.set(uniqueDays);
  }

  toggleSlot(date: string, hour: string) {
    this.slots.update(slots =>
      slots.map(s =>
        s.timestamp === `${date}T${hour}:00`
          ? { ...s, selected: !s.selected }
          : s
      )
    );
  }

  generateSchedule(start: Date, end: Date): TimeSlot[] {
    const slots: TimeSlot[] = [];

    const current = new Date(start);

    while (current <= end) {

      const dateStr = current.toISOString().split('T')[0];

      // Horaris de matins
      for (let h = 8; h < 14; h++) {
        const hourStr = `${h.toString().padStart(2, '0')}:00`;
        slots.push({
          timestamp: `${dateStr}T${hourStr}:00`,
          selected: true
        });
      }

      // Horaris de tarda
      for (let h = 15; h < 20; h++) {
        const hourStr = `${h.toString().padStart(2, '0')}:00`;
        slots.push({
          timestamp: `${dateStr}T${hourStr}:00`,
          selected: true
        });
      }

      current.setDate(current.getDate() + 1);
    }

    return slots;
  }
  isSelected(date: string, hour: string): boolean {
    const target = `${date}T${hour}:00`;
    return this.slots().some(s => s.timestamp === target && s.selected);
  }
  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  formatHour(hour: number): string {
    return hour.toString().padStart(2, '0') + ':00';
  }
}
