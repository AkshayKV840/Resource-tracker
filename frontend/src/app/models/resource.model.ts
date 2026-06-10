export interface Task {
  id?: number;
  task: string;
  storyStatus: string;
  endDate: string | null;
  daysUntilFree?: number | null;
  availabilityStatus?: string;
}

export interface Resource {
  id?: number;
  name: string;
  role: string;
  project: string;
  tasks: Task[];
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

export function emptyTask(): Task {
  return { task: '', storyStatus: 'In Progress', endDate: null };
}
