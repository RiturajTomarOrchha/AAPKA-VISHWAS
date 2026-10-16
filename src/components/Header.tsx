import React, { useState } from 'react';
import { UserProfile, Language } from '../types';
import { Bell, Globe, Download, Smartphone, Sparkles, UserPlus, Menu, X, Search } from 'lucide-react';

interface HeaderProps {
  user: UserProfile;
  lang: Language;
  onToggleLang: () => void;
  onOpenProfile: () => void;
  onOpenInstallApp: () => void;
  onOpenSignUp: () => void;
  unreadCount?: number;
  onToggleSidebar?: () => void;
}

export const Header: React.FC<HeaderProps> = ({
  user,
  lang,
  onToggleLang,
  onOpenProfile,
  onOpenInstallApp,
  onOpenSignUp,
  unreadCount = 3,
  onToggleSidebar
}) => {
  const [showNotifications, setShowNotifications] = useState(false);

  return (
    <header className="sticky top-0 z-40 bg-slate-900/95 backdrop-blur-xl border-b border-slate-800 px-3 sm:px-6 py-2.5">
      <div className="max-w-[1600px] mx-auto flex items-center justify-between gap-3">
        
        {/* Left Side: Brand / Logo */}
        <div className="flex items-center space-x-3 cursor-pointer" onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}>
          <div className="relative shrink-0">
            <img
              src="/aapka_vishwas_leader.png"
              alt="आपका विश्वास • सदैव आपके साथ"
              className="w-11 h-11 sm:w-12 sm:h-12 rounded-2xl object-cover ring-2 ring-orange-500/70 shadow-lg shadow-orange-500/30"
            />
            <span className="absolute -bottom-1 -right-1 w-3.5 h-3.5 bg-emerald-500 border-2 border-slate-900 rounded-full" />
          </div>
          <div className="min-w-0">
            <div className="flex items-center space-x-2">
              <h1 className="font-extrabold text-base sm:text-lg text-white tracking-wide truncate">
                आपका विश्वास
              </h1>
              <span className="hidden sm:inline-flex text-[10px] font-bold uppercase px-2 py-0.5 rounded-full bg-gradient-to-r from-orange-500 to-amber-500 text-white shadow-sm">
                सदैव आपके साथ
              </span>
            </div>
            <p className="text-[11px] sm:text-xs text-slate-400 truncate">
              हेमंत राठौर • टीम राठौर • {user.assembly || 'ग्वालियर विधानसभा (17)'}
            </p>
          </div>
        </div>

        {/* Center: Search Bar on Desktop/Tablet */}
        <div className="hidden lg:flex items-center flex-1 max-w-md mx-4">
          <div className="relative w-full">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-2.5" />
            <input
              type="text"
              placeholder="खोजें: समाचार, पोस्टर थीम्स, बूथ स्टेटस, परिपत्र..."
              className="w-full pl-10 pr-4 py-2 rounded-xl bg-slate-950/80 border border-slate-800 text-xs text-slate-200 placeholder-slate-500 focus:outline-none focus:border-orange-500/80 transition-all"
            />
            <span className="absolute right-3 top-2 text-[10px] px-1.5 py-0.5 rounded bg-slate-800 text-slate-400 font-mono">
              Ctrl+K
            </span>
          </div>
        </div>

        {/* Right Side Controls */}
        <div className="flex items-center space-x-2 sm:space-x-3 shrink-0">
          
          {/* Prominent "Add to Home Screen / Install App" Button (Requested by User) */}
          <button
            onClick={onOpenInstallApp}
            className="flex items-center space-x-1.5 px-3 py-1.5 sm:px-3.5 sm:py-2 rounded-xl bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-500 hover:to-amber-500 text-white font-bold text-xs shadow-lg shadow-orange-600/25 transition-all active:scale-95 border border-orange-400/30"
            title="वेब ऐप को होम स्क्रीन पर जोड़ें या APK डाउनलोड करें"
          >
            <Smartphone className="w-4 h-4 animate-bounce shrink-0" />
            <span className="hidden sm:inline">ऐप इंस्टॉल करें</span>
            <span className="sm:hidden text-[11px]">ऐप लें</span>
          </button>

          {/* Sign Up / Member Registration Button */}
          <button
            onClick={onOpenSignUp}
            className="hidden md:flex items-center space-x-1.5 px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 font-semibold text-xs border border-slate-700 transition-colors"
          >
            <UserPlus className="w-3.5 h-3.5 text-orange-400" />
            <span>पंजीकरण</span>
          </button>

          {/* Language Toggle */}
          <button
            onClick={onToggleLang}
            className="hidden sm:flex items-center space-x-1.5 px-2.5 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-medium text-slate-300 transition-colors border border-slate-700/60"
            title="भाषा बदलें (Change Language)"
          >
            <Globe className="w-3.5 h-3.5 text-orange-400" />
            <span>{lang === 'hi' ? 'EN' : 'हिन्दी'}</span>
          </button>

          {/* Notification Bell */}
          <div className="relative">
            <button
              onClick={() => setShowNotifications(!showNotifications)}
              className="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 transition-colors border border-slate-700/60 relative"
            >
              <Bell className="w-4 h-4" />
              {unreadCount > 0 && (
                <span className="absolute -top-1 -right-1 w-4 h-4 bg-red-500 text-[10px] font-bold rounded-full flex items-center justify-center text-white ring-2 ring-slate-900">
                  {unreadCount}
                </span>
              )}
            </button>

            {/* Notification Popover */}
            {showNotifications && (
              <div className="absolute right-0 mt-2 w-72 sm:w-80 rounded-2xl bg-slate-900 border border-slate-700 shadow-2xl p-4 z-50 animate-in fade-in slide-in-from-top-2">
                <div className="flex items-center justify-between pb-2 border-b border-slate-800">
                  <h4 className="text-xs font-bold text-white">ताज़ा अलर्ट्स व सूचनाएं</h4>
                  <span className="text-[10px] text-orange-400 font-semibold">3 नए</span>
                </div>
                <div className="mt-2 space-y-2 text-xs">
                  <div className="p-2 rounded-lg bg-slate-950/60 hover:bg-slate-950 transition-colors">
                    <div className="text-white font-semibold text-[11px]">🔴 वॉर रूम लाइव समीक्षा जारी</div>
                    <div className="text-[10px] text-slate-400 mt-0.5">विधानसभा 284 बूथों की बैठक प्रारंभ।</div>
                  </div>
                  <div className="p-2 rounded-lg bg-slate-950/60 hover:bg-slate-950 transition-colors">
                    <div className="text-white font-semibold text-[11px]">🎨 नया पोस्टर टेम्पलेट उपलब्ध</div>
                    <div className="text-[10px] text-slate-400 mt-0.5">किसान न्याय व युवा क्रांति बैनर लाइव।</div>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* User Profile Mini Chip */}
          <div
            onClick={onOpenProfile}
            className="flex items-center space-x-2 pl-1.5 pr-2.5 py-1 rounded-full bg-slate-800/80 hover:bg-slate-700 border border-slate-700/60 cursor-pointer transition-all"
          >
            <div className="w-7 h-7 rounded-full bg-gradient-to-tr from-orange-600 to-amber-500 flex items-center justify-center text-white font-bold text-xs shadow">
              {user.fullName ? user.fullName[0] : 'र'}
            </div>
            <div className="hidden lg:block text-left text-xs leading-tight">
              <div className="font-semibold text-slate-200 truncate max-w-[100px]">{user.fullName || 'रितुराज'}</div>
              <div className="text-[10px] text-orange-400 truncate max-w-[100px]">{user.designation || 'कार्यकर्ता'}</div>
            </div>
          </div>

        </div>

      </div>
    </header>
  );
};
