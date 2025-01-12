import React, { useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { fetchAnalyticsData, fetchCurrentPrice } from '../../services/AnalyticalModuleService';
import './AnalyticalModule.css';

const MovingAverages = ({ currencyId, interval, onSummaryChange }) => {
  const [currentPrice, setCurrentPrice] = useState(null);

  const movingAverages = ['sma', 'ema'];
  const periods = [5, 10, 20, 50, 100, 200];

  const determineDecision = (maValue) => {
    if (currentPrice > maValue) return 'Buy';
    if (currentPrice < maValue) return 'Sell';
    return 'Neutral';
  };

  const calculateSummary = (results) => {
    let buyCount = 0;
    let sellCount = 0;
    let neutralCount = 0;

    results.forEach((item) => {
      item.values.forEach(({ decision }) => {
        if (decision === 'Buy') buyCount++;
        if (decision === 'Sell') sellCount++;
        if (decision === 'Neutral') neutralCount++;
      });
    });

    let summaryDecision = 'Neutral';

    if (buyCount > sellCount && buyCount > neutralCount) {
      summaryDecision = 'Strong Buy';
    } else if (buyCount > sellCount) {
      summaryDecision = 'Buy';
    } else if (sellCount > buyCount && sellCount > neutralCount) {
      summaryDecision = 'Strong Sell';
    } else if (sellCount > buyCount) {
      summaryDecision = 'Sell';
    }

    return { summaryDecision, buyCount, sellCount, neutralCount };
  };

  const fetchMovingAverages = async () => {
    const results = await Promise.all(
      movingAverages.flatMap((maType) =>
        periods.map((period) =>
          fetchAnalyticsData(maType, currencyId, interval, period).then((value) => ({
            maType,
            period,
            value,
            decision: determineDecision(value),
          }))
        )
      )
    );

    const groupedResults = periods.map((period) => ({
      period,
      values: results.filter((item) => item.period === period),
    }));

    const summary = calculateSummary(groupedResults);
    onSummaryChange(summary);
    return { groupedResults, summary };
  };

  const { data, isLoading, error } = useQuery({
    queryKey: ['movingAverages', currencyId, interval],
    queryFn: fetchMovingAverages,
    staleTime: 300000,
    refetchInterval: 10000,
  });

  useEffect(() => {
    const fetchPrice = async () => {
      try {
        const price = await fetchCurrentPrice(currencyId);
        setCurrentPrice(price);
      } catch (err) {
        console.error('Error fetching current price:', err);
      }
    };

    const intervalId = setInterval(fetchPrice, 10000);

    return () => clearInterval(intervalId);
  }, [currencyId]);

  if (isLoading || currentPrice === null) {
    return (
      <div className="analytical-module">
        <p>Loading moving averages data...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="analytical-module">
        <p className="error-message">Failed to load moving averages data.</p>
      </div>
    );
  }

  const { groupedResults, summary } = data;

  return (
    <div className="analytical-module">
      <h2 className="table-title">Moving Averages</h2>

      <div className="summary">
        <p className="summary-decision">Summary: {summary.summaryDecision}</p>
        <p className="buy-count">Buy: {summary.buyCount}</p>
        <p className="sell-count">Sell: {summary.sellCount}</p>
        <p className="neutral-count">Neutral: {summary.neutralCount}</p>
      </div>

      <table className="indicator-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Simple</th>
            <th>Action</th>
            <th>Exponential</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {groupedResults.map(({ period, values }) => (
            <tr key={period}>
              <td>MA{period}</td>
              {values.map(({ maType, value, decision }) => (
                <React.Fragment key={maType}>
                  <td>{value.toFixed(3)}</td>
                  <td className={`decision ${decision.toLowerCase()}`}>{decision}</td>
                </React.Fragment>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default MovingAverages;
