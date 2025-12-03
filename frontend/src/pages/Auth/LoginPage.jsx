import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { FiMail, FiLock, FiLogIn } from 'react-icons/fi';
import { useAuth } from '../../contexts/AuthContext';
import './AuthPages.css';

const LoginPage = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });
  
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.username. trim()) {
      newErrors. username = 'Username is required';
    }
    if (!formData.password) {
      newErrors.password = 'Password is required';
    }
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setLoading(true);
    const success = await login(formData.username, formData.password);
    setLoading(false);

    if (success) {
      navigate('/dashboard');
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-background">
        <div className="cyber-grid"></div>
        <div className="cyber-particles"></div>
      </div>

      <motion.div 
        className="auth-container"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <div className="auth-header">
          <div className="auth-logo">PDS</div>
          <h1 className="text-gradient">Perfect Digital Society</h1>
          <p className="auth-subtitle">Welcome back to the balanced future</p>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">
              <FiMail /> Username or Email
            </label>
            <input
              type="text"
              name="username"
              className={`form-input ${errors.username ? 'error' : ''}`}
              placeholder="Enter your username or email"
              value={formData.username}
              onChange={handleChange}
            />
            {errors.username && <span className="form-error">{errors.username}</span>}
          </div>

          <div className="form-group">
            <label className="form-label">
              <FiLock /> Password
            </label>
            <input
              type="password"
              name="password"
              className={`form-input ${errors.password ? 'error' : ''}`}
              placeholder="Enter your password"
              value={formData.password}
              onChange={handleChange}
            />
            {errors.password && <span className="form-error">{errors.password}</span>}
          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? (
              <>
                <div className="loading-spinner"></div>
                Logging in...
              </>
            ) : (
              <>
                <FiLogIn /> Login
              </>
            )}
          </button>
        </form>

        <div className="auth-footer">
          <p>Don't have an account? </p>
          <Link to="/register" className="auth-link">
            Register Now
          </Link>
        </div>

        <div className="auth-divider"></div>

        <div className="auth-info">
          <p className="info-text">
            🔐 Secure authentication with JWT
          </p>
          <p className="info-text">
            ⚖️ Freedom & Security in Perfect Balance
          </p>
        </div>
      </motion. div>
    </div>
  );
};

export default LoginPage;