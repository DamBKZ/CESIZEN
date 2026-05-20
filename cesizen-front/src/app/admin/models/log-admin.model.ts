export interface AdminLog {
  id: string;
  content: string;
  createdAt: string;
  user: {
    firstname: string;
    lastname: string;
  } | null;
}
