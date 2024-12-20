import React, { useEffect, useState } from 'react';
import {
  getPortfolios,
  deletePortfolio,
  getPortfoliosByUser,
  updatePortfolioName,
} from '../../../services/AdminService';
import Sidebar from '../../../pages/Sidebar/Sidebar';
import UserTransactions from '../../../components/Admin/AdminTransactions/UserTransactions';
import  './AdminPortfloios.css';

const AdminPortfolios = ({ userId }) => {
  const [allPortfolios, setAllPortfolios] = useState([]);
  const [filteredPortfolios, setFilteredPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [editingName, setEditingName] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [editStatus, setEditStatus] = useState(null); 
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

  const handleEditPortfolio = async (portfolioId) => {
    if (!editingName.trim()) {
      setEditStatus({ success: false, message: 'Name cannot be empty.' });
      setTimeout(() => setEditStatus(null), 3000);
      return;
    }
    try {
      await updatePortfolioName(portfolioId, { name: editingName });
      fetchPortfolios();
      setSelectedPortfolio((prev) => ({ ...prev, name: editingName }));
      setEditStatus({ success: true, message: 'Portfolio name updated successfully!' });
    } catch (error) {
      console.error('Error updating portfolio:', error);
      setEditStatus({ success: false, message: 'Failed to update portfolio name.' });
    } finally {
      setTimeout(() => setEditStatus(null), 3000);
    }
  };

const handleSearchChange = (term) => {
  setSearchTerm(term);
  const lowerCaseTerm = term.toLowerCase();

  const exactMatches = allPortfolios.filter(
    (portfolio) => portfolio.portfolioid.toString() === lowerCaseTerm
  );

  const partialMatches = allPortfolios.filter(
    (portfolio) =>
      portfolio.portfolioid.toString().includes(lowerCaseTerm) &&
      portfolio.portfolioid.toString() !== lowerCaseTerm
  );

  const sortedResults = [...exactMatches, ...partialMatches];

  setFilteredPortfolios(sortedResults.slice(0, pageSize));
  setCurrentPage(0);
  setTotalPages(Math.ceil(sortedResults.length / pageSize));
};


  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      const nextPage = currentPage + 1;
      const startIndex = nextPage * pageSize;
      const endIndex = startIndex + pageSize;
      setFilteredPortfolios(allPortfolios.slice(startIndex, endIndex));
      setCurrentPage(nextPage);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 0) {
      const prevPage = currentPage - 1;
      const startIndex = prevPage * pageSize;
      const endIndex = startIndex + pageSize;
      setFilteredPortfolios(allPortfolios.slice(startIndex, endIndex));
      setCurrentPage(prevPage);
    }
  };

  const handleDeletePortfolio = async (portfolioId) => {
    const confirmDelete = window.confirm('Are you sure you want to delete this portfolio?');
    if (confirmDelete) {
      try {
        await deletePortfolio(portfolioId);
        fetchPortfolios();
      } catch (error) {
        console.error('Error deleting portfolio:', error);
      }
    }
  };

  useEffect(() => {
    fetchPortfolios();
  }, [userId]);

  if (isLoading) return <div className="loader">Loading...</div>;

  return (
    <div className="portfolio-container">
      {selectedPortfolio ? (
        <div className="portfolio-details">
          <div className="cell">
            <button onClick={() => setSelectedPortfolio(null)}>Back to Portfolios</button>
          </div>
          <h2>Edit Portfolio</h2>
          <div>
            <label htmlFor="portfolioName">Portfolio Name:</label>
            <input
              id="portfolioName"
              type="text"
              value={editingName || selectedPortfolio.name}
              onChange={(e) => setEditingName(e.target.value)}
              className="edit-input"
            />
            <button
             onClick={() => handleEditPortfolio(selectedPortfolio.portfolioid)}
             className="button edit-button"
             >
            Update Name
           </button>
                       {editStatus && (
                         <span
                           className={`edit-status ${editStatus.success ? 'success' : 'error'}`}
                         >
                           {editStatus.message}
                         </span>
                       )}
          </div>

          <div>
            <p>Created At: {new Date(selectedPortfolio.createdAt).toLocaleString()}</p>
            <p>Updated At: {new Date(selectedPortfolio.updatedAt).toLocaleString()}</p>
          </div>

          <UserTransactions portfolioId={selectedPortfolio.portfolioid} />
        </div>
      ) : (
        <div>
          <h2>List of Portfolios</h2>
          <div className="search-container">
            <input
              type="text"
              placeholder="Search by Portfolio ID"
              value={searchTerm}
              onChange={(e) => handleSearchChange(e.target.value)}
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
                      handleDeletePortfolio(portfolio.portfolioid);
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
         onClick={handlePrevPage}
         disabled={currentPage === 0}
         className="pagination-button">
          Previous
          </button>
          <span className="pagination-info">
          Page {currentPage + 1} of {totalPages}
          </span>
         <button
          onClick={handleNextPage}
          disabled={currentPage === totalPages - 1}
          className="pagination-button">
          Next
          </button>
        </div>

        </div>
      )}
    </div>
  );
};

export default AdminPortfolios;
