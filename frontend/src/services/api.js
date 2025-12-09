import axios from 'axios';
import { toast } from 'react-toastify';

// Base API URL
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Create axios instance
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      
      if (status === 401) {
        localStorage.removeItem('token');
        window.location.href = '/login';
        toast.error('Session expired. Please login again.');
      } else if (status === 403) {
        toast.error('Access denied.  Insufficient privileges.');
      } else if (status === 404) {
        toast.error('Resource not found.');
      } else if (status >= 500) {
        toast.error('Server error. Please try again later.');
      } else if (data?.message) {
        toast.error(data.message);
      }
    } else if (error.request) {
      toast.error('Network error. Please check your connection.');
    }
    
    return Promise.reject(error);
  }
);

// ===========================
// AUTHENTICATION SERVICE
// ===========================

export const authService = {
  login: async (username, password) => {
    const response = await apiClient.post('/users/login', { username, password });
    return response.data. data;
  },

  register: async (userData) => {
    const response = await apiClient.post('/users/register', userData);
    return response.data.data;
  },

  getCurrentUser: async () => {
    const response = await apiClient.get('/users/me');
    return response.data.data;
  },

  logout: async () => {
    await apiClient.post('/users/logout');
  },
};

// ===========================
// USER SERVICE
// ===========================

export const userService = {
  getProfile: async (userId) => {
    const response = await apiClient.get(`/users/profile/${userId}`);
    return response.data.data;
  },

  updateProfile: async (profileData) => {
    const response = await apiClient.put('/users/profile', profileData);
    return response.data.data;
  },

  getMyBalance: async () => {
    const response = await apiClient.get('/users/my-balance');
    return response.data.data;
  },

  getUserBalance: async (userId) => {
    const response = await apiClient.get(`/users/balance/${userId}`);
    return response.data.data;
  },
};

// ===========================
// MESSAGE SERVICE
// ===========================

export const messageService = {
  getAllMessages: async () => {
    const response = await apiClient.get('/messages');
    return response. data;
  },

  getMyMessages: async () => {
    const response = await apiClient.get('/messages/my-messages');
    return response. data;
  },

  getUserMessages: async (userId) => {
    const response = await apiClient.get(`/messages/user/${userId}`);
    return response. data;
  },

  getMessageById: async (messageId) => {
    const response = await apiClient.get(`/messages/${messageId}`);
    return response.data;
  },

  createMessage: async (messageData) => {
    const response = await apiClient.post('/messages', messageData);
    return response.data;
  },

  updateMessage: async (messageId, messageData) => {
    const response = await apiClient.put(`/messages/${messageId}`, messageData);
    return response.data;
  },

  deleteMessage: async (messageId) => {
    const response = await apiClient.delete(`/messages/${messageId}`);
    return response.data;
  },

  flagMessage: async (messageId, flagData) => {
    const response = await apiClient.post(`/messages/${messageId}/flag`, flagData);
    return response.data;
  },

  getFlaggedMessages: async () => {
    const response = await apiClient.get('/messages/flagged');
    return response.data;
  },

  getPendingMessages: async () => {
    const response = await apiClient. get('/messages/pending');
    return response.data;
  },
};

// ===========================
// BALANCE SERVICE
// ===========================

export const balanceService = {
  getCurrentBalance: async () => {
    const response = await apiClient.get('/balance/current');
    return response.data.data;
  },

  getBalanceEvents: async () => {
    const response = await apiClient.get('/balance/events');
    return response.data;
  },

  getBalanceTrends: async (hours = 24) => {
    const response = await apiClient.get(`/balance/trends?hours=${hours}`);
    return response.data;
  },

  getBalanceStatistics: async () => {
    const response = await apiClient.get('/balance/statistics');
    return response.data. data;
  },

  getUserBalance: async (userId) => {
    const response = await apiClient. get(`/balance/user/${userId}`);
    return response.data.data;
  },

  getMyBalance: async () => {
    const response = await apiClient.get('/balance/my-balance');
    return response.data.data;
  },

  triggerBalance: async (eventData) => {
    const response = await apiClient.post('/balance/trigger', eventData);
    return response.data;
  },

  triggerRebalance: async () => {
    const response = await apiClient.post('/balance/rebalance');
    return response. data;
  },
};

