import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { resetPassword } from '../../../services/AuthService';
import './ResetPasswordForm.css';

const ResetPasswordForm = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const query = new URLSearchParams(location.search);
    const token = query.get('token');

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError('');
        setMessage('');

        if (newPassword !== confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        if (!token) {
            setError('Invalid or missing token');
            return;
        }

        try {
            const response = await resetPassword(token, newPassword);
            setMessage('Password has been successfully reset. You will be redirected to login screen in 5 seconds...');
            setTimeout(() => navigate('/login'), 5000);
        } catch (err) {
            setError(err.message || 'Failed to reset password.');
        }
    };

    return (
        <div className="reset-password-page">
            <div className="reset-password-container">
                <h2>Reset Password</h2>
                <form onSubmit={handleSubmit} className="reset-password-form">
                    <div className="input-group">
                        <label htmlFor="newPassword">New Password</label>
                        <input
                            type="password"
                            id="newPassword"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            required
                        />
                    </div>
                    <div className="input-group">
                        <label htmlFor="confirmPassword">Confirm New Password</label>
                        <input
                            type="password"
                            id="confirmPassword"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />
                    </div>
                    {error && <div className="error-message">{error}</div>}
                    {message && <div className="success-message">{message}</div>}
                    <button type="submit">Reset Password</button>
                </form>
            </div>
        </div>
    );
};

export default ResetPasswordForm;
