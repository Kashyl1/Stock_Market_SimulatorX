import React, { useState } from 'react';
import { createPortfolio } from '../../../services/PortfolioService';
import '../Portfolios.css';

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
      setError('Failed to create portfolio. Please try again.');
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
