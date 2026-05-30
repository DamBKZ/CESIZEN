import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AdminService } from '../admin.service';
import { AdminStats } from '../models/stats-admin.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  private adminService = inject(AdminService);
  private router = inject(Router);

  stats = [
    { key: 'users', icon: 'group', label: 'Utilisateurs', value: 0 },
    { key: 'informations', icon: 'article', label: 'Informations', value: 0 },
    { key: 'diagnostics', icon: 'psychology', label: 'Diagnostics', value: 0 },
    { key: 'logs', icon: 'list_alt', label: 'Logs', value: 0 }
  ];

  ngOnInit(): void {
    this.adminService.getStats().subscribe((data: AdminStats) => {
      this.stats = this.stats.map(s => ({
        ...s,
        value: data[s.key as keyof AdminStats]
      }));
    });
  }

  goToUsers(): void {
    this.router.navigate(['/admin/users']);
  }

  goToInformations(): void {
    this.router.navigate(['/admin/informations']);
  }
}
