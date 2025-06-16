import {Header} from "@/components/layout/Header";
import {Footer} from "@/components/layout/Footer";
import {BackButton} from "@/components/ui/back-button";


export const MainLayout = ({ children }) => (
  <div className="flex flex-col min-h-screen bg-gray-50">
    <Header />

    <main className="flex-grow">
      {children}
      <BackButton />
    </main>

    <Footer />
  </div>
);

