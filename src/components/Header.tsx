import React, { useState } from 'react';
import { UserProfile, Language, UserRole } from '../types';
import { Bell, Globe, Smartphone, UserPlus, Search, LogOut, User, Check, Shield } from 'lucide-react';

interface HeaderProps {
  user: UserProfile;
  lang: Language;
  onToggleLang: () => void;
  onOpenProfile: () => void;
  onOpenInstallApp: () => void;
  onOpenSignUp: () => void;
  onLogout?: () => void;
  unreadCount?: number;
}

export const Header: React.FC<HeaderProps> = ({
  user,
  lang,
  onToggleLang,
  onOpenProfile,
  onOpenInstallApp,
  onOpenSignUp,
  onLogout,
  unreadCount = 2
}) => {
  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);

  const roleLabels: Record<UserRole, { title: string; color: string }> = {
    supporter: { title: 'समर्थक', color: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30' },
    worker: { title: 'सक्रिय कार्यकर्ता', color: 'bg-orange-500/20 text-orange-400 border-orange-500/30' },
    office_bearer: { title: 'पदाधिकारी', color: 'bg-purple-500/20 text-purple-300 border-purple-500/30' },
    admin: { title: 'व्यवस्थापक', color: 'bg-amber-500/20 text-amber-300 border-amber-500/30' }
  };

  return (
    <header className="sticky top-0 z-40 bg-slate-900/95 backdrop-blur-xl border-b border-slate-800 px-3 sm:px-6 py-2.5">
      <div className="max-w-[1600px] mx-auto flex items-center justify-between gap-3">
        
        {/* Left Side: Brand / Logo */}
        <div
          className="flex items-center space-x-3 cursor-pointer group"
          onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}
        >
          <div className="relative shrink-0">
            <img
              src="/aapka_vishwas_leader.png"
              alt="आपका विश्वास • सदैव आपके साथ"
              className="w-11 h-11 sm:w-12 sm:h-12 rounded-2xl object-cover ring-2 ring-orange-500/70 shadow-lg shadow-orange-500/30 group-hover:scale-105 transition-transform"
            />
            <span className="absolute -bottom-1 -right-1 w-3.5 h-3.5 bg-emerald-500 border-2 border-slate-900 rounded-full" />
          </div>
          <div className="min-w-0">
            <div className="flex items-center space-x-2">
              <h1 className="font-black text-base sm:text-lg text-white tracking-wide truncate">
                आपका विश्वास
              </h1>
              <span className="inline-flex text-[10px] font-extrabold uppercase px-2 py-0.5 rounded-full bg-gradient-to-r from-orange-600 via-amber-600 to-emerald-600 text-white shadow-sm">
                सदैव आपके साथ
              </span>
            </div>
            {/* Header subline strictly cleaned as requested */}
            <p className="text-[11px] sm:text-xs text-orange-400 font-semibold truncate">
              सदैव आपके साथ • नरेला विधानसभा (भोपाल)
            </p>
          </div>
        </div>

        {/* Center: Search Bar on Desktop/Tablet */}
        <div className="hidden lg:flex items-center flex-1 max-w-md mx-4">
          <div className="relative w-full">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-2.5" />
            <input
              type="text"
              placeholder="खोजें: दैनिक ग्राफिक, फेसबुक पोस्ट, बूथ स्थिति, सूचनाएं..."
              className="w-full pl-10 pr-4 py-2 rounded-xl bg-slate-950/80 border border-slate-800 text-xs text-slate-200 placeholder-slate-500 focus:outline-none focus:border-orange-500/80 transition-all"
            />
            <span className="absolute right-3 top-2 text-[10px] px-1.5 py-0.5 rounded bg-slate-800 text-slate-400 font-mono">
              नरेला
            </span>
          </div>
        </div>

        {/* Right Side Controls */}
        <div className="flex items-center space-x-2 sm:space-x-3 shrink-0">
          
          {/* Subtle Install App Action (single clean button) */}
          <button
            onClick={onOpenInstallApp}
            className="flex items-center space-x-1.5 px-2.5 py-1.5 sm:px-3 sm:py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 font-semibold text-xs border border-slate-700 transition-all"
            title="वेब ऐप को होम स्क्रीन पर जोड़ें"
          >
            <Smartphone className="w-3.5 h-3.5 text-emerald-400" />
            <span className="hidden sm:inline">ऐप इंस्टॉल</span>
          </button>

          {/* User Sign Up / Register Button */}
          {(!user.isLoggedIn || !user.fullName) ? (
            <button
              onClick={onOpenSignUp}
              className="flex items-center space-x-1.5 px-3 py-1.5 rounded-xl bg-gradient-to-r from-orange-600 to-emerald-600 hover:from-orange-500 hover:to-emerald-500 text-white font-bold text-xs shadow-md shadow-orange-600/30 transition-all active:scale-95"
            >
              <UserPlus className="w-3.5 h-3.5" />
              <span>साइन अप</span>
            </button>
          ) : (
            <button
              onClick={onOpenSignUp}
              className="hidden md:flex items-center space-x-1 px-2.5 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs border border-slate-700 transition-colors"
              title="नया कार्यकर्ता साइन अप फॉर्म देखें"
            >
              <UserPlus className="w-3.5 h-3.5 text-orange-400" />
              <span>नया साइन अप</span>
            </button>
          )}

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
                  <h4 className="text-xs font-bold text-white">ताज़ा परिपत्र व सूचनाएं</h4>
                  <span className="text-[10px] text-orange-400 font-semibold">2 नए</span>
                </div>
                <div className="mt-2 space-y-2 text-xs">
                  <div className="p-2.5 rounded-xl bg-slate-950/60 hover:bg-slate-950 transition-colors">
                    <div className="text-white font-semibold text-[11px]">🔴 नरेला वॉर रूम समीक्षा</div>
                    <div className="text-[10px] text-slate-400 mt-0.5">284 बूथों के प्रभारियों के साथ बैठक जारी।</div>
                  </div>
                  <div className="p-2.5 rounded-xl bg-slate-950/60 hover:bg-slate-950 transition-colors">
                    <div className="text-white font-semibold text-[11px]">🎨 नया दैनिक ग्राफिक अपलोड</div>
                    <div className="text-[10px] text-slate-400 mt-0.5">नरेला विकास पर्व जैकेट पोस्टर तैयार करें।</div>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* User Profile Chip with Dropdown */}
          <div className="relative">
            <div
              onClick={() => setShowUserMenu(!showUserMenu)}
              className="flex items-center space-x-2 pl-1.5 pr-2.5 py-1 rounded-full bg-slate-800/90 hover:bg-slate-700 border border-slate-700/70 cursor-pointer transition-all"
            >
              <div className="w-7 h-7 rounded-full bg-gradient-to-tr from-orange-600 to-emerald-600 flex items-center justify-center text-white font-bold text-xs shadow overflow-hidden">
                {user.customPhotoUri ? (
                  <img src={user.customPhotoUri} alt="" className="w-full h-full object-cover" />
                ) : (
                  <span>{user.fullName ? user.fullName[0] : 'र'}</span>
                )}
              </div>
              <div className="hidden lg:block text-left text-xs leading-tight">
                <div className="font-bold text-slate-200 truncate max-w-[100px]">{user.fullName || 'रितुराज'}</div>
                <div className="text-[10px] text-orange-400 truncate max-w-[100px]">
                  {roleLabels[user.role]?.title || 'कार्यकर्ता'}
                </div>
              </div>
            </div>

            {/* Profile Dropdown Menu */}
            {showUserMenu && (
              <div className="absolute right-0 mt-2 w-56 rounded-2xl bg-slate-900 border border-slate-700 shadow-2xl p-2 z-50 animate-in fade-in slide-in-from-top-2 text-xs">
                <div className="p-2.5 border-b border-slate-800">
                  <div className="font-bold text-white text-xs">{user.fullName || 'रितुराज तोमर'}</div>
                  <div className="text-[11px] text-orange-400 font-medium">{user.designation}</div>
                  <div className="text-[10px] text-slate-400 mt-0.5">नरेला विधानसभा (151), भोपाल</div>
                </div>

                <div className="py-1">
                  <button
                    onClick={() => {
                      setShowUserMenu(false);
                      onOpenProfile();
                    }}
                    className="w-full text-left px-3 py-2 rounded-xl text-slate-200 hover:bg-slate-800 flex items-center space-x-2 transition-colors"
                  >
                    <User className="w-3.5 h-3.5 text-orange-400" />
                    <span>मेरी प्रोफाइल व सदस्यता</span>
                  </button>

                  <button
                    onClick={() => {
                      setShowUserMenu(false);
                      onOpenSignUp();
                    }}
                    className="w-full text-left px-3 py-2 rounded-xl text-slate-200 hover:bg-slate-800 flex items-center space-x-2 transition-colors"
                  >
                    <UserPlus className="w-3.5 h-3.5 text-emerald-400" />
                    <span>साइन अप पेज देखें / बदलें</span>
                  </button>
                </div>

                {onLogout && (
                  <div className="pt-1 border-t border-slate-800">
                    <button
                      onClick={() => {
                        setShowUserMenu(false);
                        onLogout();
                      }}
                      className="w-full text-left px-3 py-2 rounded-xl text-red-400 hover:bg-red-500/10 flex items-center space-x-2 transition-colors font-bold"
                    >
                      <LogOut className="w-3.5 h-3.5" />
                      <span>लॉगआउट करें (Logout)</span>
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>

        </div>

      </div>
    </header>
  );
};
