import React, { useState, useEffect } from 'react';
import { deleteAccount } from '../../../services/UserSettings';
import { jwtDecode } from 'jwt-decode';
import { useNavigate } from 'react-router-dom';
import './DeleteAccountForm.css';

const DeleteAccountForm = ({ setIsLoggedIn }) => {
  const [confirmText, setConfirmText] = useState('');
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const [userEmail, setUserEmail] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      const decoded = jwtDecode(token);
      setUserEmail(decoded.sub);
    }
  }, []);

  const handleDeleteAccount = async (e) => {
    e.preventDefault();

    if (confirmText !== `Delete ${userEmail}`) {
      setMessage(`You must type "Delete ${userEmail}" to confirm.`);
      setMessageType('error');
      return;
    }

    try {
        const response = await deleteAccount(confirmText);
        console.log('Account deletion response:', response);

        if (response === "User account and associated data have been deleted.") {
          setMessage(response);
          setMessageType('success');

          setTimeout(() => {
                  localStorage.removeItem('jwtToken');
                  setIsLoggedIn(false);
                  navigate('/');
                }, 1000);
        } else {
          setMessage(response);
          setMessageType('error');
        }
      } catch (error) {
        console.error('Error while deleting account:', error);
        setMessage('An error occurred while deleting the account. Please try again later.');
        setMessageType('error');
      }
    };

  return (
    <form onSubmit={handleDeleteAccount} className="delete-account-form">
      <p>To delete your account, please type "Delete {userEmail}" below to confirm.</p>
      <div className="input-group">
        <input
          type="text"
          value={confirmText}
          onChange={(e) => setConfirmText(e.target.value)}
          placeholder={`Delete ${userEmail}`}
          className={messageType === 'error' ? 'error-input' : ''}
          required
        />
      </div>
      {message && <p className={messageType === 'success' ? 'success-message' : 'error-message'}>{message}</p>}
      <button type="submit">Delete Account</button>
    </form>
  );
};

export default DeleteAccountForm;