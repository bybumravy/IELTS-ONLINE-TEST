import {Header} from "@/components/layout/Header";
import {Footer} from "@/components/layout/Footer";
import {BackButton} from "@/components/ui/back-button";
import type { ReactNode } from "react";

interface MainLayoutProps {
    children: ReactNode;
}

export const MainLayout = ({ children }: MainLayoutProps) => (
    <div className="min-h-screen bg-gray-50">
        <Header />
        {children}
        <BackButton />
        <Footer />
    </div>
)
