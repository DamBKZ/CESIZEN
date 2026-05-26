import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../admin.service';
import { AdminDiagnostic } from '../models/diagnostic-admin.model';

@Component({
  selector: 'app-admin-diagnostics',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './diagnostics.component.html',
  styleUrls: ['./diagnostics.component.scss']
})
export class DiagnosticsComponent implements OnInit {

  private adminService = inject(AdminService);

  displayedColumns = ['user', 'score', 'risk', 'date', 'actions'];
  diagnostics: AdminDiagnostic[] = [];

  ngOnInit(): void {
    this.loadDiagnostics();
  }

  loadDiagnostics(): void {
    this.adminService.getAllDiagnostics().subscribe({
      next: data => this.diagnostics = data,
      error: err => console.error('Erreur chargement diagnostics', err)
    });
  }

  delete(id: string): void {
    this.adminService.deleteDiagnostic(id).subscribe(() => this.loadDiagnostics());
  }
}
