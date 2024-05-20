import React from 'react';
import ChangePasswordForm from '../components/Auth/ChangePasswordForm';

const UserSettingsPage = () => {
  return (
    <div className="auth-page">
      <div className="auth-container">
        <h2 className="auth-header">User Settings</h2>
        <ChangePasswordForm />
      </div>
    </div>
  );
};

export default UserSettingsPage;
