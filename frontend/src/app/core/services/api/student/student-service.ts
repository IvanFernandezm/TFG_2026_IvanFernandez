import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Student } from '../../../model/student-model';
import { Observable } from 'rxjs';
import { StudentDetails } from '../../../model/student-details';

@Injectable({
  providedIn: 'root',
})
export class StudentService {
  private apiUrl = '/api/estudiant'
  constructor(private http: HttpClient) { }

  getStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(this.apiUrl + '/all');
  }

  getStudentByMail(mail: string): Observable<StudentDetails> {
    return this.http.get<StudentDetails>(
      `${this.apiUrl}/tfg?mail=${mail}`
    );
  }
}
