import React from 'react';
import UserSettingsSidebar from '../../components/UserSettings/UserSettingsSidebar';
import { Route, Routes } from 'react-router-dom';
import ChangePasswordForm from '../../components/Auth/ChangePasswordForm/ChangePasswordForm';
import './UserSettingsPage.css';

const UserSettingsPage = () => {
  return (
    <div className="settings-page">
      <UserSettingsSidebar />
      <div className="settings-content">
        <Routes>
          <Route path="change-password" element={<ChangePasswordForm />} />
        </Routes>
      </div>
    </div>
  );
};

export default UserSettingsPage;
