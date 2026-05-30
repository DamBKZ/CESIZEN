import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-legal-dialog',
  standalone: true,
  templateUrl: './legal-dialog.component.html',
  styleUrls: ['./legal-dialog.component.scss'],
  imports: [MatDialogContent, MatDialogActions, MatButtonModule]
})
export class LegalDialogComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { type: string },
    private readonly dialogRef: MatDialogRef<LegalDialogComponent>
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  get title() {
    switch (this.data.type) {
      case 'legal': return 'Mentions légales';
      case 'privacy': return 'Politique de confidentialité';
      case 'cookies': return 'Gestion des cookies';
      default: return '';
    }
  }

  get content() {
    switch (this.data.type) {
      case 'legal':
        return `CESIZEN est un projet pédagogique. Les informations relatives à l’éditeur, 
        au responsable légal et à l’hébergement seront complétées en cas de mise en production publique.`;

      case 'privacy':
        return `Les données collectées dans CESIZEN sont utilisées uniquement dans le cadre pédagogique du projet. 
        Une politique de confidentialité complète conforme au RGPD sera publiée avant tout déploiement public.`;

      case 'cookies':
        return `CESIZEN n’utilise actuellement aucun cookie non essentiel. 
        Une politique de gestion des cookies sera ajoutée en cas de mise en ligne publique.`;

      default:
        return '';
    }
  }
}
