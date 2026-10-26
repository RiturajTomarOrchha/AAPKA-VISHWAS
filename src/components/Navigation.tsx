import React from 'react';
import { Home, Image as ImageIcon, Flame, Video, Users, UserCheck, Smartphone, Phone, Sparkles, MessageSquare } from 'lucide-react';

export type TabKey = 'home' | 'posters' | 'warroom' | 'videos' | 'volunteer' | 'profile';

interface NavigationProps {
  currentTab: TabKey;
  onSelectTab: (tab: TabKey) => void;
  lang: 'hi' | 'en';
  onOpenInstallApp?: () => void;
}

export const Navigation: React.FC<NavigationProps> = ({
  currentTab,
  onSelectTab,
  lang,
  onOpenInstallApp
}) => {
  const operationsTabs = [
    {
      key: 'home' as TabKey,
      icon: Home,
      labelHi: 'मुख्य डैशबोर्ड',
      labelEn: 'Dashboard',
      badge: null
    },
    {
      key: 'posters' as TabKey,
      icon: ImageIcon,
      labelHi: 'डिजिटल पोस्टर स्टूडियो',
      labelEn: 'Poster Studio',
      badge: '8 थीम्स'
    },
    {
      key: 'warroom' as TabKey,
      icon: Flame,
      labelHi: 'केंद्रीय चुनाव वॉर रूम',
      labelEn: 'War Room',
      badge: 'LIVE'
    }
  ];

  const publicConnectTabs = [
    {
      key: 'videos' as TabKey,
      icon: Video,
      labelHi: 'वीडियो व रील्स गैलरी',
      labelEn: 'Media & Reels',
      badge: 'HD'
    },
    {
      key: 'volunteer' as TabKey,
      icon: Users,
      labelHi: 'योद्धा मंच व I-Card',
      labelEn: 'Volunteers & ID',
      badge: null
    },
    {
      key: 'profile' as TabKey,
      icon: UserCheck,
      labelHi: 'प्रोफाइल व सदस्यता',
      labelEn: 'Profile',
      badge: null
    }
  ];

  const allMobileTabs = [
    ...operationsTabs,
    ...publicConnectTabs
  ];

  return (
    <>
      {/* Desktop & Tablet Sidebar (Proper Web Application Menu) */}
      <aside className="hidden md:flex flex-col w-64 lg:w-72 bg-slate-900/80 backdrop-blur-md border-r border-slate-800 p-4 shrink-0 space-y-5 min-h-[calc(100vh-65px)]">
        
        {/* Section 1: Operations */}
        <div>
          <div className="px-3 py-1.5 text-[11px] font-bold text-slate-400 uppercase tracking-wider">
            {lang === 'hi' ? 'मुख्य संचालन व अभियान' : 'Campaign Operations'}
          </div>
          <nav className="space-y-1 mt-1">
            {operationsTabs.map((tab) => {
              const Icon = tab.icon;
              const isActive = currentTab === tab.key;
              return (
                <button
                  key={tab.key}
                  onClick={() => onSelectTab(tab.key)}
                  className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-xl text-xs sm:text-sm font-semibold transition-all ${
                    isActive
                      ? 'bg-gradient-to-r from-orange-600 to-amber-600 text-white shadow-lg shadow-orange-600/25 ring-1 ring-orange-400/40'
                      : 'text-slate-300 hover:bg-slate-800/80 hover:text-white'
                  }`}
                >
                  <div className="flex items-center space-x-3">
                    <Icon className={`w-4 h-4 sm:w-5 sm:h-5 ${isActive ? 'text-white' : 'text-slate-400'}`} />
                    <span>{lang === 'hi' ? tab.labelHi : tab.labelEn}</span>
                  </div>
                  {tab.badge && (
                    <span
                      className={`text-[10px] px-2 py-0.5 rounded-full font-bold uppercase ${
                        tab.badge === 'LIVE'
                          ? 'bg-red-500/20 text-red-400 border border-red-500/40 animate-pulse'
                          : 'bg-orange-500/20 text-orange-300'
                      }`}
                    >
                      {tab.badge}
                    </span>
                  )}
                </button>
              );
            })}
          </nav>
        </div>

        {/* Section 2: Public Relations & Connect */}
        <div>
          <div className="px-3 py-1.5 text-[11px] font-bold text-slate-400 uppercase tracking-wider">
            {lang === 'hi' ? 'जनसंपर्क व संगठन' : 'Public & Organization'}
          </div>
          <nav className="space-y-1 mt-1">
            {publicConnectTabs.map((tab) => {
              const Icon = tab.icon;
              const isActive = currentTab === tab.key;
              return (
                <button
                  key={tab.key}
                  onClick={() => onSelectTab(tab.key)}
                  className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-xl text-xs sm:text-sm font-semibold transition-all ${
                    isActive
                      ? 'bg-gradient-to-r from-orange-600 to-amber-600 text-white shadow-lg shadow-orange-600/25 ring-1 ring-orange-400/40'
                      : 'text-slate-300 hover:bg-slate-800/80 hover:text-white'
                  }`}
                >
                  <div className="flex items-center space-x-3">
                    <Icon className={`w-4 h-4 sm:w-5 sm:h-5 ${isActive ? 'text-white' : 'text-slate-400'}`} />
                    <span>{lang === 'hi' ? tab.labelHi : tab.labelEn}</span>
                  </div>
                  {tab.badge && (
                    <span className="text-[10px] px-2 py-0.5 rounded-full font-bold uppercase bg-purple-500/20 text-purple-300">
                      {tab.badge}
                    </span>
                  )}
                </button>
              );
            })}
          </nav>
        </div>

        {/* PWA / APK Download Quick Widget */}
        {onOpenInstallApp && (
          <div
            onClick={onOpenInstallApp}
            className="cursor-pointer p-3.5 rounded-2xl bg-gradient-to-br from-orange-950/40 via-slate-900 to-slate-950 border border-orange-500/30 hover:border-orange-500/60 transition-all shadow-md group"
          >
            <div className="flex items-center space-x-2.5">
              <div className="p-2 rounded-xl bg-orange-600/20 text-orange-400 group-hover:scale-110 transition-transform">
                <Smartphone className="w-4 h-4" />
              </div>
              <div className="flex-1 min-w-0">
                <h5 className="text-xs font-bold text-white flex items-center space-x-1">
                  <span>ऐप इंस्टॉल करें</span>
                  <Sparkles className="w-3 h-3 text-amber-400" />
                </h5>
                <p className="text-[10px] text-slate-400 truncate">PWA Web App व APK डाउनलोड</p>
              </div>
            </div>
          </div>
        )}

        {/* Quick Campaign & Hotline Card */}
        <div className="mt-auto p-4 rounded-3xl bg-gradient-to-br from-slate-900 to-slate-950 border border-slate-800 text-center relative overflow-hidden">
          <div className="relative z-10">
            <img
              src="/aapka_vishwas_leader.png"
              alt="आपका विश्वास • सदैव आपके साथ"
              className="w-16 h-16 mx-auto rounded-2xl object-cover ring-2 ring-orange-500/60 shadow-lg shadow-orange-500/20 mb-2"
            />
            <h4 className="text-xs font-black text-amber-300">आपका विश्वास</h4>
            <p className="text-[10px] font-semibold text-slate-300">नरेला विधानसभा (भोपाल) • 24x7 हेल्पलाइन</p>
            <div className="mt-3 grid grid-cols-2 gap-2">
              <a
                href="tel:+919876543210"
                className="py-1.5 px-2 bg-orange-600 hover:bg-orange-500 text-white font-medium text-[11px] rounded-lg transition-colors flex items-center justify-center space-x-1"
              >
                <Phone className="w-3 h-3" />
                <span>कॉल</span>
              </a>
              <a
                href="https://api.whatsapp.com/send?phone=919876543210"
                target="_blank"
                rel="noreferrer"
                className="py-1.5 px-2 bg-emerald-600 hover:bg-emerald-500 text-white font-medium text-[11px] rounded-lg transition-colors flex items-center justify-center space-x-1"
              >
                <MessageSquare className="w-3 h-3" />
                <span>व्हाट्सएप</span>
              </a>
            </div>
          </div>
        </div>

      </aside>

      {/* Mobile Bottom Navigation Bar (Optimized for Mobile Chrome) */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 z-50 bg-slate-900/95 backdrop-blur-xl border-t border-slate-800/80 px-1 pt-1.5 pb-[max(env(safe-area-inset-bottom),0.5rem)] shadow-2xl">
        <div className="grid grid-cols-6 items-center">
          {allMobileTabs.map((tab) => {
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
                  {tab.badge && tab.badge !== 'LIVE' && !isActive && (
                    <span className="absolute -top-0.5 -right-1.5 w-1.5 h-1.5 rounded-full bg-orange-400" />
                  )}
                </div>
                <span className="text-[10px] mt-0.5 leading-tight truncate max-w-[54px] text-center">
                  {lang === 'hi' ? tab.labelHi.split(' ')[0] : tab.labelEn}
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
