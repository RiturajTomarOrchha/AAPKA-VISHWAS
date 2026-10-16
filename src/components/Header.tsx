import React from 'react';
import { UserProfile, Language } from '../types';
import { Bell, Globe, Shield, Sparkles } from 'lucide-react';

interface HeaderProps {
  user: UserProfile;
  lang: Language;
  onToggleLang: () => void;
  onOpenProfile: () => void;
  unreadCount?: number;
}

export const Header: React.FC<HeaderProps> = ({
  user,
  lang,
  onToggleLang,
  onOpenProfile,
  unreadCount = 3
}) => {
  return (
    <header className="sticky top-0 z-40 bg-slate-900/90 backdrop-blur-md border-b border-slate-800 px-4 py-3">
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        
        {/* Brand / Logo */}
        <div className="flex items-center space-x-3 cursor-pointer" onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}>
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-orange-600 via-amber-500 to-blue-700 flex items-center justify-center shadow-lg shadow-orange-500/20 text-white font-extrabold text-xl">
            TR
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <h1 className="font-bold text-lg text-white tracking-wide">टीम राठौर</h1>
              <span className="text-[10px] font-semibold uppercase px-2 py-0.5 rounded-full bg-gradient-to-r from-orange-500 to-amber-500 text-white shadow-sm">
                आधिकारिक
              </span>
            </div>
            <p className="text-xs text-slate-400">जनसेवा ही संकल्प • {user.assembly || 'ग्वालियर'}</p>
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex items-center space-x-2 sm:space-x-4">
          
          {/* Language Toggle */}
          <button
            onClick={onToggleLang}
            className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-xs font-medium text-slate-300 transition-colors border border-slate-700/60"
            title="भाषा बदलें (Change Language)"
          >
            <Globe className="w-3.5 h-3.5 text-orange-400" />
            <span>{lang === 'hi' ? 'EN' : 'हिन्दी'}</span>
          </button>

          {/* Notification Icon */}
          <div className="relative">
            <button className="p-2 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 transition-colors border border-slate-700/60">
              <Bell className="w-4 h-4" />
              {unreadCount > 0 && (
                <span className="absolute -top-1 -right-1 w-4 h-4 bg-red-500 text-[10px] font-bold rounded-full flex items-center justify-center text-white ring-2 ring-slate-900">
                  {unreadCount}
                </span>
              )}
            </button>
          </div>

          {/* User Profile Mini Chip */}
          <div
            onClick={onOpenProfile}
            className="flex items-center space-x-2 pl-2 pr-3 py-1 rounded-full bg-slate-800/80 hover:bg-slate-700/80 border border-slate-700/60 cursor-pointer transition-all"
          >
            <div className="w-7 h-7 rounded-full bg-orange-600 flex items-center justify-center text-white font-bold text-xs shadow">
              {user.fullName ? user.fullName[0] : 'र'}
            </div>
            <div className="hidden md:block text-left text-xs leading-tight">
              <div className="font-semibold text-slate-200">{user.fullName || 'रितुराज तोमर'}</div>
              <div className="text-[10px] text-orange-400">{user.designation || 'कार्यकर्ता'}</div>
            </div>
          </div>

        </div>

      </div>
    </header>
  );
};
