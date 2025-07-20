import { useEffect, useState } from "react";

type RoleType = "student" | "teacher" | "manager";

type User = {
    email: string;
    roles: string[];
};

export default function ManageRole({ role }: { role: RoleType }) {
    const [users, setUsers] = useState<User[]>([]);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [newRole, setNewRole] = useState<RoleType | "">("");
    const [actionType, setActionType] = useState<"add" | "update" | "delete" | "">("");
    const [searchEmail, setSearchEmail] = useState<string>("");

    const loadUsers = () => {
        fetch(`http://localhost:8080/getuser/${role}`)
            .then(res => {
                if (!res.ok) throw new Error("response was not ok");
                return res.json();
            })
            .then(data => {
                console.log("Fetched users:", JSON.stringify(data, null, 2));
                setUsers(data);
            })
            .catch(err => console.error("Fetch error:", err));
    };

    const handleAddRole = async () => {
        if (!selectedUser || !newRole.trim()) return;

        if (selectedUser.roles.includes(newRole)) {
            alert("User already has this role!");
            return;
        }

        try {
            const res = await fetch("http://localhost:8080/getuser/addrole", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    email: selectedUser.email,
                    roles: [...selectedUser.roles, newRole.trim()]
                })
            });

            if (!res.ok) throw new Error("Failed to add role");

            await loadUsers();
            resetForm();
        } catch (err) {
            console.error("Add role error:", err);
        }
    };

    const handleDeleteRole = async (email: string | undefined, roleToDelete: string) => {
        const user = users.find(user => user.email === email);
        if (!user) return;

        if (user.roles.length <= 1) {
            alert("Cannot delete the last role of a user.");
            return;
        }

        const updatedRoles = user.roles.filter(role => role !== roleToDelete);

        try {
            const res = await fetch("http://localhost:8080/getuser/deleterole", {
                method: "DELETE",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, roles: updatedRoles })
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg);
            }

            await loadUsers();
            resetForm();
        } catch (err) {
            alert("Delete role error: " + err.message);
        }
    };

    const handleUpdateRole = async () => {
        if (!selectedUser || !newRole.trim()) return;

        try {
            const currentRoles = selectedUser.roles;
            const updatedRoles = currentRoles.includes(newRole)
                ? currentRoles.filter(role => role !== newRole)
                : [...currentRoles, newRole.trim()];

            const res = await fetch("http://localhost:8080/getuser/updateuser", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    email: selectedUser.email,
                    roles: updatedRoles
                })
            });

            if (!res.ok) throw new Error("Failed to update role");

            await loadUsers();
            resetForm();
        } catch (err) {
            console.error("Update role error:", err);
        }
    };

    const resetForm = () => {
        setSelectedUser(null);
        setNewRole("");
        setActionType("");
    };

    useEffect(() => {
        loadUsers();
    }, [role]);

    // Lọc người dùng theo email
    const filteredUsers = users.filter(user =>
        user.email.toLowerCase().includes(searchEmail.toLowerCase())
    );

    return (
        <div>
            <h2>Manage Role: {role}</h2>

            <div className="mt-4 mb-2">
                <input
                    type="text"
                    value={searchEmail}
                    onChange={e => setSearchEmail(e.target.value)}
                    placeholder="Search by email..."
                    className="border px-3 py-2 rounded w-1/2"
                />
            </div>

            <table className="min-w-full border mt-2 text-left">
                <thead>
                <tr className="bg-gray-100">
                    <th className="px-4 py-2">Email</th>
                    <th className="px-4 py-2">Roles</th>
                    <th className="px-4 py-2">Action</th>
                </tr>
                </thead>
                <tbody>
                {filteredUsers.map(user => (
                    <tr key={user.email}>
                        <td className="px-4 py-2">{user.email}</td>
                        <td className="px-4 py-2">{user.roles.join(", ")}</td>
                        <td className="px-4 py-2 space-x-2">
                            <button
                                className="bg-yellow-500 text-white px-2 py-1 rounded"
                                onClick={() => {
                                    setSelectedUser(user);
                                    setActionType("update");
                                }}
                            >
                                Update
                            </button>
                            <button
                                className="bg-green-600 text-white px-2 py-1 rounded"
                                onClick={() => {
                                    setSelectedUser(user);
                                    setActionType("add");
                                }}
                            >
                                Add
                            </button>
                            <button
                                className="bg-red-600 text-white px-2 py-1 rounded"
                                onClick={() => {
                                    setSelectedUser(user);
                                    setActionType("delete");
                                }}
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {selectedUser && (
                <div className="mt-4 border p-4 rounded shadow">
                    <h3>
                        {actionType === "add" && "Add Role"}
                        {actionType === "update" && "Update Role"}
                        {actionType === "delete" && "Delete Role"} for: {selectedUser.email}
                    </h3>

                    <label>{actionType === "delete" ? "Select role to delete:" : "New Role:"}</label>
                    <select
                        value={newRole}
                        onChange={e => setNewRole(e.target.value as RoleType)}
                        className="ml-2 border px-2 py-1"
                    >
                        <option value="">Select role</option>
                        {actionType === "delete"
                            ? selectedUser.roles.map(role => (
                                <option key={role} value={role}>
                                    {role}
                                </option>
                            ))
                            : ["student", "teacher", "manager"]
                                .filter(r => !selectedUser?.roles.includes(r))
                                .map(r => (
                                    <option key={r} value={r}>
                                        {r}
                                    </option>
                                ))}
                    </select>

                    {actionType === "update" && (
                        <button
                            className="ml-4 bg-blue-500 text-white px-3 py-1 rounded"
                            onClick={handleUpdateRole}
                        >
                            Confirm Update
                        </button>
                    )}
                    {actionType === "add" && (
                        <button
                            className="ml-4 bg-green-600 text-white px-3 py-1 rounded"
                            onClick={handleAddRole}
                        >
                            Confirm Add
                        </button>
                    )}
                    {actionType === "delete" && (
                        <button
                            className="ml-4 bg-red-600 text-white px-3 py-1 rounded"
                            onClick={() =>
                                handleDeleteRole(selectedUser?.email, newRole as RoleType)
                            }
                        >
                            Confirm Delete
                        </button>
                    )}

                    <button
                        className="ml-2 text-red-500"
                        onClick={resetForm}
                    >
                        Cancel
                    </button>
                </div>
            )}
        </div>
    );
}
