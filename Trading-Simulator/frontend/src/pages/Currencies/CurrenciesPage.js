import React from 'react';
import AvailableCurrencies from '../../components/Currencies/AvailableCurrencies/AvailableCurrencies';
import '../../components/Currencies/AvailableCurrencies/AvailableCurrencies.css';
import Sidebar from '../../pages/Sidebar/Sidebar';


const CurrenciesPage = () => {
  return (

  <div className="main-page">
   <Sidebar />
     <div className="currencies-page">
       <h1>Currencies</h1>
       <AvailableCurrencies />
     </div>
    </div>
  );
};

export default CurrenciesPage;
