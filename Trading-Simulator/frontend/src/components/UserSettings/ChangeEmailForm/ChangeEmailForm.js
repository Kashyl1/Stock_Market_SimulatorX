import React, { useState, useEffect } from 'react';
import { changeEmail } from '../../../services/UserSettings';
import { useNavigate } from 'react-router-dom';
import './ChangeEmailForm.css';

const ChangeEmailForm = ({ setIsLoggedIn }) => {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newEmail, setNewEmail] = useState('');
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const navigate = useNavigate();

  const handleChangeEmail = async (e) => {
    e.preventDefault();

    try {
      const response = await changeEmail(currentPassword, newEmail);
      if (response.message === 'Email changed successfully. Please verify your new email. You will be redirected to main page in 5 seconds...') {
        setMessage(response.message);
        setMessageType('success');
        setTimeout(() => {
          localStorage.removeItem('jwtToken');
          setIsLoggedIn(false);
          navigate('/');
        }, 5000);
      } else {
        setMessage(response.message);
        setMessageType('error');
      }
    } catch (error) {
        let errorMessage = 'An error occurred while changing the email. Please try again later.';
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message;
        } else if (error.response?.data) {
          const errors = Object.values(error.response.data).join(' ');
          if (errors) {
            errorMessage = errors;
          }
        }
        setMessage(errorMessage);
        setMessageType('error');
      }
  };

  return (
    <form onSubmit={handleChangeEmail} className="change-email-form">
      <div className="input-group">
        <label htmlFor="currentPassword">Current Password</label>
        <input
          type="password"
          id="currentPassword"
          value={currentPassword}
          onChange={(e) => setCurrentPassword(e.target.value)}
          required
        />
      </div>
      <div className="input-group">
        <label htmlFor="newEmail">New Email</label>
        <input
          type="email"
          id="newEmail"
          value={newEmail}
          onChange={(e) => setNewEmail(e.target.value)}
          required
        />
      </div>
      {message && <p className={messageType === 'success' ? 'success-message' : 'error-message'}>{message}</p>}
      <button type="submit">Change Email</button>
    </form>
  );
};

export default ChangeEmailForm;
