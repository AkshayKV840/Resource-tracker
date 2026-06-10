# Resource Availability Tracker

Angular + Spring Boot + MySQL — deployable on EKS.

---

## Project Structure

```
resource-tracker/
├── backend/               # Spring Boot (Java 17)
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/              # Angular 17
│   ├── src/
│   ├── nginx.conf
│   └── Dockerfile
├── k8s/                   # Kubernetes manifests
│   ├── 00-namespace.yaml
│   ├── 01-configmap-secret.yaml
│   ├── 02-backend.yaml
│   ├── 03-frontend.yaml
│   └── 04-ingress.yaml
├── docker-compose.yml     # Local development
└── README.md
```

---

## Local Development (Docker Compose)

```bash
docker-compose up --build
```

- Frontend: http://localhost
- Backend API: http://localhost:8080/api/resources

---

## Deploy on Render (Free — recommended)

### Prerequisites
- Free account at https://render.com
- This repo pushed to GitHub (public or private)

### Step 1 — Push to GitHub

```bash
cd resource-tracker
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/resource-tracker.git
git push -u origin main
```

### Step 2 — Create a Blueprint on Render

1. Go to https://dashboard.render.com/new/blueprint
2. Connect your GitHub account and select the repo
3. Render detects `render.yaml` automatically
4. Click **Apply** — all three services (MySQL, backend, frontend) are created and deployed

### Step 3 — Wait for deployment (~5 minutes)

Render will:
- Provision a free MySQL database
- Build and deploy the Spring Boot backend
- Build and deploy the Angular frontend via Nginx
- Wire the DB credentials and backend URL automatically via the blueprint

### Step 4 — Share with your team

Once all services show **Live** in the dashboard, copy the frontend URL:
```
https://resource-tracker-frontend.onrender.com
```
Share this with your team — anyone on the URL can use the tracker.

> **Free tier note:** Render's free web services spin down after 15 minutes of inactivity
> and take ~30 seconds to wake up on the next visit. The free MySQL database has a
> 90-day limit after which you need to recreate it (data export/import is easy via
> Render's dashboard). Upgrade to the $7/month plan to avoid both limitations.

---

## EKS Deployment

### Prerequisites
- Docker + AWS CLI + kubectl configured
- ECR repository created
- EKS cluster running with nginx-ingress controller

### Step 1 — Build and push images

```bash
# Authenticate to ECR
aws ecr get-login-password --region ap-south-1 | \
  docker login --username AWS --password-stdin <your-account-id>.dkr.ecr.ap-south-1.amazonaws.com

# Backend
cd backend
docker build -t resource-tracker-backend .
docker tag resource-tracker-backend:latest \
  <your-account-id>.dkr.ecr.ap-south-1.amazonaws.com/resource-tracker-backend:latest
docker push <your-account-id>.dkr.ecr.ap-south-1.amazonaws.com/resource-tracker-backend:latest

# Frontend
cd ../frontend
docker build -t resource-tracker-frontend .
docker tag resource-tracker-frontend:latest \
  <your-account-id>.dkr.ecr.ap-south-1.amazonaws.com/resource-tracker-frontend:latest
docker push <your-account-id>.dkr.ecr.ap-south-1.amazonaws.com/resource-tracker-frontend:latest
```

### Step 2 — Update image references

Edit `k8s/02-backend.yaml` and `k8s/03-frontend.yaml`:
```yaml
image: <your-account-id>.dkr.ecr.ap-south-1.amazonaws.com/resource-tracker-backend:latest
image: <your-account-id>.dkr.ecr.ap-south-1.amazonaws.com/resource-tracker-frontend:latest
```

### Step 3 — Update the properties file and secrets

Edit `k8s/01-configmap-secret.yaml`. It contains two sections:

**a) The properties file** (embedded in the ConfigMap, mounted into the pod at `/config/application-prod.properties`):
```
spring.datasource.url=jdbc:mysql://YOUR_MYSQL_HOST:3306/resource_tracker?...
app.cors.allowed-origins=http://tracker.your-company.internal
```
Replace `YOUR_MYSQL_HOST` and your internal domain.

**b) DB credentials** (stored as a Kubernetes Secret, injected as env vars — override the properties file values):
```bash
echo -n 'your_db_username' | base64
echo -n 'your_db_password' | base64
```
Paste the output into `YOUR_BASE64_USERNAME` / `YOUR_BASE64_PASSWORD`.

The standalone `application-prod.properties` file at  
`backend/src/main/resources/application-prod.properties`  
is provided as a local dev/reference copy of the same config.

### Step 4 — Update the Ingress host

Edit `k8s/04-ingress.yaml`:
```yaml
host: tracker.your-company.internal  # your internal domain
```

### Step 5 — Deploy

```bash
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-configmap-secret.yaml
kubectl apply -f k8s/02-backend.yaml
kubectl apply -f k8s/03-frontend.yaml
kubectl apply -f k8s/04-ingress.yaml
```

### Step 6 — Verify

```bash
kubectl get pods -n resource-tracker
kubectl get svc -n resource-tracker
kubectl get ingress -n resource-tracker
```

---

## REST API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/resources | List all (supports ?search=&status=&project=) |
| GET | /api/resources/{id} | Get by ID |
| POST | /api/resources | Create resource |
| PUT | /api/resources/{id} | Update resource |
| DELETE | /api/resources/{id} | Delete resource |
| GET | /api/resources/projects | List distinct projects |
| GET | /api/resources/stats | Get availability counts |

---

## Database

MySQL 8.0. Schema auto-initialised on first startup via `schema.sql`.
Seed data loaded from `data.sql` (skipped if data already exists).

### Availability logic (computed server-side)
- **Busy** → Days until free ≥ 6
- **Almost Free** → Days until free 1–5
- **Free** → Days until free = 0
