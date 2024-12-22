import { useState, useEffect } from 'react';
import { getAdminActions, deleteAdminEvent } from '../../../services/AdminService';
import debounce from 'lodash.debounce';
import './AdminEvents.css';

const AdminActions = () => {
  const [allActions, setAllActions] = useState([]);
  const [filteredActions, setFilteredActions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [expandedRows, setExpandedRows] = useState({});
  const [pageSize] = useState(20);

  const fetchAllActions = async () => {
    setLoading(true);
    setError(null);
    try {
      let page = 0;
      let allData = [];
      let response;
      do {
        response = await getAdminActions(page, pageSize);
        allData = [...allData, ...response.content];
        page++;
      } while (!response.last);

      setAllActions(allData);
      setFilteredActions(allData);
    } catch (err) {
      console.error('Error in fetchAllActions:', err);
      setError(err.message || 'Unknown error occurred while fetching admin actions.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = debounce((value) => {
    setSearchTerm(value);
    if (value) {
      const lowerCaseTerm = value.toLowerCase();
      const matchedActions = allActions.filter((action) =>
        action.id.toString().includes(lowerCaseTerm)
      );
      setFilteredActions(matchedActions);
    } else {
      setFilteredActions(allActions);
    }
  }, 300);

  const handleDeleteEvent = async (id) => {
    try {
      await deleteAdminEvent(id);
      setFilteredActions((prev) => prev.filter((action) => action.id !== id));
      setAllActions((prev) => prev.filter((action) => action.id !== id));
      alert('Deleted event with Id: ',id);
    } catch (err) {
      console.error('Error deleting event:', err);
      alert('Failed to delete event. Please try again.');
    }
  };

  useEffect(() => {
    fetchAllActions();
  }, []);

  const startIndex = currentPage * pageSize;
  const endIndex = startIndex + pageSize;
  const actionsToDisplay = filteredActions.slice(startIndex, endIndex);

  const handleNextPage = () => {
    if (currentPage < Math.ceil(filteredActions.length / pageSize) - 1) {
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
      <h2>Admin Actions</h2>
      <div className="search-container">
        <input
          type="text"
          placeholder="Search by ID"
          onChange={(e) => handleSearchChange(e.target.value)}
          className="search-input"
        />
      </div>

      {actionsToDisplay.length > 0 ? (
        <div className="assets-table">
          <div className="table-header_admin_events">
            <div className="header-cell">ID</div>
            <div className="header-cell">Admin Email</div>
            <div className="header-cell">Event Type</div>
            <div className="header-cell">Event Time</div>
            <div className="header-cell">Details</div>
          </div>

          <div className="table-body">
            {actionsToDisplay.map((action) => (
              <div className="table-row_admin_events" key={action.id}>
                <div className="cell">{action.id}</div>
                <div className="cell">{action.adminEmail}</div>
                <div className="cell">{action.eventType}</div>
                <div className="cell">{new Date(action.eventTime).toLocaleString()}</div>
                <div className="cell">
                  <button
                    className="details-toggle-button"
                    onClick={() => toggleRowExpansion(action.id)}
                  >
                    {expandedRows[action.id] ? 'Hide Details' : 'Show Details'}
                  </button>
                  <button
                    className="delete-button"
                    onClick={() => handleDeleteEvent(action.id)}
                  >
                  Delete
                  </button>
                  {expandedRows[action.id] && (
                    <div className="details">
                      <pre>{action.details ? JSON.stringify(JSON.parse(action.details), null, 2) : 'No details'}</pre>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <p>No actions match your search.</p>
      )}

      <div className="pagination-controls">
        <button onClick={handlePrevPage} disabled={currentPage === 0}>
          Previous
        </button>
        <span>
          Page {currentPage + 1} of {Math.ceil(filteredActions.length / pageSize)}
        </span>
        <button
          onClick={handleNextPage}
          disabled={currentPage === Math.ceil(filteredActions.length / pageSize) - 1}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default AdminActions;
