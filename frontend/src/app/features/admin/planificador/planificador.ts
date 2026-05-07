import { Component, signal } from '@angular/core';

interface TimeSlot {
  dateTime: string; // ES GUARDARÀ COM A ISO STRING: "2024-09-01T08:00:00"
  selected: boolean;
}

@Component({
  selector: 'app-planificador',
  imports: [],
  templateUrl: './planificador.html',
  styleUrl: './planificador.scss',
})

export class Planificador {
sendDisponibilitats() {
throw new Error('Method not implemented.');
//Connexió amb el backend on s'envia la llista de disponibilitats seleccionades
}

  startDate = signal<Date | null>(null);
  endDate = signal<Date | null>(null);

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

    const uniqueDays = [...new Set(slots.map(s => s.dateTime.split('T')[0]))];
    this.days.set(uniqueDays);
  }

  toggleSlot(date: string, hour: string) {
    this.slots.update(slots =>
      slots.map(s =>
        s.dateTime === `${date}T${hour}:00`
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
          dateTime: `${dateStr}T${hourStr}:00`,
          selected: true
        });
      }

      // Horaris de tarda
      for (let h = 15; h < 20; h++) {
        const hourStr = `${h.toString().padStart(2, '0')}:00`;
        slots.push({
          dateTime: `${dateStr}T${hourStr}:00`,
          selected: true
        });
      }

      current.setDate(current.getDate() + 1);
    }

    return slots;
  }
  isSelected(date: string, hour: string): boolean {
    const target = `${date}T${hour}:00`;
    return this.slots().some(s => s.dateTime === target && s.selected);
  }
  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  formatHour(hour: number): string {
    return hour.toString().padStart(2, '0') + ':00';
  }
}
