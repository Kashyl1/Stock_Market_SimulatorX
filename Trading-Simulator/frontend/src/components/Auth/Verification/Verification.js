import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { verifyAccount } from '../../../services/AuthService';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEnvelope } from '@fortawesome/free-solid-svg-icons';
import './Verification.css';
import '../AuthForm.css';
import { Link } from 'react-router-dom';

const Verification = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const [message, setMessage] = useState('');
    const verificationAttempted = useRef(false);

    useEffect(() => {
        const token = searchParams.get('token');
        if (token && !verificationAttempted.current) {
            verificationAttempted.current = true;
            verifyAccountHandler(token);
        }
    }, [searchParams]);

    const verifyAccountHandler = async (token) => {
        try {
            await verifyAccount(token);
            setMessage('Verification successful! Redirecting to login...');
           // setTimeout(() => navigate('/login'), 5000);
        } catch (error) {
            setMessage(error.message || 'Unable to verify. Please try again.');
           // setTimeout(() => navigate('/login'), 5000);
        }
    };

 return (
     <div className="verification-page">
         <div className="static-background"></div>
         <div className="verification-container">
             <FontAwesomeIcon icon={faEnvelope} />
             <h2>Verification Status</h2>
             <div className="message-link-container">
                 <p>{message}</p>
                 <Link to="/login" className="resend-button">
                     If you didn't get redirected click on the link
                 </Link>
             </div>
         </div>
     </div>
 );
};

export default Verification;
