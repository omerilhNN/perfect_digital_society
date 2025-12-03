import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  FiHome, 
  FiMessageSquare, 
  FiUsers, 
  FiActivity, 
  FiUser, 
  FiShield,
  FiLogOut
} from 'react-icons/fi';
import { useAuth } from '../../contexts/AuthContext';
import './Sidebar.css';

const Sidebar = ({ isOpen }) => {
  const { user, logout, isAdmin } = useAuth();

  const menuItems = [
    { path: '/dashboard', icon: FiHome, label: 'Dashboard', roles: ['USER', 'MODERATOR', 'ADMIN'] },
    { path: '/messages', icon: FiMessageSquare, label: 'Messages', roles: ['USER', 'MODERATOR', 'ADMIN'] },
    { path: '/community', icon: FiUsers, label: 'Community', roles: ['USER', 'MODERATOR', 'ADMIN'] },
    { path: '/balance', icon: FiActivity, label: 'Balance', roles: ['USER', 'MODERATOR', 'ADMIN'] },
    { path: '/profile', icon: FiUser, label: 'Profile', roles: ['USER', 'MODERATOR', 'ADMIN'] },
    { path: '/admin', icon: FiShield, label: 'Admin Panel', roles: ['ADMIN'] },
  ];

  const filteredMenuItems = menuItems.filter(item => item.roles.includes(user?.role));

  return (
    <aside className={`sidebar ${isOpen ?  'open' : 'closed'}`}>
      <div className="sidebar-header">
        <div className="logo">
          <div className="logo-icon">PDS</div>
          {isOpen && <span className="logo-text">Perfect Digital Society</span>}
        </div>
      </div>

      <nav className="sidebar-nav">
        {filteredMenuItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
          >
            <item.icon className="nav-icon" />
            {isOpen && <span className="nav-label">{item.label}</span>}
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <button className="logout-btn" onClick={logout}>
          <FiLogOut className="nav-icon" />
          {isOpen && <span>Logout</span>}
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;