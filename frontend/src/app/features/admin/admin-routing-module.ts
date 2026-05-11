import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'planificador',
    loadComponent: () =>
      import('./planificador/planificador').then(m => m.Planificador)
  },
  {
    path: 'admin-students',
    loadComponent: () =>
      import('./admin-students/admin-students').then(m => m.AdminStudents)
  },
  {
    path: 'admin-docents',
    loadComponent: () =>
      import('./admin-docents/admin-docents').then(m => m.AdminDocents)
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
