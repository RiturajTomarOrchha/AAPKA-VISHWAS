import React, { useRef, useEffect, useState } from 'react';
import { FeedItem, UserProfile } from '../types';
import { X, Download, Share2, Sparkles, Check, Phone, User, Award, RefreshCw } from 'lucide-react';
import confetti from 'canvas-confetti';

interface PersonalizedJacketModalProps {
  isOpen: boolean;
  onClose: () => void;
  feedItem: FeedItem | null;
  user: UserProfile;
  onNavigateToStudio?: () => void;
}

export const PersonalizedJacketModal: React.FC<PersonalizedJacketModalProps> = ({
  isOpen,
  onClose,
  feedItem,
  user,
  onNavigateToStudio
}) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [userName, setUserName] = useState(user.fullName || 'रितुराज तोमर');
  const [designation, setDesignation] = useState(user.designation || 'सक्रिय कार्यकर्ता • नरेला');
  const [mobile, setMobile] = useState(user.mobile || '+91 98765 43210');
  const [userPhoto, setUserPhoto] = useState<string | null>(user.customPhotoUri || null);
  const [userLogo, setUserLogo] = useState<string | null>(user.customLogoUri || null);
  const [leaderImg, setLeaderImg] = useState<HTMLImageElement | null>(null);
  const [downloadSuccess, setDownloadSuccess] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Sync with user prop if user updates
  useEffect(() => {
    setUserName(user.fullName || 'रितुराज तोमर');
    setDesignation(user.designation || 'सक्रिय कार्यकर्ता • नरेला');
    setMobile(user.mobile || '+91 98765 43210');
    setUserPhoto(user.customPhotoUri || null);
    setUserLogo(user.customLogoUri || null);
  }, [user]);

  // Load Leader image
  useEffect(() => {
    const img = new Image();
    img.src = '/aapka_vishwas_leader.png';
    img.crossOrigin = 'anonymous';
    img.onload = () => setLeaderImg(img);
  }, []);

  // Draw personalized poster on canvas
  useEffect(() => {
    if (!isOpen || !feedItem) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const width = 1080;
    const height = 1350; // 4:5 HD banner aspect ratio
    canvas.width = width;
    canvas.height = height;

    // 1. Saffron & Green BJP Gradient Background
    const bgGrad = ctx.createLinearGradient(0, 0, width, height);
    bgGrad.addColorStop(0, '#ea580c'); // Saffron
    bgGrad.addColorStop(0.35, '#1e293b'); // Deep Navy/Slate
    bgGrad.addColorStop(0.75, '#0f172a'); // Rich Dark Slate
    bgGrad.addColorStop(1, '#15803d'); // BJP Emerald Green
    ctx.fillStyle = bgGrad;
    ctx.fillRect(0, 0, width, height);

    // Subtle Radial Center Glow
    const glow = ctx.createRadialGradient(width / 2, height / 2.5, 40, width / 2, height / 2.5, 550);
    glow.addColorStop(0, 'rgba(255, 255, 255, 0.15)');
    glow.addColorStop(1, 'rgba(0, 0, 0, 0.4)');
    ctx.fillStyle = glow;
    ctx.fillRect(0, 0, width, height);

    // 2. Official Header Jacket (भगवा-हरा तिरंगा पट्टी व शीर्षक)
    ctx.fillStyle = '#0f172a';
    ctx.fillRect(30, 30, width - 60, 130);

    // Saffron top border
    ctx.fillStyle = '#ea580c';
    ctx.fillRect(30, 30, width - 60, 8);

    // Green bottom border
    ctx.fillStyle = '#16a34a';
    ctx.fillRect(30, 152, width - 60, 8);

    // Header Leader Emblem / Badge on the left
    if (leaderImg) {
      try {
        ctx.save();
        ctx.beginPath();
        ctx.arc(105, 95, 45, 0, Math.PI * 2);
        ctx.closePath();
        ctx.clip();
        ctx.drawImage(leaderImg, 60, 50, 90, 90);
        ctx.restore();

        // Golden circle border around leader
        ctx.strokeStyle = '#f59e0b';
        ctx.lineWidth = 3;
        ctx.beginPath();
        ctx.arc(105, 95, 45, 0, Math.PI * 2);
        ctx.stroke();
      } catch {
        // fallback
      }
    }

    // Header Title
    ctx.fillStyle = '#ffffff';
    ctx.font = 'bold 36px "Noto Sans Devanagari", sans-serif';
    ctx.textAlign = 'left';
    ctx.fillText('आपका विश्वास • सदैव आपके साथ', 170, 88);

    ctx.fillStyle = '#fbbf24';
    ctx.font = '600 22px "Noto Sans Devanagari", sans-serif';
    ctx.fillText('नरेला विधानसभा (151), भोपाल • जनसेवा ही संकल्प', 170, 126);

    // 3. Central Graphic Area (Admin's Daily Graphic Content)
    const contentY = 190;
    const contentH = 880;

    // Card background
    ctx.fillStyle = 'rgba(15, 23, 42, 0.75)';
    ctx.fillRect(40, contentY, width - 80, contentH);
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.12)';
    ctx.lineWidth = 2;
    ctx.strokeRect(40, contentY, width - 80, contentH);

    // Category Badge
    ctx.fillStyle = '#ea580c';
    ctx.beginPath();
    ctx.roundRect(width / 2 - 160, contentY + 30, 320, 48, 24);
    ctx.fill();

    ctx.fillStyle = '#ffffff';
    ctx.font = 'bold 22px "Noto Sans Devanagari", sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText(feedItem.categoryHi || 'दैनिक आधिकारिक ग्राफिक', width / 2, contentY + 62);

    // Main Graphic Headline
    ctx.fillStyle = '#ffffff';
    ctx.font = 'bold 44px "Noto Sans Devanagari", sans-serif';
    ctx.textAlign = 'center';
    const title = feedItem.posterHeadlineHi || feedItem.titleHi;
    const words = title.split(' ');
    let line1 = words.slice(0, Math.ceil(words.length / 2)).join(' ');
    let line2 = words.slice(Math.ceil(words.length / 2)).join(' ');

    if (line2) {
      ctx.fillText(line1, width / 2, contentY + 160);
      ctx.fillText(line2, width / 2, contentY + 225);
    } else {
      ctx.fillText(line1, width / 2, contentY + 180);
    }

    // Sub-Headline / Slogan
    ctx.fillStyle = '#f59e0b';
    ctx.font = '600 28px "Noto Sans Devanagari", sans-serif';
    ctx.fillText(
      feedItem.posterSubHeadlineHi || 'नरेला विधानसभा में विकास, सेवा और सुशासन ही हमारा संकल्प',
      width / 2,
      contentY + 290
    );

    // Leader Large Portrait in center
    if (leaderImg) {
      try {
        const imgSize = 440;
        const imgX = (width - imgSize) / 2;
        const imgY = contentY + 330;
        ctx.save();
        ctx.drawImage(leaderImg, imgX, imgY, imgSize, imgSize);
        ctx.restore();
      } catch {
        // fallback
      }
    }

    // Leader name banner beneath portrait
    ctx.fillStyle = 'rgba(2, 6, 23, 0.9)';
    ctx.beginPath();
    ctx.roundRect(width / 2 - 250, contentY + 770, 500, 60, 30);
    ctx.fill();
    ctx.strokeStyle = '#ea580c';
    ctx.lineWidth = 2.5;
    ctx.stroke();

    ctx.fillStyle = '#ffffff';
    ctx.font = 'bold 28px "Noto Sans Devanagari", sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText('हेमंत राठौर • मुख्य जनसेवक', width / 2, contentY + 810);

    // 4. Personalized Footer Jacket (कार्यकर्ता की फोटो, नाम, पद व मोबाइल)
    const footerY = 1100;
    const footerH = 220;

    // Footer Base
    ctx.fillStyle = '#020617';
    ctx.fillRect(30, footerY, width - 60, footerH);

    // Green Top Accent Ribbon on Footer
    ctx.fillStyle = '#16a34a';
    ctx.fillRect(30, footerY, width - 60, 8);

    // Golden Side Trim
    ctx.fillStyle = '#f59e0b';
    ctx.fillRect(30, footerY + 8, 8, footerH - 8);
    ctx.fillRect(width - 38, footerY + 8, 8, footerH - 8);

    // Worker Photo Frame (Left Side of Footer)
    const photoCenterX = 150;
    const photoCenterY = footerY + footerH / 2;
    const photoRadius = 75;

    // If worker uploaded photo
    if (userPhoto) {
      const userImg = new Image();
      userImg.src = userPhoto;
      try {
        ctx.save();
        ctx.beginPath();
        ctx.arc(photoCenterX, photoCenterY, photoRadius, 0, Math.PI * 2);
        ctx.closePath();
        ctx.clip();
        ctx.drawImage(userImg, photoCenterX - photoRadius, photoCenterY - photoRadius, photoRadius * 2, photoRadius * 2);
        ctx.restore();
      } catch {
        // fallback circle
        drawDefaultAvatar(ctx, photoCenterX, photoCenterY, photoRadius, userName);
      }
    } else {
      drawDefaultAvatar(ctx, photoCenterX, photoCenterY, photoRadius, userName);
    }

    // Outer golden/emerald rings
    ctx.strokeStyle = '#f59e0b';
    ctx.lineWidth = 4;
    ctx.beginPath();
    ctx.arc(photoCenterX, photoCenterY, photoRadius, 0, Math.PI * 2);
    ctx.stroke();

    // Worker Information (Center-Right in Footer)
    ctx.textAlign = 'left';

    // "सादर प्रेषक / शुभकामना संदेश" Small Tag
    ctx.fillStyle = '#10b981';
    ctx.font = 'bold 20px "Noto Sans Devanagari", sans-serif';
    ctx.fillText('सक्रिय कार्यकर्ता • आपका विश्वास परिवार', 260, footerY + 52);

    // Worker Name
    ctx.fillStyle = '#ffffff';
    ctx.font = 'bold 44px "Noto Sans Devanagari", sans-serif';
    ctx.fillText(userName, 260, footerY + 106);

    // Worker Designation & Assembly
    ctx.fillStyle = '#f59e0b';
    ctx.font = '600 24px "Noto Sans Devanagari", sans-serif';
    ctx.fillText(`${designation} • नरेला विधानसभा`, 260, footerY + 150);

    // Mobile Number Badge
    ctx.fillStyle = '#38bdf8';
    ctx.font = 'bold 22px "Noto Sans Devanagari", sans-serif';
    ctx.fillText(`📱 ${mobile}`, 260, footerY + 188);

    // Team / Party Badge on far right
    ctx.fillStyle = 'rgba(234, 88, 12, 0.2)';
    ctx.beginPath();
    ctx.roundRect(width - 240, footerY + 40, 180, 140, 20);
    ctx.fill();
    ctx.strokeStyle = 'rgba(234, 88, 12, 0.6)';
    ctx.lineWidth = 2;
    ctx.stroke();

    ctx.fillStyle = '#ea580c';
    ctx.font = 'bold 24px "Noto Sans Devanagari", sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText('🚩 भाजपा', width - 150, footerY + 95);

    ctx.fillStyle = '#ffffff';
    ctx.font = '600 16px "Noto Sans Devanagari", sans-serif';
    ctx.fillText('नरेला, भोपाल', width - 150, footerY + 135);

  }, [isOpen, feedItem, userName, designation, mobile, userPhoto, userLogo, leaderImg]);

  const drawDefaultAvatar = (
    ctx: CanvasRenderingContext2D,
    x: number,
    y: number,
    radius: number,
    name: string
  ) => {
    ctx.fillStyle = '#ea580c';
    ctx.beginPath();
    ctx.arc(x, y, radius, 0, Math.PI * 2);
    ctx.fill();

    ctx.fillStyle = '#ffffff';
    ctx.font = 'bold 50px "Noto Sans Devanagari", sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText(name ? name[0] : 'र', x, y + 18);
  };

  const handleDownload = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const link = document.createElement('a');
    link.download = `AapkaVishwas_Narela_${Date.now()}.png`;
    link.href = canvas.toDataURL('image/png');
    link.click();

    setDownloadSuccess(true);
    setToastMessage('HD पोस्टर सफलतापूर्वक डाउनलोड हो गया!');
    confetti({ particleCount: 80, spread: 70, origin: { y: 0.6 } });
    setTimeout(() => {
      setDownloadSuccess(false);
      setToastMessage(null);
    }, 4000);
  };

  const handleShareWhatsApp = () => {
    const caption = `🚩 *आपका विश्वास • सदैव आपके साथ*\n\n"${feedItem?.titleHi}"\n\n— *${userName}* (${designation})\n📱 मो. ${mobile}\n\nनरेला विधानसभा (भोपाल)\n#AapkaVishwas #NarelaBhopal #BJPVikas`;
    const url = `https://api.whatsapp.com/send?text=${encodeURIComponent(caption)}`;
    const link = document.createElement('a');
    link.href = url;
    link.target = '_blank';
    link.rel = 'noopener noreferrer';
    link.click();

    setToastMessage('व्हाट्सएप शेयर लिंक खुल रहा है...');
    setTimeout(() => setToastMessage(null), 3000);
  };

  if (!isOpen || !feedItem) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-slate-950/85 backdrop-blur-md animate-in fade-in duration-200 overflow-y-auto">
      <div className="relative w-full max-w-4xl rounded-3xl bg-slate-900 border border-slate-800 p-4 sm:p-6 shadow-2xl text-white my-8 max-h-[90vh] flex flex-col overflow-hidden">
        
        {/* Toast Notification */}
        {toastMessage && (
          <div className="absolute top-4 left-1/2 -translate-x-1/2 z-50 bg-emerald-600 text-white px-5 py-2.5 rounded-2xl shadow-2xl font-bold text-xs flex items-center space-x-2 animate-bounce">
            <span>✓</span>
            <span>{toastMessage}</span>
          </div>
        )}

        {/* Modal Header */}
        <div className="flex items-center justify-between pb-3 border-b border-slate-800 shrink-0">
          <div className="flex items-center space-x-2.5">
            <div className="p-2 rounded-xl bg-orange-600/20 text-orange-400">
              <Sparkles className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-base sm:text-lg font-black text-white flex items-center space-x-2">
                <span>🎨 अपनी फोटो व नाम के साथ जैकेट फ्रेम लगाएं</span>
              </h3>
              <p className="text-xs text-slate-400">
                नरेला भाजपा केसरिया-हरा हेडर व कार्यकर्ता फुटर के साथ तुरंत HD में तैयार
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 rounded-full bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body: 2 Columns on desktop (Canvas Preview on left, Quick Fields on right) */}
        <div className="flex-1 overflow-y-auto py-4 grid grid-cols-1 md:grid-cols-12 gap-5 items-start">
          
          {/* Left Canvas Preview */}
          <div className="md:col-span-7 flex flex-col items-center">
            <div className="w-full max-w-[340px] sm:max-w-[380px] aspect-[4/5] bg-slate-950 rounded-2xl overflow-hidden shadow-2xl ring-2 ring-orange-500/30 flex items-center justify-center relative">
              <canvas
                ref={canvasRef}
                className="w-full h-full object-contain"
              />
            </div>
            <p className="text-[11px] text-slate-400 mt-2 text-center">
              1080x1350 HD 4:5 रेजोल्यूशन • व्हाट्सएप स्टेटस व फेसबुक के लिए अनुकूलित
            </p>
          </div>

          {/* Right Customization Controls */}
          <div className="md:col-span-5 space-y-4">
            
            <div className="p-3.5 rounded-2xl bg-slate-950/60 border border-slate-800 space-y-3">
              <div className="text-xs font-bold text-orange-400 uppercase tracking-wider flex items-center justify-between">
                <span>कार्यकर्ता फुटर विवरण</span>
                <span className="text-[10px] text-slate-400 font-normal">स्वतः सेट</span>
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-300 mb-1">
                  कार्यकर्ता का नाम
                </label>
                <div className="relative">
                  <User className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-2.5" />
                  <input
                    type="text"
                    value={userName}
                    onChange={(e) => setUserName(e.target.value)}
                    className="w-full pl-9 pr-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-300 mb-1">
                  पद / दायित्व
                </label>
                <div className="relative">
                  <Award className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-2.5" />
                  <input
                    type="text"
                    value={designation}
                    onChange={(e) => setDesignation(e.target.value)}
                    className="w-full pl-9 pr-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-300 mb-1">
                  मोबाइल नंबर (WhatsApp)
                </label>
                <div className="relative">
                  <Phone className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-2.5" />
                  <input
                    type="text"
                    value={mobile}
                    onChange={(e) => setMobile(e.target.value)}
                    className="w-full pl-9 pr-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                  />
                </div>
              </div>

              {/* Photo Upload quick toggle */}
              <div>
                <label className="block text-[11px] font-semibold text-slate-300 mb-1">
                  कार्यकर्ता फोटो
                </label>
                <label className="w-full py-2 px-3 rounded-xl bg-slate-900 hover:bg-slate-850 border border-slate-700 text-xs text-slate-300 flex items-center justify-between cursor-pointer transition-colors">
                  <span>{userPhoto ? '✓ फोटो सेट है (बदलें)' : 'फोटो चुनें'}</span>
                  <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => {
                      const file = e.target.files?.[0];
                      if (file) {
                        const reader = new FileReader();
                        reader.onload = (ev) => setUserPhoto(ev.target?.result as string);
                        reader.readAsDataURL(file);
                      }
                    }}
                    className="hidden"
                  />
                  <span className="text-[10px] text-orange-400 font-bold">ब्राउज़</span>
                </label>
              </div>

            </div>

            {/* Quick Action Buttons */}
            <div className="space-y-2.5 pt-1">
              <button
                onClick={handleDownload}
                className="w-full py-3 rounded-2xl bg-gradient-to-r from-orange-600 via-amber-600 to-emerald-600 hover:from-orange-500 hover:to-emerald-500 text-white font-bold text-xs sm:text-sm shadow-xl shadow-orange-600/30 flex items-center justify-center space-x-2 transition-all active:scale-95"
              >
                <Download className="w-4 h-4" />
                <span>HD पोस्टर डाउनलोड करें (PNG)</span>
              </button>

              <button
                onClick={handleShareWhatsApp}
                className="w-full py-2.5 rounded-2xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs flex items-center justify-center space-x-2 transition-all active:scale-95 shadow-md shadow-emerald-600/25"
              >
                <Share2 className="w-4 h-4" />
                <span>व्हाट्सएप पर सीधा शेयर करें</span>
              </button>

              {onNavigateToStudio && (
                <button
                  onClick={() => {
                    onClose();
                    onNavigateToStudio();
                  }}
                  className="w-full py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white font-semibold text-xs flex items-center justify-center space-x-1.5 transition-colors border border-slate-700"
                >
                  <RefreshCw className="w-3.5 h-3.5 text-orange-400" />
                  <span>डिजिटल पोस्टर स्टूडियो में खोलें</span>
                </button>
              )}
            </div>

          </div>

        </div>

      </div>
    </div>
  );
};
