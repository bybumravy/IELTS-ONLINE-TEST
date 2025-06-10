import { User, LogOut, Settings, Crown, History } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {useAuth} from "@/contexts/AuthContext";

interface UserMenuProps {
    onLogout: () => void
}

export function UserMenu({ onLogout }: UserMenuProps) {
    const { user } = useAuth();
    const defaultAvatarUrl = `https://api.dicebear.com/7.x/avataaars/svg?seed=${user?.username || 'default'}&backgroundColor=65C3C8`
    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                    <Avatar>
                        <AvatarImage src={defaultAvatarUrl} alt={user?.username || 'User'} />
                        <AvatarFallback className="bg-emerald-600 text-white">
                        </AvatarFallback>
                    </Avatar>

                    {/*{user?.username?.substring(0, 2).toUpperCase() || 'U'}*/}

                </Button>

            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-56" align="end" forceMount>
                <DropdownMenuItem>
                    <User className="mr-2 h-4 w-4" />
                    <span>Profile</span>
                </DropdownMenuItem>
                <DropdownMenuItem>
                    <History className="mr-2 h-4 w-4" />
                    <span>Test History</span>
                </DropdownMenuItem>
                <DropdownMenuItem>
                    <Crown className="mr-2 h-4 w-4" />
                    <span>Premium</span>
                </DropdownMenuItem>
                <DropdownMenuItem>
                    <Settings className="mr-2 h-4 w-4" />
                    <span>Settings</span>
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={onLogout}>
                    <LogOut className="mr-2 h-4 w-4" />
                    <span>Log out</span>
                </DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    )
}
