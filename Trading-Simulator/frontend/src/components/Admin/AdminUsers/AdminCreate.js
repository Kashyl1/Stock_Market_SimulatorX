import { useState } from 'react';
import { createAdmin } from '../../../services/AdminService';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({
  ripple: false,
});

const AdminCreate = () => {
  const [formData, setFormData] = useState({
    firstname: '',
    lastname: '',
    email: '',
    password: '',
    balance: 0.0,
    reservedBalance: 0.0,
  });

  const [responseMessage, setResponseMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setResponseMessage('');

    try {
      const response = await createAdmin(formData);
      notyf.success('Admin created successfully!');
      setFormData({
        firstname: '',
        lastname: '',
        email: '',
        password: '',
        balance: 0.0,
        reservedBalance: 0.0,
      });
    } catch (error) {
      setResponseMessage(
        error.response?.data?.message || 'An error occurred while creating the admin.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (

    <form onSubmit={handleSubmit} className="user-settings-form">
    <h2>Create Admin</h2>
      <div className="input-group-settings">
        <label htmlFor="firstname">First Name</label>
        <input
          type="text"
          id="firstname"
          name="firstname"
          value={formData.firstname}
          onChange={handleChange}
          required
        />
      </div>
      <div className="input-group-settings">
        <label htmlFor="lastname">Last Name</label>
        <input
          type="text"
          id="lastname"
          name="lastname"
          value={formData.lastname}
          onChange={handleChange}
          required
        />
      </div>
      <div className="input-group-settings">
        <label htmlFor="email">Email</label>
        <input
          type="email"
          id="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          required
        />
      </div>
      <div className="input-group-settings">
        <label htmlFor="password">Password</label>
        <input
          type="password"
          id="password"
          name="password"
          value={formData.password}
          onChange={handleChange}
          required
        />
      </div>
      {responseMessage && (
        <p className={responseMessage.includes('successfully') ? 'success-message' : 'error-message'}>
          {responseMessage}
        </p>
      )}
      <button type="submit" disabled={isLoading}>
        {isLoading ? 'Creating...' : 'Create Admin'}
      </button>
    </form>
  );
};

export default AdminCreate;
