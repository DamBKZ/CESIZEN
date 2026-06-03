import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { LegalDialogComponent } from '../footer/dialogs/legal-dialog.component';

@Component({
  selector: 'app-cookie-consent',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './cookie-consent.component.html',
  styleUrls: ['./cookie-consent.component.scss']
})
export class CookieConsentComponent implements OnInit {
  private readonly storageKey = 'cesizen-cookie-consent';
  private readonly dialog = inject(MatDialog);

  visible = false;

  ngOnInit(): void {
    this.visible = !localStorage.getItem(this.storageKey);
  }

  accept(): void {
    localStorage.setItem(this.storageKey, 'accepted');
    this.visible = false;
  }

  reject(): void {
    localStorage.setItem(this.storageKey, 'rejected');
    this.visible = false;
  }

  openCookiesInfo(): void {
    this.dialog.open(LegalDialogComponent, { data: { type: 'cookies' } });
  }
}