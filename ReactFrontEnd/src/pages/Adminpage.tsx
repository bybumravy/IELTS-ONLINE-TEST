
import ManageUsersTable from "@/pages/ManageUsersTable";
import {useState} from "react";

type RoleType = 'student' | 'teacher' | 'manager';

export default function AdminDashboard() {
    const [currentManageRole, setCurrentManageRole] = useState<RoleType | null>(null);

    return (
        <div style={{ padding: '20px' }}>
            <h1>Admin Dashboard</h1>

            {currentManageRole === null ? (
                // Giao diện chọn cái để quản lý
                <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
                    <button onClick={() => setCurrentManageRole('student')}>Manage Student</button>
                    <button onClick={() => setCurrentManageRole('teacher')}>Manage Teacher</button>
                    <button onClick={() => setCurrentManageRole('manager')}>Manage Manager</button>
                </div>
            ) : (
                // Giao diện CRUD
                <div style={{ marginTop: '20px' }}>
                    <button onClick={() => setCurrentManageRole(null)}>← Back</button>
                    <ManageUsersTable role={currentManageRole} />
                </div>
            )}
        </div>
    );
}
