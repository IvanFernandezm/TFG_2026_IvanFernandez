import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login').then(m => m.Login)
  },
  {
    path: '',
    loadComponent: () =>
      import('./layout/main-layout/main-layout').then(m => m.MainLayout),
    children: [
      {
        path: 'admin',
        loadChildren: () =>
          import('./features/admin/admin-module').then(m => m.AdminModule)
      },
      {
        path: '',
        pathMatch: 'full',
        loadComponent: () =>
          import('./features/welcome/welcome').then(m => m.Welcome)
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];
