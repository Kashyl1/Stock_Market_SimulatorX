import React from 'react';
import AvailableCurrencies from '../../components/Currencies/AvailableCurrencies/AvailableCurrencies';
import '../../components/Currencies/AvailableCurrencies/AvailableCurrencies.css';



const CurrenciesPage = () => {
  return (
    <div className="currencies-page">
      <h1>Currencies</h1>
      <AvailableCurrencies />
    </div>
  );
};

export default CurrenciesPage;
