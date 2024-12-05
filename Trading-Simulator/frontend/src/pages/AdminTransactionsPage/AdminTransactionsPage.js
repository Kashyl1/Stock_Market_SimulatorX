import { useState } from 'react';
import AdminSidebar from '../../components/Admin/AdminSidebar';
import AdminTransactions from '../../components/Admin/AdminTransactions/AdminTransactions';
import AdminTransactionsByUser from '../../components/Admin/AdminTransactions/AdminTransactionsByUser';
import AdminSuspiciousTransactions from '../../components/Admin/AdminTransactions/AdminSuspiciousTransactions';

const AdminTransactionsPage = () => {
  const [activeButton, setActiveButton] = useState('Transactions');
  const [showTransactions, setShowTransactions] = useState(false);
  const [showTransactionsByUser, setShowTransactionsByUser] = useState(false);
  const [showSuspiciousTransactions, setShowSuspiciousTransactions] = useState(false);

const handleButtonClick = (buttonName) => {
  setActiveButton(buttonName);
  setShowTransactions(buttonName === 'Transactions');
  setShowTransactionsByUser(buttonName === 'TransactionsByUser');
  setShowSuspiciousTransactions(buttonName === 'SuspiciousTransactions');
};

  return (
    <div className="main-page">
      <AdminSidebar />
      <div className="portfolio-details">
        <h1>Transactions Panel</h1>
        <div className="admin_button-container">
          <button
            className={`admin_action-button ${activeButton === 'Transactions' ? 'active' : ''}`}
            onClick={() => handleButtonClick('Transactions')}
          >
            Show Transactions
          </button>
          <button
            className={`admin_action-button ${activeButton === 'TransactionsByUser' ? 'active' : ''}`}
            onClick={() => handleButtonClick('TransactionsByUser')}
          >
            Transactions By User
          </button>
          <button
            className={`admin_action-button ${activeButton === 'SuspiciousTransactions' ? 'active' : ''}`}
            onClick={() => handleButtonClick('SuspiciousTransactions')}
          >
            Get Suspicious Transactions
          </button>
        </div>
        {showTransactions && <AdminTransactions />}
        {showTransactionsByUser && <AdminTransactionsByUser />}
        {showSuspiciousTransactions && <AdminSuspiciousTransactions />}
      </div>
    </div>
  );
};

export default AdminTransactionsPage;
