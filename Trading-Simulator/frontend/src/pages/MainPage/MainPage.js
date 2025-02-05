import { Link } from 'react-router-dom';
import './MainPage.css';
import UserAlert from '../../components/Admin/AdminAlerts/UserAlert';
import Sidebar from '../../pages/Sidebar/Sidebar';
import WalletCard from '../../components/Wallet/BalanceDisplay/WalletCard';
import CountCard from '../../components/Wallet/BalanceDisplay/CountCard';
import Top3 from '../../components/Wallet/BalanceDisplay/Top3';

const MainPage = () => {
  return (
    <div className="main-page">
        <Sidebar />
        <div className="portfolio-details">
        <h1>Main Page </h1>
        <UserAlert />
        <div className="tiles">
        <WalletCard/>
        <CountCard/>
        <Top3/>
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
                     </div>

                     <div className="about-us__section">
                         <h2>Navigation Menu Overview</h2>
                         <ul>
                             <li><strong>Home:</strong> Represented by a home icon, this is the central navigation hub. It provides access to the main account dashboard, where users can view basic information.</li>
                             <li><strong>Crypto:</strong> Represented by a coin icon, this section focuses on cryptocurrency analysis and market overview. Users can monitor prices, market trends, and technical parameters of various assets.</li>
                             <li><strong>Wallet:</strong> Represented by a wallet icon, this section allows users to manage their cryptocurrency holdings. It provides access to balance and deposit operations.</li>
                             <li><strong>Portfolios:</strong> Represented by a briefcase icon, this section grants access to a list of investment portfolios. Users can manage diverse investment strategies from here.</li>
                             <li><strong>Notifications:</strong> Represented by a bell icon, this section you can create email alerts or buy/sell orders.</li>
                             <li><strong>History:</strong> Represented by a clock icon, this section provides an overview of the user's transaction history, detailing all past operations conducted on the platform.</li>
                             <li><strong>User Settings:</strong> Represented by a gear icon, this section allows users to customize their account settings, update personal information or change passwords.</li>
                         </ul>
                     </div>

                 </div>
        </div>
    </div>

  );
};

export default MainPage;