import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProfileService } from '../../core/services/profile.service';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})
export class HistoryComponent implements OnInit {
  private readonly profileService = inject(ProfileService);
  private readonly router = inject(Router);

  history: any[] = [];

  backToProfile(): void {
    this.router.navigate(['/profile']);
  }

  ngOnInit(): void {
    this.profileService.getHistory().subscribe({
      next: (data) => {
        this.history = data;
      }
    });
  }
}
