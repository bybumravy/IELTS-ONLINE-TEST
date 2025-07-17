import { useEffect, useState } from "react";
import { customFetch } from "@/components/sections/customFetch";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { User, Loader2, Save, X } from "lucide-react";

// Animation helpers
import { CSSTransition } from "react-transition-group";
import "@/components/sections/admin/manage-modal-anim.css";

type User = {
  email: string;
  role: string;
  originalEmail?: string;
};

interface ManageRoleProps {
  role: "student" | "teacher" | "manager";
}

export default function ManageRole({ role }: ManageRoleProps) {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [searchEmail, setSearchEmail] = useState("");
  const [updating, setUpdating] = useState(false);
  const API_URL = import.meta.env.VITE_API_URL;

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await customFetch(`${API_URL}/getuser/${role}`);
      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      } else {
        console.error("Failed to fetch users: HTTP status", response.status);
      }
    } catch (error) {
      console.error("Failed to fetch users:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [role]);

  const handleUpdateRole = async () => {
    if (!selectedUser) return;
    setUpdating(true);
    const url = `${API_URL}/getuser/updateuser`;
    const res = await customFetch(url, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: selectedUser.email,
        role: selectedUser.role,
      }),
    });
    setUpdating(false);
    if (res.ok) {
      alert("User role updated successfully!");
      setShowForm(false);
      await fetchUsers();
    } else {
      alert("Failed to update user role");
    }
  };

  return (
    <Card className="p-6 shadow-lg border-emerald-200 bg-white">
      <h2 className="text-2xl font-bold text-emerald-700 mb-4 flex items-center gap-2">
        <User className="w-6 h-6 text-emerald-400" /> Manage {role.charAt(0).toUpperCase() + role.slice(1)}s
      </h2>
      <div className="mb-4 flex flex-col md:flex-row gap-2 items-center">
        <Input
          type="text"
          placeholder="Search email..."
          value={searchEmail}
          onChange={e => setSearchEmail(e.target.value)}
          className="w-full md:w-80 border-emerald-200 focus:border-emerald-400"
        />
      </div>
      {loading ? (
        <div className="flex items-center gap-2 text-emerald-500"><Loader2 className="animate-spin" /> Loading...</div>
      ) : (
        <div className="overflow-x-auto rounded-lg border border-emerald-100 bg-emerald-50 transition-all duration-500">
          <table className="min-w-full divide-y divide-emerald-200">
            <thead className="bg-emerald-100">
              <tr>
                <th className="px-4 py-2 text-left text-xs font-semibold text-emerald-700 uppercase">Email</th>
                <th className="px-4 py-2 text-left text-xs font-semibold text-emerald-700 uppercase">Role</th>
                <th className="px-4 py-2 text-left text-xs font-semibold text-emerald-700 uppercase">Update Role</th>
              </tr>
            </thead>
            <tbody>
              {users.filter(user => user.email.toLowerCase().includes(searchEmail.toLowerCase())).map(user => (
                <tr key={user.email} className="border-b border-emerald-100 hover:bg-emerald-200/30 transition-colors">
                  <td className="px-4 py-2 text-sm text-gray-800">{user.email}</td>
                  <td className="px-4 py-2 text-sm text-gray-800">{user.role}</td>
                  <td className="px-4 py-2">
                    <Button
                      className="bg-emerald-600 hover:bg-emerald-700 text-white font-semibold px-4 py-1 rounded shadow transition-all duration-200"
                      onClick={() => {
                        setSelectedUser({ ...user, originalEmail: user.email });
                        setShowForm(true);
                      }}
                    >
                      Update
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {/* Modal for update form with animation */}
      <CSSTransition in={showForm && !!selectedUser} timeout={300} classNames="fade-slide" unmountOnExit>
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
          <div className="bg-white rounded-xl shadow-2xl border border-emerald-200 p-8 w-full max-w-md animate-fade-in-up relative">
            <button
              className="absolute top-3 right-3 text-emerald-400 hover:text-emerald-700 transition-colors"
              onClick={() => setShowForm(false)}
            >
              <X className="w-6 h-6" />
            </button>
            <h3 className="text-xl font-bold text-emerald-700 mb-4 flex items-center gap-2">
              <User className="w-5 h-5 text-emerald-400" /> Update Role
            </h3>
            <div className="mb-4">
              <label className="block text-emerald-700 mb-1">Role</label>
              <select
                value={selectedUser?.role}
                onChange={e => setSelectedUser(su => su ? { ...su, role: e.target.value } : su)}
                className="w-full border border-emerald-200 rounded px-3 py-2 focus:border-emerald-400 focus:outline-none"
              >
                <option value="student">student</option>
                <option value="teacher">teacher</option>
                <option value="manager">manager</option>
              </select>
            </div>
            <Button
              className="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-semibold flex items-center justify-center gap-2 shadow-md transition-all duration-200"
              onClick={handleUpdateRole}
              disabled={updating}
            >
              {updating ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />} Save
            </Button>
          </div>
        </div>
      </CSSTransition>
    </Card>
  );
}