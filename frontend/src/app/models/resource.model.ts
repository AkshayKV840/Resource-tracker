export interface Resource {
  id?: number;
  name: string;
  role: string;
  task: string;
  project: string;
  storyStatus: string;
  endDate: string | null;
  daysUntilFree: number | null;
  availabilityStatus?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ResourceStats {
  total: number;
  busy: number;
  almostFree: number;
  free: number;
}

export const STORY_STATUSES = ['In Progress', 'Done', 'To Do', 'Blocked', 'In Review'];
