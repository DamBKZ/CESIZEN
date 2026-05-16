import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { UserStore } from '../../../core/stores/user.store';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  private readonly userStore = inject(UserStore);

  isAuthenticated = computed(() => this.userStore.isAuthenticated());
  role = computed(() => this.userStore.role());
}
