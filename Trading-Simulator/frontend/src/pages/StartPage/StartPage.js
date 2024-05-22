import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import './StartPage.css';
import { initParticles } from '../../assets/customParticles';
import featureOneImage from '../../assets/StartPageFeatureOne.png';
import featureTwoImage from '../../assets/StartPageFeatureTwo.png';

const HomePage = () => {
  useEffect(() => {
    initParticles();
  }, []);

  return (
    <div className="home-page">
      <div className="static-background"></div>
      <header className="header">
        <h1>Welcome to Our Trading Platform</h1>
        <p>Start trading cryptocurrencies and stocks today!</p>
        <div className="button-container">
          <Link to="/login" className="button">Login</Link>
          <Link to="/register" className="button">Register</Link>
        </div>
      </header>
      <section className="features">
        <div className="feature">
          <img src={featureOneImage} alt="Feature 1" />
          <h2>Feature 1</h2>
          <p>Discover the world of cryptocurrencies. Our platform allows you to easily trade a variety of digital currencies, stay updated with market trends, and manage your portfolio efficiently. Start your journey in the crypto market today!</p>
        </div>
        <div className="feature">
          <img src={featureTwoImage} alt="Feature 2" />
          <h2>Feature 2</h2>
          <p>Experience seamless stock trading with our platform. Get access to a wide range of stocks, real-time data, and advanced trading tools. Enhance your trading strategies and make informed investment decisions. Join us and explore the stock market now!</p>
        </div>
      </section>
      <canvas className="background"></canvas>
    </div>
  );
};

export default HomePage;
