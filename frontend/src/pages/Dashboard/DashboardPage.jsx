import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  FiUsers, 
  FiMessageSquare, 
  FiActivity, 
  FiTrendingUp,
  FiAlertCircle,
  FiShield
} from 'react-icons/fi';
import { 
  LineChart, 
  Line, 
  AreaChart, 
  Area,
  BarChart,
  Bar,
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  Legend
} from 'recharts';
import { useAuth } from '../../contexts/AuthContext';
import { balanceService, communityService, messageService } from '../../services/api';
import './DashboardPage.css';

const DashboardPage = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [dashboardData, setDashboardData] = useState({
    systemBalance: null,
    communityMetrics: null,
    recentMessages: [],
    balanceEvents: [],
    userBalance: null,
  });

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [balance, metrics, messages, events, userBal] = await Promise.all([
        balanceService.getCurrentBalance(),
        communityService.getMetrics(),
        messageService.getAllMessages(),
        balanceService.getBalanceEvents(),
        balanceService.getMyBalance(),
      ]);

      setDashboardData({
        systemBalance: balance,
        communityMetrics: metrics,
        recentMessages: Array.isArray(messages) ? messages.slice(0, 5) : [],
        balanceEvents: Array.isArray(events) ? events.slice(0, 10) : [],
        userBalance: userBal,
      });
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading Dashboard...</p>
      </div>
    );
  }

  const { systemBalance, communityMetrics, recentMessages, balanceEvents, userBalance } = dashboardData;

  // Prepare chart data
  const balanceChartData = balanceEvents.map(event => ({
    name: new Date(event.createdAt).toLocaleDateString(),
    freedom: event.newFreedomLevel,
    security: event.newSecurityLevel,
  }));

  const statsCards = [
    {
      title: 'System Balance',
      value: systemBalance?.balanceScore?.toFixed(2) || '0.00',
      icon: FiActivity,
      color: 'var(--cyber-primary)',
      trend: systemBalance?.trend || 'STABLE',
    },
    {
      title: 'Total Users',
      value: communityMetrics?.totalUsers || 0,
      icon: FiUsers,
      color: 'var(--cyber-secondary)',
      subtitle: `${communityMetrics?.activeUsers || 0} active`,
    },
    {
      title: 'Total Messages',
      value: communityMetrics?.totalMessages || 0,
      icon: FiMessageSquare,
      color: 'var(--cyber-accent)',
      subtitle: `${communityMetrics?.flaggedMessages || 0} flagged`,
    },
    {
      title: 'Community Health',
      value: `${communityMetrics?.communityHealth?.toFixed(1) || 0}%`,
      icon: FiShield,
      color: 'var(--cyber-success)',
      trend: 'HEALTHY',
    },
  ];

  return (
    <div className="dashboard-page">
      <motion.div 
        className="dashboard-header"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <div>
          <h1 className="text-gradient">Dashboard</h1>
          <p className="page-subtitle">Welcome back, {user?.username}!  Monitor the perfect balance. </p>
        </div>
      </motion.div>

      {/* Stats Cards */}
      <div className="stats-grid">
        {statsCards.map((stat, index) => (
          <motion.div
            key={stat.title}
            className="stat-card"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
          >
            <div className="stat-icon" style={{ color: stat.color }}>
              <stat.icon />
            </div>
            <div className="stat-content">
              <span className="stat-label">{stat.title}</span>
              <h2 className="stat-value">{stat.value}</h2>
              {stat.subtitle && <span className="stat-subtitle">{stat.subtitle}</span>}
              {stat.trend && (
                <span className={`stat-trend ${stat.trend.toLowerCase()}`}>
                  <FiTrendingUp /> {stat.trend}
                </span>
              )}
            </div>
          </motion.div>
        ))}
      </div>

      {/* User Balance Overview */}
      <motion.div 
        className="card balance-overview-card"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4 }}
      >
        <div className="card-header">
          <h3 className="card-title">Your Balance</h3>
        </div>
        <div className="balance-scores">
          <div className="balance-score freedom">
            <div className="score-circle">
              <svg viewBox="0 0 100 100">
                <circle cx="50" cy="50" r="45" />
                <circle 
                  cx="50" 
                  cy="50" 
                  r="45" 
                  style={{
                    strokeDasharray: `${(userBalance?.freedomScore || 0) * 2.83} 283`,
                  }}
                />
              </svg>
              <div className="score-text">
                <span className="score-number">{userBalance?.freedomScore || 0}</span>
                <span className="score-label">Freedom</span>
              </div>
            </div>
          </div>
          <div className="balance-score security">
            <div className="score-circle">
              <svg viewBox="0 0 100 100">
                <circle cx="50" cy="50" r="45" />
                <circle 
                  cx="50" 
                  cy="50" 
                  r="45" 
                  style={{
                    strokeDasharray: `${(userBalance?.securityScore || 0) * 2.83} 283`,
                  }}
                />
              </svg>
              <div className="score-text">
                <span className="score-number">{userBalance?.securityScore || 0}</span>
                <span className="score-label">Security</span>
              </div>
            </div>
          </div>
          <div className="balance-score reputation">
            <div className="score-circle">
              <svg viewBox="0 0 100 100">
                <circle cx="50" cy="50" r="45" />
                <circle 
                  cx="50" 
                  cy="50" 
                  r="45" 
                  style={{
                    strokeDasharray: `${Math.min((userBalance?.reputationScore || 0) * 2.83, 283)} 283`,
                  }}
                />
              </svg>
              <div className="score-text">
                <span className="score-number">{userBalance?.reputationScore || 0}</span>
                <span className="score-label">Reputation</span>
              </div>
            </div>
          </div>
        </div>
        <div className="balance-ratio">
          <span className="ratio-label">Balance Ratio:</span>
          <span className="ratio-value">{userBalance?.balanceRatio?.toFixed(2) || '0.00'}</span>
        </div>
      </motion.div>

      {/* Charts Section */}
      <div className="charts-grid">
        {/* System Balance Trend */}
        <motion.div 
          className="card chart-card"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
        >
          <div className="card-header">
            <h3 className="card-title">System Balance Trend</h3>
          </div>
          <div className="chart-container">
            <ResponsiveContainer width="100%" height={300}>
              <AreaChart data={balanceChartData}>
                <defs>
                  <linearGradient id="colorFreedom" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="var(--cyber-primary)" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="var(--cyber-primary)" stopOpacity={0}/>
                  </linearGradient>
                  <linearGradient id="colorSecurity" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="var(--cyber-secondary)" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="var(--cyber-secondary)" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" />
                <XAxis dataKey="name" stroke="var(--text-muted)" />
                <YAxis stroke="var(--text-muted)" />
                <Tooltip 
                  contentStyle={{
                    background: 'var(--bg-card)',
                    border: '2px solid var(--border-color)',
                    borderRadius: 'var(--radius-md)',
                  }}
                />
                <Legend />
                <Area 
                  type="monotone" 
                  dataKey="freedom" 
                  stroke="var(--cyber-primary)" 
                  fillOpacity={1} 
                  fill="url(#colorFreedom)" 
                  name="Freedom"
                />
                <Area 
                  type="monotone" 
                  dataKey="security" 
                  stroke="var(--cyber-secondary)" 
                  fillOpacity={1} 
                  fill="url(#colorSecurity)" 
                  name="Security"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* Community Metrics */}
        <motion.div 
          className="card chart-card"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.6 }}
        >
          <div className="card-header">
            <h3 className="card-title">Community Metrics</h3>
          </div>
          <div className="chart-container">
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={[
                { name: 'Avg Freedom', value: communityMetrics?.averageFreedomScore || 0 },
                { name: 'Avg Security', value: communityMetrics?.averageSecurityScore || 0 },
                { name: 'Health', value: communityMetrics?.communityHealth || 0 },
              ]}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" />
                <XAxis dataKey="name" stroke="var(--text-muted)" />
                <YAxis stroke="var(--text-muted)" />
                <Tooltip 
                  contentStyle={{
                    background: 'var(--bg-card)',
                    border: '2px solid var(--border-color)',
                    borderRadius: 'var(--radius-md)',
                  }}
                />
                <Bar dataKey="value" fill="var(--cyber-primary)" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </motion.div>
      </div>

      {/* Recent Activity */}
      <motion.div 
        className="card"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.7 }}
      >
        <div className="card-header">
          <h3 className="card-title">Recent Messages</h3>
        </div>
        <div className="recent-messages">
          {recentMessages.length > 0 ?  (
            recentMessages.map((message, index) => (
              <div key={message.id || index} className="message-item">
                <div className="message-author">
                  <div className="author-avatar">
                    {message.username?.charAt(0).toUpperCase() || 'U'}
                  </div>
                  <div className="author-info">
                    <span className="author-name">{message.username || 'Unknown'}</span>
                    <span className="message-time">
                      {message.createdAt ?  new Date(message.createdAt).toLocaleString() : 'Unknown time'}
                    </span>
                  </div>
                </div>
                <p className="message-content">{message.content}</p>
                <div className="message-badges">
                  <span className="badge badge-info">{message.messageType}</span>
                  {message.flagCount > 0 && (
                    <span className="badge badge-warning">
                      <FiAlertCircle /> {message.flagCount} flags
                    </span>
                  )}
                </div>
              </div>
            ))
          ) : (
            <p className="empty-state">No recent messages</p>
          )}
        </div>
      </motion.div>
    </div>
  );
};

export default DashboardPage;