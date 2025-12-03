import React from 'react';
import { FiMenu, FiBell, FiSettings } from 'react-icons/fi';
import { useAuth } from '../../contexts/AuthContext';
import './Header.css';

const Header = ({ toggleSidebar, sidebarOpen }) => {
  const { user } = useAuth();

  return (
    <header className="header">
      <div className="header-left">
        <button className="menu-toggle" onClick={toggleSidebar}>
          <FiMenu />
        </button>
        <div className="header-title">
          <h2 className="text-gradient">Perfect Digital Society</h2>
          <p className="header-subtitle">Freedom & Security in Balance</p>
        </div>
      </div>

      <div className="header-right">
        <div className="header-scores">
          <div className="score-badge freedom">
            <span className="score-label">Freedom</span>
            <span className="score-value">{user?.freedomScore || 0}</span>
          </div>
          <div className="score-badge security">
            <span className="score-label">Security</span>
            <span className="score-value">{user?.securityScore || 0}</span>
          </div>
          <div className="score-badge reputation">
            <span className="score-label">Reputation</span>
            <span className="score-value">{user?.reputationScore || 0}</span>
          </div>
        </div>

        <button className="icon-btn">
          <FiBell />
          <span className="notification-badge">3</span>
        </button>

        <button className="icon-btn">
          <FiSettings />
        </button>

        <div className="user-avatar">
          <div className="avatar-circle">
            {user?.username?.charAt(0). toUpperCase()}
          </div>
          <div className="user-info">
            <span className="user-name">{user?.username}</span>
            <span className="user-role">{user?.role}</span>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;