import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Resource, Task, ResourceStats } from '../../models/resource.model';
import { ResourceService } from '../../services/resource.service';
import { ResourceFormComponent } from '../resource-form/resource-form.component';

interface FlatRow {
  resource: Resource;
  task: Task;
  isFirst: boolean;   // first task row for this person
  rowspan: number;    // how many task rows this person spans
  index: number;      // person number (only set on first row)
}

@Component({
  selector: 'app-resource-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ResourceFormComponent],
  templateUrl: './resource-list.component.html',
  styleUrls: ['./resource-list.component.scss']
})
export class ResourceListComponent implements OnInit {
  resources: Resource[] = [];
  rows: FlatRow[] = [];
  projects: string[] = [];
  stats: ResourceStats = { total: 0, busy: 0, almostFree: 0, free: 0 };

  searchTerm = '';
  filterStatus = '';
  filterProject = '';

  showForm = false;
  selectedResource: Resource | null = null;
  loading = false;
  today = new Date().toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' });

  constructor(private svc: ResourceService) {}

  ngOnInit(): void {
    this.load();
    this.loadProjects();
    this.loadStats();
  }

  load(): void {
    this.loading = true;
    this.svc.getAll(this.searchTerm, this.filterStatus, this.filterProject).subscribe({
      next: data => { this.resources = data; this.buildRows(); this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  buildRows(): void {
    this.rows = [];
    let personNum = 0;
    for (const r of this.resources) {
      personNum++;
      const tasks = r.tasks && r.tasks.length ? r.tasks : [{ task: '', storyStatus: '', endDate: null } as Task];
      tasks.forEach((t, i) => {
        this.rows.push({
          resource: r,
          task: t,
          isFirst: i === 0,
          rowspan: tasks.length,
          index: personNum
        });
      });
    }
  }

  loadProjects(): void {
    this.svc.getProjects().subscribe(p => this.projects = p);
  }

  loadStats(): void {
    this.svc.getStats().subscribe(s => this.stats = s);
  }

  onSearch(): void { this.load(); }
  onFilter(): void { this.load(); }

  openAdd(): void {
    this.selectedResource = null;
    this.showForm = true;
  }

  openEdit(r: Resource): void {
    this.selectedResource = JSON.parse(JSON.stringify(r));
    this.showForm = true;
  }

  onSaved(): void {
    this.showForm = false;
    this.load();
    this.loadStats();
    this.loadProjects();
  }

  onCancelled(): void {
    this.showForm = false;
  }

  deleteResource(r: Resource): void {
    if (!confirm(`Delete ${r.name} and all their tasks?`)) return;
    this.svc.delete(r.id!).subscribe(() => {
      this.load();
      this.loadStats();
    });
  }

  availabilityClass(status: string | undefined): string {
    if (status === 'Busy') return 'badge busy';
    if (status === 'Almost Free') return 'badge almost';
    if (status === 'Free') return 'badge free';
    return '';
  }

  exportCSV(): void {
    const header = ['#', 'Resource Name', 'Role / Designation', 'Current Story / Task', 'Sprint / Project', 'Story Status', 'Estimated End Date', 'Days Until Free', 'Availability Status'];
    const rows: (string | number)[][] = [];
    this.rows.forEach(row => {
      rows.push([
        row.isFirst ? row.index : '',
        row.isFirst ? row.resource.name : '',
        row.isFirst ? row.resource.role : '',
        row.task.task ?? '',
        row.isFirst ? row.resource.project : '',
        row.task.storyStatus ?? '',
        this.fmtDate(row.task.endDate),
        row.task.daysUntilFree ?? '',
        row.task.availabilityStatus ?? ''
      ]);
    });
    const csv = [header, ...rows].map(r =>
      r.map(c => `"${String(c).replace(/"/g, '""')}"`).join(',')
    ).join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = `resource-availability-${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
  }

  fmtDate(d: string | null | undefined): string {
    if (!d) return '';
    const date = new Date(d);
    return date.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: '2-digit' }).replace(/ /g, '-');
  }

  printReport(): void {
    window.print();
  }
}
