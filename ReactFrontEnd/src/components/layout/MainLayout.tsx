import {Header} from "@/components/layout/Header";
import {Footer} from "@/components/layout/Footer";
import {BackButton} from "@/components/ui/back-button";


export const MainLayout = ({ children }) => (
    <div className="min-h-screen bg-gray-50">
        <Header />
        {children}
        <BackButton />
        <Footer />
    </div>
)
