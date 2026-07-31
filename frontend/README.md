# PlanIT frontend

This directory contains the React frontend for the PlanIT monorepo. Project-wide setup, development and verification commands are documented in the [root README](../README.md).

Frontend-only commands can be run from this directory:

```bash
npm ci
npm run dev
npm run lint
npm test -- --runInBand
npm run build
```

During local development, Vite proxies `/api` requests to `http://localhost:8080`. Set `VITE_API_BASE_URL` when the API is hosted on a different origin.
