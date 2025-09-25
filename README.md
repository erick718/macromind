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

# 1) Create profile
curl -s -X POST http://localhost:5003/api/profile \
 -H "Content-Type: application/json" \
 -d '{"goal":"Lose Weight","preferences":["Vegetarian"],"availabilityPerWeek":3,"dietaryRestrictions":["Gluten-free"]}'

# 2) Read my profile
curl -s http://localhost:5003/api/profile/me | jq

# 3) Update profile (change goal)
curl -s -X PUT http://localhost:5003/api/profile \
 -H "Content-Type: application/json" \
 -d '{"goal":"Build Muscle"}'

# 4) Get weekly plan
curl -s http://localhost:5003/api/plan/week | jq
