import {
  Component,
  computed,
  inject,
  OnInit,
  signal
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import {
  Information,
  InformationService
} from '../information.service';

import {
  UserStore
} from '../../core/stores/user.store';

import {
  ConfirmService
} from '../../shared/services/confirm.service';

import {
  UiStore
} from '../../core/stores/ui.store';

@Component({
  selector: 'app-information-list',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly service = inject(InformationService);
  private readonly userStore = inject(UserStore);
  private readonly confirmService = inject(ConfirmService);
  private readonly ui = inject(UiStore);

  readonly items = signal<Information[]>([]);
  readonly keyword = signal('');

  readonly isAuthenticated =
    this.userStore.isAuthenticated;

  readonly filteredItems = computed(() => {
    const query = this.keyword()
      .trim()
      .toLocaleLowerCase('fr');

    if (!query) {
      return this.items();
    }

    return this.items().filter(item => {
      const searchableContent = [
        item.title,
        item.author,
        item.slug,
        item.categoryName,
        item.content,
        ...item.tags
      ]
        .filter(
          (value): value is string =>
            typeof value === 'string'
            && value.length > 0
        )
        .join(' ')
        .toLocaleLowerCase('fr');

      return searchableContent.includes(query);
    });
  });

  ngOnInit(): void {
    this.loadInformations();
  }

  canEditOrDelete(item: Information): boolean {
    const currentUser = this.userStore.user();

    if (!currentUser) {
      return false;
    }

    if (currentUser.role.roleName === 'ADMIN') {
      return true;
    }

    return item.ownerId === currentUser.userId;
  }

  open(slug: string): void {
    if (!slug) {
      return;
    }

    void this.router.navigate([
      '/informations/details',
      slug
    ]);
  }

  edit(
    slug: string,
    event: Event
  ): void {
    event.stopPropagation();

    if (!slug) {
      return;
    }

    void this.router.navigate([
      '/informations/edit',
      slug
    ]);
  }

  async delete(
    item: Information,
    event: Event
  ): Promise<void> {
    event.stopPropagation();

    if (!this.canEditOrDelete(item)) {
      return;
    }

    const confirmed =
      await this.confirmService.confirm(
        `Confirmer la suppression de « ${item.title} » ?`
      );

    if (!confirmed) {
      return;
    }

    this.service
      .delete(item.informationId)
      .subscribe({
        next: () => {
          this.items.update(items =>
            items.filter(
              currentItem =>
                currentItem.informationId
                  !== item.informationId
            )
          );

          this.ui.showSnackbar(
            'Information supprimée',
            'success'
          );
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
    void this.router.navigate([
      '/informations/create'
    ]);
  }

  clearSearch(): void {
    this.keyword.set('');
  }

  private loadInformations(): void {
    this.service.getAll().subscribe({
      next: items => {
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
}
