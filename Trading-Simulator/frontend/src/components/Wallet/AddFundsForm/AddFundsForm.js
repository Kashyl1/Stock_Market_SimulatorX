import React, { useState } from 'react';
import { addFunds } from '../../../services/WalletService';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({
  ripple: false,
});

const AddFundsForm = ({ onFundsAdded }) => {
  const [amount, setAmount] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

const handleSubmit = async (e) => {
  e.preventDefault();


  if (!amount || parseFloat(amount) <= 0) {
    notyf.error('Amount must be greater than zero.');
    return;
  }

  try {
    const response = await addFunds(amount);
    notyf.success({
      message: 'Funds added successfully',
    });
    setAmount('');
    onFundsAdded();
  } catch (error) {
    let errorMessage = 'Failed to add funds. Please try again.';
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message;
    } else if (error.response?.data) {
      const errors = Object.values(error.response.data).join(' ');
      if (errors) {
        errorMessage = errors;
      }
    }
    notyf.error({
      message: errorMessage,
    });
  }
};

  return (
    <form onSubmit={handleSubmit} className="user-settings-form">
      <div >
        <input className="search-input"
          type="number"
          id="amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="Amount to add..."
          required
        />
      </div>
      <button type="submit">Add Funds</button>
      {message && <p className="success-message">{message}</p>}
      {error && <p className="error-message">{error}</p>}
    </form>
  );
};

export default AddFundsForm;
