import React from 'react';
import AvailableCurrencies from '../../components/Currencies/AvailableCurrencies';
import '../../components/Currencies/AvailableCurrencies.css';



const CurrenciesPage = () => {
  return (
    <div className="currencies-page">
      <h1>Currencies</h1>
      <AvailableCurrencies />
    </div>
  );
};

export default CurrenciesPage;
