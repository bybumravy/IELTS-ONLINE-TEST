import { useState } from "react";
import ManageRole from "@/components/sections/admin/ManageRole";
import { AdminLayout } from "@/components/layout/AdminLayout";
import { UserCog, Users, UserCheck, ArrowLeft } from "lucide-react";

type RoleType = "student" | "teacher" | "manager";

export default function AdminPage() {
    const [currentManageRole, setCurrentManageRole] = useState<RoleType | null>(null);

    return (
        <AdminLayout>
            <div className="max-w-4xl mx-auto py-10 px-4 min-h-[70vh]">
                <h1 className="text-3xl font-bold text-emerald-700 mb-2 text-center flex items-center justify-center gap-2">
                    <UserCog className="w-8 h-8 text-emerald-600" /> Admin Dashboard
                </h1>
                <p className="text-gray-500 text-center mb-8">Manage users and roles with professional tools</p>

                {currentManageRole === null ? (
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
                        <button
                            className="bg-emerald-600 hover:bg-emerald-700 text-white font-semibold py-6 rounded-xl shadow-lg flex flex-col items-center gap-2 transition-all duration-200"
                            onClick={() => setCurrentManageRole("student")}
                        >
                            <Users className="w-8 h-8" />
                            Manage Students
                        </button>
                        <button
                            className="bg-gray-200 hover:bg-gray-300 text-emerald-700 font-semibold py-6 rounded-xl shadow-lg flex flex-col items-center gap-2 transition-all duration-200"
                            onClick={() => setCurrentManageRole("teacher")}
                        >
                            <UserCheck className="w-8 h-8" />
                            Manage Teachers
                        </button>
                        <button
                            className="bg-gray-100 hover:bg-gray-200 text-emerald-700 font-semibold py-6 rounded-xl shadow-lg flex flex-col items-center gap-2 transition-all duration-200"
                            onClick={() => setCurrentManageRole("manager")}
                        >
                            <UserCog className="w-8 h-8" />
                            Manage Managers
                        </button>
                    </div>
                ) : (
                    <div className="mt-8">
                        <button
                            className="flex items-center gap-2 text-emerald-600 hover:text-emerald-800 font-semibold mb-4"
                            onClick={() => setCurrentManageRole(null)}
                        >
                            <ArrowLeft className="w-5 h-5" /> Back
                        </button>
                        <ManageRole role={currentManageRole} />
                    </div>
                )}
            </div>
        </AdminLayout>
    );
}