import React, { useState, useEffect } from 'react';
import { getAvailableAssets } from '../../../services/CurrenciesService';
import { getUserPortfolios } from '../../../services/PortfolioService';
import BuyAssetModal from '../../Transaction/BuyAssetModal/BuyAssetModal';
import debounce from 'lodash.debounce';
import { adjustSidebarHeight } from '../../../pages/Currencies/adjustSidebarHeight';
import './AvailableCurrencies.css';

const AvailableCurrencies = () => {
  const [portfolios, setPortfolios] = useState([]);
  const [selectedCurrency, setSelectedCurrency] = useState(null);
  const [showBuyModal, setShowBuyModal] = useState(false);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(50);
  const [data, setData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchPortfolios = async () => {
      try {
        const portfoliosData = await getUserPortfolios();
        setPortfolios(portfoliosData);
      } catch (err) {
        setError('Failed to fetch portfolios.');
      }
    };

    fetchPortfolios();
  }, []);

  const fetchAvailableAssets = async () => {
    setIsLoading(true);
    try {
      const availableAssets = await getAvailableAssets(page, size);
      setData(availableAssets.content);
      setIsLoading(false);
    } catch (err) {
        let errorMessage = 'Failed to fetch available assets.';
        if (err.response?.data?.message) {
          errorMessage = err.response.data.message;
        }
        setError(errorMessage);
        setIsLoading(false);
      }
  };

  useEffect(() => {
    fetchAvailableAssets();
    const interval = setInterval(fetchAvailableAssets, 60000);
    return () => clearInterval(interval);
  }, [page]);

  const handleNextPage = () => {
    if (data && page < data.totalPages - 1) {
      setPage(prevPage => prevPage + 1);
    }
  };

  const handlePrevPage = () => {
    if (page > 0) {
      setPage(prevPage => prevPage - 1);
    }
  };

  const handleBuyClick = (currency) => {
    setSelectedCurrency(currency);
    setShowBuyModal(true);
  };

  const handleCloseBuyModal = () => {
    setShowBuyModal(false);
    setSelectedCurrency(null);
  };

  const debouncedSetSearchTerm = debounce((value) => {
    setSearchTerm(value);
  }, 300);

  const handleSearchChange = (e) => {
    debouncedSetSearchTerm(e.target.value);
  };

  useEffect(() => {
    adjustSidebarHeight();
  }, [portfolios, searchTerm, page, data]);

  if (isLoading) {
    return <p>Loading...</p>;
  }

  if (error) {
    return <p className="error-message">{error}</p>;
  }

  const filteredCurrencies = data.filter((currency) =>
    currency.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const formatPrice = (price) => {
    if (price === undefined || price === null || isNaN(price)) return 'N/A';
    if (price >= 1) return price.toFixed(2);
    if (price >= 0.01) return price.toFixed(4);
    if (price >= 0.0001) return price.toFixed(6);
    if (price >= 0.00000001) return price.toFixed(8);
    return price.toFixed(12);
  };

  return (
    <div className="main-container">
      <div className="table-container">
        <h2 className="table-title">Available Currencies</h2>
        <input
          type="text"
          placeholder="Search for crypto..."
          onChange={handleSearchChange}
          className="search-input"
        />
        <div className="table-header">
          <div className="header-cell">Name</div>
          <div className="header-cell">Price</div>
          <div className="header-cell">Change (24h)</div>
          <div className="header-cell">Volume (24h)</div>
          <div className="header-cell">Action</div>
        </div>
        {filteredCurrencies.map((currency, index) => (
          <div className="table-row" key={index}>
            <div className="cell currency-info">
              <img src={currency.image_url} alt={currency.name} className="currency-icon" />
              <div>
                <span className="currency-name">{currency.name}</span>
                <span className="currency-symbol">{currency.id.toUpperCase()}</span>
              </div>
            </div>
            <div className="cell">${formatPrice(currency.price_in_usd)}</div>
            <div className="cell">
              <span
                className={`currency-change ${
                  currency.price_change_percent_24h > 0 ? 'positive' : 'negative'
                }`}
              >
                {currency.price_change_percent_24h > 0 ? '+' : ''}
                {currency.price_change_percent_24h.toFixed(2)}%
              </span>
            </div>
            <div className="cell">${formatPrice(currency.volume_24h)}</div>
            <div className="cell">
              <button onClick={() => handleBuyClick(currency)}>Buy</button>
            </div>
          </div>
        ))}
        <div className="pagination-controls">
          <button onClick={handlePrevPage} disabled={page === 0}>
            Previous
          </button>
          <span>Page {page + 1}</span>
          <button onClick={handleNextPage} disabled={data && page >= data.totalPages - 1}>
            Next
          </button>
        </div>
        {showBuyModal && selectedCurrency && (
          <BuyAssetModal
            currency={selectedCurrency}
            portfolios={portfolios}
            onClose={handleCloseBuyModal}
          />
        )}
      </div>
    </div>
  );
};

export default AvailableCurrencies;
