import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { getAvailableAssets } from '../../../services/CurrenciesService';
import { getUserPortfolios } from '../../../services/PortfolioService';
import BuyAssetModal from '../../Transaction/BuyAssetModal/BuyAssetModal';
import { FixedSizeList as List } from 'react-window';
import debounce from 'lodash.debounce';
import './AvailableCurrencies.css';

const AvailableCurrencies = () => {
  const [portfolios, setPortfolios] = useState([]);
  const [selectedCurrency, setSelectedCurrency] = useState(null);
  const [showBuyModal, setShowBuyModal] = useState(false);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(50);

  const { data, isLoading, isError } = useQuery(
    ['availableAssets', page],
    () => getAvailableAssets(page, size),
    {
      refetchInterval: 300000,
      onSuccess: (data) => {
        getUserPortfolios()
          .then(setPortfolios)
          .catch(() => setError('Failed to fetch portfolios.'));
      },
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
    refetch();
  };

  const debouncedSetSearchTerm = debounce((value) => {
    setSearchTerm(value);
  }, 300);

  const handleSearchChange = (e) => {
    debouncedSetSearchTerm(e.target.value);
  };

  if (isLoading) {
    return <p>Loading...</p>;
  }

  if (isError || error) {
    return <p className="error-message">{error || 'Error fetching data.'}</p>;
  }

  const filteredCurrencies = data.content.filter((currency) =>
    currency.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const Row = ({ index, style }) => {
    const currency = filteredCurrencies[index];
    return (
      <div style={style} className="currency-card">
        <img src={currency.image} alt={`${currency.name} icon`} />
        <h2>{currency.name}</h2>
        <p className="currency-price">${currency.price_in_usd}</p>
        <p
          className={`currency-change ${
            currency.price_change_percentage_24h > 0 ? 'positive' : 'negative'
          }`}
        >
          {currency.price_change_percentage_24h > 0 ? '+' : ''}
          {currency.price_change_percentage_24h}%
        </p>
        <button onClick={() => handleBuyClick(currency)}>Buy</button>
      </div>
    );
  };

  return (
    <div>
      <h2 className="available-cryptocurrencies">Explore the economics of cryptocurrencies</h2>

      <div className="search-container">
        <span className="search-icon">&#128269;</span>
        <input
          type="text"
          placeholder="Search"
          onChange={handleSearchChange}
          className="search-input"
        />
      </div>

      <div className="currency-list">
        <List
          height={600}
          itemCount={filteredCurrencies.length}
          itemSize={300}
          width={'100%'}
        >
          {Row}
        </List>
      </div>

      <div className="pagination-controls">
        <button onClick={handlePrevPage} disabled={page === 0}>
          Previous
        </button>
        <span> Page {page + 1} z {data.totalPages} </span>
        <button onClick={handleNextPage} disabled={page >= data.totalPages - 1}>
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
  );
};

export default AvailableCurrencies;
