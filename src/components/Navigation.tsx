import React from 'react';
import { Home, Image as ImageIcon, Flame, Video, Users, UserCheck } from 'lucide-react';

export type TabKey = 'home' | 'posters' | 'warroom' | 'videos' | 'volunteer' | 'profile';

interface NavigationProps {
  currentTab: TabKey;
  onSelectTab: (tab: TabKey) => void;
  lang: 'hi' | 'en';
}

export const Navigation: React.FC<NavigationProps> = ({ currentTab, onSelectTab, lang }) => {
  const tabs = [
    {
      key: 'home' as TabKey,
      icon: Home,
      labelHi: 'मुख्य पृष्ठ',
      labelEn: 'Home',
      badge: null
    },
    {
      key: 'posters' as TabKey,
      icon: ImageIcon,
      labelHi: 'पोस्टर मेकर',
      labelEn: 'Posters',
      badge: 'नया'
    },
    {
      key: 'warroom' as TabKey,
      icon: Flame,
      labelHi: 'वॉर रूम',
      labelEn: 'War Room',
      badge: 'LIVE'
    },
    {
      key: 'videos' as TabKey,
      icon: Video,
      labelHi: 'वीडियो गैलरी',
      labelEn: 'Videos',
      badge: null
    },
    {
      key: 'volunteer' as TabKey,
      icon: Users,
      labelHi: 'योद्धा मंच',
      labelEn: 'Volunteers',
      badge: null
    },
    {
      key: 'profile' as TabKey,
      icon: UserCheck,
      labelHi: 'प्रोफाइल',
      labelEn: 'Profile',
      badge: null
    }
  ];

  return (
    <>
      {/* Desktop Sidebar (Left side on md and above) */}
      <aside className="hidden md:flex flex-col w-64 bg-slate-900 border-r border-slate-800 p-4 shrink-0 space-y-2 min-h-[calc(100vh-65px)]">
        <div className="px-3 py-2 text-xs font-semibold text-slate-400 uppercase tracking-wider">
          {lang === 'hi' ? 'मुख्य मेनू' : 'Main Menu'}
        </div>
        <nav className="space-y-1">
          {tabs.map((tab) => {
            const Icon = tab.icon;
            const isActive = currentTab === tab.key;
            return (
              <button
                key={tab.key}
                onClick={() => onSelectTab(tab.key)}
                className={`w-full flex items-center justify-between px-3.5 py-3 rounded-xl text-sm font-medium transition-all ${
                  isActive
                    ? 'bg-gradient-to-r from-orange-600 to-amber-600 text-white shadow-lg shadow-orange-600/20'
                    : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                }`}
              >
                <div className="flex items-center space-x-3">
                  <Icon className={`w-5 h-5 ${isActive ? 'text-white' : 'text-slate-400'}`} />
                  <span>{lang === 'hi' ? tab.labelHi : tab.labelEn}</span>
                </div>
                {tab.badge && (
                  <span
                    className={`text-[10px] px-2 py-0.5 rounded-full font-bold uppercase ${
                      tab.badge === 'LIVE'
                        ? 'bg-red-500/20 text-red-400 border border-red-500/30 animate-pulse'
                        : 'bg-orange-400/20 text-orange-300'
                    }`}
                  >
                    {tab.badge}
                  </span>
                )}
              </button>
            );
          })}
        </nav>

        {/* Quick Hotline Banner */}
        <div className="mt-auto p-4 rounded-2xl bg-gradient-to-br from-slate-800 to-slate-900 border border-slate-700/60 text-center">
          <div className="w-8 h-8 mx-auto rounded-full bg-orange-500/20 text-orange-400 flex items-center justify-center font-bold mb-2">
            📞
          </div>
          <h4 className="text-xs font-bold text-white">हेल्पलाइन व वॉर रूम</h4>
          <p className="text-[11px] text-slate-400 mt-1">24x7 जनसंपर्क एवं समस्या निवारण</p>
          <a
            href="tel:+919876543210"
            className="mt-3 block w-full py-1.5 px-3 bg-orange-600 hover:bg-orange-500 text-white font-medium text-xs rounded-lg transition-colors"
          >
            संपर्क करें
          </a>
        </div>
      </aside>

      {/* Mobile Bottom Navigation Bar (Fixed at bottom on phones, Chrome Mobile optimized) */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 z-50 bg-slate-900/95 backdrop-blur-xl border-t border-slate-800/80 px-1 pt-1.5 pb-[max(env(safe-area-inset-bottom),0.5rem)] shadow-2xl">
        <div className="grid grid-cols-6 items-center">
          {tabs.map((tab) => {
            const Icon = tab.icon;
            const isActive = currentTab === tab.key;
            return (
              <button
                key={tab.key}
                onClick={() => onSelectTab(tab.key)}
                className={`flex flex-col items-center justify-center py-1 px-0.5 rounded-xl transition-all relative ${
                  isActive ? 'text-orange-400 font-bold' : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                <div className="relative">
                  <div className={`p-1 rounded-xl transition-colors ${isActive ? 'bg-orange-500/15' : ''}`}>
                    <Icon className={`w-5 h-5 ${isActive ? 'scale-110 text-orange-400' : ''} transition-transform`} />
                  </div>
                  {tab.badge === 'LIVE' && (
                    <span className="absolute -top-0.5 -right-1 w-2 h-2 rounded-full bg-red-500 animate-ping" />
                  )}
                  {tab.badge === 'नया' && !isActive && (
                    <span className="absolute -top-0.5 -right-1.5 w-1.5 h-1.5 rounded-full bg-orange-400" />
                  )}
                </div>
                <span className="text-[10px] mt-0.5 leading-tight truncate max-w-[54px] text-center">
                  {lang === 'hi' ? tab.labelHi : tab.labelEn}
                </span>
                {isActive && (
                  <span className="w-1 h-1 rounded-full bg-orange-500 mt-0.5" />
                )}
              </button>
            );
          })}
        </div>
      </nav>
    </>
  );
};
