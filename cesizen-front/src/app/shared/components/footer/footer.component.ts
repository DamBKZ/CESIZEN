import { Component, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';

import { environment } from '../../../../environments/environment';
import { LegalDialogComponent } from './dialogs/legal-dialog.component';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [MatIcon],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent {

  private readonly dialog = inject(MatDialog);

  readonly currentYear = new Date().getFullYear();

  readonly applicationVersion = environment.applicationVersion;

  readonly showApplicationVersion =
    environment.production &&
    environment.applicationVersion.trim().length > 0;

  openLegal(): void {
    this.dialog.open(LegalDialogComponent, {
      data: { type: 'legal' }
    });
  }

  openPrivacy(): void {
    this.dialog.open(LegalDialogComponent, {
      data: { type: 'privacy' }
    });
  }

  openCookies(): void {
    this.dialog.open(LegalDialogComponent, {
      data: { type: 'cookies' }
    });
  }
}
