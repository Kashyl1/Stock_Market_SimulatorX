import { Link } from 'react-router-dom';
import './MainPage.css';
import BalanceDisplay from '../../components/Wallet/BalanceDisplay/BalanceDisplay';
import Sidebar from '../../pages/Sidebar/Sidebar';

const MainPage = () => {
  return (
    <div className="main-page">
        <Sidebar />
        <div className="content">
            <BalanceDisplay />
        </div>
    </div>

  );
};

export default MainPage;