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

import {message} from "antd";

interface UserMenuProps {
    onLogout: () => void
}
const handlePremiumClick = async () => {
    try {
        const orderId = `ORDER_${Date.now()}`;
        const response = await fetch(`http://localhost:8080/api/payment/create`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
            orderId,
            amount: 399000, // hoặc tuỳ theo gói bạn định mặc định
            orderInfo: 'Thanh toán nâng cấp Premium',
            extraData: '',
            returnUrl: `http://localhost:5173/payment-callback?orderId=${orderId}`,
            notifyUrl: 'http://localhost:8080/api/payment/ipn'
            })
        });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();

        if (data.resultCode === 0) {
            window.location.href = data.payUrl;
        } else {
            message.error("Không thể khởi tạo thanh toán: " + data.message);
        }
    } catch (error: any) {
        message.error("Lỗi khi thanh toán: " + error.message);
    }
};

export function UserMenu({ onLogout }: UserMenuProps) {
    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                    <Avatar className="h-8 w-8">
                        <AvatarImage src="/placeholder.svg" alt="User" />
                        <AvatarFallback>JD</AvatarFallback>
                    </Avatar>
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
                <DropdownMenuItem onClick={handlePremiumClick}>
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
