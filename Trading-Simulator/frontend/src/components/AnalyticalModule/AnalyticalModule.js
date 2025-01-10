import React, { useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { fetchAnalyticsData } from '../../services/AnalyticalModuleService';
import './AnalyticalModule.css';

const AnalyticalModule = ({ currencyId, interval, onSummaryChange, currentPrice }) => {
  const indicators = ['adx', 'bp', 'rsi', 'macd', 'cci', 'atr', 'williamsR', 'volatility'];
  const [summary, setSummary] = useState({});

  const indicatorNames = {
    adx: 'ADX',
    bp: 'Bull/Bear Power(13)',
    williamsR: 'Williams %R',
    macd: 'MACD',
    cci: 'CCI(20)',
    rsi: 'RSI(14)',
    volatility: 'Royal Coin Indicator',
    atr: 'ATR(14)',
  };

  const determineDecision = (indicator, value) => {
    if (indicator === 'macd' && typeof value === 'object') {
      const { macd, signalLine } = value;
      const diff = macd - signalLine;

      if (diff > 5) return 'Buy';
      if (diff > 1) return 'Buy';
      if (diff > -1) return 'Neutral';
      if (diff > -5) return 'Sell';
      return 'Sell';
    }

    switch (indicator) {
      case 'adx':
        if (value > 40) return 'Buy';
        if (value > 25) return 'Buy';
        if (value > 20) return 'Neutral';
        if (value > 10) return 'Sell';
        return 'Sell';

      case 'bp':
        if (value > 10) return 'Buy';
        if (value > 0) return 'Buy';
        if (value > -0.5) return 'Neutral';
        if (value > -10) return 'Sell';
        return 'Sell';

      case 'williamsR':
        if (value > -20) return 'Overbought';
        if (value < -80) return 'Oversold';
        if (value < -50) return 'Buy';
        return 'Neutral';

      case 'rsi':
        if (value < 30) return 'Oversell';
        if (value < 50) return 'Buy';
        if (value <= 70) return 'Neutral';
        return 'Overbought';

      case 'cci':
        if (value > 200) return 'Buy';
        if (value > 100) return 'Buy';
        if (value > -100) return 'Neutral';
        if (value > -200) return 'Sell';
        return 'Sell';

      case 'atr':
        if (!currentPrice || currentPrice <= 0) return 'Neutral';
        const relativeVolatility = (value / currentPrice) * 100;
        if (relativeVolatility > 1.75) return 'High Volatility';
        if (relativeVolatility > 0.75) return 'Moderate Volatility';
        return 'Low Volatility';

      case 'volatility':
        if (value === 'High Volatility') return 'High Volatility';
        if (value === 'Moderate Volatility') return 'Moderate Volatility';
        if (value === 'Low Volatility') return 'Low Volatility';
        return 'Neutral';

      default:
        return 'Neutral';
    }
  };

 const { data, isLoading, error } = useQuery({
  queryKey: ['analyticsData', currencyId, interval],
  queryFn: async () => {
    const results = await Promise.all(
      indicators.map((indicator) =>
        fetchAnalyticsData(indicator, currencyId, interval).then((value) => ({
          indicator,
          value,
          decision: determineDecision(indicator, value),
        }))
      )
    );
    const signalCounts = calculateSummary(results);
    setSummary(signalCounts);
    onSummaryChange(signalCounts);
    return results;
  },
  staleTime: 300000,
  refetchInterval: 10000,
});

  const calculateSummary = (results) => {
    const signalCounts = { Buy: 0, Sell: 0, Neutral: 0 };

    results.forEach((item) => {
      const decision = item.decision;
      if (['Buy', 'Sell', 'Neutral'].includes(decision)) {
        signalCounts[decision]++;
      }
    });

    return signalCounts;
  };

  if (isLoading) {
    return (
      <div className="analytical-module">
        <p>Loading indicators data...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="analytical-module">
        <p className="error-message">Failed to load analytical data.</p>
      </div>
    );
  }

  return (
    <div className="analytical-module">
      <h2 className="table-title">Technical Indicators</h2>
      <div className="summary">
        <p className="summary-decision">
          Summary: {summary.Buy > summary.Sell ? 'Buy' : summary.Sell > summary.Buy ? 'Sell' : 'Neutral'}
        </p>
        <p className="buy-count"><strong>Buy:</strong> {summary.Buy}</p>
        <p className="sell-count"><strong>Sell:</strong> {summary.Sell}</p>
        <p className="neutral-count"><strong>Neutral:</strong> {summary.Neutral}</p>
      </div>

      <table className="indicator-table">
        <thead>
          <tr>
            <th>Indicator</th>
            <th>Value</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {data.map((item, index) => (
            <tr key={index}>
              <td>{indicatorNames[item.indicator]}</td>
              <td>
                {item.indicator === 'macd' && typeof item.value === 'object'
                  ? `MACD: ${item.value.macd.toFixed(3)}, Signal: ${item.value.signalLine.toFixed(3)}`
                  : item.value !== undefined
                  ? item.value.toFixed(3)
                  : 'N/A'}
              </td>
              <td className={`decision ${item.decision.toLowerCase().replace(/\s+/g, '-')}`}>{item.decision}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AnalyticalModule;
