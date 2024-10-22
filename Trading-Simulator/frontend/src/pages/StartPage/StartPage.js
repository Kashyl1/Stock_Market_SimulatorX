import React from 'react';
import { Link } from 'react-router-dom';
import './StartPage.css';
import CustomParticlesBackground from '../../assets/CustomParticlesBackground';
import iconOne from '../../assets/secure.png';
import iconTwo from '../../assets/tools.png';
import iconThree from '../../assets/experience.png';
import logo from '../../assets/stock_logov2.png';
// import video from '../../assets/Jano.mp4';
import exampleImageOne from '../../assets/gold_chart.png';
import exampleImageTwo from '../../assets/vision.png';

// Funkcja, która przesuwa widok o określony offset
function scrollToSection(event, offset) {
    event.preventDefault();
    const targetId = event.currentTarget.getAttribute("href").substring(1);
    const targetElement = document.getElementById(targetId);

    if (targetElement) {
        window.scrollTo({
            top: targetElement.offsetTop - offset,
            behavior: "smooth"
        });
    }
}


const HomePage = () => {
  return (
    <div className="home-page">
      <CustomParticlesBackground />
      <div className="static-background"></div>
      <div className="start_menu">
            <div className="logo-container">
              <img src={logo} alt="Logo" className="logo" />
            </div>
            <div className="links">
               <a href="#home" onClick={(e) => scrollToSection(e, 100)}>Home</a>
               <a href="#about-us" onClick={(e) => scrollToSection(e, 90)}>About us</a>
               <a href="#education" onClick={(e) => scrollToSection(e, 100)}>Education</a>
            </div>
          </div>
      <header id="home" className="header">
        <h1>Get started in crypto trading <span>without risking your own money</span></h1>
        <p>Sign up for a free Demo account today to gain real market experience with Demo funds!</p>
        <div className="button-container">
          <Link to="/login" className="button">Login</Link>
          <Link to="/register" className="button">Register</Link>
        </div>
      </header>
      <section className="features">
        <div className="feature">
          <div className="icon-container">
            <svg fill="#d4af37" width="60px" height="60px" viewBox="-4 -2 24 24" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMinYMin" className="jam jam-shield"><path d='M2 4.386V8a9.02 9.02 0 0 0 3.08 6.787L8 17.342l2.92-2.555A9.019 9.019 0 0 0 14 8V4.386l-6-2.25-6 2.25zM.649 2.756L8 0l7.351 2.757a1 1 0 0 1 .649.936V8c0 3.177-1.372 6.2-3.763 8.293L8 20l-4.237-3.707A11.019 11.019 0 0 1 0 8V3.693a1 1 0 0 1 .649-.936z'/></svg>
          </div>
          <h2>Learn in a safe environment</h2>
          <p>Demo Mode, also known as Paper trading, lets you try crypto trading with simulated funds. Sharpen your trading skills at your own pace and when you are ready, switch to Live trading.</p>
        </div>
        <div className="feature">
          <div className="icon-container">
            <svg width="60px" height="60px" viewBox="0 0 16 16" xmlns="http://www.w3.org/2000/svg" fill="#d4af37"><path fillRule="evenodd" clipRule="evenodd" d="M14.773 3.485l-.78-.184-2.108 2.096-1.194-1.216 2.056-2.157-.18-.792a4.42 4.42 0 0 0-1.347-.228 3.64 3.64 0 0 0-1.457.28 3.824 3.824 0 0 0-1.186.84 3.736 3.736 0 0 0-.875 1.265 3.938 3.938 0 0 0 0 2.966 335.341 335.341 0 0 0-6.173 6.234c-.21.275-.31.618-.284.963a1.403 1.403 0 0 0 .464.967c.124.135.272.247.437.328.17.075.353.118.538.127.316-.006.619-.126.854-.337 1.548-1.457 4.514-4.45 6.199-6.204.457.194.948.294 1.444.293a3.736 3.736 0 0 0 2.677-1.133 3.885 3.885 0 0 0 1.111-2.73 4.211 4.211 0 0 0-.196-1.378zM2.933 13.928a.31.31 0 0 1-.135.07.437.437 0 0 1-.149 0 .346.346 0 0 1-.144-.057.336.336 0 0 1-.114-.11c-.14-.143-.271-.415-.14-.568 1.37-1.457 4.191-4.305 5.955-6.046.1.132.21.258.328.376.118.123.245.237.38.341-1.706 1.75-4.488 4.564-5.98 5.994zm11.118-9.065c.002.765-.296 1.5-.832 2.048a2.861 2.861 0 0 1-4.007 0 2.992 2.992 0 0 1-.635-3.137A2.748 2.748 0 0 1 10.14 2.18a2.76 2.76 0 0 1 1.072-.214h.254L9.649 3.839v.696l1.895 1.886h.66l1.847-1.816v.258zM3.24 6.688h1.531l.705.717.678-.674-.665-.678V6.01l.057-1.649-.22-.437-2.86-1.882-.591.066-.831.849-.066.599 1.838 2.918.424.215zm-.945-3.632L4.609 4.58 4.57 5.703H3.494L2.002 3.341l.293-.285zm7.105 6.96l.674-.673 3.106 3.185a1.479 1.479 0 0 1 0 2.039 1.404 1.404 0 0 1-1.549.315 1.31 1.31 0 0 1-.437-.315l-3.142-3.203.679-.678 3.132 3.194a.402.402 0 0 0 .153.105.477.477 0 0 0 .359 0 .403.403 0 0 0 .153-.105.436.436 0 0 0 .1-.153.525.525 0 0 0 .036-.184.547.547 0 0 0-.035-.184.436.436 0 0 0-.1-.153L9.4 10.016z"/></svg>
          </div>
          <h2>Try different tools and strategies</h2>
          <p>As you develop or discover new strategies and techniques, it makes sense to test them first to see how these methods are likely to perform. Experiment with different instruments and advanced trading tools in a risk-free sandbox.</p>
        </div>
        <div className="feature">
           <div className="icon-container">
              <svg fill="#d4af37" width="60px" height="60px" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"><path d="M272.064 319.984H48c-17.68 0-32 14.32-32 32V992c0 17.68 14.32 32 32 32h224.064c17.68 0 32-14.32 32-32V351.984c0-17.68-14.32-32-32-32zm-32 640.016H80V383.984h160.064V960zm383.68-449.744h-224.08c-17.68 0-32 14.32-32 32V992c0 17.68 14.32 32 32 32h224.08c17.68 0 32-14.32 32-32V542.256c0-17.696-14.304-32-32-32zm-32 449.744h-160.08V574.256h160.08V960zM976 0H752.272c-17.68 0-32 14.32-32 32v960c0 17.68 14.32 32 32 32H976c17.68 0 32-14.32 32-32V32c0-17.68-14.32-32-32-32zm-32 960H784.272V64H944v896z"/></svg>
           </div>
          <h2>Get a live market experience</h2>
          <p>Trade crypto in real-time while using the virtual account, which has all the same market conditions as real trading on the platform. Our system will autonomously execute your orders, showing your actual trading results.</p>
        </div>
      </section>

       <div className="features-section">
            <div className="video-column">
              <video controls className="feature-video" width="1000" >
                Your browser does not support the video tag.
               </video>
            </div>
            <div className="info-column">
              <div className="feature-text">
                <h2>Easy to understand for newbies, yet advanced enough for experienced traders</h2>
                <p>
                  Whether you are a new or experienced cryptocurrency trader, the simulated exchange is simple to understand and use,
                  while keeping the advanced features that even the most experienced traders will find useful to improve their crypto trading.
                </p>
              </div>
            </div>
          </div>

         <div id="about-us" className="about-us">
             <div className="about-us__header">
                 <h1>About Royal Coin</h1>
                 <p>Your all-in-one solution for managing assets and tracking market trends.</p>
             </div>

             <div className="about-us__section about-us__offer-section">
                 <div className="about-us__text">
                     <h2>What We Offer?</h2>
                     <p>
                         Our application provides a comprehensive platform for managing investment portfolios with
                         variable-rate assets. Users can track past and current prices of assets, buy and sell with
                         instant or conditional orders (stop or limit), and access advanced analytics for trend-based
                         predictions.
                     </p>
                     <p>
                         The app is designed for investors, finance students, and individuals seeking alternative
                         income sources who want to manage investment assets, monitor prices, and experiment with
                         various investment strategies. Whether you are new to investing or a seasoned trader, our
                         app offers the tools you need to optimize your portfolio.
                     </p>
                 </div>
                 <div className="about-us__image">
                     <img src={exampleImageOne} alt="Investment Management" />
                 </div>
             </div>

             <div className="about-us__section">
                 <h2>Why Choose Our App?</h2>
                 <ul>
                     <li><strong>Real-time Asset Tracking:</strong> Stay updated with automatic asset price updates in real-time or at regular intervals.</li>
                     <li><strong>Comprehensive Transaction Management:</strong> Buy and sell assets with instant, stop, or limit orders, and keep track of all your past transactions.</li>
                     <li><strong>Portfolio Optimization Tools:</strong> Use capital allocation optimization and technical analysis tools to fine-tune your investment strategy.</li>
                     <li><strong>Advanced Alerts:</strong> Set price alerts and get notified about significant changes or emerging trends in the market.</li>
                     <li><strong>Cutting-edge Analytics:</strong> Leverage predictive models, such as ARIMA and technical indicators, to gain insights into future market movements.</li>
                     <li><strong>Reliable and Fast Performance:</strong> The app is built to handle thousands of users simultaneously, ensuring a smooth experience with response times of under two seconds.</li>
                 </ul>
             </div>
         </div>

        <div id="education" className="education">
            <h2>Education</h2>
            <p>Enhance your trading skills with these free resources:</p>
            <div className="tile-container">
                <div className="tile">
                    <a href="https://www.investopedia.com/terms/t/trading.asp" target="_blank" rel="noopener noreferrer">
                        <h3>Investopedia - Trading Basics</h3>
                        <p>Learn the essentials of trading with comprehensive articles and tutorials.</p>
                    </a>
                </div>
                <div className="tile">
                    <a href="https://www.coursera.org/learn/financial-markets" target="_blank" rel="noopener noreferrer">
                        <h3>Coursera - Financial Markets (Yale University)</h3>
                        <p>Explore financial markets in depth with a course from Yale University.</p>
                    </a>
                </div>
                <div className="tile">
                    <a href="https://www.udemy.com/course/stock-trading-101/" target="_blank" rel="noopener noreferrer">
                        <h3>Udemy - Stock Trading 101</h3>
                        <p>Get started with stock trading through beginner-friendly lessons.</p>
                    </a>
                </div>
                <div className="tile">
                    <a href="https://www.khanacademy.org/economics-finance-domain/core-finance/stock-and-bonds" target="_blank" rel="noopener noreferrer">
                        <h3>Khan Academy - Stocks and Bonds</h3>
                        <p>Understand the basics of stocks and bonds with these free educational videos.</p>
                    </a>
                </div>
                <div className="tile">
                    <a href="https://www.youtube.com/user/SMBCapital" target="_blank" rel="noopener noreferrer">
                        <h3>YouTube - SMB Capital Trading Videos</h3>
                        <p>Watch professional trading insights and strategies from SMB Capital.</p>
                    </a>
                </div>
            </div>
        </div>


    </div>



  );
};

export default HomePage;
