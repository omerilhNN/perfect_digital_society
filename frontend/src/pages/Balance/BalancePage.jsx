import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  FiActivity, 
  FiTrendingUp,
  FiClock,
  FiRefreshCw
} from 'react-icons/fi';
import { 
  LineChart, 
  Line, 
  AreaChart,
  Area,
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  Legend,
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  Radar
} from 'recharts';
import { useAuth } from '../../contexts/AuthContext';
import { balanceService } from '../../services/api';
import { toast } from 'react-toastify';
import './BalancePage.css';

const BalancePage = () => {
  const { user, isModerator } = useAuth();
  const [loading, setLoading] = useState(true);
  const [balanceData, setBalanceData] = useState({
    currentBalance: null,
    userBalance: null,
    events: [],
    trends: [],
    statistics: null,
  });

  useEffect(() => {
    loadBalanceData();
  }, []);

  const loadBalanceData = async () => {
    try {
      setLoading(true);
      const [current, userBal, events, trends, stats] = await Promise.all([
        balanceService.getCurrentBalance(),
        balanceService. getMyBalance(),
        balanceService.getBalanceEvents(),
        balanceService.getBalanceTrends(24),
        balanceService.getBalanceStatistics(),
      ]);

      setBalanceData({
        currentBalance: current,
        userBalance: userBal,
        events: Array.isArray(events) ? events : [],
        trends: Array. isArray(trends) ? trends : [],
        statistics: stats,
      });
    } catch (error) {
      console.error('Failed to load balance data:', error);
      toast.error('Failed to load balance data');
    } finally {
      setLoading(false);
    }
  };

  const handleTriggerRebalance = async () => {
    if (!window.confirm('Are you sure you want to trigger system rebalancing?')) {
      return;
    }

    try {
      await balanceService.triggerRebalance();
      toast.success('System rebalancing triggered successfully! ');
      loadBalanceData();
    } catch (error) {
      console.error('Failed to trigger rebalancing:', error);
      toast.error('Failed to trigger rebalancing');
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading Balance Data...</p>
      </div>
    );
  }

  const { currentBalance, userBalance, events, trends, statistics } = balanceData;

  // Prepare chart data
  const trendChartData = events.slice(0, 20).reverse(). map(event => ({
    name: new Date(event.createdAt).toLocaleTimeString(),
    freedom: event.newFreedomLevel,
    security: event.newSecurityLevel,
    balance: ((event.newFreedomLevel + event.newSecurityLevel) / 2).toFixed(1),
  }));

  const radarData = [
    {
      metric: 'Freedom',
      system: currentBalance?.currentFreedomLevel || 0,
      user: userBalance?.freedomScore || 0,
    },
    {
      metric: 'Security',
      system: currentBalance?.currentSecurityLevel || 0,
      user: userBalance?.securityScore || 0,
    },
    {
      metric: 'Reputation',
      system: 50,
      user: userBalance?.reputationScore || 0,
    },
  ];

  const getTrendIcon = (trend) => {
    if (! trend) return <FiActivity />;
    if (trend. includes('INCREASING') || trend.includes('UP')) {
      return <FiTrendingUp className="trend-up" />;
    }
    return <FiActivity />;
  };

  const getTrendColor = (trend) => {
    if (!trend) return 'var(--cyber-primary)';
    if (trend.includes('FREEDOM')) return 'var(--cyber-primary)';
    if (trend. includes('SECURITY')) return 'var(--cyber-secondary)';
    return 'var(--cyber-accent)';
  };

  return (
    <div className="balance-page">
      <motion.div 
        className="page-header"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <div>
          <h1 className="text-gradient">Balance Monitor</h1>
          <p className="page-subtitle">Track the perfect equilibrium</p>
        </div>
        {isModerator() && (
          <button 
            className="btn btn-primary"
            onClick={handleTriggerRebalance}
          >
            <FiRefreshCw /> Trigger Rebalance
          </button>
        )}
      </motion.div>

      {/* System Balance Overview */}
      <div className="balance-overview">
        <motion.div 
          className="card balance-card system-balance"
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
        >
          <div className="card-header">
            <h3 className="card-title">System Balance</h3>
            <span className="balance-trend" style={{ color: getTrendColor(currentBalance?.trend) }}>
              {getTrendIcon(currentBalance?.trend)}
              {currentBalance?.trend || 'STABLE'}
            </span>
          </div>
          <div className="balance-scores-grid">
            <div className="balance-metric freedom">
              <span className="metric-label">Freedom Level</span>
              <h2 className="metric-value">{currentBalance?.currentFreedomLevel || 0}</h2>
              <div className="metric-bar">
                <div 
                  className="metric-fill"
                  style={{ width: `${currentBalance?.currentFreedomLevel || 0}%` }}
                />
              </div>
            </div>
            <div className="balance-metric security">
              <span className="metric-label">Security Level</span>
              <h2 className="metric-value">{currentBalance?.currentSecurityLevel || 0}</h2>
              <div className="metric-bar">
                <div 
                  className="metric-fill"
                  style={{ width: `${currentBalance?.currentSecurityLevel || 0}%` }}
                />
              </div>
            </div>
            <div className="balance-metric balance-score">
              <span className="metric-label">Balance Score</span>
              <h2 className="metric-value">{currentBalance?.balanceScore?.toFixed(2) || '0.00'}</h2>
              <p className="metric-description">
                {currentBalance?.balanceScore >= 0.8 ? 'Excellent Balance' :
                 currentBalance?.balanceScore >= 0.6 ? 'Good Balance' :
                 currentBalance?.balanceScore >= 0.4 ? 'Moderate Balance' :
                 'Needs Adjustment'}
              </p>
            </div>
          </div>
        </motion.div>

        <motion.div 
          className="card balance-card user-balance"
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.1 }}
        >
          <div className="card-header">
            <h3 className="card-title">Your Balance</h3>
          </div>
          <div className="user-balance-content">
            <div className="balance-circle-chart">
              <ResponsiveContainer width="100%" height={250}>
                <RadarChart data={radarData}>
                  <PolarGrid stroke="var(--border-color)" />
                  <PolarAngleAxis dataKey="metric" stroke="var(--text-muted)" />
                  <PolarRadiusAxis stroke="var(--text-muted)" />
                  <Radar 
                    name="System" 
                    dataKey="system" 
                    stroke="var(--cyber-secondary)" 
                    fill="var(--cyber-secondary)" 
                    fillOpacity={0.3} 
                  />
                  <Radar 
                    name="You" 
                    dataKey="user" 
                    stroke="var(--cyber-primary)" 
                    fill="var(--cyber-primary)" 
                    fillOpacity={0.5} 
                  />
                  <Legend />
                </RadarChart>
              </ResponsiveContainer>
            </div>
            <div className="user-balance-stats">
              <div className="stat-item">
                <span className="stat-label">Freedom Score</span>
                <span className="stat-value freedom">{userBalance?.freedomScore || 0}</span>
              </div>
              <div className="stat-item">
                <span className="stat-label">Security Score</span>
                <span className="stat-value security">{userBalance?.securityScore || 0}</span>
              </div>
              <div className="stat-item">
                <span className="stat-label">Reputation</span>
                <span className="stat-value reputation">{userBalance?.reputationScore || 0}</span>
              </div>
              <div className="stat-item">
                <span className="stat-label">Balance Ratio</span>
                <span className="stat-value balance">{userBalance?.balanceRatio?.toFixed(2) || '0.00'}</span>
              </div>
            </div>
          </div>
        </motion.div>
      </div>

      {/* Trend Chart */}
      <motion.div 
        className="card chart-card"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
      >
        <div className="card-header">
          <h3 className="card-title">Balance Trend (Last 20 Events)</h3>
        </div>
        <div className="chart-container">
          <ResponsiveContainer width="100%" height={400}>
            <AreaChart data={trendChartData}>
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
              <YAxis stroke="var(--text-muted)" domain={[0, 100]} />
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

      {/* Balance Events */}
      <motion.div 
        className="card"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
      >
        <div className="card-header">
          <h3 className="card-title">Recent Balance Events</h3>
        </div>
        <div className="events-list">
          {events.slice(0, 10).map((event, index) => (
            <div key={event.id || index} className="event-item">
              <div className="event-icon">
                <FiClock />
              </div>
              <div className="event-content">
                <h4 className="event-type">{event.triggerType}</h4>
                <p className="event-description">{event.eventDescription}</p>
                <div className="event-changes">
                  <span className="change-item freedom">
                    Freedom: {event.previousFreedomLevel} → {event.newFreedomLevel}
                    <span className={event.newFreedomLevel > event.previousFreedomLevel ?  'change-up' : 'change-down'}>
                      ({event.newFreedomLevel - event.previousFreedomLevel > 0 ? '+' : ''}
                      {event.newFreedomLevel - event.previousFreedomLevel})
                    </span>
                  </span>
                  <span className="change-item security">
                    Security: {event.previousSecurityLevel} → {event. newSecurityLevel}
                    <span className={event.newSecurityLevel > event.previousSecurityLevel ? 'change-up' : 'change-down'}>
                      ({event.newSecurityLevel - event.previousSecurityLevel > 0 ? '+' : ''}
                      {event.newSecurityLevel - event. previousSecurityLevel})
                    </span>
                  </span>
                </div>
              </div>
              <div className="event-time">
                {new Date(event.createdAt).toLocaleString()}
              </div>
            </div>
          ))}
        </div>
      </motion.div>
    </div>
  );
};

export default BalancePage;