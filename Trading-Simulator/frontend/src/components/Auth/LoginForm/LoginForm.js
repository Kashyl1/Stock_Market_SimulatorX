import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login, resendVerificationEmail } from '../../../services/AuthService';
import '../AuthForm.css';
import './LoginForm.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEnvelope, faLock } from '@fortawesome/free-solid-svg-icons';
import logo from '../../../assets/stock_logov2.png';

const LoginForm = ({ setIsLoggedIn }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errormessage, setErrorMessage] = useState('');
    const [resendMessage, setResendMessage] = useState('');
    const [canResend, setCanResend] = useState(false);
    const [resendTimer, setResendTimer] = useState(0);

    useEffect(() => {
        let timer;
        if (resendTimer > 0) {
            timer = setTimeout(() => setResendTimer(resendTimer - 1), 1000);
        } else if (resendTimer === 0 && !canResend) {
            setCanResend(true);
        }
        return () => clearTimeout(timer);
    }, [resendTimer, canResend]);

    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        setErrorMessage('');
        setResendMessage('');
        try {
            const response = await login(email, password);
            if (response.token) {
                setIsLoggedIn(true);
                navigate('/main');
            }
        } catch (error) {
            setIsLoggedIn(false);
            if (error.status === 403 && error.message === 'User is not verified') {
                setErrorMessage('Your account is not verified.');
                setResendMessage('Please verify your account.');
                setCanResend(true);
                setResendTimer(60);
            } else if (error.status === 401 && error.message === 'Invalid email or password') {
                setErrorMessage('Wrong email or password!');
            } else {
                setErrorMessage(error.message || 'An unexpected error occurred.');
            }
        }
    };

    const handleResendVerification = async () => {
        if (canResend) {
            try {
                const response = await resendVerificationEmail(email);
                if (response.success) {
                    setResendMessage('Verification email has been resent. Please check your inbox.');
                    setCanResend(false);
                    setResendTimer(60);
                } else {
                    setResendMessage('Failed to resend verification email. Please try again later.');
                }
            } catch (error) {
                let errorMessage = 'Failed to resend verification email. Please try again later.';
                if (error.response?.data?.message) {
                    errorMessage = error.response.data.message;
                }
                setResendMessage(errorMessage);
                setCanResend(false);
                setResendTimer(60);
            }
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-container">
                <img src={logo} alt="Logo" className="logo_login" />
                <h2 className="auth-header">User login</h2>
                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="input-group">
                        <FontAwesomeIcon icon={faEnvelope} className="input-icon" />
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Email"
                        />
                    </div>
                    <div className="input-group">
                        <FontAwesomeIcon icon={faLock} className="input-icon" />
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Password"
                        />
                    </div>
                    {errormessage && <div className="error-message">{errormessage}</div>}
                    {resendMessage && (
                        <div className="resend-container">
                            <p>{resendMessage}</p>
                            <button onClick={handleResendVerification} className="resend-button" disabled={!canResend}>
                                {canResend ? 'Resend Verification Email' : `Wait ${resendTimer}s`}
                            </button>
                        </div>
                    )}
                    <div className="form-footer">
                        <button type="submit">Login</button>
                        <Link to="/register" className="link-prompt">
                            <p>Don't have an account? Sign up</p>
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default LoginForm;
