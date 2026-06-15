import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Student } from '../../../model/student-model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StudentService {
  private apiUrl = '/api/estudiant'
  constructor(private http: HttpClient) { }

  getStudents():Observable<Student[]> {
    return this.http.get<Student[]>(this.apiUrl+'/all');
  }
}
