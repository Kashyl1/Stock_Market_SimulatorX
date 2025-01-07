import React, { useState } from 'react';
import { createPortfolio } from '../../../services/PortfolioService';
import '../Portfolios.css';
import './CreatePortfolio.css';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({
  ripple: false,
});

const CreatePortfolio = ({ onPortfolioCreated }) => {
  const [name, setName] = useState('');
  const [error, setError] = useState('');

  const handleCreatePortfolio = async (e) => {
    e.preventDefault();

    if (name.length > 12) {
      setError('Portfolio name cannot exceed 12 characters.');
      notyf.error('Portfolio name cannot exceed 12 characters.');
      return;
    }

    try {
      const newPortfolio = await createPortfolio(name);
      setName('');
      setError('');
      onPortfolioCreated(newPortfolio);
      notyf.success('Portfolio created successfully.');
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
      notyf.error(`Failed to create portfolio: ${errorMessage}`);
    }
  };

  const handleNameChange = (e) => {
    const newName = e.target.value;


    if (newName.length <= 12) {
      setName(newName);
      setError('');
    } else {
      setError('Portfolio name cannot exceed 12 characters.');
    }
  };

  return (
    <form onSubmit={handleCreatePortfolio} className="create-portfolio-form">
      <h2>Create New Portfolio</h2>
      <input
        type="text"
        placeholder="Portfolio Name"
        value={name}
        onChange={handleNameChange}
        required
        className="portfolio-input"
      />
      <button type="submit" className="portfolio-button" disabled={name.length > 12}>
        Create Portfolio
      </button>
      {error && <p className="error-message">{error}</p>}
    </form>
  );
};

export default CreatePortfolio;
