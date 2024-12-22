import { useState } from 'react';
import AdminSidebar from '../../components/Admin/AdminSidebar';
import AdminAlerts from '../../components/Admin/AdminAlerts/AdminAlerts';
import AdminAlertsCreate from '../../components/Admin/AdminAlerts/AdminAlertsCreate';

const AdminPageAlerts = () => {
  const [activeButton, setActiveButton] = useState('Alerts');
  const [showAlerts, setShowAlerts] = useState(false);
  const [showAdminAlertsCreate, setShowAdminAlertsCreate] = useState(false);

  const handleButtonClick = (buttonName) => {
    setActiveButton(buttonName);
    if (buttonName === 'Alerts') {
      setShowAlerts(true);
      setShowAdminAlertsCreate(false);
    } else if (buttonName === 'AlertsCreate') {
      setShowAlerts(false);
      setShowAdminAlertsCreate(true);
    }
  };

  return (
    <div className="main-page">
      <AdminSidebar />
      <div className="portfolio-details">
        <h1>Alerts Panel</h1>
        <div className="admin_button-container">
          <button
            className={`admin_action-button ${activeButton === 'Alerts' ? 'active' : ''}`}
            onClick={() => handleButtonClick('Alerts')}
          >
            Show Admin Alerts
          </button>
          <button
            className={`admin_action-button ${activeButton === 'AlertsCreate' ? 'active' : ''}`}
            onClick={() => handleButtonClick('AlertsCreate')}
          >
            Create Global Alert
          </button>
        </div>
        {showAlerts && <AdminAlerts />}
        {showAdminAlertsCreate && <AdminAlertsCreate />}
      </div>
    </div>
  );
};

export default AdminPageAlerts;
