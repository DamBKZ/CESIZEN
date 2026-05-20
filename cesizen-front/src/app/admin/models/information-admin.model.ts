export interface AdminInformation {
  id: string;
  title: string;
  type: 'ARTICLE' | 'VIDEO' | 'PDF';
  createdAt: string;
  category: {
    id: string;
    name: string;
  };
}
