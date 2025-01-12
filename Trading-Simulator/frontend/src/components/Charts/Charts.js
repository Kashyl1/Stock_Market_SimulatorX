import React, { useEffect, useState } from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  TimeScale,
  Tooltip,
  Legend,
} from 'chart.js';
import { CandlestickController, CandlestickElement } from 'chartjs-chart-financial';
import 'chartjs-adapter-date-fns';
import { Chart } from 'react-chartjs-2';
import { useQuery } from '@tanstack/react-query';
import { fetchChartData } from '../../services/ChartsService';
import { buyAsset } from '../../services/TransactionService';
import CreateTradeAlertModal from '../../components/Alerts/TradeAlerts/CreateTradeAlertModal';
import AnalyticalModule from '../../components/AnalyticalModule/AnalyticalModule';
import MovingAverages from '../../components/AnalyticalModule/MovingAverages';
import Summary from '../../components/AnalyticalModule/Summary';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';
import './Charts.css';

ChartJS.register(
  CategoryScale,
  LinearScale,
  TimeScale,
  CandlestickController,
  CandlestickElement,
  Tooltip,
  Legend
);

const Charts = ({ currencyId, currencySymbol, portfolios, onClose}) => {
  const [analyticalSummary, setAnalyticalSummary] = useState({});
  const [movingAveragesSummary, setMovingAveragesSummary] = useState({});

  const [selectedPortfolioId, setSelectedPortfolioId] = useState('');

  const [interval, setInterval] = useState('1m');
  const [buyType, setBuyType] = useState('USD');
  const [amountInUSD, setAmountInUSD] = useState('');
  const [amountOfCrypto, setAmountOfCrypto] = useState('');
  const [buyError, setBuyError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showCreateTradeModal, setShowCreateTradeModal] = useState(false);

   const handleSummaryChange = (summary, type) => {
      if (type === 'analytical') {
        setAnalyticalSummary(summary);
      } else if (type === 'movingAverages') {
        setMovingAveragesSummary(summary);
      }
    };

const getChartTitle = (interval, currencyId) => {
  switch (interval) {
    case '1m':
      return `One minute ${currencyId} Chart`;
    case '3m':
      return `Three minutes ${currencyId} Chart`;
    case '5m':
      return `Five minutes ${currencyId} Chart`;
    case '30m':
      return `Thirty minutes ${currencyId} Chart`;
    case '1h':
      return `One hour ${currencyId} Chart`;
    default:
      return 'Chart';
  }
};

 const { data: chartData, isLoading, isError } = useQuery({
   queryKey: ['chartData', currencyId, interval],
   queryFn: async () => {
     const data = await fetchChartData(currencyId, interval);
     const now = Date.now();

     let filteredData;
     if (interval === '1m') {
       filteredData = data.filter((item) => item.openTime >= now - 60 * 60 * 1000);
     } else if (interval === '3m') {
       filteredData = data.filter((item) => item.openTime >= now - 3 * 60 * 60 * 1000);
     } else if (interval === '5m') {
       filteredData = data.filter((item) => item.openTime >= now - 6 * 60 * 60 * 1000);
     } else if (interval === '30m') {
       filteredData = data.filter((item) => item.openTime >= now - 24 * 60 * 60 * 1000);
     } else if (interval === '1h') {
       filteredData = data.filter((item) => item.openTime >= now - 7 * 24 * 60 * 60 * 1000);
     }

     return filteredData.map((item) => ({
       x: item.openTime,
       o: item.openPrice,
       h: item.highPrice,
       l: item.lowPrice,
       c: item.closePrice,
     }));
   },
   staleTime: 300000,
   refetchInterval: 10000,
   retry: 1,
 });


  const handleIntervalChange = (newInterval) => {
    setInterval(newInterval);
  };

   const notyf = new Notyf({
     ripple: false,
   });

const handleBuy = async () => {
  if (!selectedPortfolioId) {
    notyf.error('Please select a portfolio.');
    return;
  }

  if (buyType === 'USD' && (!amountInUSD || parseFloat(amountInUSD) <= 0)) {
    notyf.error('Please enter a valid amount in USD.');
    return;
  }

  if (buyType === 'Crypto' && (!amountOfCrypto || parseFloat(amountOfCrypto) <= 0)) {
    notyf.error('Please enter a valid amount of cryptocurrency.');
    return;
  }

  const purchaseData = {
    portfolioId: selectedPortfolioId,
    currencyId,
    amountInUSD: buyType === 'USD' ? parseFloat(amountInUSD) : null,
    amountOfCrypto: buyType === 'Crypto' ? parseFloat(amountOfCrypto) : null,
  };

  setLoading(true);
  try {
    await buyAsset(
      purchaseData.portfolioId,
      purchaseData.currencyId,
      purchaseData.amountInUSD,
      purchaseData.amountOfCrypto
    );

    notyf.success({
      message: 'Asset purchased successfully',
    });

    setAmountInUSD('');
    setAmountOfCrypto('');
  } catch (err) {
    let errorMessage = 'An unexpected error occurred.';
    if (err.response?.data?.message) {
      errorMessage = err.response.data.message;
    }
    notyf.error({
      message: 'Failed to purchase asset. ' + errorMessage,
    });
  } finally {
    setLoading(false);
  }
};

  const timeUnit = interval === '1m' ? 'minute' :
                   interval === '3m' ? 'minute' :
                   interval === '5m' ? 'minute' :
                   interval === '30m' ? 'minute' : 'hour';

  const options = {
    responsive: true,
    plugins: {
      legend: { display: false },
      tooltip: { mode: 'index', intersect: false },
    },
    scales: {
      x: {
        type: 'time',
        time: {
          unit: timeUnit,
          displayFormats: {
            hour: 'HH:mm',
            minute: 'HH:mm',
          },
        },
        title: {
          display: true,
          text: 'Time',
          color: '#ffffff',
        },
        ticks: {
          color: '#ffffff',
          autoSkip: true,
          maxTicksLimit: 10,
        },
      },
      y: {
        beginAtZero: false,
        title: {
          display: true,
          text: 'Price',
          color: '#ffffff',
        },
        ticks: {
          color: '#ffffff',
        },
      },
    },
    elements: {
      candlestick: {
        color: {
          up: '#00ff00',
          down: '#ff0000',
        },
      },
    },
  };

  const data = {
    datasets: [
      {
        label: `${currencyId} Candlestick Chart`,
        data: chartData,
        borderColor: '#f0d90a',
        backgroundColor: '#f0d90a33',
      },
    ],
  };

 if (isLoading) return <p>Loading chart data...</p>;
 if (isError) return <p className="error-message">Failed to load chart data.</p>;


  const handleOpenCreateTradeModal = () => {
    setShowCreateTradeModal(true);
  };

  const handleCloseCreateTradeModal = () => {
    setShowCreateTradeModal(false);
  };


  return (
    <div className="charts-container">
       <h2 className="table-title">{getChartTitle(interval,currencyId)}</h2>
      <button className="close-charts" onClick={onClose}>
        Back
      </button>

      <button onClick={handleOpenCreateTradeModal} className="close-charts">
        Create New Trade Order
      </button>
        {showCreateTradeModal && (
        <CreateTradeAlertModal
        onClose={handleCloseCreateTradeModal}
        currencyIdProp={currencyId}
      />
      )}
      <div className="buy-form">
        <h3>Buy {currencySymbol}</h3>
        <div className="buy-type-selector">
          <label>
            <input
              type="radio"
              value="USD"
              checked={buyType === 'USD'}
              onChange={() => setBuyType('USD')}
            />
            USD
          </label>
          <label>
            <input
              type="radio"
              value="Crypto"
              checked={buyType === 'Crypto'}
              onChange={() => setBuyType('Crypto')}
            />
            {currencyId}
          </label>
        </div>

        {buyType === 'USD' && (
          <label>
            Amount in USD:
            <input
              type="number"
              value={amountInUSD}
              onChange={(e) => setAmountInUSD(e.target.value)}
              min="0"
              step="0.01"
            />
          </label>
        )}
        {buyType === 'Crypto' && (
          <label>
            Amount of {currencyId}:
            <input
              type="number"
              value={amountOfCrypto}
              onChange={(e) => setAmountOfCrypto(e.target.value)}
              min="0"
              step="0.0001"
            />
          </label>
        )}

        <label>
          Select Portfolio:
          <select
            value={selectedPortfolioId}
            onChange={(e) => setSelectedPortfolioId(e.target.value)}
          >
            <option value="">Select a portfolio</option>
            {portfolios.map((portfolio) => (
              <option key={portfolio.portfolioid} value={portfolio.portfolioid}>
                {portfolio.name}
              </option>
            ))}
          </select>
        </label>

        <button onClick={handleBuy} disabled={loading}>
          {loading ? 'Processing...' : 'Buy'}
        </button>

        {buyError && <p className="error-message">{buyError}</p>}
      </div>

      <div className="interval-switcher">
        {['1m', '3m', '5m', '30m', '1h'].map((int) => (
          <button
            key={int}
            onClick={() => handleIntervalChange(int)}
            className={`interval-button ${interval === int ? 'active' : ''}`}
          >
            {int.toUpperCase()}
          </button>
        ))}
      </div>

      <Chart type="candlestick" data={data} options={options} />
      <Summary  movingAveragesSummary={movingAveragesSummary} analyticalSummary={analyticalSummary} />
      <AnalyticalModule currencyId={currencyId} interval={interval} onSummaryChange={(summary) => handleSummaryChange(summary, 'analytical')} />
      <MovingAverages currencyId={currencyId} interval={interval} onSummaryChange={(summary) => handleSummaryChange(summary, 'movingAverages')} />
    </div>
  );
};

export default Charts;