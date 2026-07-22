import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { FooterComponent } from '../footer/footer.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { SnackbarComponent } from '../snackbar/snackbar.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    SidebarComponent,
    FooterComponent,
    SnackbarComponent
  ],
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent {}
