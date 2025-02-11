import React from 'react';
import './Footer.css';

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-content">
        <p>&copy; {new Date().getFullYear()} Royal Coin. All Rights Reserved.</p>
        <ul className="footer-links">
          <li><a href="/main">About Us</a></li>
          <li><a href="/contact">Contact</a></li>
          <li><a href="/privacypolicy">Privacy Policy</a></li>
        </ul>
      </div>
    </footer>
  );
};

export default Footer;
