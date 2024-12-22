import { useState } from 'react';
import AdminSidebar from '../../components/Admin/AdminSidebar';
import AdminEvents from '../../components/Admin/AdminEvents/AdminEvents';
import AdminStatistics from '../../components/Admin/AdminEvents/AdminStatistics';
import UserEvents from '../../components/Admin/AdminEvents/UserEvents';

const AdminPageEvents = () => {
  const [activeButton, setActiveButton] = useState('events');
  const [showEvents, setShowEvents] = useState(false);
  const [showAdminStatistics, setShowAdminStatistics] = useState(false);
  const [showUserEvents, setShowUserEvents] = useState(false);

  const handleButtonClick = (buttonName) => {
    setActiveButton(buttonName);
    if (buttonName === 'events') {
      setShowEvents(true);
      setShowAdminStatistics(false);
      setShowUserEvents(false);
    } else if (buttonName === 'statistics') {
      setShowEvents(false);
      setShowAdminStatistics(true);
      setShowUserEvents(false);
    } else if (buttonName === 'userevents') {
      setShowEvents(false);
      setShowAdminStatistics(false);
      setShowUserEvents(true);
    }
  };

  return (
    <div className="main-page">
      <AdminSidebar />
      <div className="portfolio-details">
        <h1>Events Panel</h1>
        <div className="admin_button-container">
          <button
            className={`admin_action-button ${activeButton === 'events' ? 'active' : ''}`}
            onClick={() => handleButtonClick('events')}
          >
            Show Admin Events
          </button>
          <button
            className={`admin_action-button ${activeButton === 'statistics' ? 'active' : ''}`}
            onClick={() => handleButtonClick('statistics')}
          >
            Statistics
          </button>
           <button
            className={`admin_action-button ${activeButton === 'userevents' ? 'active' : ''}`}
            onClick={() => handleButtonClick('userevents')}
            >
            Show User Events
          </button>
        </div>
        {showEvents && <AdminEvents />}
        {showAdminStatistics && <AdminStatistics />}
        {showUserEvents && <UserEvents />}
      </div>
    </div>
  );
};

export default AdminPageEvents;
