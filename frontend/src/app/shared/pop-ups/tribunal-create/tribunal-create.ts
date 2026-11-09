import { Component, inject } from '@angular/core';
import { Docent } from '../../../core/model/docent-model';
import { Disponibilitat } from '../../../core/model/disponibilitat-model';
import { StudentDetails } from '../../../core/model/student-details';
import { Tribunal } from '../../../core/model/tribunal-model';
import { TribunalService } from '../../../core/services/api/tribunal/tribunal-service';
import { DocentService } from '../../../core/services/api/docent/docent-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { StudentService } from '../../../core/services/api/student/student-service';
import { DialogRef } from '@angular/cdk/dialog';
import { HttpErrorResponse } from '@angular/common/http';
import { MatSelect, MatOption } from '@angular/material/select';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-tribunal-create',
  imports: [FormsModule, MatSelect, MatOption],
  templateUrl: './tribunal-create.html',
  styleUrl: './tribunal-create.scss',
})
export class TribunalCreate {
  private dialogRef = inject(DialogRef);
  private snackBar = inject(MatSnackBar);
  
  dispOptions: Disponibilitat[] = [];
  docOptions: Docent[] = [];
  tribsOptions: StudentDetails[] = [];
  selectedStudent: StudentDetails | null = null;
  newTribunal: Tribunal = {
    clase: '',
    president: '',
    vocal: '',
    data: new Date(),
    TFGTitol: '',
    estudiant: '',
    tutor: '',
    expertesa: '',
  };

    constructor(
      private docentService: DocentService,
      private tribunalService: TribunalService,
      private studentService: StudentService    
    ) { }

    ngOnInit() {
    this.tribunalService.getDisponibilitats().subscribe(disponibilitats => {
      this.dispOptions = disponibilitats;
    });

    this.docentService.getDocents().subscribe(
      (docents: Docent[]) => {
        this.docOptions = docents;
      }
    );

    this.studentService.getNoTribunalStudents().subscribe(
      (students: StudentDetails[]) => {
        this.tribsOptions = students;
      }
    );

  }

  closeDialog() {
    this.dialogRef.close();
  }

  onStudentSelected(student: StudentDetails | null): void {
    if (!student) {
      return;
    }

    this.newTribunal = {
      ...this.newTribunal,
      TFGTitol: student.tfg,
      estudiant: student.name,
      expertesa: student.expertesa,
      tutor: student.tutorNom,
    };
  }

    onSubmit() {
    if (!this.selectedStudent) {
      this.snackBar.open('Si us plau, selecciona un estudiant per crear el tribunal.', 'Tancar', {
        duration: 3000,
      });
      return;
    }

    this.tribunalService.crearTribunal(this.newTribunal).subscribe(
      (result: Tribunal) => {
        this.snackBar.open('Tribunal creat correctament', 'Tancar', {
          duration: 3000,
        });
        this.closeDialog();
      },
      (error: HttpErrorResponse) => {
        this.snackBar.open('Error al crear el tribunal: ' + error.error, 'Tancar', {
          duration: 3000,
        });
      }
    );
  }

}
