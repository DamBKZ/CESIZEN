import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LogsService } from '../logs.service';
import { UserStore } from '../../core/stores/user.store';

@Component({
  selector: 'app-logs-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent {

  private logsService = inject(LogsService);
  private userStore = inject(UserStore);

  logs: any[] = [];

  ngOnInit() {
    const user = this.userStore.user();
    if (!user) return;

    this.logsService.getLogsForUser(user.userId).subscribe((data) => {
      this.logs = data;
    });
  }
}
