import React from 'react';

const Contact = () => {
  return (
    <div className="main-page">
      <div className="portfolio-details">
        <h1>Contact</h1>
        <div id="about-us" className="about-us">

          <div className="about-us__section">
            <h2>Meet the Developers</h2>
            <ul>
              <li>
                <strong>Kamil Babiński</strong> - Backend Developer<br />
                <a href="https://github.com/Kashyl1" target="_blank" rel="noopener noreferrer">
                  GitHub: https://github.com/Kashyl1
                </a>
              </li>
              <li>
                <strong>Jan Chodorowski</strong> - Frontend Developer<br />
                <a href="https://github.com/DrogerPL" target="_blank" rel="noopener noreferrer">
                  GitHub: https://github.com/DrogerPL
                </a>
              </li>
            </ul>
          </div>

          <div className="about-us__section">
            <h2>Get in Touch</h2>
            <p>If you have any questions or want to collaborate, feel free to reach out.</p>
            <p>Email: <a href="mailto:babinskikamil8@gmail.com">babinskikamil8@gmail.com</a></p>
            <p>Email: <a href="mailto:jan.chodorowski02@wp.pl">jan.chodorowski02@wp.pl</a></p>
          </div>

        </div>
      </div>
    </div>
  );
};

export default Contact;
