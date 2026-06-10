# Stage 1: Build Angular
FROM node:20-alpine AS builder
WORKDIR /app
COPY package.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build:prod

# Stage 2: Serve with Nginx
FROM nginx:alpine
RUN apk add --no-cache gettext    # provides envsubst
RUN rm -rf /usr/share/nginx/html/*
COPY --from=builder /app/dist/resource-tracker-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/templates/default.conf.template

# BACKEND_HOST = bare hostname of the backend (no protocol)
ENV BACKEND_HOST=resource-tracker-backend.onrender.com
EXPOSE 80
CMD ["/bin/sh", "-c", "envsubst '${BACKEND_HOST}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
