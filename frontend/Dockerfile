# syntax=docker/dockerfile:1

FROM node:22-alpine AS build

WORKDIR /workspace

COPY package.json package-lock.json ./
RUN --mount=type=cache,target=/root/.npm npm ci

COPY . .

ARG VITE_API_BASE_URL=""
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

RUN npm run build

FROM nginx:1.28-alpine AS runtime

LABEL org.opencontainers.image.title="PlanIT Frontend" \
      org.opencontainers.image.description="React web interface for PlanIT" \
      org.opencontainers.image.source="https://github.com/Sissighn/planit-v2" \
      org.opencontainers.image.licenses="MIT"

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --quiet --spider http://127.0.0.1/healthz || exit 1

CMD ["nginx", "-g", "daemon off;"]
