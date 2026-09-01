# Smart Grocery Tracking Dashboard

A full-stack grocery inventory management system with React frontend, Spring Boot backend, and Android app.

## Quick Start

### Frontend (Local Development)
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:5173
```

### Backend (Local Development)
```bash
cd backend
mvn spring-boot:run
# Runs on http://localhost:8080
```

### Android App
```bash
cd android-apk
./gradlew assembleDebug
```

## Deployment to Vercel

### Prerequisites
- GitHub account
- Vercel account (free at https://vercel.com)

### Steps

1. **Push to GitHub:**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/smart-grocery-dashboard.git
   git push -u origin main
   ```

2. **Connect to Vercel:**
   - Go to https://vercel.com/new
   - Sign in with GitHub
   - Select your repository
   - Set **Root Directory** to `frontend`
   - Click **Deploy**

3. **Configure Environment Variables (in Vercel Dashboard):**
   - Go to Settings → Environment Variables
   - Add: `VITE_API_BASE_URL` = `https://your-backend-api.com/api`

4. **Access Your App:**
   - Your deployed URL will be shown (e.g., `https://smart-grocery-dashboard.vercel.app`)
   - Works on any device: desktop, tablet, mobile browser
   - Can be installed as a PWA (Add to Home Screen)

## Project Structure

```
├── frontend/          # React + Vite webapp
│   ├── src/
│   ├── public/
│   └── package.json
├── backend/           # Java Spring Boot API
│   ├── src/
│   └── pom.xml
├── android-apk/       # Android native app
│   ├── app/
│   └── build.gradle
└── database/          # Database schema
    └── schema.sql
```

## Features

- 📊 Real-time inventory tracking
- 📱 Responsive web interface
- 📲 Native Android app
- 💾 PostgreSQL backend
- 📤 Bill/receipt upload and processing
- 🔄 Automatic synchronization

## Environment Variables

Create a `.env` file in the root:

```env
# Backend API URL (for deployed Vercel frontend)
VITE_API_BASE_URL=https://your-backend-api.com/api
```

## API Endpoints

Base URL: `http://localhost:8080/api`

- `GET /api/inventory` - Get all inventory items
- `POST /api/inventory` - Add inventory item
- `POST /api/bills/upload` - Upload receipt
- `GET /api/categories` - Get all categories

## License

Private - All rights reserved

## Support

For issues or questions, contact the development team.
