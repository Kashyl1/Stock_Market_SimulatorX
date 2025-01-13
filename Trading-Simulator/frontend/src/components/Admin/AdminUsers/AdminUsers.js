import { useState, useEffect } from 'react';
import { getUsers, blockUser, deleteUser, unblockUser } from '../../../services/AdminService';
import debounce from 'lodash.debounce';
import './AdminUsers.css';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({
  ripple: false,
});


const AdminUsers = () => {
  const [allUsers, setAllUsers] = useState([]);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(20);

  const fetchAllUsers = async () => {
    setLoading(true);
    setError(null);
    try {
      let page = 0;
      let allData = [];
      let response;
      do {
        response = await getUsers(page, 20);
        allData = [...allData, ...response.content];
        page++;
      } while (!response.last);
      setAllUsers(allData);
      setFilteredUsers(allData);
    } catch (err) {
      setError('Error while fetching users.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = debounce((value) => {
    setSearchTerm(value);
    if (value) {
      const lowerCaseTerm = value.toLowerCase();
      const matchedUsers = allUsers.filter((user) =>
        user.email.toLowerCase().includes(lowerCaseTerm)
      );
      setFilteredUsers(matchedUsers);
    } else {
      setFilteredUsers(allUsers);
    }
  }, 300);

  const handleBlockUser = async (userId) => {
    try {
      await blockUser(userId);
      notyf.success(`User with ID: ${userId} has been blocked.`);
      fetchAllUsers();
    } catch (error) {
      notyf.error('Failed to block user.');
    }
  };


   const handleUnblockUser = async (userId) => {
     try {
       await unblockUser(userId);
       notyf.success(`User with ID: ${userId} has been unblocked.`);
       fetchAllUsers();
     } catch (error) {
       notyf.error('Failed to unblock user.');
     }
   };


  const handleDeleteUser = async (userId) => {
    try {
      await deleteUser(userId);
      notyf.success(`User with ID: ${userId} has been deleted.`);
      fetchAllUsers();
    } catch (error) {
      notyf.error('Failed to delete user.');
    }
  };

  useEffect(() => {
    fetchAllUsers();
  }, []);

  const startIndex = currentPage * pageSize;
  const endIndex = startIndex + pageSize;
  const usersToDisplay = filteredUsers.slice(startIndex, endIndex);

  const handleNextPage = () => {
    if (currentPage < Math.ceil(filteredUsers.length / pageSize) - 1) {
      setCurrentPage((prev) => prev + 1);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 0) {
      setCurrentPage((prev) => prev - 1);
    }
  };

  if (loading) return <p>Loading data...</p>;
  if (error) return <p className="error">{error}</p>;

  return (
    <div>
      <h2>List of users</h2>
      <div className="search-container">
        <input
          type="text"
          placeholder="Search by email"
          onChange={(e) => handleSearchChange(e.target.value)}
          className="search-input"
        />
      </div>

      {usersToDisplay.length > 0 ? (
        <div className="assets-table">
          <div className="table-header_admin">
            <div className="header-cell">ID</div>
            <div className="header-cell">Name</div>
            <div className="header-cell">Surname</div>
            <div className="header-cell">Email</div>
            <div className="header-cell">Role</div>
            <div className="header-cell">Blocked</div>
            <div className="header-cell">Actions</div>
          </div>

          <div className="table-body">
            {usersToDisplay.map((user) => (
              <div className="table-row_admin" key={user.id}>
                <div className="cell">{user.id}</div>
                <div className="cell">{user.firstname}</div>
                <div className="cell">{user.lastname}</div>
                <div className="cell">{user.email}</div>
                <div className="cell">{user.role}</div>
                <div className="cell">{user.blocked ? 'Yes' : 'No'}</div>
                <div className="cell">
                  {user.blocked ? (
                    <button onClick={() => handleUnblockUser(user.id)}>Unblock</button>
                  ) : (
                    <button onClick={() => handleBlockUser(user.id)}>Block</button>
                  )}
                  <button onClick={() => handleDeleteUser(user.id)}>Delete</button>
                </div>
              </div>
            ))}
          </div>

        </div>
      ) : (
        <p>No users match your search.</p>
      )}

      <div className="pagination-controls">
        <button onClick={handlePrevPage} disabled={currentPage === 0}>
          Previous
        </button>
        <span>
          Page {currentPage + 1} of {Math.ceil(filteredUsers.length / pageSize)}
        </span>
        <button
          onClick={handleNextPage}
          disabled={currentPage === Math.ceil(filteredUsers.length / pageSize) - 1}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default AdminUsers;
