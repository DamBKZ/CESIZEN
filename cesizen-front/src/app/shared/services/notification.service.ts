import { Injectable, inject } from '@angular/core';
import { ToastService } from './toast.service';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private toast = inject(ToastService);

  uploadSuccess(message = 'Fichier importé') {
    this.toast.success(message);
  }

  uploadError(message = 'Erreur lors de l’import') {
    this.toast.error(message);
  }

  jobStarted(message = 'Traitement lancé') {
    this.toast.success(message);
  }

  jobSuccess(message = 'Traitement terminé') {
    this.toast.success(message);
  }

  jobError(message = 'Erreur lors du traitement') {
    this.toast.error(message);
  }

  autosaveSuccess(message = 'Sauvegarde automatique enregistrée') {
    this.toast.success(message, 2000);
  }

  autosaveError(message = 'Échec de la sauvegarde automatique') {
    this.toast.error(message);
  }

  permissionChanged(message = 'Permissions mises à jour') {
    this.toast.success(message);
  }

  permissionError(message = 'Erreur lors de la mise à jour des permissions') {
    this.toast.error(message);
  }
}
