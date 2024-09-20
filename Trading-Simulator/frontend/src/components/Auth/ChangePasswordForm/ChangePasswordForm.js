import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../AuthForm.css';

const ChangePasswordForm = () => {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmNewPassword, setConfirmNewPassword] = useState('');
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (message) {
      const timer = setTimeout(() => setMessage(''), 3000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  const handleChangePassword = async (e) => {
    e.preventDefault();

    if (newPassword !== confirmNewPassword) {
      setErrors({ confirmNewPassword: 'New passwords do not match.' });
      return;
    }

    try {
      const token = localStorage.getItem('jwtToken');
      const response = await axios.post('/api/auth/change-password',
        {
          currentPassword,
          newPassword
        },
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      setMessage(response.data.message);
      setMessageType(response.data.message === "Password changed successfully." ? 'success' : 'error');
      setErrors({});
    } catch (error) {
      if (error.response && error.response.status === 400) {
        if (error.response.data && typeof error.response.data === 'object') {
          setErrors(error.response.data);
        } else {
          setErrors({ form: 'An error occurred during password change. Please try again later.' });
        }
      } else {
        setErrors({ form: 'An unexpected error occurred. Please try again later.' });
      }
    }
  };

  return (
    <form onSubmit={handleChangePassword} className="auth-form">
      <div className="input-group">
        <label htmlFor="currentPassword">Current Password</label>
        <input
          type="password"
          id="currentPassword"
          value={currentPassword}
          onChange={(e) => setCurrentPassword(e.target.value)}
          required
        />
        {errors.currentPassword && <div className="error-message">{errors.currentPassword}</div>}
      </div>
      <div className="input-group">
        <label htmlFor="newPassword">New Password</label>
        <input
          type="password"
          id="newPassword"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          required
        />
        {errors.newPassword && <div className="error-message">{errors.newPassword}</div>}
      </div>
      <div className="input-group">
        <label htmlFor="confirmNewPassword">Confirm New Password</label>
        <input
          type="password"
          id="confirmNewPassword"
          value={confirmNewPassword}
          onChange={(e) => setConfirmNewPassword(e.target.value)}
          required
        />
        {errors.confirmNewPassword && <div className="error-message">{errors.confirmNewPassword}</div>}
      </div>
      {message && <p className={messageType === 'success' ? 'success-message' : 'error-message'}>{message}</p>}
      <button type="submit">Change Password</button>
      {errors.form && <div className="error-message">{errors.form}</div>}
    </form>
  );
};

export default ChangePasswordForm;
