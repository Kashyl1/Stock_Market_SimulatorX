import React from 'react';

const PrivacyPolicy = () => {
  return (
    <div className="main-page">
      <div className="portfolio-details">
        <h1>Privacy Policy & Disclaimer</h1>
        <div id="about-us" className="about-us">

          <div className="about-us__section">
            <h2>No Financial Advice</h2>
            <p>
              The information provided on this platform is for informational and educational purposes only.
              We do not provide financial, investment, tax, or legal advice.
              Any financial decisions made based on our content are solely at the user's own discretion and risk.
            </p>
          </div>

          <div className="about-us__section">
            <h2>Investment Risks</h2>
            <p>
              Investing in cryptocurrencies and other financial assets carries significant risks.
              Prices of digital assets are highly volatile and may result in financial loss.
              Our analytical module is designed to help users track trends but does not guarantee future performance.
              Users should conduct their own research and consult with professional financial advisors before making any investment decisions.
            </p>
          </div>

          <div className="about-us__section">
            <h2>Liability Disclaimer</h2>
            <p>
              We are not responsible for any losses, damages, or financial consequences resulting from the use of our platform.
              By using this site, you acknowledge that all investment decisions are made at your own risk.
            </p>
          </div>

        </div>
      </div>
    </div>
  );
};

export default PrivacyPolicy;
