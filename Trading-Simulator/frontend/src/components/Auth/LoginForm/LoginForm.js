import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login, resendVerificationEmail } from '../../../services/AuthService';
import '../AuthForm.css';
import './LoginForm.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faLock, faEnvelope } from '@fortawesome/free-solid-svg-icons';
import logo from '../../../assets/stock_logov2.png';

const LoginForm = ({ setIsLoggedIn }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errormessage, setErrorMessage] = useState('');
    const [resendMessage, setResendMessage] = useState('');
    const [canResend, setCanResend] = useState(true);
    const [resendTimer, setResendTimer] = useState(0);

    useEffect(() => {
        let timer;
        if (resendTimer > 0) {
            timer = setTimeout(() => setResendTimer(resendTimer - 1), 1000);
        } else {
            setCanResend(true);
        }
        return () => clearTimeout(timer);
    }, [resendTimer]);

    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            const response = await login(email, password);
            if (response.token) {
                setIsLoggedIn(true);
                navigate('/main');
            } else {
                setErrorMessage(response.message);
                if (response.resend) {
                    setResendMessage('Your account is not verified. Please verify your account.');
                    setCanResend(false);
                    setResendTimer(60);
                }
            }
        } catch (error) {
            setErrorMessage('Wrong email or password!');
            setIsLoggedIn(false);
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
                setResendMessage('Failed to resend verification email. Please try again later.');
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
