import React from 'react';
import AlertItem from './AlertItem';
import './AlertList.css';

const AlertList = ({ alerts, loading, error, onAlertDeactivated, onAlertDeleted }) => {
  if (loading) {
    return <p>Loading alerts...</p>;
  }

  if (error) {
    return <p className="error-message">{error}</p>;
  }

  if (alerts.length === 0) {
    return <p>You have no set alerts.</p>;
  }

  return (
    <div className="alert-list">
      {alerts.map((alert) => (
        <AlertItem
          key={alert.alertId}
          alertData={alert}
          onAlertDeactivated={onAlertDeactivated}
          onAlertDeleted={onAlertDeleted}
        />
      ))}
    </div>
  );
};

export default AlertList;
