import React, { useState, useEffect } from 'react';
import { changePassword } from '../../../services/UserSettings';
import './ChangePasswordForm.css';

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
      const response = await changePassword(currentPassword, newPassword);
      setMessage(response.message || response);
      setMessageType(response.message === 'Password changed successfully' ? 'success' : 'error');
      setErrors({});
    } catch (error) {
      setErrors({ form: 'An unexpected error occurred. Please try again later.' });
    }
  };

  return (
    <form onSubmit={handleChangePassword} className="change-password-form">
      <div className="input-group">
        <label htmlFor="currentPassword">Current Password</label>
        <input
          type="password"
          id="currentPassword"
          value={currentPassword}
          onChange={(e) => setCurrentPassword(e.target.value)}
          className={errors.currentPassword ? 'error-input' : ''}
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
          className={errors.newPassword ? 'error-input' : ''}
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
          className={errors.confirmNewPassword ? 'error-input' : ''}
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
