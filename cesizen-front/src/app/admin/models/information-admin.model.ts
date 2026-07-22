export interface AdminInformation {
  informationId: string;
  title: string;
  type: 'ARTICLE' | 'VIDEO' | 'PDF';
  author: string;
  slug: string;
  categoryId: string;
  categoryName: string;
  createdAt: string;
  ownerId?: string;
  ownerPseudo?: string;
}
