import { useState, useEffect } from 'react';
import { getUserEvents, deleteUserEvent } from '../../../services/AdminService';
import debounce from 'lodash.debounce';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({
  ripple: false,
});

const UserEvents = () => {
  const [allEvents, setAllEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [expandedRows, setExpandedRows] = useState({});
  const [pageSize] = useState(20);

  const fetchUserEvents = async () => {
    setLoading(true);
    setError(null);
    try {
      let page = 0;
      let allData = [];
      let response;
      do {
        response = await getUserEvents(page, pageSize);
        allData = [...allData, ...response.content];
        page++;
      } while (!response.last);

      setAllEvents(allData);
      setFilteredEvents(allData);
    } catch (err) {
      console.error('Error in fetchUserEvents:', err);
      setError(err.message || 'Unknown error occurred while fetching user events.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = debounce((value) => {
    setSearchTerm(value);
    if (value) {
      const lowerCaseTerm = value.toLowerCase();
      const matchedEvents = allEvents.filter(
        (event) =>
          event.id.toString().includes(lowerCaseTerm) ||
          event.email.toLowerCase().includes(lowerCaseTerm) ||
          event.eventType.toLowerCase().includes(lowerCaseTerm)
      );
      setFilteredEvents(matchedEvents);
    } else {
      setFilteredEvents(allEvents);
    }
  }, 300);

  const handleDeleteEvent = async (id) => {
    try {
      await deleteUserEvent(id);
      setFilteredEvents((prev) => prev.filter((event) => event.id !== id));
      setAllEvents((prev) => prev.filter((event) => event.id !== id));
      notyf.success(`Event with ID: ${id} deleted successfully!`);
    } catch (err) {
      console.error('Error deleting event:', err);
      notyf.error('Failed to delete event. Please try again.');
    }
  };

  const toggleRowExpansion = (id) => {
    setExpandedRows((prev) => ({
      ...prev,
      [id]: !prev[id],
    }));
  };

  useEffect(() => {
    fetchUserEvents();
  }, []);

  const startIndex = currentPage * pageSize;
  const endIndex = startIndex + pageSize;
  const eventsToDisplay = filteredEvents.slice(startIndex, endIndex);

  const handleNextPage = () => {
    if (currentPage < Math.ceil(filteredEvents.length / pageSize) - 1) {
      setCurrentPage((prev) => prev + 1);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 0) {
      setCurrentPage((prev) => prev - 1);
    }
  };

  if (loading) return <p>Loading data...</p>;
  if (error) return <p className="error">{error}</p>;

  return (
    <div>
      <h2>User Events</h2>
      <div className="search-container">
        <input
          type="text"
          placeholder="Search by ID"
          onChange={(e) => handleSearchChange(e.target.value)}
          className="search-input"
        />
      </div>

      {eventsToDisplay.length > 0 ? (
        <div className="assets-table">
          <div className="table-header_admin_events">
            <div className="header-cell">ID</div>
            <div className="header-cell">Email</div>
            <div className="header-cell">Event Type</div>
            <div className="header-cell">Event Time</div>
            <div className="header-cell">Actions</div>
          </div>

          <div className="table-body">
            {eventsToDisplay.map((event) => (
              <div className="table-row_admin_events" key={event.id}>
                <div className="cell">{event.id}</div>
                <div className="cell">{event.email}</div>
                <div className="cell">{event.eventType}</div>
                <div className="cell">{new Date(event.eventTime).toLocaleString()}</div>
                <div className="cell">
                  <button
                    className="details-toggle-button"
                    onClick={() => toggleRowExpansion(event.id)}
                  >
                    {expandedRows[event.id] ? 'Hide Details' : 'Show Details'}
                  </button>
                  <button
                    className="delete-button"
                    onClick={() => handleDeleteEvent(event.id)}
                  >
                    Delete
                  </button>
                  {expandedRows[event.id] && (
                    <div className="details">
                      <pre>{event.details ? JSON.stringify(JSON.parse(event.details), null, 2) : 'No details'}</pre>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <p>No events match your search.</p>
      )}

      <div className="pagination-controls">
        <button onClick={handlePrevPage} disabled={currentPage === 0}>
          Previous
        </button>
        <span>
          Page {currentPage + 1} of {Math.ceil(filteredEvents.length / pageSize)}
        </span>
        <button
          onClick={handleNextPage}
          disabled={currentPage === Math.ceil(filteredEvents.length / pageSize) - 1}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default UserEvents;
