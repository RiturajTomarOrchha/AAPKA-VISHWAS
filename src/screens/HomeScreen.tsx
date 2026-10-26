import React, { useState } from 'react';
import { FeedItem, ImportantNotice, UserProfile, WarRoomStats, ProgramItem, PosterTemplate, UserRole } from '../types';
import {
  Heart,
  Share2,
  Download,
  MessageCircle,
  AlertTriangle,
  ChevronRight,
  Sparkles,
  CheckCircle2,
  Flame,
  Radio,
  Phone,
  ArrowRight,
  Calendar,
  Users,
  Smartphone,
  UserPlus,
  Image as ImageIcon,
  Send,
  ExternalLink,
  ShieldCheck,
  CheckSquare,
  Award
} from 'lucide-react';
import { PersonalizedJacketModal } from '../components/PersonalizedJacketModal';
import confetti from 'canvas-confetti';

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
  onUpdateRole?: (role: UserRole) => void;
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
  onOpenSignUp,
  onUpdateRole
}) => {
  const [likes, setLikes] = useState<Record<string, boolean>>({});
  const [shareSuccess, setShareSuccess] = useState<string | null>(null);
  const [activeFeedCategory, setActiveFeedCategory] = useState<string>('सभी');
  const [selectedJacketItem, setSelectedJacketItem] = useState<FeedItem | null>(null);
  const [isJacketModalOpen, setIsJacketModalOpen] = useState(false);
  const [activeRole, setActiveRole] = useState<UserRole>(user.role || 'worker');

  // Grievance / Connect state for Supporters
  const [grievanceText, setGrievanceText] = useState('');
  const [grievanceSubmitted, setGrievanceSubmitted] = useState(false);

  const toggleLike = (id: string) => {
    setLikes(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const handleRoleChange = (role: UserRole) => {
    setActiveRole(role);
    onUpdateRole?.(role);
  };

  // Direct Raw WhatsApp share (Without Frame) as requested by user
  const handleRawWhatsAppShare = (item: FeedItem) => {
    const text = `🚩 *आपका विश्वास • सदैव आपके साथ*\n\n"${item.titleHi}"\n\n${item.captionHi}\n\n📲 आधिकारिक वेब पोर्टल: नरेला विधानसभा (भोपाल)\n#AapkaVishwas #NarelaBhopal #BJPVikas`;
    const url = `https://api.whatsapp.com/send?text=${encodeURIComponent(text)}`;
    const link = document.createElement('a');
    link.href = url;
    link.target = '_blank';
    link.rel = 'noopener noreferrer';
    link.click();

    setShareSuccess(item.id);
    setTimeout(() => setShareSuccess(null), 3000);
  };

  // Direct Raw Graphic Download
  const handleDownloadRawGraphic = (item: FeedItem) => {
    const link = document.createElement('a');
    link.href = item.imageUrl || '/aapka_vishwas_leader.png';
    link.download = `AapkaVishwas_RawGraphic_${Date.now()}.png`;
    link.click();
    confetti({ particleCount: 50, spread: 60 });
  };

  // Open Personalized Jacket Modal
  const handleOpenJacketModal = (item: FeedItem) => {
    setSelectedJacketItem(item);
    setIsJacketModalOpen(true);
  };

  // Facebook Direct Share
  const handleFacebookShare = (item: FeedItem) => {
    const shareUrl = item.facebookUrl || 'https://www.facebook.com';
    const link = document.createElement('a');
    link.href = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(shareUrl)}`;
    link.target = '_blank';
    link.rel = 'noopener noreferrer';
    link.click();
  };

  // Facebook WhatsApp Share
  const handleFacebookWhatsAppShare = (item: FeedItem) => {
    const text = `📘 *फेसबुक पोस्ट: आपका विश्वास • नरेला भोपाल*\n\n"${item.titleHi}"\n\n${item.facebookContentHi || item.captionHi}\n\n🔗 फेसबुक पर पोस्ट देखें व शेयर करें:\n${item.facebookUrl || 'https://www.facebook.com'}\n\n#AapkaVishwas #NarelaBhopal`;
    const url = `https://api.whatsapp.com/send?text=${encodeURIComponent(text)}`;
    const link = document.createElement('a');
    link.href = url;
    link.target = '_blank';
    link.rel = 'noopener noreferrer';
    link.click();

    setShareSuccess(item.id);
    setTimeout(() => setShareSuccess(null), 3000);
  };

  const handleGrievanceSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!grievanceText.trim()) return;
    setGrievanceSubmitted(true);
    confetti({ particleCount: 60, spread: 70 });
    setTimeout(() => {
      setGrievanceSubmitted(false);
      setGrievanceText('');
    }, 4000);
  };

  const urgentNotice = notices.find(n => n.isUrgent);

  const feedCategories = ['सभी', 'दैनिक ग्राफिक', 'Facebook पोस्ट', 'सुविचार व संवाद'];

  const filteredFeed = activeFeedCategory === 'सभी'
    ? feedItems
    : feedItems.filter(item => {
        if (activeFeedCategory === 'दैनिक ग्राफिक') return item.type === 'ADMIN_GRAPHIC';
        if (activeFeedCategory === 'Facebook पोस्ट') return item.type === 'FACEBOOK_POST';
        if (activeFeedCategory === 'सुविचार व संवाद') return item.categoryHi.includes('सुविचार') || item.type === 'VIDEO';
        return true;
      });

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
                  अति आवश्यक कार्यालय परिपत्र
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

      {/* 2. Interactive Role Switcher Pill Bar (Adapts UI between Supporter, Worker, Office Bearer) */}
      <div className="p-3 rounded-2xl bg-slate-900/90 border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-3 shadow-lg">
        <div className="flex items-center space-x-2">
          <span className="text-xs font-bold text-slate-300 uppercase tracking-wider">
            भूमिका अनुसार इंटरफेस:
          </span>
          <span className="text-[10px] text-orange-400 font-semibold hidden md:inline">
            (समर्थक / कार्यकर्ता / पदाधिकारी अनुसार डैशबोर्ड बदलेगा)
          </span>
        </div>

        <div className="flex items-center space-x-1.5 overflow-x-auto pb-0.5 scrollbar-none">
          {[
            { id: 'supporter' as UserRole, label: 'समर्थक (Supporter)', color: 'bg-emerald-600 text-white' },
            { id: 'worker' as UserRole, label: 'सक्रिय कार्यकर्ता (Worker)', color: 'bg-orange-600 text-white' },
            { id: 'office_bearer' as UserRole, label: 'पदाधिकारी (Office Bearer)', color: 'bg-purple-600 text-white' },
          ].map(r => (
            <button
              key={r.id}
              onClick={() => handleRoleChange(r.id)}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap ${
                activeRole === r.id
                  ? `${r.color} shadow-lg ring-1 ring-white/30 scale-105`
                  : 'bg-slate-950/60 text-slate-400 hover:text-white border border-slate-800'
              }`}
            >
              {r.label}
            </button>
          ))}
        </div>
      </div>

      {/* 3. Main Dual Column Layout (PC & Tablet Web App Grid) */}
      <div className="grid grid-cols-1 xl:grid-cols-12 gap-6 items-start">
        
        {/* Left / Main Column (xl:col-span-8) */}
        <div className="xl:col-span-8 space-y-6">
          
          {/* Hero Welcome & Quick Action Card with BJP Saffron-Green Accent & Official Emblem */}
          <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-orange-600 via-amber-600 to-emerald-800 p-6 sm:p-8 text-white shadow-2xl flex flex-col md:flex-row md:items-center justify-between gap-6 border border-orange-400/30">
            <div className="relative z-10 max-w-xl">
              <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-white/20 backdrop-blur-md text-xs font-bold mb-2">
                <Sparkles className="w-3.5 h-3.5 text-amber-200" />
                <span>राष्ट्र सर्वोपरि • सेवा ही संगठन • नरेला विधानसभा (151)</span>
              </div>
              
              <h2 className="text-2xl sm:text-3xl font-black tracking-tight">
                स्वागतम्, {user.fullName || 'रितुराज तोमर'} जी!
              </h2>
              
              <div className="text-base sm:text-xl font-black text-amber-200 mt-1 flex items-center space-x-2">
                <span>🚩 आपका विश्वास • सदैव आपके साथ</span>
              </div>

              {/* Role-tailored intro paragraph */}
              <p className="mt-2 text-xs sm:text-sm text-orange-100 max-w-xl leading-relaxed">
                {activeRole === 'supporter' && (
                  'समर्थक मंच पर आपका स्वागत है। यहाँ से आप प्रतिदिन के आधिकारिक ग्राफिक्स अपनी फोटो व नाम के साथ 1-क्लिक में डाउनलोड व व्हाट्सएप पर शेयर कर सकते हैं।'
                )}
                {activeRole === 'worker' && (
                  'सक्रिय कार्यकर्ता मंच: नरेला विधानसभा के 284 बूथों को सशक्त बनाने के लिए दैनिक प्रचार सामग्री, डिजिटल I-Card व मतदाता पुनरीक्षण अभियान से जुड़ें।'
                )}
                {activeRole === 'office_bearer' && (
                  'पदाधिकारी वॉर रूम नियंत्रण कक्ष: बूथ स्तर के कार्यकर्ताओं की समीक्षा, टास्क मॉनिटरिंग, लाइव समीक्षा बैठक एवं जनसुनवाई का केंद्रीय मंच।'
                )}
              </p>

              {/* Dynamic Action Buttons based on Role */}
              <div className="mt-5 flex flex-wrap gap-2.5 sm:gap-3">
                <button
                  onClick={onNavigateToPosters}
                  className="px-4 sm:px-5 py-2.5 rounded-xl bg-white text-orange-700 hover:bg-orange-50 font-black text-xs sm:text-sm shadow-lg shadow-black/20 transition-transform active:scale-95 flex items-center space-x-2"
                >
                  <ImageIcon className="w-4 h-4 text-orange-600" />
                  <span>🎨 डिजिटल पोस्टर बनाएं</span>
                </button>

                {(activeRole === 'worker' || activeRole === 'office_bearer') && (
                  <button
                    onClick={onNavigateToWarRoom}
                    className="px-4 sm:px-5 py-2.5 rounded-xl bg-slate-900/85 hover:bg-slate-900 text-white font-bold text-xs sm:text-sm border border-emerald-400/30 backdrop-blur-md shadow-lg transition-transform active:scale-95 flex items-center space-x-2"
                  >
                    <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping mr-1" />
                    <span>केंद्रीय वॉर रूम (284 बूथ)</span>
                  </button>
                )}

                {activeRole === 'supporter' && (
                  <button
                    onClick={() => {
                      const el = document.getElementById('citizen-connect-box');
                      el?.scrollIntoView({ behavior: 'smooth' });
                    }}
                    className="px-4 sm:px-5 py-2.5 rounded-xl bg-emerald-700 hover:bg-emerald-600 text-white font-bold text-xs sm:text-sm shadow-lg transition-transform active:scale-95 flex items-center space-x-2"
                  >
                    <Send className="w-4 h-4" />
                    <span>सीधा सुझाव / समस्या भेजें</span>
                  </button>
                )}
              </div>
            </div>

            {/* Right side: Grand Official Leader Emblem */}
            <div className="relative z-10 flex flex-col items-center shrink-0 self-center">
              <div className="relative group">
                <div className="absolute -inset-1.5 bg-gradient-to-r from-amber-400 via-orange-500 to-emerald-400 rounded-3xl blur-md opacity-70 group-hover:opacity-100 transition duration-500 pointer-events-none" />
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
            <div className="absolute -right-16 -bottom-16 w-64 h-64 rounded-full bg-emerald-500/20 blur-2xl pointer-events-none" />
          </div>

          {/* Quick Metrics (Dynamic per Role) */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 sm:gap-4">
            {[
              { label: 'नरेला कुल बूथ', val: '284', color: 'text-orange-400', bg: 'bg-orange-950/30' },
              { label: 'सक्रिय कार्यकर्ता', val: '1,540+', color: 'text-emerald-400', bg: 'bg-emerald-950/30' },
              { label: 'दैनिक पोस्टर शेयर', val: '58.4K', color: 'text-amber-400', bg: 'bg-amber-950/30' },
              { label: 'जनसमस्या समाधान', val: '98%', color: 'text-blue-400', bg: 'bg-blue-950/30' },
            ].map((stat, i) => (
              <div key={i} className={`p-3.5 sm:p-4 rounded-2xl border border-slate-800/80 ${stat.bg} text-center hover:border-slate-700 transition-colors`}>
                <div className={`text-xl sm:text-2xl font-black ${stat.color}`}>{stat.val}</div>
                <div className="text-[11px] sm:text-xs text-slate-400 mt-1 font-medium">{stat.label}</div>
              </div>
            ))}
          </div>

          {/* Role-Specific Box A: Active Worker Field Task Board */}
          {activeRole === 'worker' && (
            <div className="p-4 sm:p-5 rounded-3xl bg-slate-900 border border-orange-500/30 shadow-xl space-y-3">
              <div className="flex items-center justify-between pb-2 border-b border-slate-800">
                <div className="flex items-center space-x-2">
                  <CheckSquare className="w-5 h-5 text-orange-400" />
                  <h4 className="font-bold text-sm text-white">दैनिक कार्यकर्ता फील्ड चेकलिस्ट (नरेला)</h4>
                </div>
                <span className="text-xs px-2.5 py-0.5 rounded-full bg-orange-500/20 text-orange-400 font-bold border border-orange-500/30">
                  3/4 पूर्ण
                </span>
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5 text-xs">
                <div className="p-2.5 rounded-xl bg-slate-950/60 border border-emerald-500/30 flex items-center space-x-2">
                  <span className="text-emerald-400 font-bold">✓</span>
                  <span className="text-slate-200">आज का दैनिक पोस्टर व्हाट्सएप स्टेटस पर लगाया</span>
                </div>
                <div className="p-2.5 rounded-xl bg-slate-950/60 border border-emerald-500/30 flex items-center space-x-2">
                  <span className="text-emerald-400 font-bold">✓</span>
                  <span className="text-slate-200">फेसबुक पोस्ट को पार्टी ग्रुप्स में साझा किया</span>
                </div>
                <div className="p-2.5 rounded-xl bg-slate-950/60 border border-orange-500/30 flex items-center space-x-2">
                  <span className="text-orange-400 font-bold">⏳</span>
                  <span className="text-slate-200">बूथ के 5 नए मतदाताओं का SIR फॉर्म सत्यापन</span>
                </div>
                <div className="p-2.5 rounded-xl bg-slate-950/60 border border-emerald-500/30 flex items-center space-x-2">
                  <span className="text-emerald-400 font-bold">✓</span>
                  <span className="text-slate-200">वार्ड बैठक की अग्रिम सूचना सदस्यों तक पहुंचाई</span>
                </div>
              </div>
            </div>
          )}

          {/* Role-Specific Box B: Office Bearer War Room Live Monitor */}
          {activeRole === 'office_bearer' && (
            <div className="p-4 sm:p-5 rounded-3xl bg-gradient-to-br from-slate-900 to-purple-950/40 border border-purple-500/30 shadow-xl space-y-3">
              <div className="flex items-center justify-between pb-2 border-b border-slate-800">
                <div className="flex items-center space-x-2">
                  <Flame className="w-5 h-5 text-purple-400" />
                  <h4 className="font-bold text-sm text-white">केंद्रीय वॉर रूम: 284 बूथ लाइव समन्वय</h4>
                </div>
                <span className="text-xs px-2.5 py-0.5 rounded-full bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/30 flex items-center space-x-1">
                  <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping mr-1" />
                  <span>मीटिंग सक्रिय</span>
                </span>
              </div>
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs text-slate-300">
                <div>
                  <div className="font-bold text-white text-sm">नरेला विधानसभा साप्ताहिक समीक्षा बैठक</div>
                  <div className="text-slate-400 mt-0.5">सभी मंडल अध्यक्ष व सेक्टर प्रभारियों की उपस्थिति अनिवार्य</div>
                </div>
                <button
                  onClick={onNavigateToWarRoom}
                  className="px-4 py-2 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white font-bold text-xs shadow-lg transition-all active:scale-95 shrink-0"
                >
                  लाइव मीटिंग में जुड़ें
                </button>
              </div>
            </div>
          )}

          {/* Role-Specific Box C: Supporter Direct Citizen Connect */}
          {activeRole === 'supporter' && (
            <div id="citizen-connect-box" className="p-4 sm:p-5 rounded-3xl bg-slate-900 border border-emerald-500/30 shadow-xl space-y-3">
              <div className="flex items-center justify-between pb-2 border-b border-slate-800">
                <div className="flex items-center space-x-2">
                  <Phone className="w-5 h-5 text-emerald-400" />
                  <h4 className="font-bold text-sm text-white">विधायक जी / कार्यालय को सीधा सुझाव व समस्या भेजें</h4>
                </div>
                <span className="text-xs text-emerald-400 font-semibold">नरेला जनसुनवाई</span>
              </div>
              {grievanceSubmitted ? (
                <div className="p-4 rounded-2xl bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 font-bold text-xs flex items-center space-x-2">
                  <span>✓</span>
                  <span>आपका संदेश विधायक जनसेवा कार्यालय नरेला में सफलतापूर्वक दर्ज हो गया है!</span>
                </div>
              ) : (
                <form onSubmit={handleGrievanceSubmit} className="space-y-3">
                  <textarea
                    rows={2}
                    value={grievanceText}
                    onChange={(e) => setGrievanceText(e.target.value)}
                    placeholder="अपने वार्ड या क्षेत्र की सड़क, पानी, बिजली या कोई अन्य जनसमस्या यहाँ लिखें..."
                    className="w-full p-3 rounded-2xl bg-slate-950 border border-slate-700 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-emerald-500"
                  />
                  <div className="flex justify-end">
                    <button
                      type="submit"
                      className="px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs shadow-md transition-all flex items-center space-x-1.5"
                    >
                      <Send className="w-3.5 h-3.5" />
                      <span>आवेदन भेजें</span>
                    </button>
                  </div>
                </form>
              )}
            </div>
          )}

          {/* Feed Stream with Filtering */}
          <div className="space-y-4">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <div>
                <h3 className="text-base sm:text-lg font-bold text-white flex items-center space-x-2">
                  <span>📢 दैनिक आधिकारिक प्रचार सामग्री व पोस्ट्स</span>
                </h3>
                <p className="text-xs text-slate-400">
                  एडमिन द्वारा जारी ग्राफिक्स पर अपनी फोटो-नाम लगाकर डाउनलोड व शेयर करें
                </p>
              </div>
              
              {/* Filter Pills */}
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
                const isFacebook = item.type === 'FACEBOOK_POST';
                const isGraphic = item.type === 'ADMIN_GRAPHIC';

                return (
                  <article
                    key={item.id}
                    className="bg-slate-900/90 border border-slate-800 rounded-3xl overflow-hidden shadow-xl hover:border-slate-700 transition-all"
                  >
                    {/* Card Header */}
                    <div className="p-3.5 sm:p-5 flex items-start justify-between">
                      <div className="flex items-center space-x-3">
                        <div className={`w-10 h-10 sm:w-11 sm:h-11 rounded-full flex items-center justify-center text-white font-bold text-sm shadow ${
                          isFacebook ? 'bg-blue-600' : 'bg-gradient-to-tr from-orange-600 to-emerald-600'
                        }`}>
                          {isFacebook ? 'f' : 'HR'}
                        </div>
                        <div>
                          <div className="flex items-center space-x-2">
                            <h4 className="font-bold text-sm sm:text-base text-white">
                              {item.facebookAuthor || 'हेमंत राठौर • आधिकारिक'}
                            </h4>
                            <CheckCircle2 className="w-4 h-4 text-blue-400" />
                          </div>
                          <div className="flex items-center space-x-2 text-[11px] sm:text-xs text-slate-400 mt-0.5">
                            <span className="text-orange-400 font-semibold">{item.categoryHi}</span>
                            <span>•</span>
                            <span>{item.timestampHi}</span>
                          </div>
                        </div>
                      </div>

                      <span className={`text-[10px] sm:text-[11px] font-bold px-2.5 py-1 rounded-full border ${
                        isFacebook
                          ? 'bg-blue-500/20 text-blue-400 border-blue-500/30'
                          : 'bg-orange-500/20 text-orange-400 border-orange-500/30'
                      }`}>
                        {isFacebook ? '📘 Facebook पोस्ट' : '🖼️ दैनिक ग्राफिक'}
                      </span>
                    </div>

                    {/* Card Title & Caption */}
                    <div className="px-3.5 sm:px-5 pb-3">
                      <h3 className="text-sm sm:text-lg font-bold text-slate-100 leading-snug">
                        {item.titleHi}
                      </h3>
                      <p className="mt-1.5 text-xs sm:text-sm text-slate-300 leading-relaxed">
                        {item.facebookContentHi || item.captionHi}
                      </p>
                    </div>

                    {/* Attached Image / Media Banner */}
                    {item.imageUrl && (
                      <div className="relative aspect-[16/9] sm:aspect-[2/1] bg-slate-950 overflow-hidden group">
                        <img
                          src={item.imageUrl}
                          alt={item.titleHi}
                          className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                          loading="lazy"
                        />
                        {item.posterHeadlineHi && (
                          <div className="absolute bottom-0 inset-x-0 bg-gradient-to-t from-slate-950 via-slate-950/85 to-transparent p-3 sm:p-4">
                            <p className="text-white font-black text-xs sm:text-base">{item.posterHeadlineHi}</p>
                            <p className="text-amber-300 text-[10px] sm:text-xs mt-0.5 font-semibold">{item.posterSubHeadlineHi}</p>
                          </div>
                        )}
                      </div>
                    )}

                    {/* Core Action Buttons as explicitly requested by user */}
                    <div className="p-3.5 sm:p-4 bg-slate-950/70 border-t border-slate-800 space-y-3">
                      
                      {/* Scenario A: Admin Graphic Options (Personalize Jacket Frame vs Raw WhatsApp Share vs Raw Download) */}
                      {isGraphic && (
                        <div className="flex flex-wrap items-center gap-2">
                          
                          {/* 1. Personalize with Jacket Frame (Photo + Name + Logo) */}
                          <button
                            onClick={() => handleOpenJacketModal(item)}
                            className="flex-1 sm:flex-none px-3.5 py-2 rounded-xl bg-gradient-to-r from-orange-600 via-amber-600 to-emerald-600 hover:from-orange-500 hover:to-emerald-500 text-white font-bold text-xs shadow-md shadow-orange-600/30 flex items-center justify-center space-x-1.5 transition-all active:scale-95"
                          >
                            <Sparkles className="w-3.5 h-3.5" />
                            <span>जैकेट फ्रेम लगाएं (अपनी फोटो व नाम सहित)</span>
                          </button>

                          {/* 2. Direct WhatsApp Share (Without Frame) */}
                          <button
                            onClick={() => handleRawWhatsAppShare(item)}
                            className="px-3 py-2 rounded-xl bg-emerald-600/20 hover:bg-emerald-600/30 text-emerald-400 font-bold text-xs border border-emerald-500/30 flex items-center space-x-1.5 transition-colors"
                            title="बिना किसी फ्रेम के सीधा व्हाट्सएप पर भेजें"
                          >
                            <Share2 className="w-3.5 h-3.5" />
                            <span>WhatsApp पर शेयर (बिना फ्रेम)</span>
                          </button>

                          {/* 3. Raw Graphic Download */}
                          <button
                            onClick={() => handleDownloadRawGraphic(item)}
                            className="px-3 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold text-xs border border-slate-700 flex items-center space-x-1.5 transition-colors"
                            title="मूल ग्राफिक डाउनलोड करें"
                          >
                            <Download className="w-3.5 h-3.5 text-orange-400" />
                            <span>डाउनलोड (रॉ)</span>
                          </button>

                        </div>
                      )}

                      {/* Scenario B: Facebook Post Sharing Options */}
                      {isFacebook && (
                        <div className="flex flex-wrap items-center gap-2">
                          
                          {/* 1. Direct Facebook Share Button */}
                          <button
                            onClick={() => handleFacebookShare(item)}
                            className="flex-1 sm:flex-none px-3.5 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs shadow-md shadow-blue-600/30 flex items-center justify-center space-x-1.5 transition-all active:scale-95"
                          >
                            <ExternalLink className="w-3.5 h-3.5" />
                            <span>Facebook पर शेयर करें</span>
                          </button>

                          {/* 2. Direct WhatsApp Share of Facebook post */}
                          <button
                            onClick={() => handleFacebookWhatsAppShare(item)}
                            className="flex-1 sm:flex-none px-3.5 py-2 rounded-xl bg-emerald-600/20 hover:bg-emerald-600/30 text-emerald-400 font-bold text-xs border border-emerald-500/30 flex items-center justify-center space-x-1.5 transition-colors"
                          >
                            <Share2 className="w-3.5 h-3.5" />
                            <span>WhatsApp पर पोस्ट भेजें</span>
                          </button>

                        </div>
                      )}

                      {/* Card Bottom Meta / Likes */}
                      <div className="flex items-center justify-between text-xs text-slate-400 pt-1 border-t border-slate-800/60">
                        <div className="flex items-center space-x-4">
                          <button
                            onClick={() => toggleLike(item.id)}
                            className={`flex items-center space-x-1.5 transition-colors ${
                              isLiked ? 'text-red-500 font-bold' : 'hover:text-white'
                            }`}
                          >
                            <Heart className={`w-4 h-4 ${isLiked ? 'fill-red-500' : ''}`} />
                            <span className="text-xs">{isLiked ? 'पसंद किया' : item.likesCount}</span>
                          </button>

                          <div className="flex items-center space-x-1.5">
                            <MessageCircle className="w-4 h-4" />
                            <span className="text-xs">{item.commentsCount} टिप्पणियाँ</span>
                          </div>
                        </div>

                        {shareSuccess === item.id && (
                          <span className="text-[11px] text-emerald-400 font-bold animate-pulse">
                            ✓ लिंक सफलतापूर्वक भेजा गया!
                          </span>
                        )}
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
          
          {/* 1. Official Digital I-Card Card */}
          <div className="bg-gradient-to-br from-slate-900 to-slate-950 border border-slate-800 rounded-3xl p-5 shadow-xl space-y-3">
            <div className="flex items-center justify-between pb-2 border-b border-slate-800">
              <span className="text-xs font-bold text-orange-400 uppercase tracking-wider">
                डिजिटल पहचान पत्र (I-Card)
              </span>
              <span className="text-[10px] text-emerald-400 font-bold px-2 py-0.5 rounded-full bg-emerald-500/20 border border-emerald-500/30">
                सत्यापित सदस्य
              </span>
            </div>
            
            <div className="p-3.5 rounded-2xl bg-slate-950/70 border border-slate-800 flex items-center space-x-3">
              <div className="w-12 h-12 rounded-xl bg-orange-600/20 text-orange-400 flex items-center justify-center font-bold text-lg overflow-hidden border border-orange-500/30 shrink-0">
                {user.customPhotoUri ? (
                  <img src={user.customPhotoUri} alt="" className="w-full h-full object-cover" />
                ) : (
                  <span>{user.fullName ? user.fullName[0] : 'र'}</span>
                )}
              </div>
              <div className="min-w-0">
                <div className="font-bold text-white text-xs truncate">{user.fullName || 'रितुराज तोमर'}</div>
                <div className="text-[11px] text-orange-400 font-medium truncate">{user.designation}</div>
                <div className="text-[10px] text-slate-400 truncate">नरेला विधानसभा (151), भोपाल</div>
              </div>
            </div>

            <button
              onClick={onNavigateToPosters}
              className="w-full py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 font-semibold text-xs flex items-center justify-center space-x-1.5 transition-colors border border-slate-700"
            >
              <Award className="w-4 h-4 text-orange-400" />
              <span>डिजिटल पोस्टर स्टूडियो में जाएं</span>
            </button>
          </div>

          {/* 2. Official Circulars & Notices */}
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-5 shadow-xl">
            <h4 className="font-bold text-sm text-white pb-3 border-b border-slate-800 flex items-center space-x-2">
              <span>📋 ताज़ा कार्यालय परिपत्र</span>
            </h4>
            <div className="mt-3.5 space-y-3">
              {notices.slice(0, 3).map((notice) => (
                <div key={notice.id} className="p-3 rounded-xl bg-slate-950/50 border border-slate-800/80">
                  <div className="flex items-center justify-between text-[10px] text-slate-400 mb-1">
                    <span className="font-semibold text-orange-400">{notice.departmentHi || 'कार्यालय प्रकोष्ठ'}</span>
                    <span>{notice.dateHi}</span>
                  </div>
                  <h5 className="text-xs font-bold text-white line-clamp-2 leading-relaxed">
                    {notice.titleHi}
                  </h5>
                </div>
              ))}
            </div>
          </div>

          {/* 3. Narela Control Room & Helpline */}
          <div className="bg-gradient-to-br from-slate-900 to-slate-950 border border-slate-800 rounded-3xl p-5 text-center">
            <div className="w-10 h-10 mx-auto rounded-full bg-orange-500/20 text-orange-400 flex items-center justify-center font-bold mb-2">
              📞
            </div>
            <h4 className="text-xs font-bold text-white">नरेला विधानसभा जनसंपर्क सेल</h4>
            <p className="text-[11px] text-slate-400 mt-1">अशोका गार्डन / सुभाष नगर कार्यालय, भोपाल</p>
            <p className="text-xs font-black text-amber-300 mt-2">हेल्पलाइन: +91 755-2489000</p>
          </div>

        </div>

      </div>

      {/* Personalized Jacket Frame Modal */}
      <PersonalizedJacketModal
        isOpen={isJacketModalOpen}
        onClose={() => setIsJacketModalOpen(false)}
        feedItem={selectedJacketItem}
        user={user}
        onNavigateToStudio={onNavigateToPosters}
      />

    </div>
  );
};
