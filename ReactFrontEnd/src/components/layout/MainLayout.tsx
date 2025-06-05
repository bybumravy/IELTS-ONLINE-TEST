import {Header} from "@/components/layout/Header";
import {Footer} from "@/components/layout/Footer";


export const MainLayout = ({ children }) => (
    <div className="min-h-screen bg-gray-50">
        <Header />
        {children}
        <Footer />
    </div>
)
