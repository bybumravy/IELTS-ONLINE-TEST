import { useState } from "react";
import { Link } from "react-router-dom";
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
} from "@/components/ui/navigation-menu";
import { Button } from "@/components/ui/button";
import { UserMenu } from "./UserMenu";

type Role = "teacher" | "manager";

export function StaffNavigationMenu() {
  const [role, setRole] = useState<Role>("teacher");

  const handleRoleChange = () => {
    setRole(role === "teacher" ? "manager" : "teacher");
  };

  const handleLogout = () => {
    // Handle logout logic here
    window.location.href = "/logout";
  };

  return (
    <div className="flex h-16 items-center px-4 container mx-auto">
      <NavigationMenu>
        <NavigationMenuList className="space-x-2">
          <NavigationMenuItem>
            <Link to="/staff-page">
              <Button variant="link" className="text-lg font-semibold text-emerald-600 hover:text-emerald-700">
                LANGUAGES
              </Button>
            </Link>
          </NavigationMenuItem>


          {/* Teacher Menu Items */}
          {role === "teacher" && (
            <>
              <NavigationMenuItem>
                <Link to="/add-test">
                  <Button variant="ghost" className="text-slate-600 hover:text-slate-900 hover:bg-slate-100">
                    Add Test
                  </Button>
                </Link>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <Link to="/grade-writing">
                  <Button variant="ghost" className="text-slate-600 hover:text-slate-900 hover:bg-slate-100">
                    Grade Writing
                  </Button>
                </Link>
              </NavigationMenuItem>
            </>
          )}

          {/* Manager Menu Items */}
          {role === "manager" && (
            <>
              <NavigationMenuItem>
                <NavigationMenuTrigger className="text-slate-600 hover:text-slate-900 hover:bg-slate-100">
                  User Management
                </NavigationMenuTrigger>
                <NavigationMenuContent>
                  <div className="grid gap-3 p-4 w-[400px] bg-white rounded-xl shadow-lg border border-slate-200">
                    <NavigationMenuLink asChild>
                      <Link to="/manage-students" className="block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-slate-100">
                        <div className="text-sm font-medium leading-none text-slate-900">Student Management</div>
                        <p className="line-clamp-2 text-sm leading-snug text-slate-600 mt-1">
                          View, add, edit, and remove students
                        </p>
                      </Link>
                    </NavigationMenuLink>
                    <NavigationMenuLink asChild>
                      <Link to="/manage-teachers" className="block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-slate-100">
                        <div className="text-sm font-medium leading-none text-slate-900">Teacher Management</div>
                        <p className="line-clamp-2 text-sm leading-snug text-slate-600 mt-1">
                          View, add, edit, and remove teachers
                        </p>
                      </Link>
                    </NavigationMenuLink>
                  </div>
                </NavigationMenuContent>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <Link to="/handle-reports">
                  <Button variant="ghost" className="text-slate-600 hover:text-slate-900 hover:bg-slate-100">
                    Handle Reports
                  </Button>
                </Link>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <Link to="/transactions">
                  <Button variant="ghost" className="text-slate-600 hover:text-slate-900 hover:bg-slate-100">
                    Transaction History
                  </Button>
                </Link>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <Link to="/accept-tests">
                  <Button variant="ghost" className="text-slate-600 hover:text-slate-900 hover:bg-slate-100">
                    Accept Tests
                  </Button>
                </Link>
              </NavigationMenuItem>
              
            </>
          )}
        </NavigationMenuList>
      </NavigationMenu>

      <div className="ml-auto flex items-center space-x-4">
        <div className="flex items-center space-x-2">
          <label className="inline-flex items-center cursor-pointer">
            <input
              type="checkbox"
              className="sr-only"
              checked={role === "manager"}
              onChange={handleRoleChange}
            />
            <div className={`relative w-10 h-5 bg-slate-200 rounded-full transition-colors duration-200 ease-in-out ${role === "manager" ? "bg-indigo-600" : ""}`}>
              <div className={`absolute w-3 h-3 bg-white rounded-full transition-transform duration-200 ease-in-out transform ${role === "manager" ? "translate-x-6" : "translate-x-1"} top-1`}></div>
            </div>
            <span className="ml-2 text-sm font-medium text-slate-700">
              {role === "teacher" ? "Teacher Mode" : "Manager Mode"}
            </span>
          </label>
        </div>
        <UserMenu onLogout={handleLogout} />
      </div>
    </div>
  );
} 