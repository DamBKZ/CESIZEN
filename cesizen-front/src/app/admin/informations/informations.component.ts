import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../admin.service';
import { Router } from '@angular/router';
import { AdminInformation } from '../models/information-admin.model';

@Component({
  selector: 'app-admin-informations',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './informations.component.html',
  styleUrls: ['./informations.component.scss']
})
export class InformationsComponent implements OnInit {

  private adminService = inject(AdminService);
  private router = inject(Router);

  displayedColumns = ['title', 'type', 'category', 'createdAt', 'actions'];
  informations: AdminInformation[] = [];

  ngOnInit(): void {
    this.loadInformations();
  }

  loadInformations(): void {
    this.adminService.getAllInformations().subscribe(data => {
      this.informations = data;
    });
  }

  delete(id: string): void {
    this.adminService.deleteInformation(id).subscribe(() => this.loadInformations());
  }

  edit(id: string): void {
    this.router.navigate(['/informations/edit', id]);
  }
}
