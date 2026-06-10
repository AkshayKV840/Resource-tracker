import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Resource, ResourceStats } from '../models/resource.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ResourceService {
  private base = `${environment.apiUrl}/resources`;

  constructor(private http: HttpClient) {}

  getAll(search?: string, status?: string, project?: string): Observable<Resource[]> {
    let params = new HttpParams();
    if (search) params = params.set('search', search);
    if (status) params = params.set('status', status);
    if (project) params = params.set('project', project);
    return this.http.get<Resource[]>(this.base, { params });
  }

  getById(id: number): Observable<Resource> {
    return this.http.get<Resource>(`${this.base}/${id}`);
  }

  create(resource: Resource): Observable<Resource> {
    return this.http.post<Resource>(this.base, resource);
  }

  update(id: number, resource: Resource): Observable<Resource> {
    return this.http.put<Resource>(`${this.base}/${id}`, resource);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  getProjects(): Observable<string[]> {
    return this.http.get<string[]>(`${this.base}/projects`);
  }

  getStats(): Observable<ResourceStats> {
    return this.http.get<ResourceStats>(`${this.base}/stats`);
  }
}
