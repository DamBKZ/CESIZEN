import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileService } from '../profile.service';
import { UserStore } from '../../core/stores/user.store';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})
export class HistoryComponent {

  private profileService = inject(ProfileService);
  private userStore = inject(UserStore);

  history: any[] = [];

ngOnInit() {
  const user = this.userStore.user();
  if (!user) return;

  this.profileService.getHistory(user.userId).subscribe((data) => {
    this.history = data;
  });
}

}
