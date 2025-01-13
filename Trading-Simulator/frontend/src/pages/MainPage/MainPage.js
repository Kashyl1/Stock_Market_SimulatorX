import { Link } from 'react-router-dom';
import './MainPage.css';
import UserAlert from '../../components/Admin/AdminAlerts/UserAlert';
import Sidebar from '../../pages/Sidebar/Sidebar';

const MainPage = () => {
  return (
    <div className="main-page">
        <Sidebar />
        <div className="portfolio-details">
        <h1>Main Page </h1>
        <UserAlert />
        </div>
    </div>

  );
};

export default MainPage;