import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { InformationService } from '../information.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserStore } from '../../core/stores/user.store';
import { ConfirmService } from '../../shared/services/confirm.service';

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

  canDelete = (item: any) => {
    const current = this.userStore.user();
    if (!current) { return false; }
    return current.pseudo === item.author;
  };

  ngOnInit(): void {
    this.service.getAll().subscribe({
      next: (res: any) => this.items.set(res)
    });
  }

  open(slug: string) {
    this.router.navigate(['/information/details', slug]);
  }

  edit(slug: string) {
    this.router.navigate(['/information/edit', slug]);
  }

  async delete(item: any, event: Event) {
    event.stopPropagation();
    const ok = await this.confirmService.confirm('Confirmer la suppression de cette information ?');
    if (!ok) { return; }

    this.service.delete(item.informationId ?? item.slug).subscribe({
      next: () => {
        this.items.set(this.items().filter(i => i.informationId !== item.informationId && i.slug !== item.slug));
      },
      error: () => {
        alert('Échec de la suppression');
      }
    });
  }

  addInformation(): void {
    this.router.navigate(['/information/create']);
  }

  clearSearch(): void {
    this.keyword.set('');
  }
}
