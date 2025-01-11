import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getAvailableAssets } from '../../../services/CurrenciesService';
import { getUserPortfolios } from '../../../services/PortfolioService';
import debounce from 'lodash.debounce';
import Charts from '../../Charts/Charts';
import './AvailableCurrencies.css';

const AvailableCurrencies = () => {
  const [portfolios, setPortfolios] = useState([]);
  const [showCharts, setShowCharts] = useState(false);
  const [selectedCurrencyForChart, setSelectedCurrencyForChart] = useState(null);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const size = 50;

  React.useEffect(() => {
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

  const {
    data,
    isLoading,
    error: queryError,
    refetch,
  } = useQuery({
    queryKey: ['availableAssets', page],
    queryFn: () => getAvailableAssets(page, size),
    refetchInterval: 2000,
    keepPreviousData: true,
  });

  const handleNextPage = () => {
    if (data && page < data.totalPages - 1) {
      setPage((prevPage) => prevPage + 1);
    }
  };

  const handlePrevPage = () => {
    if (page > 0) {
      setPage((prevPage) => prevPage - 1);
    }
  };

  const handleViewChart = (currency) => {
    setSelectedCurrencyForChart(currency);
    setShowCharts(true);
  };

  const debouncedSetSearchTerm = debounce((value) => {
    setSearchTerm(value);
  }, 300);

  const handleSearchChange = (e) => {
    debouncedSetSearchTerm(e.target.value);
  };

  const formatLargeNumber = (num) => {
    if (num === undefined || num === null || isNaN(num)) return 'N/A';

    if (num >= 1e12) return (num / 1e12).toFixed(2) + 'T';
    if (num >= 1e9) return (num / 1e9).toFixed(2) + 'B';
    if (num >= 1e6) return (num / 1e6).toFixed(2) + 'M';
    if (num >= 1e3) return (num / 1e3).toFixed(2) + 'K';
    return num.toFixed(0);
  };

  const formatPrice = (price) => {
    if (price === undefined || price === null || isNaN(price)) return 'N/A';
    if (price >= 1) return price.toFixed(2);
    if (price >= 0.01) return price.toFixed(4);
    if (price >= 0.0001) return price.toFixed(6);
    if (price >= 0.00000001) return price.toFixed(8);
    return price.toFixed(12);
  };

  if (isLoading) {
    return <p>Loading...</p>;
  }

  if (error || queryError) {
    return <p className="error-message">{error || queryError.message}</p>;
  }

  if (showCharts && selectedCurrencyForChart) {
    return (
      <Charts
        currencyId={selectedCurrencyForChart.id}
        currencySymbol={selectedCurrencyForChart.symbol}
        portfolios={portfolios}
        onClose={() => setShowCharts(false)}
        currentPrice={selectedCurrencyForChart.price_in_usd}
      />
    );
  }

  const filteredCurrencies = data?.content.filter((currency) =>
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
        <div className="scroll-container">
          <div className="table-header">
            <div className="header-cell">Name</div>
            <div className="header-cell">Price</div>
            <div className="header-cell">Change (24h)</div>
            <div className="header-cell">Volume (24h)</div>
            <div className="header-cell">Market cap</div>
            <div className="header-cell">Action</div>
          </div>
          {filteredCurrencies?.map((currency, index) => (
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
              <div className="cell">${formatLargeNumber(currency.volume_24h)}</div>
              <div className="cell">${formatLargeNumber(currency.market_cap)}</div>
              <div className="cell">
                <button className="action-button" onClick={() => handleViewChart(currency)}>
                  Buy/Chart
                </button>
              </div>
            </div>
          ))}
        </div>

        <div className="pagination-controls">
          <button onClick={handlePrevPage} disabled={page === 0}>
            Previous
          </button>
          <span>Page {page + 1}</span>
          <button onClick={handleNextPage} disabled={data && page >= data.totalPages - 1}>
            Next
          </button>
        </div>
      </div>
    </div>
  );
};

export default AvailableCurrencies;
