import React from 'react';
import TransactionHistory from '../components/TransactionHistory/TransactionHistory';
import './Dashboard.css';

const Dashboard = () => {
  return (
    <div className="dashboard">
      <h1>Dashboard</h1>
      <TransactionHistory />
    </div>
  );
};

export default Dashboard;
