import { Component, computed, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { UserStore } from '../../../core/stores/user.store';

@Component({
  selector: 'app-header',
  standalone: true,
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  private readonly userStore = inject(UserStore);
  private readonly router = inject(Router);

  isAuthenticated = computed(() => this.userStore.isAuthenticated());
  user = computed(() => this.userStore.user());

  logout(): void {
    this.userStore.logout();
    this.router.navigate(['/login']);
  }
}
