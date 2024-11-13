import React, { useState } from 'react';
import { createPortfolio } from '../../../services/PortfolioService';
import '../Portfolios.css';
import './CreatePortfolio.css';

const CreatePortfolio = ({ onPortfolioCreated }) => {
  const [name, setName] = useState('');
  const [error, setError] = useState('');

  const handleCreatePortfolio = async (e) => {
    e.preventDefault();
    try {
      const newPortfolio = await createPortfolio(name);
      setName('');
      setError('');
      onPortfolioCreated(newPortfolio);
    } catch (err) {
      let errorMessage = 'Failed to create portfolio. Please try again.';
      if (err.response?.data?.message) {
        errorMessage = err.response.data.message;
      } else if (err.response?.data) {
        const errors = Object.values(err.response.data).join(' ');
        if (errors) {
          errorMessage = errors;
        }
      }
      setError(errorMessage);
    }
  };

  return (
    <form onSubmit={handleCreatePortfolio} className="create-portfolio-form">
      <h2>Create New Portfolio</h2>
      <input
        type="text"
        placeholder="Portfolio Name"
        value={name}
        onChange={(e) => setName(e.target.value)}
        required
        className="portfolio-input"
      />
      <button type="submit" className="portfolio-button">
        Create Portfolio
      </button>
      {error && <p className="error-message">{error}</p>}
    </form>
  );
};

export default CreatePortfolio;
