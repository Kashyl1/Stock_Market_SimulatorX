import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../Portfolios.css';
import './PortfolioList.css';
import { deletePortfolio } from '../../../services/PortfolioService';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({ ripple: false });

const PortfolioList = ({ portfolios, onDeleteSuccess }) => {
  const navigate = useNavigate();
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);

  const formatDateTime = (dateTimeStr) => {
    if (!dateTimeStr) return 'N/A';
    const options = {
      year: 'numeric',
      month: 'numeric',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    };
    return new Date(dateTimeStr).toLocaleString(undefined, options);
  };

  const confirmDelete = (portfolio) => {
    setSelectedPortfolio(portfolio);
    setModalOpen(true);
  };

  const handleDeletePortfolio = async () => {
    if (!selectedPortfolio) return;
    try {
      await deletePortfolio(selectedPortfolio.portfolioid);
      notyf.success('Portfolio deleted successfully!');
      onDeleteSuccess(selectedPortfolio.portfolioid);
      setModalOpen(false);
    } catch (error) {
      console.error('Error deleting portfolio:', error);
      notyf.error('Failed to delete the portfolio.');
    }
  };

  return (
    <div className="portfolio-list">
      {portfolios.map((portfolio) => (
        <div key={portfolio.portfolioid} className="portfolio-card">
          <h3>{portfolio.name}</h3>
          <p>Created At: {formatDateTime(portfolio.createdAt)}</p>
          <p>Updated At: {formatDateTime(portfolio.updatedAt)}</p>
          <div className="portfolio-actions">
            <button
              onClick={() => navigate(`/portfolios/${portfolio.portfolioid}`)}
              className="view-button"
            >
              View Details
            </button>
            <button
              className="delete-button"
              onClick={() => confirmDelete(portfolio)}
            >
              Delete
            </button>
          </div>
        </div>
      ))}

      {modalOpen && (
        <div className="modal-overlay">
          <div className="modal">
            <h2>Confirm Deletion</h2>
            <p>Are you sure you want to delete "{selectedPortfolio?.name}"?</p>
            <div className="modal-actions">
              <button onClick={() => setModalOpen(false)} className="cancel-button">Cancel</button>
              <button onClick={handleDeletePortfolio} className="confirm-button">Delete</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PortfolioList;
