import React, { useEffect, useState } from 'react';
import { getPortfolios, deletePortfolio, getPortfoliosByUser } from '../../../services/AdminService';
import Sidebar from '../../../pages/Sidebar/Sidebar';
import UserTransactions from '../../../components/Admin/AdminTransactions/UserTransactions';

const AdminPortfolios = ({ userId }) => {
  const [allPortfolios, setAllPortfolios] = useState([]);
  const [filteredPortfolios, setFilteredPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
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

  const handleSearchChange = (term) => {
    setSearchTerm(term);
    const lowerCaseTerm = term.toLowerCase();

    const filtered = allPortfolios.filter((portfolio) =>
      portfolio.portfolioid.toString().includes(lowerCaseTerm)
    );

    setFilteredPortfolios(filtered.slice(0, pageSize));
    setCurrentPage(0);
    setTotalPages(Math.ceil(filtered.length / pageSize));
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
          <button
            onClick={() => setSelectedPortfolio(null)}

          >
            Back to Portfolios
          </button>
          </div>
          <h2>{selectedPortfolio.name}</h2>
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
                  onClick={() => setSelectedPortfolio(portfolio)}
                  className="button view-button"
                >
                  View Details
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
              className="pagination-button"
            >
              Previous
            </button>
            <span className="pagination-info">
              Page {currentPage + 1} of {totalPages}
            </span>
            <button
              onClick={handleNextPage}
              disabled={currentPage === totalPages - 1}
              className="pagination-button"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPortfolios;
