import { useState } from 'react';
import AdminSidebar from '../../components/Admin/AdminSidebar';
import AdminUsers from '../../components/Admin/AdminUsers/AdminUsers';
import AdminCreate from '../../components/Admin/AdminUsers/AdminCreate'; // Importujemy nowy komponent
import './AdminPageUsers.css';

const AdminPageUsers = () => {
  const [activeButton, setActiveButton] = useState('users');
  const [showUsers, setShowUsers] = useState(false);
  const [showCreateAdmin, setShowCreateAdmin] = useState(false);

  const handleButtonClick = (buttonName) => {
    setActiveButton(buttonName);
    if (buttonName === 'users') {
      setShowUsers(true);
      setShowCreateAdmin(false);
    } else if (buttonName === 'create') {
      setShowUsers(false);
      setShowCreateAdmin(true);
    }
  };

  return (
    <div className="main-page">
      <AdminSidebar />
      <div className="portfolio-details">
        <h1>Users Panel</h1>
        <div className="admin_button-container">
          <button
            className={`admin_action-button ${activeButton === 'users' ? 'active' : ''}`}
            onClick={() => handleButtonClick('users')}
          >
            Show Users
          </button>
          <button
            className={`admin_action-button ${activeButton === 'create' ? 'active' : ''}`}
            onClick={() => handleButtonClick('create')}
          >
            Create Admin
          </button>
        </div>
        {showUsers && <AdminUsers />}
        {showCreateAdmin && <AdminCreate />}
      </div>
    </div>
  );
};

export default AdminPageUsers;
