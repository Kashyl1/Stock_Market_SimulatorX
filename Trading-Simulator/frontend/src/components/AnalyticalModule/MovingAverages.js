import React, { useEffect, useState } from 'react';
import { fetchAnalyticsData } from '../../services/AnalyticalModuleService';
import './AnalyticalModule.css';

const MovingAverages = ({ currencyId, interval, currentPrice, onSummaryChange  }) => {
  const movingAverages = ['sma', 'ema'];
  const periods = [5, 10, 20, 50, 100, 200];
  const [data, setData] = useState([]);
  const [summary, setSummary] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

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

  const determineDecision = (maValue) => {
    if (currentPrice > maValue) return 'Buy';
    if (currentPrice < maValue) return 'Sell';
    return 'Neutral';
  };

  const fetchAllMovingAverages = async () => {
    try {
      setIsLoading(true);

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

      setData(groupedResults);
      setSummary(calculateSummary(groupedResults));
      onSummaryChange(calculateSummary(groupedResults));
    } catch (err) {
      console.error('Error fetching moving averages data:', err);
      setError('Failed to load moving averages data.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchAllMovingAverages();
  }, [currencyId, interval]);

  if (isLoading) {
    return (
      <div className="analytical-module">
        <p>Loading moving averages data...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="analytical-module">
        <p className="error-message">{error}</p>
      </div>
    );
  }

  return (
    <div className="analytical-module">
      <h2 className="table-title">Moving Averages</h2>

      <div className="summary">
        <p className="summary-decision">Summary: {summary.summaryDecision}</p>
        <p className="buy-count">Buy: {summary.buyCount}</p>
        <p className="sell-count">Sell: {summary.sellCount}</p>
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
          {data.map(({ period, values }) => (
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
