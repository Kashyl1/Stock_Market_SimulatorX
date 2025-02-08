import React, { useEffect, useState } from 'react';
import {
  getPortfolios,
  deletePortfolio,
  getPortfoliosByUser,
  updatePortfolioName,
} from '../../../services/AdminService';
import Sidebar from '../../../pages/Sidebar/Sidebar';
import UserTransactions from '../../../components/Admin/AdminTransactions/UserTransactions';
import './AdminPortfloios.css';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({ ripple: false });

const AdminPortfolios = ({ userId }) => {
  const [allPortfolios, setAllPortfolios] = useState([]);
  const [filteredPortfolios, setFilteredPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [editingName, setEditingName] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [portfolioToDelete, setPortfolioToDelete] = useState(null);
  const pageSize = 20;

  const fetchPortfolios = async () => {
    setIsLoading(true);
    let allData = [];
    let page = 0;

    try {
      while (true) {
        const data = userId
          ? await getPortfoliosByUser(userId, page, pageSize)
          : await getPortfolios(page, pageSize);

        allData = [...allData, ...data.content];
        if (page >= data.totalPages - 1) break;
        page++;
      }

      const uniquePortfolios = Array.from(new Map(allData.map((p) => [p.portfolioid, p])).values());

      setAllPortfolios(uniquePortfolios);
      setFilteredPortfolios(uniquePortfolios.slice(0, pageSize));
      setTotalPages(Math.ceil(uniquePortfolios.length / pageSize));
    } catch (error) {
      console.error('Error fetching portfolios:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const confirmDelete = (portfolio) => {
    setPortfolioToDelete(portfolio);
    setModalOpen(true);
  };

  const handleDeletePortfolio = async () => {
    if (!portfolioToDelete) return;
    try {
      await deletePortfolio(portfolioToDelete.portfolioid);
      notyf.success('Portfolio deleted successfully!');
      fetchPortfolios();
      setModalOpen(false);
    } catch (error) {
      console.error('Error deleting portfolio:', error);
      notyf.error('Failed to delete the portfolio.');
    }
  };

  useEffect(() => {
    fetchPortfolios();
  }, [userId]);

  if (isLoading) return <div className="loader">Loading...</div>;

  return (
    <div className="portfolio-container">
      <h2>List of Portfolios</h2>
      <div className="search-container">
        <input
          type="text"
          placeholder="Search by Portfolio ID"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
      </div>
      <div className="portfolio-grid">
        {filteredPortfolios.map((portfolio) => (
          <div key={portfolio.portfolioid} className="portfolio-card-custom">
            <h3 className="portfolio-title">{portfolio.name}</h3>
            <p className="portfolio-id">Portfolio ID: {portfolio.portfolioid}</p>
            <div className="portfolio-buttons">
              <button
                onClick={() => {
                  setSelectedPortfolio(portfolio);
                  setEditingName(portfolio.name);
                }}
                className="button view-button"
              >
                View/Edit Details
              </button>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  confirmDelete(portfolio);
                }}
                className="button delete-button"
              >
                Delete Portfolio
              </button>
            </div>
          </div>
        ))}
      </div>

      <div className="pagination-controls">
        <button
          onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 0))}
          disabled={currentPage === 0}
          className="pagination-button"
        >
          Previous
        </button>
        <span className="pagination-info">
          Page {currentPage + 1} of {totalPages}
        </span>
        <button
          onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages - 1))}
          disabled={currentPage === totalPages - 1}
          className="pagination-button"
        >
          Next
        </button>
      </div>

      {modalOpen && (
        <div className="modal-overlay">
          <div className="modal">
            <h2>Confirm Deletion</h2>
            <p>Are you sure you want to delete "{portfolioToDelete?.name}"?</p>
            <div className="modal-actions">
              <button onClick={() => setModalOpen(false)} className="cancel-button">
                Cancel
              </button>
              <button onClick={handleDeletePortfolio} className="confirm-button">
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPortfolios;
