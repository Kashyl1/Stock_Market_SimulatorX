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
            const response = await verifyAccount(token);
            setMessage(response);
            setTimeout(() => navigate('/login'), 5000);
        } catch (error) {
            setMessage('Unable to verify. Please try again.');
            setTimeout(() => navigate('/login'), 5000);
        }
    };

    return (
        <div className="verification-container">
            <FontAwesomeIcon icon={faEnvelope} />
            <h2>Verification Status</h2>
            <p>{message}</p>
            <div className="link-prompt">
                If you didn't get redirected click on the link <Link to="/login">Sign In</Link>
            </div>
        </div>
    );
};

export default Verification;
