import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import './AdminAlertsCreate.css';
import { postGlobalAlert } from '../../../services/AdminService';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({
  ripple: false,
});

const AdminAlertsCreate = () => {
  const [message, setMessage] = useState('');
  const [scheduledFor, setScheduledFor] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(false);
    try {
      const alertBody = {
        message,
        scheduledFor: scheduledFor ? scheduledFor.toISOString() : null,
      };
      await postGlobalAlert(alertBody);
      setMessage('');
      setScheduledFor(null);
      notyf.success('Global alert created successfully!');
    } catch (err) {
      console.error('Error creating alert:', err);
      notyf.error('Failed to create alert. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="admin-alerts-container">
      <h2>Create Global Alert</h2>
      <form onSubmit={handleSubmit} className="alert-form">
        <div className="form-group">
          <label htmlFor="message">Alert Message:</label>
          <textarea
            id="message"
            className="message-input"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="date-picker">Scheduled For:</label>
          <DatePicker
            id="date-picker"
            className="date-picker-input"
            selected={scheduledFor}
            onChange={(date) => setScheduledFor(date)}
            showTimeSelect
            dateFormat="yyyy-MM-dd HH:mm"
            timeFormat="HH:mm"
            timeCaption="Time"
            placeholderText="Select a date and time"
          />
        </div>
        <div className="cell">
          <button className="create_alert-button" disabled={loading}>
            {loading ? 'Creating...' : 'Create Alert'}
          </button>
        </div>
      </form>
      {error && <p className="error-message">{error}</p>}
      {success && <p className="success-message">Alert created successfully!</p>}
    </div>
  );
};

export default AdminAlertsCreate;
