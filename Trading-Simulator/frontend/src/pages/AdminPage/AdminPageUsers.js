import { useState } from 'react';
import AdminSidebar from '../../components/Admin/AdminSidebar';
import AdminUsers from '../../components/Admin/AdminUsers';
import './AdminPageUsers.css';

const AdminPageUsers = () => {
  const [activeButton, setActiveButton] = useState('users');
  const [showUsers, setShowUsers] = useState(false);

  const handleButtonClick = (buttonName) => {
    setActiveButton(buttonName);
    if (buttonName === 'users') {
      setShowUsers(true);
    } else {
      setShowUsers(false);
    }
  };

  return (
    <div className="main-page">
      <AdminSidebar />
      <div className="portfolio-details">
        <h1>Users Panel</h1>
        <div className="admin_button-container ">
          <button
            className={`action-button ${activeButton === 'users' ? 'active' : ''}`}
            onClick={() => handleButtonClick('users')}
          >
            Show Users
          </button>
          <button
            className={`action-button ${activeButton === 'settings' ? 'active' : ''}`}
            onClick={() => handleButtonClick('settings')}
          >
            Delete Users
          </button>
          <button
            className={`action-button ${activeButton === 'analytics' ? 'active' : ''}`}
            onClick={() => handleButtonClick('analytics')}
          >
             Block User
          </button>
          <button
            className={`action-button ${activeButton === 'logs' ? 'active' : ''}`}
            onClick={() => handleButtonClick('logs')}
          >
            Create Admin
          </button>
        </div>
        {showUsers && <AdminUsers />}
      </div>
    </div>
  );
};

export default AdminPageUsers;
