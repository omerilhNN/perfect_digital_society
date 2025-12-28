import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  FiUsers, 
  FiShield, 
  FiActivity,
  FiAlertTriangle,
  FiSettings,
  FiTool,
  FiDownload,
  FiSearch
} from 'react-icons/fi';
import { useAuth } from '../../contexts/AuthContext';
import { adminService } from '../../services/api';
import { toast } from 'react-toastify';
import './AdminPage.css';

const AdminPage = () => {
  const { user, isAdmin } = useAuth();
  const [loading, setLoading] = useState(true);
  const [adminData, setAdminData] = useState({
    users: [],
    metrics: null,
    suspiciousUsers: [],
    auditLogs: [],
  });
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState('dashboard'); // dashboard, users, suspicious, logs

  useEffect(() => {
    if (isAdmin()) {
      loadAdminData();
    }
  }, []);

  const loadAdminData = async () => {
    try {
      setLoading(true);
      const [users, metrics, suspicious, logs] = await Promise.all([
        adminService.getAllUsers(),
        adminService.getDashboard(),
        adminService.getSuspiciousUsers(),
        adminService.getAuditLogs(50),
      ]);

      setAdminData({
        users: Array.isArray(users) ? users : [],
        metrics,
        suspiciousUsers: Array.isArray(suspicious) ? suspicious : [],
        auditLogs: Array.isArray(logs) ? logs : [],
      });
    } catch (error) {
      console.error('Failed to load admin data:', error);
      toast.error('Failed to load admin data');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateUserStatus = async (userId, status, reason) => {
    try {
      await adminService.updateUserStatus(userId, { status, reason });
      toast.success('User status updated successfully! ');
      loadAdminData();
    } catch (error) {
      console.error('Failed to update user status:', error);
      toast.error('Failed to update user status');
    }
  };

  const handlePerformMaintenance = async () => {
    if (!window.confirm('Are you sure you want to perform system maintenance?')) {
      return;
    }

    try {
      await adminService.performMaintenance();
      toast.success('System maintenance completed successfully!');
      loadAdminData();
    } catch (error) {
      console.error('Failed to perform maintenance:', error);
      toast.error('Failed to perform maintenance');
    }
  };

  const handleGenerateReport = async () => {
    try {
      const report = await adminService.generateReport();
      const blob = new Blob([report], { type: 'text/plain' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `system-report-${new Date().toISOString()}.txt`;
      a.click();
      toast.success('Report downloaded successfully!');
    } catch (error) {
      console.error('Failed to generate report:', error);
      toast.error('Failed to generate report');
    }
  };

  const filteredUsers = adminData.users.filter(u => 
    u.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.email?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (! isAdmin()) {
    return (
      <div className="access-denied">
        <FiShield size={64} />
        <h2>Access Denied</h2>
        <p>You do not have permission to access this page. </p>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading Admin Panel...</p>
      </div>
    );
  }

  const { metrics } = adminData;

  return (
    <div className="admin-page">
      <motion.div 
        className="page-header"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <div>
          <h1 className="text-gradient">Admin Panel</h1>
          <p className="page-subtitle">System oversight and management</p>
        </div>
        <div className="header-actions">
          <button 
            className="btn btn-secondary"
            onClick={handleGenerateReport}
          >
            <FiDownload /> Generate Report
          </button>
          <button 
            className="btn btn-primary"
            onClick={handlePerformMaintenance}
          >
            <FiTool /> Maintenance
          </button>
        </div>
      </motion.div>

      {/* Admin Tabs */}
      <div className="admin-tabs">
        <button 
          className={`tab-btn ${activeTab === 'dashboard' ? 'active' : ''}`}
          onClick={() => setActiveTab('dashboard')}
        >
          <FiActivity /> Dashboard
        </button>
        <button 
          className={`tab-btn ${activeTab === 'users' ? 'active' : ''}`}
          onClick={() => setActiveTab('users')}
        >
          <FiUsers /> Users
        </button>
        <button 
          className={`tab-btn ${activeTab === 'suspicious' ? 'active' : ''}`}
          onClick={() => setActiveTab('suspicious')}
        >
          <FiAlertTriangle /> Suspicious
        </button>
        <button 
          className={`tab-btn ${activeTab === 'logs' ? 'active' : ''}`}
          onClick={() => setActiveTab('logs')}
        >
          <FiSettings /> Audit Logs
        </button>
      </div>

      {/* Dashboard Tab */}
      {activeTab === 'dashboard' && metrics && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
        >
          <div className="admin-metrics-grid">
            <div className="metric-card">
              <FiUsers className="metric-icon" />
              <div className="metric-content">
                <span className="metric-label">Total Users</span>
                <h3 className="metric-value">{metrics.totalUsers}</h3>
                <span className="metric-subtitle">{metrics.activeUsers} active</span>
              </div>
            </div>
            <div className="metric-card">
              <FiActivity className="metric-icon" />
              <div className="metric-content">
                <span className="metric-label">Total Messages</span>
                <h3 className="metric-value">{metrics.totalMessages}</h3>
              </div>
            </div>
            <div className="metric-card">
              <FiShield className="metric-icon" />
              <div className="metric-content">
                <span className="metric-label">System Health</span>
                <h3 className="metric-value">{metrics.systemHealth?.toFixed(1)}%</h3>
              </div>
            </div>
            <div className="metric-card">
              <FiActivity className="metric-icon" />
              <div className="metric-content">
                <span className="metric-label">Balance Events</span>
                <h3 className="metric-value">{metrics.totalBalanceEvents}</h3>
              </div>
            </div>
          </div>

          <div className="system-balance-overview">
            <div className="card">
              <div className="card-header">
                <h3 className="card-title">System Balance Levels</h3>
              </div>
              <div className="balance-levels">
                <div className="level-item">
                  <span className="level-label">Freedom Level</span>
                  <div className="level-bar">
                    <div 
                      className="level-fill freedom"
                      style={{ width: `${metrics.systemFreedomLevel || 0}%` }}
                    >
                      {metrics.systemFreedomLevel}
                    </div>
                  </div>
                </div>
                <div className="level-item">
                  <span className="level-label">Security Level</span>
                  <div className="level-bar">
                    <div 
                      className="level-fill security"
                      style={{ width: `${metrics.systemSecurityLevel || 0}%` }}
                    >
                      {metrics.systemSecurityLevel}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </motion.div>
      )}

      {/* Users Tab */}
      {activeTab === 'users' && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
        >
          <div className="search-bar">
            <FiSearch />
            <input
              type="text"
              placeholder="Search users..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
          </div>

          <div className="users-table-container">
            <table className="users-table">
              <thead>
                <tr>
                  <th>User</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Scores</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map(user => (
                  <tr key={user.id}>
                    <td>
                      <div className="user-cell">
                        <div className="user-avatar-small">
                          {user.username?.charAt(0).toUpperCase()}
                        </div>
                        <div>
                          <div className="user-name">{user.username}</div>
                          <div className="user-fullname">{user.firstName} {user.lastName}</div>
                        </div>
                      </div>
                    </td>
                    <td>{user.email}</td>
                    <td>
                      <span className={`badge badge-${user.role?.toLowerCase()}`}>
                        {user. role}
                      </span>
                    </td>
                    <td>
                      <div className="scores-cell">
                        <span className="score freedom">F:{user.freedomScore}</span>
                        <span className="score security">S:{user.securityScore}</span>
                        <span className="score reputation">R:{user.reputationScore}</span>
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${user.isActive ? 'badge-success' : 'badge-danger'}`}>
                        {user.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td>
                      <div className="action-buttons">
                        {user.isActive ?  (
                          <button 
                            className="btn-icon danger"
                            onClick={() => handleUpdateUserStatus(user.id, 'INACTIVE', 'Admin action')}
                            title="Deactivate"
                          >
                            Suspend
                          </button>
                        ) : (
                          <button 
                            className="btn-icon success"
                            onClick={() => handleUpdateUserStatus(user.id, 'ACTIVE', 'Admin action')}
                            title="Activate"
                          >
                            Activate
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </motion.div>
      )}

      {/* Suspicious Users Tab */}
      {activeTab === 'suspicious' && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
        >
          <div className="card">
            <div className="card-header">
              <h3 className="card-title">
                <FiAlertTriangle /> Suspicious Users ({adminData.suspiciousUsers.length})
              </h3>
            </div>
            {adminData.suspiciousUsers.length > 0 ? (
              <div className="suspicious-list">
                {adminData.suspiciousUsers.map(user => (
                  <div key={user.id} className="suspicious-item">
                    <div className="user-info">
                      <div className="user-avatar-small">
                        {user.username?.charAt(0).toUpperCase()}
                      </div>
                      <div>
                        <div className="user-name">{user.username}</div>
                        <div className="user-email">{user.email}</div>
                      </div>
                    </div>
                    <div className="suspicious-metrics">
                      <span className="metric-badge">Messages: {user.messageCount}</span>
                      <span className="metric-badge danger">Flags: {user.flagCount}</span>
                      <span className="metric-badge">F:{user.freedomScore}</span>
                      <span className="metric-badge">S:{user.securityScore}</span>
                    </div>
                    <button 
                      className="btn btn-danger"
                      onClick={() => handleUpdateUserStatus(user.id, 'SUSPENDED', 'Suspicious activity')}
                    >
                      Suspend
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                <p>No suspicious users detected</p>
              </div>
            )}
          </div>
        </motion.div>
      )}

      {/* Audit Logs Tab */}
      {activeTab === 'logs' && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
        >
          <div className="card">
            <div className="card-header">
              <h3 className="card-title">Audit Logs</h3>
            </div>
            <div className="logs-list">
              {adminData.auditLogs.map((log, index) => (
                <div key={log.id || index} className="log-item">
                  <div className="log-icon">
                    <FiSettings />
                  </div>
                  <div className="log-content">
                    <h4 className="log-type">{log.triggerType}</h4>
                    <p className="log-description">{log.eventDescription}</p>
                    <div className="log-meta">
                      <span>By: {log.triggeredByUsername || 'System'}</span>
                      <span>{new Date(log.createdAt).toLocaleString()}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </motion.div>
      )}
    </div>
  );
};

export default AdminPage;