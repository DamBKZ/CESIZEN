import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/components/layout/layout.component';

export const APP_ROUTES: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
    ]
  }
];
