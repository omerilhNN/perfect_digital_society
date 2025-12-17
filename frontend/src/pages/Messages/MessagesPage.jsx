import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  FiMessageSquare, 
  FiSend, 
  FiEdit2, 
  FiTrash2, 
  FiFlag,
  FiAlertCircle,
  FiCheck,
  FiX
} from 'react-icons/fi';
import { useAuth } from '../../contexts/AuthContext';
import { messageService } from '../../services/api';
import { toast } from 'react-toastify';
import './MessagesPage.css';

const MessagesPage = () => {
  const { user } = useAuth();
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showComposer, setShowComposer] = useState(false);
  const [editingMessage, setEditingMessage] = useState(null);
  const [newMessage, setNewMessage] = useState({
    content: '',
    messageType: 'PUBLIC',
  });
  const [filter, setFilter] = useState('all'); // all, my-messages

  useEffect(() => {
    loadMessages();
  }, [filter]);

  const loadMessages = async () => {
    try {
      setLoading(true);
      let data;
      if (filter === 'my-messages') {
        data = await messageService.getMyMessages();
      } else {
        data = await messageService.getAllMessages();
      }
      setMessages(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Failed to load messages:', error);
      toast.error('Failed to load messages');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateMessage = async (e) => {
    e. preventDefault();
    
    if (!newMessage.content. trim()) {
      toast.error('Message content is required');
      return;
    }

    try {
      await messageService.createMessage(newMessage);
      toast.success('Message created successfully! ');
      setNewMessage({ content: '', messageType: 'PUBLIC' });
      setShowComposer(false);
      loadMessages();
    } catch (error) {
      console.error('Failed to create message:', error);
      toast.error('Failed to create message');
    }
  };

  const handleUpdateMessage = async (messageId) => {
    if (!editingMessage?.content.trim()) {
      toast. error('Message content is required');
      return;
    }

    try {
      await messageService. updateMessage(messageId, { content: editingMessage.content });
      toast.success('Message updated successfully!');
      setEditingMessage(null);
      loadMessages();
    } catch (error) {
      console.error('Failed to update message:', error);
      toast.error('Failed to update message');
    }
  };

  const handleDeleteMessage = async (messageId) => {
    if (! window.confirm('Are you sure you want to delete this message?')) {
      return;
    }

    try {
      await messageService.deleteMessage(messageId);
      toast.success('Message deleted successfully!');
      loadMessages();
    } catch (error) {
      console.error('Failed to delete message:', error);
      toast. error('Failed to delete message');
    }
  };

  const handleFlagMessage = async (messageId) => {
    const reason = window.prompt('Please provide a reason for flagging this message:');
    if (!reason) return;

    try {
      await messageService.flagMessage(messageId, { 
        reason, 
        description: reason 
      });
      toast.success('Message flagged successfully!');
      loadMessages();
    } catch (error) {
      console.error('Failed to flag message:', error);
      toast. error('Failed to flag message');
    }
  };

  const startEditing = (message) => {
    setEditingMessage({ id: message.id, content: message.content });
  };

  const cancelEditing = () => {
    setEditingMessage(null);
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading Messages...</p>
      </div>
    );
  }

  return (
    <div className="messages-page">
      <motion.div 
        className="page-header"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <div>
          <h1 className="text-gradient">Messages</h1>
          <p className="page-subtitle">Communicate with the community</p>
        </div>
        <div className="header-actions">
          <select 
            className="filter-select"
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
          >
            <option value="all">All Messages</option>
            <option value="my-messages">My Messages</option>
          </select>
          <button 
            className="btn btn-primary"
            onClick={() => setShowComposer(!showComposer)}
          >
            <FiMessageSquare /> New Message
          </button>
        </div>
      </motion.div>

      {/* Message Composer */}
      {showComposer && (
        <motion.div 
          className="card message-composer"
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
        >
          <div className="card-header">
            <h3 className="card-title">Compose Message</h3>
          </div>
          <form onSubmit={handleCreateMessage}>
            <div className="form-group">
              <label className="form-label">Message Type</label>
              <select
                className="form-select"
                value={newMessage.messageType}
                onChange={(e) => setNewMessage({ ...newMessage, messageType: e.target.value })}
              >
                <option value="PUBLIC">Public</option>
                <option value="PRIVATE">Private</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Content</label>
              <textarea
                className="form-textarea"
                placeholder="Share your thoughts with the community..."
                value={newMessage.content}
                onChange={(e) => setNewMessage({ ... newMessage, content: e.target.value })}
                rows={5}
              />
            </div>
            <div className="form-actions">
              <button type="submit" className="btn btn-primary">
                <FiSend /> Post Message
              </button>
              <button 
                type="button" 
                className="btn btn-secondary"
                onClick={() => setShowComposer(false)}
              >
                Cancel
              </button>
            </div>
          </form>
        </motion.div>
      )}

      {/* Messages List */}
      <div className="messages-list">
        {messages. length > 0 ? (
          messages.map((message, index) => (
            <motion.div
              key={message.id}
              className="card message-card"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.05 }}
            >
              <div className="message-header">
                <div className="message-author">
                  <div className="author-avatar">
                    {message.username?.charAt(0). toUpperCase() || 'U'}
                  </div>
                  <div className="author-info">
                    <span className="author-name">{message.username || 'Unknown'}</span>
                    <span className="message-time">
                      {message.createdAt ?  new Date(message.createdAt).toLocaleString() : 'Unknown time'}
                    </span>
                  </div>
                </div>
                <div className="message-actions">
                  {message.userId === user?.id && (
                    <>
                      <button 
                        className="icon-btn"
                        onClick={() => startEditing(message)}
                        title="Edit"
                      >
                        <FiEdit2 />
                      </button>
                      <button 
                        className="icon-btn danger"
                        onClick={() => handleDeleteMessage(message.id)}
                        title="Delete"
                      >
                        <FiTrash2 />
                      </button>
                    </>
                  )}
                  {message.userId !== user?.id && (
                    <button 
                      className="icon-btn"
                      onClick={() => handleFlagMessage(message.id)}
                      title="Flag Message"
                    >
                      <FiFlag />
                    </button>
                  )}
                </div>
              </div>

              <div className="message-body">
                {editingMessage?.id === message. id ? (
                  <div className="message-edit-form">
                    <textarea
                      className="form-textarea"
                      value={editingMessage.content}
                      onChange={(e) => setEditingMessage({ ...editingMessage, content: e.target.value })}
                      rows={3}
                    />
                    <div className="edit-actions">
                      <button 
                        className="btn btn-sm btn-success"
                        onClick={() => handleUpdateMessage(message.id)}
                      >
                        <FiCheck /> Save
                      </button>
                      <button 
                        className="btn btn-sm btn-secondary"
                        onClick={cancelEditing}
                      >
                        <FiX /> Cancel
                      </button>
                    </div>
                  </div>
                ) : (
                  <p className="message-content">{message.content}</p>
                )}
              </div>

              <div className="message-footer">
                <div className="message-badges">
                  <span className="badge badge-info">{message.messageType}</span>
                  <span className={`badge ${message.moderationStatus === 'APPROVED' ? 'badge-success' : message.moderationStatus === 'REJECTED' ? 'badge-danger' : 'badge-warning'}`}>
                    {message.moderationStatus}
                  </span>
                  {message.flagCount > 0 && (
                    <span className="badge badge-danger">
                      <FiAlertCircle /> {message.flagCount} flags
                    </span>
                  )}
                </div>
                <div className="message-impact">
                  <span className="impact-badge freedom">
                    Freedom: +{message.freedomImpact || 0}
                  </span>
                  <span className="impact-badge security">
                    Security: +{message.securityImpact || 0}
                  </span>
                </div>
              </div>
            </motion.div>
          ))
        ) : (
          <div className="empty-state">
            <FiMessageSquare size={64} />
            <h3>No messages yet</h3>
            <p>Be the first to share your thoughts!</p>
            <button 
              className="btn btn-primary"
              onClick={() => setShowComposer(true)}
            >
              <FiMessageSquare /> Create Message
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default MessagesPage;