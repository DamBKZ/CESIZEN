import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoaderComponent } from '../loader/loader.component';
import { SnackbarComponent } from '../snackbar/snackbar.component';
import { HeaderComponent } from "../header/header.component";
import { SidebarComponent } from "../sidebar/sidebar.component";

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    LoaderComponent,
    SnackbarComponent,
    HeaderComponent,
    SidebarComponent
],
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent {}
