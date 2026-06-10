import { Component } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ResourceListComponent } from './components/resource-list/resource-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HttpClientModule, ResourceListComponent],
  template: `<app-resource-list></app-resource-list>`
})
export class AppComponent {}
