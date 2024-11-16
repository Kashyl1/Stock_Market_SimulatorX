import React, { useState, useEffect } from 'react';
import { changePassword } from '../../../services/UserSettings';


const ChangePasswordForm = () => {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmNewPassword, setConfirmNewPassword] = useState('');
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (message) {
      const timer = setTimeout(() => setMessage(''), 5000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  const validateForm = () => {
    const newErrors = {};

    if (!currentPassword) {
      newErrors.currentPassword = 'Current password is required.';
    }

    if (!newPassword) {
      newErrors.newPassword = 'New password is required.';
    } else {
      if (newPassword.length < 8) {
        newErrors.newPassword = 'Password must be at least 8 characters long.';
      }
      if (!/[A-Z]/.test(newPassword)) {
        newErrors.newPassword = 'Password must contain at least one uppercase letter.';
      }
      if (!/\d/.test(newPassword)) {
        newErrors.newPassword = 'Password must contain at least one digit.';
      }
    }

    if (!confirmNewPassword) {
      newErrors.confirmNewPassword = 'Please confirm your new password.';
    } else if (newPassword !== confirmNewPassword) {
      newErrors.confirmNewPassword = 'New passwords do not match.';
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      const response = await changePassword(currentPassword, newPassword);
      setMessage(response.message || 'Password changed successfully.');
      setMessageType('success');
      setErrors({});
      setCurrentPassword('');
      setNewPassword('');
      setConfirmNewPassword('');
    } catch (error) {
      let errorMessage = 'An unexpected error occurred. Please try again later.';
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.response?.data) {
        const validationErrors = error.response.data;
        const newErrors = {};
        for (const field in validationErrors) {
          newErrors[field] = validationErrors[field];
        }
        setErrors(newErrors);
        errorMessage = '';
      }
      setMessage(errorMessage);
      setMessageType('error');
    }
  };

  return (
    <form onSubmit={handleChangePassword} className="user-settings-form">
      <div className="input-group-settings">
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
      <div className="input-group-settings">
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
      <div className="input-group-settings">
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
