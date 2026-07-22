export interface InformationCategory {
  id: string;
  label: string;
}

export const INFORMATION_CATEGORIES: InformationCategory[] = [
  { id: 'cat-stress', label: 'Stress' },
  { id: 'cat-relaxation', label: 'Relaxation' },
  { id: 'cat-bien-etre', label: 'Bien-être' },
  { id: 'cat-sommeil', label: 'Sommeil' },
  { id: 'cat-travail', label: 'Travail' },
  { id: 'cat-prevention', label: 'Prévention' }
];

export function getInformationCategoryLabel(categoryId: string): string {
  return INFORMATION_CATEGORIES.find((category) => category.id === categoryId)?.label ?? 'Catégorie';
}
