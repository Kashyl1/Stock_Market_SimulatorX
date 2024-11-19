import { useState, useEffect } from 'react';
import { getUsers } from '../../services/AdminService';

const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUsers = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await getUsers(0, 20);
        setUsers(data.content);
      } catch (err) {
        setError('Error while fetching users.');
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  if (loading) return <p>Loading data...</p>;
  if (error) return <p className="error">{error}</p>;

  return (
    <div>
      <h2>List of users</h2>
      {users.length > 0 ? (
        <div className="assets-table">
          <div className="table-header">
            <div className="header-cell">ID</div>
            <div className="header-cell">Name</div>
            <div className="header-cell">Surname</div>
            <div className="header-cell">Email</div>
            <div className="header-cell">Role</div>
            <div className="header-cell">Blocked</div>
          </div>

          <div className="table-body">
            {users.map(user => (
              <div className="table-row" key={user.id}>
                <div className="cell">{user.id}</div>
                <div className="cell">{user.firstname}</div>
                <div className="cell">{user.lastname}</div>
                <div className="cell">{user.email}</div>
                <div className="cell">{user.role}</div>
                <div className="cell">{user.blocked ? 'Yes' : 'No'}</div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <p>No users available.</p>
      )}
    </div>
  );
};

export default AdminUsers;
