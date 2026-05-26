import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../admin.service';
import { AdminLog } from '../models/log-admin.model';

@Component({
  selector: 'app-admin-logs',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.scss']
})
export class LogsComponent implements OnInit {

  private adminService = inject(AdminService);

  displayedColumns = ['user', 'content', 'date', 'actions'];
  logs: AdminLog[] = [];

  ngOnInit(): void {
    this.loadLogs();
  }

  loadLogs(): void {
    this.adminService.getAllLogs().subscribe({
      next: data => this.logs = data,
      error: err => console.error('Erreur chargement logs', err)
    });
  }

  delete(id: string): void {
    this.adminService.deleteLog(id).subscribe(() => this.loadLogs());
  }
}
