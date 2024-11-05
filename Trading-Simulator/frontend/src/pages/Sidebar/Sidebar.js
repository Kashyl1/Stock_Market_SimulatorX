import React from 'react';
import { Link } from 'react-router-dom';
import './Sidebar.css';
import logo from '../../assets/stock_logov2.png';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHome, faCoins, faWallet, faBriefcase, faCog } from '@fortawesome/free-solid-svg-icons';


const Sidebar = () => {
    return (
            <div className="sidebar_main">
                    <div className="logo-container">
                        <Link to="/main">
                            <img src={logo} alt="Logo" className="logo" />
                        </Link>
                    </div>
                <div className="menu-links">
                    <Link to="/main">
                        <FontAwesomeIcon icon={faHome} /> Main Page
                    </Link>
                    <Link to="/currencies">
                        <FontAwesomeIcon icon={faCoins} /> Crypto
                    </Link>
                    <Link to="/wallet">
                        <FontAwesomeIcon icon={faWallet} /> Wallet
                    </Link>
                    <Link to="/portfolios">
                        <FontAwesomeIcon icon={faBriefcase} /> Go to Portfolios
                    </Link>
                    <Link to="/settings">
                        <FontAwesomeIcon icon={faCog} /> User Settings
                    </Link>
                    <Link to="/alerts">(ikonka dzwonka? XD) Alerty Wrzuciłem to tutaj, ale możesz walnąć to gdzie indziej jak masz wizje</Link>
                </div>
            </div>
    );
};

export default Sidebar;
