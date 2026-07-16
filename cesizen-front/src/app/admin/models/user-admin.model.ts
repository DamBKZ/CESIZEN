export interface AdminUser {
  userId: string;
  email: string;
  pseudo: string;
  active: boolean;
  userCreatedAt?: string;
  role: {
    roleId: string;
    roleName: 'USER' | 'ADMIN';
  };
}
