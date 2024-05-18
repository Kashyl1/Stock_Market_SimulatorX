import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import '../AuthForm.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faLock, faEnvelope } from '@fortawesome/free-solid-svg-icons';

const LoginForm = ({ setIsLoggedIn }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errormessage, setErrorMessage] = useState('');

    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            const response = await axios.post('/api/auth/authenticate', { email, password });
            if (response.data.token) {
                localStorage.setItem('jwtToken', response.data.token);
                setIsLoggedIn(true);
                navigate('/demo');
            } else if (response.data.message) {
                setErrorMessage(response.data.message);
            }
        } catch (error) {
            setErrorMessage("Wrong email or password!");
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-container">
                <FontAwesomeIcon icon={faUser} size="3x" className="user-icon" />
                <h2 className="auth-header">USER LOGIN</h2>
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
                    <div className="form-footer">
                        <button type="submit">Login</button>
                        <div className="link-prompt">
                            <p>Don't have an account? <Link to="/register">Sign up</Link></p>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default LoginForm;
