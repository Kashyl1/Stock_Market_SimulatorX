import React from 'react';
import UserSettingsSidebar from '../../components/UserSettings/UserSettingsSidebar/UserSettingsSidebar';
import { Route, Routes } from 'react-router-dom';
import ChangePasswordForm from '../../components/UserSettings/ChangePasswordForm/ChangePasswordForm';
import DeleteAccountForm from '../../components/UserSettings/DeleteAccountForm/DeleteAccountForm';
import ChangeEmailForm from '../../components/UserSettings/ChangeEmailForm/ChangeEmailForm';
import './UserSettingsPage.css';
import Sidebar from '../../pages/Sidebar/Sidebar';

const UserSettingsPage = ({ setIsLoggedIn }) => {
  return (
  <div className="main-page">
   <Sidebar />
    <div className="settings-page">
      <UserSettingsSidebar />
      <div className="settings-content">
        <Routes>
          <Route path="change-password" element={<ChangePasswordForm />} />
          <Route path="delete-account" element={<DeleteAccountForm setIsLoggedIn={setIsLoggedIn} />} />
          <Route path="change-email" element={<ChangeEmailForm setIsLoggedIn={setIsLoggedIn} />} />
        </Routes>
      </div>
    </div>
     </div>
  );
};

export default UserSettingsPage;
