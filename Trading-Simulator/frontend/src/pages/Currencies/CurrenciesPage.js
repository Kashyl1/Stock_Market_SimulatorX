import React from 'react';
import AvailableCurrencies from '../../components/Currencies/AvailableCurrencies';

const CurrenciesPage = () => {
  return (
    <div className="currencies-page">
      <h1>Currencies</h1>
      <AvailableCurrencies />
    </div>
  );
};

export default CurrenciesPage;
