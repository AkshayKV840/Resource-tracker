import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Resource, ResourceStats } from '../../models/resource.model';
import { ResourceService } from '../../services/resource.service';
import { ResourceFormComponent } from '../resource-form/resource-form.component';

@Component({
  selector: 'app-resource-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ResourceFormComponent],
  templateUrl: './resource-list.component.html',
  styleUrls: ['./resource-list.component.scss']
})
export class ResourceListComponent implements OnInit {
  resources: Resource[] = [];
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
      next: data => { this.resources = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
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
    this.selectedResource = { ...r };
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
    if (!confirm(`Delete ${r.name}?`)) return;
    this.svc.delete(r.id!).subscribe(() => {
      this.load();
      this.loadStats();
    });
  }

  availabilityClass(status: string): string {
    if (status === 'Busy') return 'badge busy';
    if (status === 'Almost Free') return 'badge almost';
    return 'badge free';
  }

  exportCSV(): void {
    const header = ['#', 'Resource Name', 'Role', 'Current Task', 'Project', 'Story Status', 'Est. End Date', 'Days Until Free', 'Availability'];
    const rows = this.resources.map((r, i) => [
      i + 1, r.name, r.role, r.task, r.project,
      r.storyStatus, r.endDate ?? '', r.daysUntilFree ?? '', r.availabilityStatus ?? ''
    ]);
    const csv = [header, ...rows].map(row =>
      row.map(c => `"${String(c).replace(/"/g, '""')}"`).join(',')
    ).join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = `resource-availability-${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
  }

  printReport(): void {
    window.print();
  }
}
