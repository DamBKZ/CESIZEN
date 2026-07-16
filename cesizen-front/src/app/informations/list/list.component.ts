import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { InformationService } from '../information.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserStore } from '../../core/stores/user.store';
import { ConfirmService } from '../../shared/services/confirm.service';
import { UiStore } from '../../core/stores/ui.store';

@Component({
  selector: 'app-information-list',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly service = inject(InformationService);
  private readonly userStore = inject(UserStore);
  private readonly confirmService = inject(ConfirmService);
  private readonly ui = inject(UiStore);

  items = signal<any[]>([]);
  keyword = signal('');

  filteredItems = computed(() => {
    const query = this.keyword().trim().toLowerCase();

    if (!query) {
      return this.items();
    }

    return this.items().filter((item) => {
      const haystack = [
        item.title,
        item.author,
        item.slug,
        item.categoryName,
        item.content,
        ...(item.tags ?? [])
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();

      return haystack.includes(query);
    });
  });

  canEditOrDelete(item: any): boolean {
    const current = this.userStore.user();

    if (!current) {
      return false;
    }

    if (current.role?.roleName === 'ADMIN') {
      return true;
    }

    return item.ownerId === current.userId;
  }

  ngOnInit(): void {
    this.service.getAll().subscribe({
      next: (res) => this.items.set(res),
      error: () => this.ui.showSnackbar('Erreur lors du chargement des informations', 'error')
    });
  }

  open(slug: string): void {
    this.router.navigate(['/informations/details', slug]);
  }

  edit(slug: string, event?: Event): void {
    event?.stopPropagation();
    this.router.navigate(['/informations/edit', slug]);
  }

  async delete(item: any, event: Event): Promise<void> {
    event.stopPropagation();

    const ok = await this.confirmService.confirm('Confirmer la suppression de cette information ?');

    if (!ok) {
      return;
    }

    this.service.delete(item.informationId).subscribe({
      next: () => {
        this.items.set(this.items().filter(i => i.informationId !== item.informationId));
        this.ui.showSnackbar('Information supprimée', 'success');
      },
      error: () => {
        this.ui.showSnackbar('Échec de la suppression', 'error');
      }
    });
  }

  addInformation(): void {
    this.router.navigate(['/informations/create']);
  }

  clearSearch(): void {
    this.keyword.set('');
  }
}
