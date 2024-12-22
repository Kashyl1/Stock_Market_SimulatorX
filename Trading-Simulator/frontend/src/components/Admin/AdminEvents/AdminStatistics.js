import { useState, useEffect } from 'react';
import { getAdminStats, getUserStats } from '../../../services/AdminService';


const AdminStatistics = () => {
  const [adminStats, setAdminStats] = useState({});
  const [userStats, setUserStats] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchStatistics = async () => {
    setLoading(true);
    setError(null);
    try {
      const [adminStatsResponse, userStatsResponse] = await Promise.all([
        getAdminStats(),
        getUserStats(),
      ]);

      setAdminStats(adminStatsResponse);
      setUserStats(userStatsResponse);
    } catch (err) {
      console.error('Error fetching statistics:', err);
      setError(err.message || 'An error occurred while fetching statistics.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStatistics();
  }, []);

  if (loading) return <p>Loading statistics...</p>;
  if (error) return <p className="error">{error}</p>;

  return (
    <div>
      <div className="stats-section">
        <h3>Admin Statistics</h3>
        {Object.keys(adminStats).length > 0 ? (
          <table className="stats-table">
            <thead>
              <tr>
                <th>Action</th>
                <th>Count</th>
              </tr>
            </thead>
            <tbody>
              {Object.entries(adminStats).map(([action, count]) => (
                <tr key={action}>
                  <td>{action}</td>
                  <td>{count}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No admin statistics available.</p>
        )}
      </div>

      <div className="stats-section">
        <h3>User Statistics</h3>
        {Object.keys(userStats).length > 0 ? (
          <table className="stats-table">
            <thead>
              <tr>
                <th>Action</th>
                <th>Count</th>
              </tr>
            </thead>
            <tbody>
              {Object.entries(userStats).map(([action, count]) => (
                <tr key={action}>
                  <td>{action}</td>
                  <td>{count}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No user statistics available.</p>
        )}
      </div>
    </div>
  );
};

export default AdminStatistics;
