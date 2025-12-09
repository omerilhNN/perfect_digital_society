# Perfect Digital Society - Frontend

A modern, cyber-themed React application built with Vite for managing digital society balance between freedom and security.

## 🚀 Features

- **Modern UI/UX**: Cyber-themed design with smooth animations
- **User Authentication**: Secure login and registration system
- **Balance Management**: Track and manage freedom vs security balance
- **Community Rules**: Create and vote on community guidelines
- **Admin Dashboard**: Comprehensive administrative tools
- **Real-time Updates**: Live data updates and notifications
- **Responsive Design**: Works on all device sizes

## 🛠 Technology Stack

- **Frontend Framework**: React 18
- **Build Tool**: Vite
- **Styling**: CSS3 with custom cyber theme
- **Animations**: Framer Motion
- **Charts**: Recharts
- **Icons**: React Icons
- **HTTP Client**: Axios
- **Notifications**: React Toastify
- **Routing**: React Router DOM

## 📦 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/omerilhNN/perfect_digital_society.git
   cd perfect-digital-society-frontend
   ```

2. **Install dependencies**
   ```bash
   npm install --legacy-peer-deps
   ```

3. **Set up environment variables**
   ```bash
   cp .env.example .env
   ```
   
   Update `.env` with your configuration:
   ```
   VITE_API_URL=http://localhost:8080/api
   ```

4. **Start development server**
   ```bash
   npm run dev
   ```

5. **Open your browser**
   ```
   Navigate to http://localhost:3000
   ```

## 🏗 Build for Production

```bash
npm run build
```

The built files will be in the `dist/` directory.

## 🔧 Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build locally

## 📁 Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── Layout/         # Layout components (Header, Sidebar, etc.)
│   └── ProtectedRoute.jsx
├── contexts/           # React contexts
│   └── AuthContext.jsx
├── pages/             # Page components
│   ├── Admin/         # Admin dashboard
│   ├── Auth/          # Login/Register
│   ├── Balance/       # Balance management
│   ├── Community/     # Community rules
│   ├── Dashboard/     # Main dashboard
│   ├── Messages/      # Message center
│   └── Profile/       # User profile
├── services/          # API services
│   └── api.js
├── styles/           # Global styles
│   └── index.css
├── App.jsx           # Main app component
└── main.jsx         # Entry point
```

## 🎨 Design System

The application uses a custom cyber-themed design system with:

- **Primary Color**: Cyan (#00ffff)
- **Secondary Color**: Purple (#8b5cf6)  
- **Background**: Dark theme with gradients
- **Typography**: Modern, clean fonts
- **Animations**: Smooth transitions and effects

## 🔐 Security Features

- JWT token authentication
- Protected routes
- Role-based access control
- Secure API communication
- Input validation

## 🌐 Browser Support

- Chrome (recommended)
- Firefox
- Safari
- Edge

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Related

- [Backend Repository](https://github.com/yourusername/perfect-digital-society-backend)
- [Documentation](https://github.com/yourusername/perfect-digital-society-docs)

## ⚡ Quick Start

```bash
# Clone and install
git clone https://github.com/yourusername/perfect-digital-society-frontend.git
cd perfect-digital-society-frontend
npm install --legacy-peer-deps

# Start development
npm run dev
```

## 📞 Support

If you have any questions or issues, please:

1. Check the [Issues](https://github.com/yourusername/perfect-digital-society-frontend/issues) page
2. Create a new issue if your problem isn't addressed
3. Join our [Discord community](https://discord.gg/yourinvite)

---

Made with ❤️ for the Perfect Digital Society