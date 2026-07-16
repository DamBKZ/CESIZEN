import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { AdminService } from '../admin.service';
import { AdminLog } from '../models/log-admin.model';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-admin-logs',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule
  ],
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.scss']
})
export class LogsComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly ui = inject(UiStore);

  displayedColumns = ['user', 'content', 'date'];
  logs: AdminLog[] = [];

  ngOnInit(): void {
    this.loadLogs();
  }

  loadLogs(): void {
    this.adminService.getAllLogs().subscribe({
      next: data => this.logs = data,
      error: () => this.ui.showSnackbar('Erreur chargement logs', 'error')
    });
  }
}
