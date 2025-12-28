import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  FiUsers, 
  FiPlus, 
  FiThumbsUp, 
  FiThumbsDown,
  FiActivity,
  FiShield,
  FiCheckCircle,
  FiXCircle
} from 'react-icons/fi';
import { useAuth } from '../../contexts/AuthContext';
import { communityService } from '../../services/api';
import { toast } from 'react-toastify';
import './CommunityPage.css';

const CommunityPage = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [rules, setRules] = useState([]);
  const [metrics, setMetrics] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [filter, setFilter] = useState('all');
  const [newRule, setNewRule] = useState({
    title: '',
    description: '',
    ruleType: 'BALANCE',
    priority: 1,
    threshold: 3,
    action: 'WARN',
  });

  useEffect(() => {
    loadCommunityData();
  }, [filter]);

  const loadCommunityData = async () => {
    try {
      setLoading(true);
      const [rulesData, metricsData] = await Promise.all([
        filter === 'all' 
          ? communityService.getRules() 
          : filter === 'my-rules'
          ? communityService.getMyRules()
          : communityService.getRulesByType(filter. toUpperCase()),
        communityService.getMetrics(),
      ]);
      
      setRules(Array.isArray(rulesData) ? rulesData : []);
      setMetrics(metricsData);
    } catch (error) {
      console.error('Failed to load community data:', error);
      toast.error('Failed to load community data');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateRule = async (e) => {
    e. preventDefault();
    
    if (!newRule.title. trim() || !newRule.description.trim()) {
      toast.error('Title and description are required');
      return;
    }

    try {
      await communityService.createRule(newRule);
      toast.success('Community rule created successfully!');
      setNewRule({
        title: '',
        description: '',
        ruleType: 'BALANCE',
        priority: 1,
        threshold: 3,
        action: 'WARN',
      });
      setShowCreateForm(false);
      loadCommunityData();
    } catch (error) {
      console.error('Failed to create rule:', error);
      toast.error('Failed to create rule');
    }
  };

  const handleVote = async (ruleId, vote) => {
    try {
      await communityService.voteOnRule(ruleId, vote);
      toast.success(`Vote ${vote ?  'up' : 'down'} recorded! `);
      loadCommunityData();
    } catch (error) {
      console.error('Failed to vote:', error);
      toast.error('Failed to vote on rule');
    }
  };

  const getRuleTypeIcon = (type) => {
    switch (type) {
      case 'FREEDOM':
        return <FiActivity className="rule-type-icon freedom" />;
      case 'SECURITY':
        return <FiShield className="rule-type-icon security" />;
      case 'BALANCE':
        return <FiUsers className="rule-type-icon balance" />;
      default:
        return <FiUsers className="rule-type-icon" />;
    }
  };

  const getRuleTypeColor = (type) => {
    switch (type) {
      case 'FREEDOM':
        return 'var(--cyber-primary)';
      case 'SECURITY':
        return 'var(--cyber-secondary)';
      case 'BALANCE':
        return 'var(--cyber-accent)';
      default:
        return 'var(--cyber-primary)';
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading Community... </p>
      </div>
    );
  }

  return (
    <div className="community-page">
      <motion.div 
        className="page-header"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <div>
          <h1 className="text-gradient">Community</h1>
          <p className="page-subtitle">Governance through collaboration</p>
        </div>
        <div className="header-actions">
          <select 
            className="filter-select"
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
          >
            <option value="all">All Rules</option>
            <option value="my-rules">My Rules</option>
            <option value="freedom">Freedom Rules</option>
            <option value="security">Security Rules</option>
            <option value="balance">Balance Rules</option>
          </select>
          <button 
            className="btn btn-primary"
            onClick={() => setShowCreateForm(! showCreateForm)}
          >
            <FiPlus /> Propose Rule
          </button>
        </div>
      </motion.div>

      {/* Community Metrics */}
      {metrics && (
        <motion.div 
          className="community-metrics"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <div className="metrics-grid">
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
                <span className="metric-subtitle">{metrics.flaggedMessages} flagged</span>
              </div>
            </div>
            <div className="metric-card">
              <FiShield className="metric-icon" />
              <div className="metric-content">
                <span className="metric-label">Community Health</span>
                <h3 className="metric-value">{metrics.communityHealth?.toFixed(1)}%</h3>
                <span className="metric-subtitle">
                  {metrics.communityHealth >= 80 ? 'Excellent' : 
                   metrics.communityHealth >= 60 ? 'Good' : 
                   metrics.communityHealth >= 40 ? 'Fair' : 'Needs Attention'}
                </span>
              </div>
            </div>
            <div className="metric-card">
              <FiActivity className="metric-icon" />
              <div className="metric-content">
                <span className="metric-label">Balance Scores</span>
                <h3 className="metric-value">
                  F:{metrics.averageFreedomScore?.toFixed(0)} S:{metrics.averageSecurityScore?.toFixed(0)}
                </h3>
                <span className="metric-subtitle">Average scores</span>
              </div>
            </div>
          </div>
        </motion.div>
      )}

      {/* Create Rule Form */}
      {showCreateForm && (
        <motion.div 
          className="card create-rule-form"
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
        >
          <div className="card-header">
            <h3 className="card-title">Propose New Rule</h3>
          </div>
          <form onSubmit={handleCreateRule}>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Rule Title</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="Enter rule title"
                  value={newRule.title}
                  onChange={(e) => setNewRule({ ...newRule, title: e. target.value })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Rule Type</label>
                <select
                  className="form-select"
                  value={newRule. ruleType}
                  onChange={(e) => setNewRule({ ...newRule, ruleType: e.target.value })}
                >
                  <option value="FREEDOM">Freedom</option>
                  <option value="SECURITY">Security</option>
                  <option value="BALANCE">Balance</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Description</label>
              <textarea
                className="form-textarea"
                placeholder="Describe the rule in detail..."
                value={newRule.description}
                onChange={(e) => setNewRule({ ...newRule, description: e. target.value })}
                rows={4}
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Priority (1-10)</label>
                <input
                  type="number"
                  className="form-input"
                  min="1"
                  max="10"
                  value={newRule.priority}
                  onChange={(e) => setNewRule({ ...newRule, priority: parseInt(e.target.value) })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Threshold</label>
                <input
                  type="number"
                  className="form-input"
                  min="1"
                  value={newRule.threshold}
                  onChange={(e) => setNewRule({ ...newRule, threshold: parseInt(e.target.value) })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Action</label>
                <select
                  className="form-select"
                  value={newRule.action}
                  onChange={(e) => setNewRule({ ...newRule, action: e.target.value })}
                >
                  <option value="WARN">Warn</option>
                  <option value="RESTRICT">Restrict</option>
                  <option value="SUSPEND">Suspend</option>
                </select>
              </div>
            </div>

            <div className="form-actions">
              <button type="submit" className="btn btn-primary">
                <FiPlus /> Propose Rule
              </button>
              <button 
                type="button" 
                className="btn btn-secondary"
                onClick={() => setShowCreateForm(false)}
              >
                Cancel
              </button>
            </div>
          </form>
        </motion.div>
      )}

      {/* Rules List */}
      <div className="rules-list">
        {rules.length > 0 ? (
          rules.map((rule, index) => (
            <motion.div
              key={rule.id}
              className={`card rule-card ${! rule.isActive ? 'inactive' : ''}`}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.05 }}
              style={{ borderColor: getRuleTypeColor(rule. ruleType) }}
            >
              <div className="rule-header">
                <div className="rule-title-section">
                  {getRuleTypeIcon(rule.ruleType)}
                  <div>
                    <h3 className="rule-title">{rule.title}</h3>
                    <div className="rule-meta">
                      <span className="rule-author">by {rule.createdByUsername}</span>
                      <span className="rule-date">
                        {new Date(rule.createdAt).toLocaleDateString()}
                      </span>
                    </div>
                  </div>
                </div>
                <div className="rule-status">
                  {rule.isActive ? (
                    <span className="badge badge-success">
                      <FiCheckCircle /> Active
                    </span>
                  ) : (
                    <span className="badge badge-danger">
                      <FiXCircle /> Inactive
                    </span>
                  )}
                </div>
              </div>

              <p className="rule-description">{rule.description}</p>

              <div className="rule-details">
                <div className="rule-detail-item">
                  <span className="detail-label">Type:</span>
                  <span className={`badge badge-${rule.ruleType. toLowerCase()}`}>
                    {rule.ruleType}
                  </span>
                </div>
                <div className="rule-detail-item">
                  <span className="detail-label">Priority:</span>
                  <span className="detail-value">{rule.priority}</span>
                </div>
                <div className="rule-detail-item">
                  <span className="detail-label">Threshold:</span>
                  <span className="detail-value">{rule.threshold}</span>
                </div>
                <div className="rule-detail-item">
                  <span className="detail-label">Action:</span>
                  <span className={`badge badge-${rule.action.toLowerCase()}`}>
                    {rule.action}
                  </span>
                </div>
              </div>

              <div className="rule-footer">
                <div className="rule-votes">
                  <span className="votes-count">
                    {rule.votes} {rule.votes === 1 ?  'vote' : 'votes'}
                  </span>
                </div>
                <div className="vote-actions">
                  <button 
                    className="vote-btn upvote"
                    onClick={() => handleVote(rule.id, true)}
                    title="Vote Up"
                  >
                    <FiThumbsUp /> Upvote
                  </button>
                  <button 
                    className="vote-btn downvote"
                    onClick={() => handleVote(rule.id, false)}
                    title="Vote Down"
                  >
                    <FiThumbsDown /> Downvote
                  </button>
                </div>
              </div>
            </motion.div>
          ))
        ) : (
          <div className="empty-state">
            <FiUsers size={64} />
            <h3>No rules found</h3>
            <p>Be the first to propose a community rule!</p>
            <button 
              className="btn btn-primary"
              onClick={() => setShowCreateForm(true)}
            >
              <FiPlus /> Propose Rule
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default CommunityPage;