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
      <div className="assets-table">

        <div className="table-header">
          <div className="header-cell">Currency</div>
          <div className="header-cell">Initial Price</div>
          <div className="header-cell">Alert Type</div>
          <div className="header-cell">Target Price / Change</div>
          <div className="header-cell"></div>
          <div className="header-cell">Actions</div>
        </div>


        <div className="table-body">
          {alerts.map((alert) => (
            <EmailAlertItem
              key={alert.alertId}
              alertData={alert}
              onAlertDeactivated={onAlertDeactivated}
              onAlertDeleted={onAlertDeleted}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default EmailAlertList;
