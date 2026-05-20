export interface AdminUser {
  id: string;
  email: string;
  firstname: string;
  lastname: string;
  role: 'USER' | 'ADMIN';
  active: boolean;
}
