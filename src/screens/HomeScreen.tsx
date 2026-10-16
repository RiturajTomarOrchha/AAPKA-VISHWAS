import React, { useState } from 'react';
import { FeedItem, ImportantNotice, UserProfile, WarRoomStats, ProgramItem, PosterTemplate } from '../types';
import { Heart, Share2, Download, MessageCircle, AlertTriangle, ChevronRight, Sparkles, CheckCircle2, Flame, Radio, Phone, ArrowRight, Calendar, Users, Smartphone, UserPlus } from 'lucide-react';

interface HomeScreenProps {
  feedItems: FeedItem[];
  notices: ImportantNotice[];
  user: UserProfile;
  lang: 'hi' | 'en';
  stats?: WarRoomStats;
  programs?: ProgramItem[];
  templates?: PosterTemplate[];
  onNavigateToPosters: () => void;
  onNavigateToWarRoom: () => void;
  onOpenInstallApp?: () => void;
  onOpenSignUp?: () => void;
}

export const HomeScreen: React.FC<HomeScreenProps> = ({
  feedItems,
  notices,
  user,
  lang,
  stats,
  programs = [],
  templates = [],
  onNavigateToPosters,
  onNavigateToWarRoom,
  onOpenInstallApp,
  onOpenSignUp
}) => {
  const [likes, setLikes] = useState<Record<string, boolean>>({});
  const [shareSuccess, setShareSuccess] = useState<string | null>(null);
  const [activeFeedCategory, setActiveFeedCategory] = useState<string>('सभी');

  const toggleLike = (id: string) => {
    setLikes(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const handleShare = (item: FeedItem) => {
    const text = `${item.titleHi}\n\n${item.captionHi}\n\n🔗 टीम राठौर आधिकारिक वेब पोर्टल: https://github.com/RiturajTomarOrchha/AAPKA-VISHWAS\n#TeamRathore #AapkaVishwas`;
    if (navigator.share) {
      navigator.share({
        title: item.titleHi,
        text: text,
      }).catch(() => {});
    } else {
      navigator.clipboard.writeText(text);
      setShareSuccess(item.id);
      setTimeout(() => setShareSuccess(null), 3000);
    }
  };

  const urgentNotice = notices.find(n => n.isUrgent);

  const feedCategories = ['सभी', 'जनसंपर्क व विकास', 'किसान कल्याण', 'युवा शक्ति'];

  const filteredFeed = activeFeedCategory === 'सभी'
    ? feedItems
    : feedItems.filter(item => item.categoryHi.includes(activeFeedCategory) || item.categoryHi === activeFeedCategory);

  return (
    <div className="space-y-6">
      
      {/* 1. Urgent Notice Ticker */}
      {urgentNotice && (
        <div
          onClick={onNavigateToWarRoom}
          className="cursor-pointer bg-gradient-to-r from-red-950/90 via-orange-950/80 to-slate-900 border border-red-500/50 rounded-2xl p-3.5 sm:p-4 flex items-center justify-between shadow-xl shadow-red-950/20 hover:border-red-400 transition-all active:scale-[0.99]"
        >
          <div className="flex items-center space-x-3">
            <div className="p-2 rounded-xl bg-red-600/20 text-red-400 shrink-0">
              <AlertTriangle className="w-5 h-5 animate-bounce" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="text-[10px] font-extrabold px-2 py-0.5 rounded bg-red-600 text-white uppercase tracking-wider">
                  अति आवश्यक सूचना
                </span>
                <span className="text-xs text-slate-400">{urgentNotice.dateHi}</span>
              </div>
              <h4 className="text-xs sm:text-sm font-bold text-white mt-1 line-clamp-1">
                {urgentNotice.titleHi}
              </h4>
            </div>
          </div>
          <ChevronRight className="w-5 h-5 text-slate-400 shrink-0" />
        </div>
      )}

      {/* 2. "Add to Home Screen / Install Web App" Prominent Banner (Requested by User) */}
      {onOpenInstallApp && (
        <div className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-orange-900/40 via-amber-950/30 to-slate-900 border border-orange-500/40 p-3.5 sm:p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 shadow-lg">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-orange-600/20 text-orange-400 flex items-center justify-center shrink-0 border border-orange-500/30">
              <Smartphone className="w-5 h-5 animate-pulse" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="text-xs font-bold text-white">
                  टीम राठौर ऐप अपने फोन या पीसी की होम स्क्रीन पर जोड़ें
                </span>
                <span className="text-[10px] px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/30">
                  APK उपलब्ध
                </span>
              </div>
              <p className="text-[11px] text-slate-300 mt-0.5">
                सीधे ब्राउज़र से 1-क्लिक में वेब ऐप इंस्टॉल करें या 30 MB Android APK डाउनलोड करें।
              </p>
            </div>
          </div>

          <div className="flex items-center space-x-2 shrink-0">
            <button
              onClick={onOpenInstallApp}
              className="px-4 py-2 rounded-xl bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-500 hover:to-amber-500 text-white font-bold text-xs shadow-md shadow-orange-600/30 flex items-center space-x-1.5 transition-all active:scale-95"
            >
              <Smartphone className="w-3.5 h-3.5" />
              <span>होम स्क्रीन पर जोड़ें</span>
            </button>
          </div>
        </div>
      )}

      {/* 3. Main Dual Column Layout (PC & Tablet Web App Grid) */}
      <div className="grid grid-cols-1 xl:grid-cols-12 gap-6 items-start">
        
        {/* Left / Main Column (xl:col-span-8) */}
        <div className="xl:col-span-8 space-y-6">
          
          {/* Hero Welcome & Quick Action Card with Official Leader Emblem */}
          <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-orange-600 via-amber-600 to-blue-900 p-6 sm:p-8 text-white shadow-2xl flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div className="relative z-10 max-w-xl">
              <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-white/20 backdrop-blur-md text-xs font-semibold mb-2">
                <Sparkles className="w-3.5 h-3.5 text-amber-200" />
                <span>जनसेवा ही संकल्प • राष्ट्र सर्वोपरि</span>
              </div>
              
              <h2 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
                स्वागतम्, {user.fullName || 'रितुराज तोमर'} जी!
              </h2>
              
              <div className="text-base sm:text-xl font-black text-amber-200 mt-1 flex items-center space-x-2">
                <span>🚩 आपका विश्वास • सदैव आपके साथ</span>
              </div>

              <p className="mt-2 text-xs sm:text-sm text-orange-100 max-w-xl leading-relaxed">
                टीम राठौर के आधिकारिक डिजिटल मंच पर आपका स्वागत है। यहाँ से आप 4:5 HD डिजिटल पोस्टर बना सकते हैं, बूथ वॉर रूम में जुड़ सकते हैं और जनसंपर्क अभियान को गति दे सकते हैं।
              </p>

              <div className="mt-5 flex flex-wrap gap-2.5 sm:gap-3">
                <button
                  onClick={onNavigateToPosters}
                  className="px-4 sm:px-5 py-2.5 rounded-xl bg-white text-orange-700 hover:bg-orange-50 font-bold text-xs sm:text-sm shadow-lg shadow-black/20 transition-transform active:scale-95 flex items-center space-x-2"
                >
                  <span>🎨 पोस्टर बनाएं</span>
                </button>
                <button
                  onClick={onNavigateToWarRoom}
                  className="px-4 sm:px-5 py-2.5 rounded-xl bg-slate-900/80 hover:bg-slate-900 text-white font-bold text-xs sm:text-sm border border-white/20 backdrop-blur-md shadow-lg transition-transform active:scale-95 flex items-center space-x-2"
                >
                  <span className="w-2 h-2 rounded-full bg-red-500 animate-ping mr-1" />
                  <span>वॉर रूम में जुड़ें</span>
                </button>
                {onOpenSignUp && (
                  <button
                    onClick={onOpenSignUp}
                    className="px-4 sm:px-5 py-2.5 rounded-xl bg-amber-500/20 hover:bg-amber-500/30 text-amber-200 font-bold text-xs sm:text-sm border border-amber-400/30 backdrop-blur-md shadow-lg transition-transform active:scale-95 flex items-center space-x-2"
                  >
                    <UserPlus className="w-4 h-4" />
                    <span>सदस्यता / साइन अप</span>
                  </button>
                )}
              </div>
            </div>

            {/* Right side: Grand Official Leader Emblem "आपका विश्वास • सदैव आपके साथ" */}
            <div className="relative z-10 flex flex-col items-center shrink-0 self-center">
              <div className="relative group">
                <div className="absolute -inset-1.5 bg-gradient-to-r from-amber-400 via-orange-500 to-amber-300 rounded-3xl blur-md opacity-70 group-hover:opacity-100 transition duration-500 pointer-events-none" />
                <img
                  src="/aapka_vishwas_leader.png"
                  alt="आपका विश्वास • सदैव आपके साथ"
                  className="relative w-44 sm:w-52 lg:w-56 h-auto rounded-3xl shadow-2xl ring-2 ring-white/30 transform hover:scale-105 transition-transform duration-300 object-contain"
                />
              </div>
              <span className="mt-2 text-[10px] sm:text-[11px] font-black uppercase tracking-wider px-3 py-0.5 rounded-full bg-slate-950/85 text-amber-300 border border-amber-400/40 shadow">
                ★ आपका विश्वास • सदैव आपके साथ ★
              </span>
            </div>

            {/* Decorative background shape */}
            <div className="absolute -right-16 -bottom-16 w-64 h-64 rounded-full bg-white/10 blur-2xl pointer-events-none" />
          </div>

          {/* Quick Stats Grid */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 sm:gap-4">
            {[
              { label: 'सक्रिय बूथ', val: '284+', color: 'text-orange-400', bg: 'bg-orange-950/30' },
              { label: 'पंजीकृत कार्यकर्ता', val: '1,240+', color: 'text-blue-400', bg: 'bg-blue-950/30' },
              { label: 'साझा किए गए पोस्टर', val: '45.8K', color: 'text-emerald-400', bg: 'bg-emerald-950/30' },
              { label: 'जनसमस्या समाधान', val: '96%', color: 'text-amber-400', bg: 'bg-amber-950/30' },
            ].map((stat, i) => (
              <div key={i} className={`p-3.5 sm:p-4 rounded-2xl border border-slate-800/80 ${stat.bg} text-center hover:border-slate-700 transition-colors`}>
                <div className={`text-xl sm:text-2xl font-black ${stat.color}`}>{stat.val}</div>
                <div className="text-[11px] sm:text-xs text-slate-400 mt-1 font-medium">{stat.label}</div>
              </div>
            ))}
          </div>

          {/* Feed Items Stream with Category Filtering */}
          <div className="space-y-4">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <h3 className="text-base sm:text-lg font-bold text-white flex items-center space-x-2">
                <span>📢 मुख्य जनसंपर्क व समाचार</span>
              </h3>
              
              {/* Category Filter Pills */}
              <div className="flex items-center space-x-1.5 overflow-x-auto pb-1 scrollbar-none">
                {feedCategories.map((cat) => (
                  <button
                    key={cat}
                    onClick={() => setActiveFeedCategory(cat)}
                    className={`px-3 py-1 rounded-xl text-xs font-semibold whitespace-nowrap transition-colors ${
                      activeFeedCategory === cat
                        ? 'bg-orange-600 text-white'
                        : 'bg-slate-800 text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    {cat}
                  </button>
                ))}
              </div>
            </div>

            <div className="grid grid-cols-1 gap-5">
              {filteredFeed.map((item) => {
                const isLiked = likes[item.id];
                return (
                  <article
                    key={item.id}
                    className="bg-slate-900/90 border border-slate-800 rounded-3xl overflow-hidden shadow-xl hover:border-slate-700 transition-all"
                  >
                    {/* Header */}
                    <div className="p-3.5 sm:p-5 flex items-start justify-between">
                      <div className="flex items-center space-x-3">
                        <div className="w-10 h-10 sm:w-11 sm:h-11 rounded-full bg-gradient-to-tr from-orange-600 to-amber-500 flex items-center justify-center text-white font-bold text-sm shadow">
                          HR
                        </div>
                        <div>
                          <div className="flex items-center space-x-2">
                            <h4 className="font-bold text-sm sm:text-base text-white">हेमंत राठौर • आधिकारिक</h4>
                            <CheckCircle2 className="w-4 h-4 text-blue-400" />
                          </div>
                          <div className="flex items-center space-x-2 text-[11px] sm:text-xs text-slate-400 mt-0.5">
                            <span className="text-orange-400 font-medium">{item.categoryHi}</span>
                            <span>•</span>
                            <span>{item.timestampHi}</span>
                          </div>
                        </div>
                      </div>

                      <span className="text-[10px] sm:text-[11px] font-semibold px-2.5 py-1 rounded-full bg-slate-800 text-slate-300 border border-slate-700">
                        {item.type === 'ADMIN_GRAPHIC' ? '🖼️ ग्राफिक' : item.type === 'VIDEO' ? '🎬 वीडियो' : '📘 सोशल'}
                      </span>
                    </div>

                    {/* Body Content */}
                    <div className="px-3.5 sm:px-5 pb-3">
                      <h3 className="text-sm sm:text-lg font-bold text-slate-100 leading-snug">
                        {item.titleHi}
                      </h3>
                      <p className="mt-1.5 text-xs sm:text-sm text-slate-300 leading-relaxed">
                        {item.captionHi}
                      </p>
                    </div>

                    {/* Attached Image / Media Banner */}
                    {item.imageUrl && (
                      <div className="relative aspect-[16/9] sm:aspect-[2/1] bg-slate-950 overflow-hidden">
                        <img
                          src={item.imageUrl}
                          alt={item.titleHi}
                          className="w-full h-full object-cover transition-transform duration-500 hover:scale-105"
                          loading="lazy"
                        />
                        {item.posterHeadlineHi && (
                          <div className="absolute bottom-0 inset-x-0 bg-gradient-to-t from-slate-950 via-slate-950/80 to-transparent p-3 sm:p-4">
                            <p className="text-white font-bold text-xs sm:text-base">{item.posterHeadlineHi}</p>
                            <p className="text-orange-400 text-[10px] sm:text-xs mt-0.5">{item.posterSubHeadlineHi}</p>
                          </div>
                        )}
                      </div>
                    )}

                    {/* Footer Engagement Bar */}
                    <div className="px-3.5 sm:px-5 py-3 bg-slate-900/60 border-t border-slate-800 flex items-center justify-between text-xs text-slate-400">
                      <div className="flex items-center space-x-3 sm:space-x-6">
                        
                        <button
                          onClick={() => toggleLike(item.id)}
                          className={`flex items-center space-x-1.5 transition-colors ${
                            isLiked ? 'text-red-500 font-bold' : 'hover:text-white'
                          }`}
                        >
                          <Heart className={`w-4 h-4 ${isLiked ? 'fill-red-500' : ''}`} />
                          <span className="text-xs">{isLiked ? 'पसंद किया' : item.likesCount}</span>
                        </button>

                        <button className="flex items-center space-x-1.5 hover:text-white transition-colors">
                          <MessageCircle className="w-4 h-4" />
                          <span className="text-xs">{item.commentsCount} टिप्पणियाँ</span>
                        </button>

                      </div>

                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => handleShare(item)}
                          className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-orange-600/20 text-orange-400 hover:bg-orange-600/30 transition-colors font-medium border border-orange-500/30 text-xs"
                        >
                          <Share2 className="w-3.5 h-3.5" />
                          <span>{shareSuccess === item.id ? 'लिंक कॉपी!' : 'साझा करें'}</span>
                        </button>
                        
                        <button
                          onClick={onNavigateToPosters}
                          className="hidden sm:flex items-center space-x-1 px-3 py-1.5 rounded-lg bg-slate-800 text-slate-300 hover:bg-slate-700 transition-colors text-xs"
                        >
                          <Download className="w-3.5 h-3.5" />
                          <span>पोस्टर बनाएं</span>
                        </button>
                      </div>
                    </div>

                  </article>
                );
              })}
            </div>
          </div>

        </div>

        {/* Right Desktop Companion Sidebar (Optimized for PC Desktop & Tablet) */}
        <div className="hidden xl:flex xl:col-span-4 flex-col space-y-5 sticky top-20">
          
          {/* 1. Add to Home Screen & APK Download Widget */}
          {onOpenInstallApp && (
            <div className="bg-gradient-to-br from-orange-950/60 via-slate-900 to-slate-950 border border-orange-500/40 rounded-3xl p-5 shadow-xl relative overflow-hidden">
              <div className="flex items-center justify-between pb-3 border-b border-slate-800">
                <div className="flex items-center space-x-2">
                  <Smartphone className="w-4 h-4 text-orange-400" />
                  <h4 className="font-bold text-sm text-white">ऐप इंस्टॉल / APK</h4>
                </div>
                <span className="text-[10px] px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/30">
                  Android & Web
                </span>
              </div>

              <div className="mt-3.5 space-y-3">
                <p className="text-xs text-slate-300 leading-relaxed">
                  वेब ऐप को बिना ब्राउज़र एड्रेस बार के नेटिव ऐप की तरह चलाने के लिए होम स्क्रीन पर जोड़ें।
                </p>

                <div className="space-y-2">
                  <button
                    onClick={onOpenInstallApp}
                    className="w-full py-2.5 rounded-xl bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-500 hover:to-amber-500 text-white font-bold text-xs flex items-center justify-center space-x-2 shadow-lg shadow-orange-600/25 transition-all active:scale-95"
                  >
                    <Smartphone className="w-3.5 h-3.5" />
                    <span>होम स्क्रीन पर जोड़ें (Install)</span>
                  </button>

                  <a
                    href="/TeamRathore.apk"
                    download="TeamRathore_Official.apk"
                    className="w-full py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 font-semibold text-xs flex items-center justify-center space-x-2 border border-slate-700 transition-colors"
                  >
                    <Download className="w-3.5 h-3.5 text-emerald-400" />
                    <span>Android APK डाउनलोड (~30 MB)</span>
                  </a>
                </div>
              </div>
            </div>
          )}

          {/* 2. Live War Room Companion Card */}
          <div className="bg-gradient-to-br from-slate-900 via-indigo-950/40 to-slate-900 border border-indigo-500/30 rounded-3xl p-5 shadow-xl relative overflow-hidden">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <div className="flex items-center space-x-2">
                <span className="w-2.5 h-2.5 rounded-full bg-red-500 animate-ping" />
                <h4 className="font-bold text-sm text-white">केंद्रीय वॉर रूम (Live)</h4>
              </div>
              <span className="text-[10px] px-2 py-0.5 rounded-full bg-red-500/20 text-red-400 font-bold border border-red-500/30">
                24x7 ऑनलाइन
              </span>
            </div>

            <div className="mt-3.5 space-y-3">
              <p className="text-xs text-slate-300">
                विधानसभा क्षेत्र के समस्त 284 बूथों की लाइव समीक्षा एवं दैनिक ऑडियो-वीडियो संवाद सक्रिय है।
              </p>
              
              <div className="grid grid-cols-2 gap-2 text-center">
                <div className="p-2.5 rounded-xl bg-slate-950/60 border border-slate-800">
                  <div className="text-lg font-extrabold text-orange-400">284 / 284</div>
                  <div className="text-[10px] text-slate-400">बूथ निगरानी</div>
                </div>
                <div className="p-2.5 rounded-xl bg-slate-950/60 border border-slate-800">
                  <div className="text-lg font-extrabold text-emerald-400">142+</div>
                  <div className="text-[10px] text-slate-400">लाइव कार्यकर्ता</div>
                </div>
              </div>

              <button
                onClick={onNavigateToWarRoom}
                className="w-full py-2.5 rounded-xl bg-gradient-to-r from-red-600 to-orange-600 hover:from-red-500 hover:to-orange-500 text-white font-bold text-xs flex items-center justify-center space-x-2 shadow-lg shadow-red-600/20 transition-all active:scale-95"
              >
                <Radio className="w-3.5 h-3.5 animate-pulse" />
                <span>वॉर रूम में सीधे जुड़ें</span>
              </button>
            </div>
          </div>

          {/* 3. Trending Posters Studio Teaser with All Lined up Previews */}
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-5 shadow-xl">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <h4 className="font-bold text-sm text-white flex items-center space-x-2">
                <span>🎨 पोस्टर स्टूडियो थीम्स ({templates.length})</span>
              </h4>
              <button
                onClick={onNavigateToPosters}
                className="text-xs text-orange-400 hover:text-orange-300 flex items-center space-x-0.5"
              >
                <span>सभी देखें</span>
                <ChevronRight className="w-3.5 h-3.5" />
              </button>
            </div>

            <div className="mt-3.5 space-y-2.5">
              {(templates.length > 0 ? templates.slice(0, 3) : []).map((tpl, idx) => (
                <div
                  key={tpl.id || idx}
                  onClick={onNavigateToPosters}
                  className="p-3 rounded-2xl bg-slate-950/70 border border-slate-800 hover:border-orange-500/50 cursor-pointer transition-all flex items-center justify-between"
                >
                  <div className="flex items-center space-x-3">
                    <div
                      className="w-10 h-10 rounded-xl shadow shrink-0"
                      style={{
                        background: `linear-gradient(135deg, ${tpl.backgroundGradient.join(', ')})`
                      }}
                    />
                    <div>
                      <div className="text-xs font-bold text-white">{tpl.title}</div>
                      <div className="text-[10px] text-slate-400">{tpl.category} • 4:5 HD</div>
                    </div>
                  </div>
                  <span className="text-xs px-2.5 py-1 rounded-lg bg-orange-600/20 text-orange-400 font-semibold">
                    बनाएं
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* 4. Official Notices Bulletins */}
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-5 shadow-xl">
            <h4 className="font-bold text-sm text-white pb-3 border-b border-slate-800 flex items-center space-x-2">
              <span>📋 ताज़ा कार्यालय परिपत्र</span>
            </h4>
            <div className="mt-3.5 space-y-3">
              {notices.slice(0, 3).map((notice) => (
                <div key={notice.id} className="p-3 rounded-xl bg-slate-950/50 border border-slate-800/80">
                  <div className="flex items-center justify-between text-[10px] text-slate-400 mb-1">
                    <span className="font-semibold text-orange-400">{notice.departmentHi}</span>
                    <span>{notice.dateHi}</span>
                  </div>
                  <h5 className="text-xs font-bold text-white line-clamp-2 leading-relaxed">
                    {notice.titleHi}
                  </h5>
                </div>
              ))}
            </div>
          </div>

          {/* 5. Quick Helpline & Control Room */}
          <div className="bg-gradient-to-br from-slate-900 to-slate-950 border border-slate-800 rounded-3xl p-5 text-center">
            <div className="w-10 h-10 mx-auto rounded-full bg-orange-500/20 text-orange-400 flex items-center justify-center font-bold mb-2">
              📞
            </div>
            <h4 className="text-xs font-bold text-white">24x7 चुनाव नियंत्रण कक्ष</h4>
            <p className="text-[11px] text-slate-400 mt-1">ग्वालियर विधानसभा जनसंपर्क सेल</p>
            <div className="mt-3 flex items-center space-x-2">
              <a
                href="tel:+919876543210"
                className="flex-1 py-2 rounded-xl bg-orange-600 hover:bg-orange-500 text-white font-semibold text-xs transition-colors"
              >
                कॉल करें
              </a>
              <a
                href="https://api.whatsapp.com/send?phone=919876543210"
                target="_blank"
                rel="noreferrer"
                className="flex-1 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-xs transition-colors"
              >
                व्हाट्सएप
              </a>
            </div>
          </div>

        </div>

      </div>

    </div>
  );
};
