// Import
import { useEffect, useState } from "react";
import { customFetch } from "@/components/sections/customFetch";

// Interface
interface ManageUsersTableProps {
    role: 'student' | 'teacher' | 'manager';
}

interface User {
    firstName: string | null;
    lastName: string | null;
    email: string;
    role: string;
    premium: boolean;
    birthDate: string | null;
    gender: string | null;
    phone: string | null;
    createdAt: string | null;
    country: string | null;
    timeZone: string | null;
    cuurency: string | null;
    originalEmail?: string;
}

const API_URL = import.meta.env.VITE_API_URL;

export default function ManageUsersTable({ role }: ManageUsersTableProps) {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(false);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [showForm, setShowForm] = useState(false);
    const [isCreateMode, setIsCreateMode] = useState(false);
    const [birthDateError, setBirthDateError] = useState<string | null>(null);
    const [emailError, setEmailError] = useState<string | null>(null);
    const [phoneError, setPhoneError] = useState<string | null>(null);
    const [originalEmail, setOriginalEmail] = useState('');
    const [searchEmail, setSearchEmail] = useState('');
    const fetchUsers = async () => {
        setLoading(true);
        try {
            const response = await customFetch(`${API_URL}/getuser/${role}`);
            if (response.ok) {
                const data = await response.json();
                setUsers(data);
            } else {
                console.error('Failed to fetch users: HTTP status', response.status);
            }
        } catch (error) {
            console.error('Failed to fetch users:', error);
        } finally {
            setLoading(false);
        }
    };

    const updateUserField = <K extends keyof User>(key: K, value: User[K]) => {
        setSelectedUser(prev => {
            if (!prev) return prev;
            const newUser = { ...prev, [key]: value };
            if (key === 'email') validateEmail(newUser.email);
            if (key === 'phone' && value) validatePhone(value as string);
            return newUser;
        });
    };

    const handleDelete = async (userEmail: string) => {
        const res = await customFetch(`${API_URL}/getuser/deleteuser/${userEmail}`, {
            method: 'DELETE'
        });
        if (res.ok) {
            alert('User deleted!');
            fetchUsers();
        } else {
            alert('Failed to delete user');
        }
    };

    function validateEmail(email: string) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            setEmailError('Email phai co dinh dang abc@gmail.com');
            return false;
        }
        setEmailError(null);
        return true;
    }

    function validatePhone(phone: string) {
        const phoneRegex = /^0[0-9]{8,14}$/;
        if (!phoneRegex.test(phone)) {
            setPhoneError('Phone must start with 0 and have 9-15 digits');
            return false;
        }
        setPhoneError(null);
        return true;
    }

    useEffect(() => {
        fetchUsers();
    }, [role]);

    return (
        <div style={{ padding: '20px' }}>
            <h2>Manage {role.charAt(0).toUpperCase() + role.slice(1)}</h2>
            <button
                style={{ marginBottom: '10px' }}
                onClick={() => {
                    const newUser: User = {
                        firstName: '',
                        lastName: '',
                        email: '',
                        role: role,
                        premium: false,
                        birthDate: '',
                        gender: '',
                        phone: '',
                        createdAt: null,
                        country: '',
                        timeZone: '',
                        cuurency: '',
                    };
                    setSelectedUser(newUser);
                    setIsCreateMode(true);
                    setShowForm(true);
                }}
            >
                + Create New {role}
            </button>
            <div style={{ marginBottom: '10px' }}>
                <input
                    type="text"
                    placeholder="Search email..."
                    value={searchEmail}
                    onChange={(e) => setSearchEmail(e.target.value)}
                    style={{ padding: '6px 8px', width: '300px', border: '1px solid #ccc', borderRadius: '4px' }}
                />
            </div>
            {loading ? (
                <p>Loading...</p>
            ) : (
                <div style={{ border: '1px solid #ccc', borderRadius: '8px', overflowX: 'auto', padding: '10px', backgroundColor: '#fafafa' }}>
                    <table style={{ borderCollapse: 'collapse', width: '100%', minWidth: '1200px', textAlign: 'left' }}>
                        <thead style={{ backgroundColor: '#f0f0f0' }}>
                        <tr>
                            <th style={thStyle}>Email</th>
                            <th style={thStyle}>First Name</th>
                            <th style={thStyle}>Last Name</th>
                            <th style={thStyle}>Role</th>
                            {role === 'student' && <th style={thStyle}>Premium</th>}
                            <th style={thStyle}>Birth Date</th>
                            <th style={thStyle}>Gender</th>
                            <th style={thStyle}>Phone</th>
                            <th style={thStyle}>Created At</th>
                            <th style={thStyle}>Country</th>
                            <th style={thStyle}>Time Zone</th>
                            <th style={thStyle}>Currency</th>
                            <th style={thStyle}>Update</th>
                            <th style={thStyle}>Delete</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users
                            .filter(user => user.email.toLowerCase().includes(searchEmail.toLowerCase()))
                            .map((user) => (
                                <tr key={user.email} style={{ borderBottom: '1px solid #ddd' }}>
                                    <td style={tdStyle}>{user.email}</td>
                                    <td style={tdStyle}>{user.firstName || '-'}</td>
                                    <td style={tdStyle}>{user.lastName || '-'}</td>
                                    <td style={tdStyle}>{user.role}</td>
                                    {role === 'student' && <td style={tdStyle}>{user.premium ? 'Yes' : 'No'}</td>}
                                    <td style={tdStyle}>{user.birthDate || '-'}</td>
                                    <td style={tdStyle}>{user.gender || '-'}</td>
                                    <td style={tdStyle}>{user.phone || '-'}</td>
                                    <td style={tdStyle}>{user.createdAt || '-'}</td>
                                    <td style={tdStyle}>{user.country || '-'}</td>
                                    <td style={tdStyle}>{user.timeZone || '-'}</td>
                                    <td style={tdStyle}>{user.cuurency || '-'}</td>
                                    <td style={tdStyle}>
                                        <button
                                            style={actionButtonStyle}
                                            onClick={() => {
                                                setSelectedUser({ ...user, originalEmail: user.email });
                                                setIsCreateMode(false);
                                                setShowForm(true);
                                            }}
                                        >
                                            Update
                                        </button>
                                    </td>
                                    <td style={tdStyle}>
                                        <button
                                            style={{ ...actionButtonStyle, backgroundColor: '#f44336', color: '#fff' }}
                                            onClick={() => handleDelete(user.email)}
                                        >
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>

                    </table>
                </div>
            )}

            {showForm && selectedUser && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
                    <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', width: '500px', maxHeight: '80vh', overflowY: 'auto' }}>
                        <h3>{isCreateMode ? 'Create New User' : 'Update User'}</h3>

                        {renderInput(
                            "Email",
                            selectedUser.email,
                            (val) => {
                                updateUserField("email", val);
                                validateEmail(val);
                            },
                            emailError
                        )}
                        {renderInput("First Name", selectedUser.firstName, (val) => updateUserField("firstName", val))}
                        {renderInput("Last Name", selectedUser.lastName, (val) => updateUserField("lastName", val))}
                        {renderSelect("Role", selectedUser.role, (val) => updateUserField("role", val), ['student', 'teacher', 'manager'])}

                        {selectedUser.role === 'student' && renderSelect(
                            "Premium",
                            selectedUser.premium ? 'true' : 'false',
                            (val) => updateUserField("premium", val === 'true'),
                            ['true', 'false']
                        )}

                        {renderInputDate("Birth Date", selectedUser.birthDate, (val) => updateUserField("birthDate", val))}
                        {renderSelect("Gender", selectedUser.gender || '', (val) => updateUserField("gender", val), ['male', 'female'])}
                        {renderInput(
                            "Phone",
                            selectedUser.phone,
                            (val) => {
                                updateUserField("phone", val);
                                validatePhone(val);
                            },
                            phoneError
                        )}                        {renderSelect("Country", selectedUser.country || '', (val) => updateUserField("country", val), ['Vietnam', 'USA'])}
                        {renderSelect("Time Zone", selectedUser.timeZone || '', (val) => updateUserField("timeZone", val), ['Asia/Ho_Chi_Minh', 'Europe/London'])}
                        {renderSelect("Currency", selectedUser.cuurency || '', (val) => updateUserField("cuurency", val), ['VND', 'USD'])}

                        <div style={{ marginTop: '10px' }}>
                            <button
                                onClick={async () => {
                                    let isValid = true;

                                    if (!selectedUser.email || !validateEmail(selectedUser.email)) isValid = false;
                                    if (selectedUser.phone && !validatePhone(selectedUser.phone)) isValid = false;
                                    if (selectedUser.birthDate) {
                                        const today = new Date().toISOString().split('T')[0];
                                        if (selectedUser.birthDate > today) {
                                            setBirthDateError('Birth date cannot be in the future!');
                                            isValid = false;
                                        } else {
                                            setBirthDateError(null);
                                        }
                                    }
                                    if (!selectedUser.firstName || !selectedUser.lastName) {
                                        alert('First name and Last name cannot be empty!');
                                        isValid = false;
                                    }

                                    if (!isValid) return;

                                    const url = isCreateMode
                                        ? `${API_URL}/getuser/createuser`
                                        : `${API_URL}/getuser/updateuser`;

                                    const method = isCreateMode ? 'POST' : 'PUT';
                                    console.log(JSON.stringify(selectedUser, null, 2));
                                    const res = await customFetch(url, {
                                        method: method,
                                        headers: { 'Content-Type': 'application/json' },
                                        body: JSON.stringify({
                                            ...selectedUser,

                                        })

                                    });

                                    if (res.ok) {
                                        alert(isCreateMode ? 'User created successfully!' : 'User updated successfully!');
                                        setShowForm(false);
                                        fetchUsers();
                                    } else {
                                        alert(isCreateMode ? 'Failed to create user' : 'Failed to update user');
                                    }
                                }}
                            >
                                Save
                            </button>
                            <button onClick={() => setShowForm(false)} style={{ marginLeft: '10px' }}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

// Styles
const thStyle = { padding: '8px 12px', borderBottom: '2px solid #ddd', fontWeight: 'bold', whiteSpace: 'nowrap' as const };
const tdStyle = { padding: '8px 12px', whiteSpace: 'nowrap' as const };
const actionButtonStyle = { marginRight: '8px', padding: '4px 8px', border: 'none', borderRadius: '4px', cursor: 'pointer', backgroundColor: '#1976d2', color: '#fff' };

// Render helpers
function renderInput(label: string, value: any, onChange: (val: string) => void, error?: string | null) {
    return (
        <div style={{ marginBottom: '8px' }}>
            <label style={{ display: 'block', marginBottom: '4px' }}>{label}</label>
            <input
                type="text"
                value={value || ''}
                onChange={(e) => onChange(e.target.value)}
                style={{
                    width: '100%',
                    padding: '6px 8px',
                    border: `1px solid ${error ? '#f44336' : '#ccc'}`,
                    borderRadius: '4px'
                }}
            />
            {error && (
                <div style={{ color: '#f44336', fontSize: '12px', marginTop: '4px' }}>
                    {error}
                </div>
            )}
        </div>
    );
}

function renderSelect(label: string, value: string, onChange: (val: string) => void, options: string[]) {
    return (
        <div style={{ marginBottom: '8px' }}>
            <label style={{ display: 'block', marginBottom: '4px' }}>{label}</label>
            <select value={value} onChange={(e) => onChange(e.target.value)} style={{ width: '100%', padding: '6px 8px', border: '1px solid #ccc', borderRadius: '4px' }}>
                {options.map((opt) => <option key={opt} value={opt}>{opt}</option>)}
            </select>
        </div>
    );
}

function renderInputDate(label: string, value: any, onChange: (val: string) => void) {
    return (
        <div style={{ marginBottom: '8px' }}>
            <label style={{ display: 'block', marginBottom: '4px' }}>{label}</label>
            <input type="date" value={value || ''} onChange={(e) => onChange(e.target.value)} style={{ width: '100%', padding: '6px 8px', border: '1px solid #ccc', borderRadius: '4px' }} />
        </div>
    );
}
