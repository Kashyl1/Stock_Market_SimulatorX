import React from 'react';
import EmailAlertItem from './EmailAlertItem';
import './EmailAlertList.css';

const EmailAlertList = ({ alerts, loading, error, onAlertDeactivated, onAlertDeleted }) => {
  if (loading) {
    return <p>Loading email alerts...</p>;
  }

  if (error) {
    return <p className="error-message">{error}</p>;
  }

  if (alerts.length === 0) {
    return <p>You have no set email alerts.</p>;
  }

  return (
    <div className="email-alert-list">
      {alerts.map((alert) => (
        <EmailAlertItem
          key={alert.alertId}
          alertData={alert}
          onAlertDeactivated={onAlertDeactivated}
          onAlertDeleted={onAlertDeleted}
        />
      ))}
    </div>
  );
};

export default EmailAlertList;
