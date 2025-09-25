# Fitness Backend – Profile & Plan (Sprint 1)

## What this is
- Profile API: create / read / update
- Auto weekly plan generation on create/update
- Auth shim (JWT or .env DEV_FIXED_USER_ID)

## Quick start
```bash
cp .env.example .env
# edit MONGO_URI if needed; keep DEV_FIXED_USER_ID for demo

npm i
npm run dev
# API: http://localhost:$PORT  (default 5003)
