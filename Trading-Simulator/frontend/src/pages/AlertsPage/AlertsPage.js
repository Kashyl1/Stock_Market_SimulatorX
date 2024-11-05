import React, { useState, useEffect } from 'react';
import Sidebar from '../Sidebar/Sidebar';
import AlertList from '../../components/Alerts/AlertList';
import CreateAlertModal from '../../components/Alerts/CreateAlertModal';
import { getAvailableAssets } from '../../services/CurrenciesService';
import { getUserAlerts } from '../../services/AlertService';
import './AlertsPage.css';

const AlertsPage = () => {
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [selectedCurrency, setSelectedCurrency] = useState(null);
  const [currencies, setCurrencies] = useState([]);
  const [error, setError] = useState('');

  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchCurrencies = async () => {
    try {
      const availableCurrencies = await getAvailableAssets(0, 100);
      setCurrencies(availableCurrencies.content);
    } catch (err) {
      setError('Failed to fetch currency list.');
    }
  };

  const fetchAlerts = async () => {
    setLoading(true);
    try {
      const userAlerts = await getUserAlerts();
      setAlerts(userAlerts);
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to fetch alerts.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCurrencies();
    fetchAlerts();
  }, []);

  const handleOpenCreateModal = (currency) => {
    setSelectedCurrency(currency);
    setShowCreateModal(true);
  };

  const handleCloseCreateModal = () => {
    setShowCreateModal(false);
    setSelectedCurrency(null);
  };

  const handleAlertCreated = (newAlert) => {
    fetchAlerts();
  };

  const handleAlertDeactivated = () => {
    fetchAlerts();
  };

  const handleAlertDeleted = () => {
    fetchAlerts();
  };

  return (
    <div className="main-page">
      <Sidebar />
      <div className="alerts-page">
        <h1>Your Price Alerts</h1>
        <button
          onClick={() => setShowCreateModal(true)}
          className="create-alert-button"
        >
          Create New Alert
        </button>
        {error && <p className="error-message">{error}</p>}
        <AlertList
          alerts={alerts}
          loading={loading}
          error={error}
          onAlertDeactivated={handleAlertDeactivated}
          onAlertDeleted={handleAlertDeleted}
        />
        {showCreateModal && !selectedCurrency && (
          <div className="select-currency-modal">
            <h2>Select Currency for Alert</h2>
            <div className="currency-list">
              {currencies.map((currency) => (
                <div key={currency.currencyid} className="currency-item">
                  <img
                    src={currency.image_url}
                    alt={currency.name}
                    className="currency-icon"
                  />
                  <span>
                    {currency.name} ({currency.symbol})
                  </span>
                  <button onClick={() => handleOpenCreateModal(currency)}>
                    Select
                  </button>
                </div>
              ))}
            </div>
            <button onClick={handleCloseCreateModal} className="close-button">
              Close
            </button>
          </div>
        )}
        {showCreateModal && selectedCurrency && (
          <CreateAlertModal
            currency={selectedCurrency}
            onClose={handleCloseCreateModal}
            onAlertCreated={handleAlertCreated}
          />
        )}
      </div>
    </div>
  );
};

export default AlertsPage;
