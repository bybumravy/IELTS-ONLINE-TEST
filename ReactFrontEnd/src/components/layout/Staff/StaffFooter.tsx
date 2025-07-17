import { Link } from "react-router-dom";

export function StaffFooter() {
  return (
    <footer className="bg-emerald-700 border-t border-emerald-800">
      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Quick Links */}
          <div>
            <h3 className="text-sm font-semibold text-emerald-100 mb-3">Quick Links</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/staff-page" className="text-sm text-emerald-100 hover:text-white">
                  Dashboard
                </Link>
              </li>
              <li>
                <Link to="/add-test" className="text-sm text-emerald-100 hover:text-white">
                  Add Test
                </Link>
              </li>
              <li>
                <Link to="/grade-writing" className="text-sm text-emerald-100 hover:text-white">
                  Grade Writing
                </Link>
              </li>
            </ul>
          </div>

          {/* Resources */}
          <div>
            <h3 className="text-sm font-semibold text-emerald-100 mb-3">Resources</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/staff-guide" className="text-sm text-emerald-100 hover:text-white">
                  Staff Guide
                </Link>
              </li>
              <li>
                <Link to="/faq" className="text-sm text-emerald-100 hover:text-white">
                  FAQ
                </Link>
              </li>
              <li>
                <Link to="/support" className="text-sm text-emerald-100 hover:text-white">
                  Support
                </Link>
              </li>
            </ul>
          </div>

          {/* Legal */}
          <div>
            <h3 className="text-sm font-semibold text-emerald-100 mb-3">Legal</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/privacy" className="text-sm text-emerald-100 hover:text-white">
                  Privacy Policy
                </Link>
              </li>
              <li>
                <Link to="/terms" className="text-sm text-emerald-100 hover:text-white">
                  Terms of Service
                </Link>
              </li>
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h3 className="text-sm font-semibold text-emerald-100 mb-3">Contact</h3>
            <ul className="space-y-2">
              <li className="text-sm text-emerald-100">
                Email: support@languages.com
              </li>
              <li className="text-sm text-emerald-100">
                Phone: (123) 456-7890
              </li>
              <li className="text-sm text-emerald-100">
                Hours: Mon-Fri 9:00 AM - 5:00 PM
              </li>
            </ul>
          </div>
        </div>

        <div className="mt-8 pt-8 border-t border-emerald-800">
          <p className="text-center text-sm text-emerald-100">
            © {new Date().getFullYear()} LANGUAGES. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
}
