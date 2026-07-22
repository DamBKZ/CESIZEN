import { Component, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { LegalDialogComponent } from './dialogs/legal-dialog.component';
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [MatIcon],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent {

  private dialog = inject(MatDialog);
  currentYear = new Date().getFullYear();

  openLegal() {
    this.dialog.open(LegalDialogComponent, { data: { type: 'legal' } });
  }

  openPrivacy() {
    this.dialog.open(LegalDialogComponent, { data: { type: 'privacy' } });
  }

  openCookies() {
    this.dialog.open(LegalDialogComponent, { data: { type: 'cookies' } });
  }
}
