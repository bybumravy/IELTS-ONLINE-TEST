import { BookOpen, ClipboardList, Zap } from "lucide-react";

export function StaffPage() {
  return (
    <div className="container mx-auto py-8 px-4">
      <div className="bg-gradient-to-r from-indigo-600 to-indigo-700 rounded-2xl p-8 mb-8 shadow-lg">
        <h1 className="text-3xl font-bold text-white mb-2">Welcome to Staff Portal</h1>
        <p className="text-indigo-100">Manage your tasks and monitor your activities efficiently</p>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* Recent Activities Card */}
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 hover:border-indigo-200 transition-colors duration-300">
          <div className="p-6">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center space-x-3">
                <div className="p-2 bg-indigo-100 rounded-lg">
                  <BookOpen className="h-6 w-6 text-indigo-600" />
                </div>
                <h2 className="text-lg font-semibold text-slate-800">Recent Activities</h2>
              </div>
              <span className="text-xs font-medium px-2.5 py-0.5 rounded-full bg-indigo-100 text-indigo-600">
                Last 24h
              </span>
            </div>
            <p className="text-slate-600">View your recent activities and tasks</p>
            <div className="mt-4 space-y-2">
              <div className="text-sm text-slate-600 flex items-center space-x-2">
                <span className="w-2 h-2 rounded-full bg-green-400"></span>
                <span>Graded 3 writing tests</span>
              </div>
              <div className="text-sm text-slate-600 flex items-center space-x-2">
                <span className="w-2 h-2 rounded-full bg-blue-400"></span>
                <span>Added new test materials</span>
              </div>
            </div>
          </div>
        </div>

        {/* Pending Tasks Card */}
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 hover:border-indigo-200 transition-colors duration-300">
          <div className="p-6">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center space-x-3">
                <div className="p-2 bg-amber-100 rounded-lg">
                  <ClipboardList className="h-6 w-6 text-amber-600" />
                </div>
                <h2 className="text-lg font-semibold text-slate-800">Pending Tasks</h2>
              </div>
              <span className="text-xs font-medium px-2.5 py-0.5 rounded-full bg-amber-100 text-amber-600">
                5 tasks
              </span>
            </div>
            <p className="text-slate-600">Check your pending tasks and assignments</p>
            <div className="mt-4 space-y-2">
              <div className="text-sm text-slate-600 flex items-center space-x-2">
                <span className="w-2 h-2 rounded-full bg-amber-400"></span>
                <span>Writing tests to grade</span>
              </div>
              <div className="text-sm text-slate-600 flex items-center space-x-2">
                <span className="w-2 h-2 rounded-full bg-red-400"></span>
                <span>Reports to review</span>
              </div>
            </div>
          </div>
        </div>

        {/* Quick Actions Card */}
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 hover:border-indigo-200 transition-colors duration-300">
          <div className="p-6">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center space-x-3">
                <div className="p-2 bg-emerald-100 rounded-lg">
                  <Zap className="h-6 w-6 text-emerald-600" />
                </div>
                <h2 className="text-lg font-semibold text-slate-800">Quick Actions</h2>
              </div>
              <span className="text-xs font-medium px-2.5 py-0.5 rounded-full bg-emerald-100 text-emerald-600">
                Available
              </span>
            </div>
            <p className="text-slate-600">Access frequently used tools and features</p>
            <div className="mt-4 grid grid-cols-2 gap-2">
              <button className="text-sm px-3 py-2 bg-slate-100 hover:bg-slate-200 rounded-lg text-slate-700 transition-colors duration-200">
                Add Test
              </button>
              <button className="text-sm px-3 py-2 bg-slate-100 hover:bg-slate-200 rounded-lg text-slate-700 transition-colors duration-200">
                Grade Writing
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}