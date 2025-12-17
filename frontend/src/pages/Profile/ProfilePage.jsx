import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  FiUser, 
  FiMail, 
  FiEdit2, 
  FiSave,
  FiX,
  FiActivity,
  FiAward,
  FiCalendar
} from 'react-icons/fi';
import { useAuth } from '../../contexts/AuthContext';
import { userService } from '../../services/api';
import { toast } from 'react-toastify';
import './ProfilePage.css';

const ProfilePage = () => {
  const { user, refreshUser } = useAuth();
  const [loading, setLoading] = useState(false);
  const [editing, setEditing] = useState(false);
  const [profileData, setProfileData] = useState({
    firstName: '',
    lastName: '',
    email: '',
  });
  const [userBalance, setUserBalance] = useState(null);

  useEffect(() => {
    if (user) {
      setProfileData({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || '',
      });
      loadUserBalance();
    }
  }, [user]);

  const loadUserBalance = async () => {
    try {
      const balance = await userService.getMyBalance();
      setUserBalance(balance);
    } catch (error) {
      console.error('Failed to load user balance:', error);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfileData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!profileData.firstName. trim() || !profileData.lastName.trim() || !profileData.email.trim()) {
      toast.error('All fields are required');
      return;
    }

    try {
      setLoading(true);
      await userService.updateProfile(profileData);
      await refreshUser();
      toast.success('Profile updated successfully! ');
      setEditing(false);
    } catch (error) {
      console.error('Failed to update profile:', error);
      toast.error('Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  const cancelEditing = () => {
    setProfileData({
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      email: user.email || '',
    });
    setEditing(false);
  };

  if (! user) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading Profile...</p>
      </div>
    );
  }

  return (
    <div className="profile-page">
      <motion.div 
        className="page-header"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <div>
          <h1 className="text-gradient">Profile</h1>
          <p className="page-subtitle">Manage your account information</p>
        </div>
        {! editing && (
          <button 
            className="btn btn-primary"
            onClick={() => setEditing(true)}
          >
            <FiEdit2 /> Edit Profile
          </button>
        )}
      </motion.div>

      <div className="profile-content">
        {/* Profile Card */}
        <motion.div 
          className="card profile-card"
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
        >
          <div className="profile-header">
            <div className="profile-avatar-large">
              {user.username?.charAt(0).toUpperCase()}
            </div>
            <div className="profile-info">
              <h2 className="profile-name">{user.firstName} {user. lastName}</h2>
              <p className="profile-username">@{user.username}</p>
              <span className={`role-badge role-${user.role?.toLowerCase()}`}>
                {user.role}
              </span>
            </div>
          </div>

          <div className="profile-stats">
            <div className="stat-box">
              <FiCalendar className="stat-icon" />
              <div>
                <span className="stat-label">Member Since</span>
                <span className="stat-value">
                  {new Date(user.createdAt).toLocaleDateString()}
                </span>
              </div>
            </div>
            <div className="stat-box">
              <FiActivity className="stat-icon" />
              <div>
                <span className="stat-label">Last Login</span>
                <span className="stat-value">
                  {user.lastLoginAt ? new Date(user.lastLoginAt).toLocaleString() : 'N/A'}
                </span>
              </div>
            </div>
            <div className="stat-box">
              <FiAward className="stat-icon" />
              <div>
                <span className="stat-label">Account Status</span>
                <span className={`stat-value ${user. isActive ? 'active' : 'inactive'}`}>
                  {user.isActive ? 'Active' : 'Inactive'}
                </span>
              </div>
            </div>
          </div>

          {editing ?  (
            <form onSubmit={handleSubmit} className="profile-form">
              <div className="form-group">
                <label className="form-label">
                  <FiUser /> First Name
                </label>
                <input
                  type="text"
                  name="firstName"
                  className="form-input"
                  value={profileData.firstName}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">
                  <FiUser /> Last Name
                </label>
                <input
                  type="text"
                  name="lastName"
                  className="form-input"
                  value={profileData.lastName}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">
                  <FiMail /> Email
                </label>
                <input
                  type="email"
                  name="email"
                  className="form-input"
                  value={profileData.email}
                  onChange={handleChange}
                />
              </div>

              <div className="form-actions">
                <button type="submit" className="btn btn-success" disabled={loading}>
                  {loading ? (
                    <>
                      <div className="loading-spinner"></div>
                      Saving...
                    </>
                  ) : (
                    <>
                      <FiSave /> Save Changes
                    </>
                  )}
                </button>
                <button 
                  type="button" 
                  className="btn btn-secondary"
                  onClick={cancelEditing}
                >
                  <FiX /> Cancel
                </button>
              </div>
            </form>
          ) : (
            <div className="profile-details">
              <div className="detail-row">
                <span className="detail-label">
                  <FiUser /> First Name
                </span>
                <span className="detail-value">{user.firstName}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">
                  <FiUser /> Last Name
                </span>
                <span className="detail-value">{user.lastName}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">
                  <FiMail /> Email
                </span>
                <span className="detail-value">{user.email}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">
                  <FiUser /> Username
                </span>
                <span className="detail-value">@{user.username}</span>
              </div>
            </div>
          )}
        </motion.div>

        {/* Balance Scores Card */}
        <motion.div 
          className="card balance-scores-card"
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.1 }}
        >
          <div className="card-header">
            <h3 className="card-title">Balance Scores</h3>
          </div>

          <div className="scores-grid">
            <div className="score-item freedom">
              <div className="score-icon">
                <FiActivity />
              </div>
              <div className="score-content">
                <span className="score-label">Freedom</span>
                <h3 className="score-value">{user.freedomScore || 0}</h3>
                <div className="score-bar">
                  <div 
                    className="score-fill"
                    style={{ width: `${user.freedomScore || 0}%` }}
                  />
                </div>
              </div>
            </div>

            <div className="score-item security">
              <div className="score-icon">
                <FiActivity />
              </div>
              <div className="score-content">
                <span className="score-label">Security</span>
                <h3 className="score-value">{user.securityScore || 0}</h3>
                <div className="score-bar">
                  <div 
                    className="score-fill"
                    style={{ width: `${user.securityScore || 0}%` }}
                  />
                </div>
              </div>
            </div>

            <div className="score-item reputation">
              <div className="score-icon">
                <FiAward />
              </div>
              <div className="score-content">
                <span className="score-label">Reputation</span>
                <h3 className="score-value">{user.reputationScore || 0}</h3>
                <div className="score-bar">
                  <div 
                    className="score-fill"
                    style={{ width: `${Math.min((user.reputationScore || 0), 100)}%` }}
                  />
                </div>
              </div>
            </div>
          </div>

          {userBalance && (
            <div className="balance-ratio-section">
              <div className="ratio-card">
                <span className="ratio-label">Balance Ratio</span>
                <h2 className="ratio-value">{userBalance.balanceRatio?.toFixed(2) || '0.00'}</h2>
                <p className="ratio-description">
                  {userBalance.balanceRatio > 1.2 ? 'Freedom Oriented' :
                   userBalance.balanceRatio < 0.8 ? 'Security Oriented' :
                   'Well Balanced'}
                </p>
              </div>
            </div>
          )}

          <div className="score-info">
            <p className="info-text">
              💡 Your scores are dynamically calculated based on your community participation and behavior.
            </p>
            <p className="info-text">
              🎯 Strive for balance to maximize your influence in the community. 
            </p>
          </div>
        </motion. div>
      </div>
    </div>
  );
};

export default ProfilePage;