// ===========================
// COMMUNITY SERVICE
// ===========================

export const communityService = {
  getRules: async (params = {}) => {
    const queryString = new URLSearchParams(params). toString();
    const response = await apiClient.get(`/community/rules${queryString ?  '?' + queryString : ''}`);
    return response.data;
  },

  getRuleById: async (ruleId) => {
    const response = await apiClient.get(`/community/rules/${ruleId}`);
    return response.data;
  },

  getRulesByType: async (type) => {
    const response = await apiClient. get(`/community/rules/type/${type}`);
    return response.data;
  },

  getTopVotedRules: async (limit = 10) => {
    const response = await apiClient. get(`/community/rules/top-voted? limit=${limit}`);
    return response.data;
  },

  getRecentActivity: async (limit = 20) => {
    const response = await apiClient.get(`/community/recent-activity?limit=${limit}`);
    return response.data;
  },

  createRule: async (ruleData) => {
    const response = await apiClient.post('/community/rules', ruleData);
    return response.data;
  },

  voteOnRule: async (ruleId, vote) => {
    const response = await apiClient.post(`/community/rules/${ruleId}/vote`, { 
      ruleId, 
      vote 
    });
    return response. data;
  },

  getMetrics: async () => {
    const response = await apiClient.get('/community/metrics');
    return response.data. data;
  },

  getMyRules: async () => {
    const response = await apiClient.get('/community/my-rules');
    return response. data;
  },

  getHealthReport: async () => {
    const response = await apiClient.get('/community/health-report');
    return response.data;
  },

  evaluateRules: async () => {
    const response = await apiClient.post('/community/evaluate-rules');
    return response. data;
  },
};

// ===========================
// ADMIN SERVICE
// ===========================

export const adminService = {
  getAllUsers: async (params = {}) => {
    const queryString = new URLSearchParams(params).toString();
    const response = await apiClient.get(`/admin/users${queryString ? '?' + queryString : ''}`);
    return response.data;
  },

  getUserById: async (userId) => {
    const response = await apiClient.get(`/admin/users/${userId}`);
    return response.data;
  },

  searchUsers: async (searchParams) => {
    const queryString = new URLSearchParams(searchParams).toString();
    const response = await apiClient.get(`/admin/users/search? ${queryString}`);
    return response.data;
  },

  getSuspiciousUsers: async () => {
    const response = await apiClient.get('/admin/users/suspicious');
    return response. data;
  },

  updateUserStatus: async (userId, statusData) => {
    const response = await apiClient.put(`/admin/users/${userId}/status`, statusData);
    return response. data;
  },

  getSystemMetrics: async () => {
    const response = await apiClient.get('/admin/system/metrics');
    return response.data. data;
  },

  getDashboard: async () => {
    const response = await apiClient. get('/admin/dashboard');
    return response.data. data;
  },

  adjustBalance: async (adjustmentData) => {
    const response = await apiClient.post('/admin/balance/adjust', adjustmentData);
    return response.data;
  },

  getAuditLogs: async (limit = 100) => {
    const response = await apiClient.get(`/admin/audit-logs?limit=${limit}`);
    return response.data;
  },

  performEmergency: async (action, reason) => {
    const response = await apiClient.post('/admin/emergency', null, {
      params: { action, reason }
    });
    return response.data;
  },

  generateReport: async () => {
    const response = await apiClient.get('/admin/system/report');
    return response. data;
  },

  performMaintenance: async () => {
    const response = await apiClient.post('/admin/system/maintenance');
    return response. data;
  },
};

// ===========================
// HEALTH SERVICE
// ===========================

export const healthService = {
  checkHealth: async () => {
    const response = await apiClient.get('/health');
    return response. data;
  },

  getVersion: async () => {
    const response = await apiClient.get('/health/version');
    return response.data;
  },
};

export default apiClient;