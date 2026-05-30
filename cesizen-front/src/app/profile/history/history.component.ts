import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProfileService } from '../../core/services/profile.service';
import { UserStore } from '../../core/stores/user.store';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})
export class HistoryComponent implements OnInit {

  private profileService = inject(ProfileService);
  private userStore = inject(UserStore);
  private router = inject(Router);

  history: any[] = [];

  backToProfile(): void {
    this.router.navigate(['/profile']);
  }

  ngOnInit(): void {
    const cachedUser = this.userStore.user();

    if (cachedUser) {
      this.profileService.getHistory(cachedUser.userId).subscribe((data) => {
        this.history = data;
      });
      return;
    }

    this.profileService.getCurrentUser().subscribe({
      next: (user: any) => {
        this.userStore.setUser(user);
        this.profileService.getHistory(user.userId).subscribe((data) => {
          this.history = data;
        });
      }
    });
  }

}
