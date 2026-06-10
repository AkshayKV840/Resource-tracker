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

# envsubst replaces ${BACKEND_URL} in the template at container start
ENV BACKEND_URL=http://localhost:8080
EXPOSE 80
CMD ["/bin/sh", "-c", "envsubst '${BACKEND_URL}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
