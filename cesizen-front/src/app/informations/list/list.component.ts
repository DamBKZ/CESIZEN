import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { InformationService } from '../information.service';
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

  readonly items = signal<any[]>([]);
  readonly keyword = signal('');

  readonly filteredItems = computed(() => {
    const query = this.keyword().trim().toLowerCase();

    if (!query) {
      return this.items();
    }

    return this.items().filter((item) => {
      const haystack = [
        item?.title,
        item?.author,
        item?.slug,
        item?.categoryName,
        item?.content,
        ...this.getTags(item)
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();

      return haystack.includes(query);
    });
  });

  ngOnInit(): void {
    this.service.getAll().subscribe({
      next: (response: any) => {
        const items = Array.isArray(response)
          ? response
          : Array.isArray(response?.content)
            ? response.content
            : Array.isArray(response?.items)
              ? response.items
              : [];

        this.items.set(items);
      },
      error: () => {
        this.items.set([]);

        this.ui.showSnackbar(
          'Erreur lors du chargement des informations',
          'error'
        );
      }
    });
  }

  getTags(item: any): string[] {
    const tags = item?.tags;

    if (Array.isArray(tags)) {
      return tags
        .map((tag) => {
          if (typeof tag === 'string') {
            return tag;
          }

          return tag?.name ?? tag?.tagName ?? tag?.label ?? '';
        })
        .filter(Boolean);
    }

    if (typeof tags === 'string') {
      return tags
        .split(',')
        .map((tag) => tag.trim())
        .filter(Boolean);
    }

    return [];
  }

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

  open(slug: string): void {
    this.router.navigate(['/informations/details', slug]);
  }

  edit(slug: string, event?: Event): void {
    event?.stopPropagation();
    this.router.navigate(['/informations/edit', slug]);
  }

  async delete(item: any, event: Event): Promise<void> {
    event.stopPropagation();

    const confirmed = await this.confirmService.confirm(
      'Confirmer la suppression de cette information ?'
    );

    if (!confirmed) {
      return;
    }

    this.service.delete(item.informationId).subscribe({
      next: () => {
        this.items.update((items) =>
          items.filter(
            (currentItem) =>
              currentItem.informationId !== item.informationId
          )
        );

        this.ui.showSnackbar('Information supprimée', 'success');
      },
      error: () => {
        this.ui.showSnackbar(
          'Échec de la suppression',
          'error'
        );
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
