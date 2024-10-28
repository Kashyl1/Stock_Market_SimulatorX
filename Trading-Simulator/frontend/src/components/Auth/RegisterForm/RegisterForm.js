import React, { useState, useEffect } from 'react';
import { register, resendVerificationEmail } from '../../../services/AuthService';
import { Link } from 'react-router-dom';
import './RegisterForm.css';
import '../AuthForm.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faLock, faEnvelope } from '@fortawesome/free-solid-svg-icons';
import logo from '../../../assets/stock_logov2.png';

const RegisterForm = () => {
    const [formData, setFormData] = useState({
        firstname: '',
        lastname: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [errors, setErrors] = useState({});
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

    const validateForm = () => {
        const newErrors = {};

        if (!formData.firstname.trim()) {
            newErrors.firstname = 'First name is required';
        }

        if (!formData.lastname.trim()) {
            newErrors.lastname = 'Last name is required';
        }

        if (!formData.email.trim()) {
            newErrors.email = 'Email is required';
        } else {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(formData.email)) {
                newErrors.email = 'The email address is invalid';
            }
        }

        if (!formData.password) {
            newErrors.password = 'Password is required';
        } else {
            if (formData.password.length < 8) {
                newErrors.password = 'Password must be at least 8 characters long';
            }
            if (!/[A-Z]/.test(formData.password)) {
                newErrors.password = 'Password must contain at least one uppercase letter';
            }
            if (!/\d/.test(formData.password)) {
                newErrors.password = 'Password must contain at least one digit';
            }
        }

        if (!formData.confirmPassword) {
            newErrors.confirmPassword = 'Please confirm your password';
        } else if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = 'Passwords do not match';
        }

        setErrors(newErrors);

        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setErrors({});

        if (!validateForm()) {
            return;
        }

        try {
            await register(formData.firstname, formData.lastname, formData.email, formData.password);
            setIsSubmitted(true);
        } catch (error) {
            let errorMessage = 'An error occurred during registration. Please try again later.';
            if (error.response?.status === 409) {
                errorMessage = 'This email is already registered.';
            } else if (error.response?.status === 400) {
                errorMessage = 'Please ensure all fields are correctly filled.';
            } else if (error.response?.status === 500) {
                errorMessage = 'Internal server error. Please try again later.';
            }

            setErrors({ form: errorMessage });
        }
    };

    const handleChange = (event) => {
        const { id, value } = event.target;
        setFormData({ ...formData, [id]: value });
    };

    const handleResendVerification = async () => {
        if (canResend) {
            try {
                const response = await resendVerificationEmail(formData.email);
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

    if (isSubmitted) {
        return (
            <div className="verification-page">
                <div className="verification-container">
                    <FontAwesomeIcon icon={faEnvelope} size="2x" />
                    <h2>Verify Your Email</h2>
                    <p>Please check your email for a link to verify your email address.</p>
                    <p>Once verified, you'll be able to sign in.</p>
                    {resendMessage && <p>{resendMessage}</p>}
                    <div className="verification-container_flex">
                        <button onClick={handleResendVerification} className="resend-button" disabled={!canResend}>
                            {canResend ? 'Resend verification email' : `Wait ${resendTimer}s `}
                        </button>
                        <Link to="/login" className="link-prompt_verification">
                            <p>Already have an account?</p>
                        </Link>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="auth-page">
            <div className="auth-container">
                <img src={logo} alt="Logo" className="logo_login" />
                <h2 className="auth-header">Create your account</h2>
                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="input-group">
                        <FontAwesomeIcon icon={faUser} className="input-icon" />
                        <input
                            id="firstname"
                            type="text"
                            value={formData.firstname}
                            onChange={handleChange}
                            placeholder="First Name"
                            className={errors.firstname ? 'error-input' : ''}
                        />
                        {errors.firstname && <div className="error-message">{errors.firstname}</div>}
                    </div>
                    <div className="input-group">
                        <FontAwesomeIcon icon={faUser} className="input-icon" />
                        <input
                            id="lastname"
                            type="text"
                            value={formData.lastname}
                            onChange={handleChange}
                            placeholder="Last Name"
                            className={errors.lastname ? 'error-input' : ''}
                        />
                        {errors.lastname && <div className="error-message">{errors.lastname}</div>}
                    </div>
                    <div className="input-group">
                        <FontAwesomeIcon icon={faEnvelope} className="input-icon" />
                        <input
                            id="email"
                            type="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="Email"
                            className={errors.email ? 'error-input' : ''}
                        />
                        {errors.email && <div className="error-message">{errors.email}</div>}
                    </div>
                    <div className="input-group">
                        <FontAwesomeIcon icon={faLock} className="input-icon" />
                        <input
                            id="password"
                            type="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Password"
                            className={errors.password ? 'error-input' : ''}
                        />
                        {errors.password && <div className="error-message">{errors.password}</div>}
                    </div>
                    <div className="input-group">
                        <FontAwesomeIcon icon={faLock} className="input-icon" />
                        <input
                            id="confirmPassword"
                            type="password"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            placeholder="Confirm Password"
                            className={errors.confirmPassword ? 'error-input' : ''}
                        />
                        {errors.confirmPassword && <div className="error-message">{errors.confirmPassword}</div>}
                    </div>
                    {errors.form && <div className="error-message">{errors.form}</div>}
                    <div className="form-footer">
                        <button type="submit">Register</button>
                        <Link to="/login" className="link-prompt">
                            <p>Already have an account? Sign In</p>
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default RegisterForm;
