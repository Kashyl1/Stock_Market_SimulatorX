import React, { useState, useEffect, useRef } from 'react';
import { useQuery } from 'react-query';
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


  const { data, isLoading, isError } = useQuery(
    ['availableAssets', page],
    () => getAvailableAssets(page, size),
    {
      refetchInterval: 300000,
      onError: () => {
        setError('Failed to fetch data.');
      },
    }
  );

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

  const handleBuySuccess = () => {
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

  if (isError || error) {
    return <p className="error-message">{error || 'Error fetching data.'}</p>;
  }


  const filteredCurrencies = data.content.filter((currency) =>
    currency.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

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
          <div className="header-cell">Change</div>
          <div className="header-cell">Market capitalization</div>
          <div className="header-cell">Volume (24h)</div>
          <div className="header-cell">Exchange</div>
        </div>
        {filteredCurrencies.map((currency, index) => (
          <div className="table-row" key={index}>
            <div className="cell currency-info">
              <img src={currency.image} alt={currency.name} className="currency-icon" />
              <div>
                <span className="currency-name">{currency.name}</span>
                <span className="currency-symbol">{currency.symbol.toUpperCase()}</span>
              </div>
            </div>
            <div className="cell">${currency.price_in_usd.toFixed(2)}</div>
            <div className="cell">
              <span
                className={`currency-change ${
                  currency.price_change_percentage_24h > 0 ? 'positive' : 'negative'
                }`}
              >
                {currency.price_change_percentage_24h > 0 ? '+' : ''}
                {currency.price_change_percentage_24h.toFixed(2)}%
              </span>
            </div>
            <div className="cell">${currency.market_cap}</div>
            <div className="cell">${currency.volume_24}</div>
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
            onBuySuccess={handleBuySuccess}
          />
        )}
      </div>
    </div>
  );
};

export default AvailableCurrencies;
