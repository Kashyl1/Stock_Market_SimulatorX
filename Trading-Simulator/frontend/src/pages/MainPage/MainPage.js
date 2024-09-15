import React from 'react';
import { Link } from 'react-router-dom';


const MainPage = () => {
  return (
    <div>
      <Link to="/settings">Go to User Settings</Link>
      <Link to="/wallet">Go to Wallet</Link>
      <Link to="/currencies">Go to Crypto</Link>

    </div>
  );
};

export default MainPage;
