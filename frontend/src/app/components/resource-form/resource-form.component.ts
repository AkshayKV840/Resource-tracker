import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Resource, Task, STORY_STATUSES, emptyTask } from '../../models/resource.model';
import { ResourceService } from '../../services/resource.service';

@Component({
  selector: 'app-resource-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './resource-form.component.html',
  styleUrls: ['./resource-form.component.scss']
})
export class ResourceFormComponent implements OnInit {
  @Input() resource: Resource | null = null;
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  form: Resource = this.blank();
  storyStatuses = STORY_STATUSES;
  isEdit = false;
  saving = false;

  constructor(private svc: ResourceService) {}

  ngOnInit(): void {
    if (this.resource) {
      this.form = this.resource;
      if (!this.form.tasks || this.form.tasks.length === 0) {
        this.form.tasks = [emptyTask()];
      }
      this.isEdit = true;
    }
  }

  blank(): Resource {
    return { name: '', role: '', project: '', tasks: [emptyTask()] };
  }

  addTask(): void {
    this.form.tasks.push(emptyTask());
  }

  removeTask(i: number): void {
    this.form.tasks.splice(i, 1);
    if (this.form.tasks.length === 0) this.form.tasks.push(emptyTask());
  }

  onEndDateChange(t: Task): void {
    if (t.endDate) {
      const today = new Date(); today.setHours(0, 0, 0, 0);
      const end = new Date(t.endDate);
      const diff = Math.round((end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
      t.daysUntilFree = Math.max(0, diff);
    }
  }

  save(): void {
    if (!this.form.name?.trim()) return;
    // drop empty task rows
    this.form.tasks = this.form.tasks.filter(t => t.task && t.task.trim());
    if (this.form.tasks.length === 0) this.form.tasks = [emptyTask()];

    this.saving = true;
    const obs = this.isEdit
      ? this.svc.update(this.form.id!, this.form)
      : this.svc.create(this.form);
    obs.subscribe({
      next: () => { this.saving = false; this.saved.emit(); },
      error: () => { this.saving = false; }
    });
  }

  cancel(): void { this.cancelled.emit(); }
}
