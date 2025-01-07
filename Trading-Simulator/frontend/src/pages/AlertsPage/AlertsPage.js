import React, { useState, useEffect } from 'react';
import Sidebar from '../Sidebar/Sidebar';
import EmailAlertList from '../../components/Alerts/EmailAlerts/EmailAlertList';
import TradeAlertList from '../../components/Alerts/TradeAlerts/TradeAlertList';
import CreateEmailAlertModal from '../../components/Alerts/EmailAlerts/CreateEmailAlertModal';
import CreateTradeAlertModal from '../../components/Alerts/TradeAlerts/CreateTradeAlertModal';
import { getAvailableAssets } from '../../services/CurrenciesService';
import { getUserAlerts } from '../../services/MailAlertService';
import { getUserTradeAlerts } from '../../services/TradeAlertService';
import './AlertsPage.css';

const AlertsPage = () => {
  const [activeTab, setActiveTab] = useState('email');

  const [showCreateEmailModal, setShowCreateEmailModal] = useState(false);
  const [emailAlerts, setEmailAlerts] = useState([]);
  const [emailLoading, setEmailLoading] = useState(true);
  const [emailError, setEmailError] = useState('');

  const [showCreateTradeModal, setShowCreateTradeModal] = useState(false);
  const [tradeAlerts, setTradeAlerts] = useState([]);
  const [tradeLoading, setTradeLoading] = useState(true);
  const [tradeError, setTradeError] = useState('');
  const [tradeCurrencies, setTradeCurrencies] = useState([]);

  const fetchEmailAlerts = async () => {
    setEmailLoading(true);
    try {
      const userEmailAlerts = await getUserAlerts();
      setEmailAlerts(userEmailAlerts);
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to fetch email alerts.';
      setEmailError(errorMessage);
    } finally {
      setEmailLoading(false);
    }
  };

const fetchTradeAlerts = async () => {
    setTradeLoading(true);
    try {
      const userTradeAlerts = await getUserTradeAlerts();
      setTradeAlerts(userTradeAlerts);
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to fetch trade orders.';
      setTradeError(errorMessage);
    } finally {
      setTradeLoading(false);
    }
  };

  const fetchTradeCurrencies = async () => {
    try {
      const availableCurrencies = await getAvailableAssets(0, 100);
      setTradeCurrencies(availableCurrencies.content);
    } catch (err) {
      console.error('Failed to fetch currencies for trade orders.');
    }
  };

  useEffect(() => {
    fetchEmailAlerts();
    fetchTradeAlerts();
    fetchTradeCurrencies();
  }, []);

  const handleOpenCreateEmailModal = () => {
    setShowCreateEmailModal(true);
  };

  const handleCloseCreateEmailModal = () => {
    setShowCreateEmailModal(false);
  };

  const handleEmailAlertCreated = () => {
    fetchEmailAlerts();
  };

  const handleEmailAlertDeactivated = () => {
    fetchEmailAlerts();
  };

  const handleEmailAlertDeleted = () => {
    fetchEmailAlerts();
  };

  const handleOpenCreateTradeModal = () => {
    setShowCreateTradeModal(true);
  };

  const handleCloseCreateTradeModal = () => {
    setShowCreateTradeModal(false);
    fetchTradeAlerts(); //to dosyć głupie obejście ale działa XD
  };

  const handleTradeAlertCreated = () => {
    fetchTradeAlerts();
  };

  const handleTradeAlertDeactivated = () => {
    fetchTradeAlerts();
  };

  const handleTradeAlertDeleted = () => {
    fetchTradeAlerts();
  };

  return (
    <div className="main-page">
      <Sidebar />
      <div className="alerts-page">
        <h1>Your Alerts</h1>
        <div className="tabs">
          <button
            className={activeTab === 'email' ? 'active' : ''}
            onClick={() => setActiveTab('email')}
          >
            Email Alerts
          </button>
          <button
            className={activeTab === 'trade' ? 'active' : ''}
            onClick={() => setActiveTab('trade')}
          >
            Trade Orders
          </button>
        </div>

        {activeTab === 'email' && (
          <>
            <button onClick={handleOpenCreateEmailModal} className="create-alert-button">
              Create New Email Order
            </button>
            {showCreateEmailModal && (
              <CreateEmailAlertModal
                onClose={handleCloseCreateEmailModal}
                onAlertCreated={handleEmailAlertCreated}
              />
            )}
            <EmailAlertList
              alerts={emailAlerts}
              loading={emailLoading}
              error={emailError}
              onAlertDeactivated={handleEmailAlertDeactivated}
              onAlertDeleted={handleEmailAlertDeleted}
            />
          </>
        )}

        {activeTab === 'trade' && (
          <>
            <button onClick={handleOpenCreateTradeModal} className="create-trade-alert-button">
              Create New Trade Order
            </button>
            {showCreateTradeModal && (
              <CreateTradeAlertModal
                onClose={handleCloseCreateTradeModal}
                onTradeAlertCreated={handleTradeAlertCreated}
              />
            )}
            <TradeAlertList
              tradeAlerts={tradeAlerts}
              loading={tradeLoading}
              error={tradeError}
              onTradeAlertDeactivated={handleTradeAlertDeactivated}
              onTradeAlertDeleted={handleTradeAlertDeleted}
            />
          </>
        )}
      </div>
    </div>
  );
};

export default AlertsPage;
