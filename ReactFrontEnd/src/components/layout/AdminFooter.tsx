
export function AdminFooter() {
  return (
    <footer className="bg-emerald-50 border-t border-emerald-200">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="flex items-center space-x-2">
            <span className="text-lg font-bold text-emerald-700">Admin Panel</span>
            <span className="text-emerald-400">| LANGUAGES</span>
          </div>
          <div className="text-sm text-emerald-700">
            Contact: <a href="mailto:admin@languages.com" className="underline">admin@languages.com</a>
          </div>
        </div>
        <div className="mt-6 pt-4 border-t border-emerald-100 text-center text-emerald-400">
          <p>&copy; {new Date().getFullYear()} LANGUAGES Admin. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
} 