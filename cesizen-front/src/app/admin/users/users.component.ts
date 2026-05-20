import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../admin.service';
import { AdminUser } from '../models/user-admin.model';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {

  private adminService = inject(AdminService);

  displayedColumns = ['email', 'firstname', 'lastname', 'role', 'active', 'actions'];
  users: AdminUser[] = [];

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.adminService.getAllUsers().subscribe(data => {
      this.users = data;
    });
  }

  activate(id: string): void {
    this.adminService.activateUser(id).subscribe(() => this.loadUsers());
  }

  deactivate(id: string): void {
    this.adminService.deactivateUser(id).subscribe(() => this.loadUsers());
  }

  delete(id: string): void {
    this.adminService.deleteUser(id).subscribe(() => this.loadUsers());
  }
}
