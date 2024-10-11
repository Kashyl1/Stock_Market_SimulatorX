import React from 'react';
import { Link } from 'react-router-dom';
import './UserSettingsSidebar.css';

const UserSettingsSidebar = () => {
  return (
    <div className="sidebar">
      <ul>
        <li><Link to="/settings/change-password">Change Password</Link></li>
      </ul>
    </div>
  );
};

export default UserSettingsSidebar;
