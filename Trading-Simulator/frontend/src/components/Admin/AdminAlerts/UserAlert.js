import React, { useEffect, useState } from 'react';
import { fetchGlobalAlert } from '../../../services/AdminService';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faExclamationTriangle } from '@fortawesome/free-solid-svg-icons';
import './UserAlert.css';

const UserAlert = () => {
    const [alertData, setAlertData] = useState(null);

    useEffect(() => {
        const checkAndFetchAlert = async () => {
            try {
                const alert = await fetchGlobalAlert();
                if (alert) {
                    const scheduledDate = new Date(alert.scheduledFor);
                    const currentDate = new Date();
                    const twoWeeksFromNow = new Date(currentDate);
                    twoWeeksFromNow.setDate(currentDate.getDate() + 14);

                    if (currentDate <= scheduledDate && scheduledDate <= twoWeeksFromNow) {
                        setAlertData(alert);
                    }
                }
            } catch (error) {
                if (error.response && error.response.status === 404) {
                    console.warn("No alerts found (404). Ignoring.");
                } else {
                    console.error("Error fetching alert:", error);
                }
            }
        };

        checkAndFetchAlert();
    }, []);

    if (!alertData) {
        return null;
    }

    const formattedDate = new Date(alertData.scheduledFor).toLocaleDateString('en-GB', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    });

    return (
        <div className="user-alert">
            <FontAwesomeIcon icon={faExclamationTriangle} className="user-alert-icon" />
            <div className="user-alert-text">
                <p className="user-alert-date">{alertData.message}</p>
                <p className="user-alert-date">Date of start: {formattedDate}</p>
            </div>
        </div>
    );
};

export default UserAlert;
