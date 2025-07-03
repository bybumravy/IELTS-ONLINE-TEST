import { StaffNavigationMenu } from "./StaffNavigationMenu";

export function StaffHeader() {
  return (
    <header className="sticky top-0 z-50 w-full border-b border-slate-200 bg-white/75 backdrop-blur-sm">
      <StaffNavigationMenu />
    </header>
  );
}
