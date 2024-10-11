import React, { useState } from 'react';
import { addFunds } from '../../../services/WalletService';

const AddFundsForm = ({ onFundsAdded }) => {
  const [amount, setAmount] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    try {
      const response = await addFunds(amount);
      setMessage('Funds added successfully');
      setAmount('');
      onFundsAdded();
    } catch (error) {
      setError('Failed to add funds. Please try again.');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="add-funds-form">
      <div className="input-group">
        <label htmlFor="amount">Amount to Add</label>
        <input
          type="number"
          id="amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
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
