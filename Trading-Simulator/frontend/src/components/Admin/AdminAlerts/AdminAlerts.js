import { useState, useEffect } from 'react';
import { getGlobalAlerts, deleteGlobalAlert } from '../../../services/AdminService';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({
  ripple: false,
});

const AdminAlerts = () => {
  const [allAlerts, setAllAlerts] = useState([]);
  const [filteredAlerts, setFilteredAlerts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [expandedRows, setExpandedRows] = useState({});
  const [pageSize] = useState(20);
  const [showModal, setShowModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');

  const fetchGlobalAlerts = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await getGlobalAlerts();
      setAllAlerts(response);
      setFilteredAlerts(response);
    } catch (err) {
      console.error('Error fetching global alerts:', err);
      setError(err.message || 'Unknown error occurred while fetching global alerts.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = (value) => {
    setSearchTerm(value);
    if (value) {
      const searchId = parseInt(value, 10);
      if (!isNaN(searchId)) {
        const matchedAlerts = allAlerts.filter((alert) => alert.globalAlertid === searchId);
        setFilteredAlerts(matchedAlerts);
      } else {
        setFilteredAlerts(allAlerts);
      }
    } else {
      setFilteredAlerts(allAlerts);
    }
  };

  const handleDeleteAlert = async (id) => {
    try {
      await deleteGlobalAlert(id);
      setFilteredAlerts((prev) => prev.filter((alert) => alert.globalAlertid !== id));
      setAllAlerts((prev) => prev.filter((alert) => alert.globalAlertid !== id));
      notyf.success(`Global alert with ID: ${id} deleted successfully!`);
    } catch (err) {
      console.error('Error deleting global alert:', err);
      notyf.error('Failed to delete alert. Please try again.');
    }
  };

  const openModal = (message) => {
    setModalMessage(message);
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setModalMessage('');
  };

  useEffect(() => {
    fetchGlobalAlerts();
  }, []);

  const startIndex = currentPage * pageSize;
  const endIndex = startIndex + pageSize;
  const alertsToDisplay = filteredAlerts.slice(startIndex, endIndex);

  const handleNextPage = () => {
    if (currentPage < Math.ceil(filteredAlerts.length / pageSize) - 1) {
      setCurrentPage((prev) => prev + 1);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 0) {
      setCurrentPage((prev) => prev - 1);
    }
  };

  const toggleRowExpansion = (id) => {
    setExpandedRows((prev) => ({
      ...prev,
      [id]: !prev[id],
    }));
  };

  if (loading) return <p>Loading data...</p>;
  if (error) return <p className="error">{error}</p>;

  return (
    <div>
      <h2>Global Alerts</h2>
      <div className="search-container">
        <input
          type="text"
          placeholder="Search by ID"
          onChange={(e) => handleSearchChange(e.target.value)}
          className="search-input"
        />
      </div>

      {alertsToDisplay.length > 0 ? (
        <div className="assets-table">
          <div className="table-header_admin_events">
            <div className="header-cell">ID</div>
            <div className="header-cell">Created At</div>
            <div className="header-cell">Scheduled For</div>
            <div className="header-cell">Active</div>
            <div className="header-cell">Actions</div>
          </div>

          <div className="table-body">
            {alertsToDisplay.map((alert) => (
              <div className="table-row_admin_events" key={alert.globalAlertid}>
                <div className="cell">{alert.globalAlertid}</div>
                <div className="cell">{new Date(alert.createdAt).toLocaleString()}</div>
                <div className="cell">
                  {alert.scheduledFor
                    ? new Date(alert.scheduledFor).toLocaleString()
                    : 'N/A'}
                </div>
                <div className="cell">{alert.active ? 'Yes' : 'No'}</div>
                <div className="cell">
                  <button
                    className="details-toggle-button"
                    onClick={() => openModal(alert.message)}
                  >
                    Show Message
                  </button>
                  <button
                    className="delete-button"
                    onClick={() => handleDeleteAlert(alert.globalAlertid)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <p>No alerts match your search.</p>
      )}

      <div className="pagination-controls">
        <button onClick={handlePrevPage} disabled={currentPage === 0}>
          Previous
        </button>
        <span>
          Page {currentPage + 1} of {Math.ceil(filteredAlerts.length / pageSize)}
        </span>
        <button
          onClick={handleNextPage}
          disabled={currentPage === Math.ceil(filteredAlerts.length / pageSize) - 1}
        >
          Next
        </button>
      </div>

      {showModal && (
        <div className="modal-admin-overlay">
          <div className="modal-admin-content">
            <h3>Alert Message</h3>
            <p>{modalMessage}</p>
            <button className="modal-close" onClick={closeModal}>
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminAlerts;
