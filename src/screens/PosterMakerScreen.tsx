import React, { useState, useRef, useEffect } from 'react';
import { UserProfile, PosterTemplate } from '../types';
import { Download, Share2, Upload, RefreshCw, Sparkles, User, Filter, ZoomIn, ZoomOut, Check, Smartphone } from 'lucide-react';
import confetti from 'canvas-confetti';

interface PosterMakerProps {
  user: UserProfile;
  templates: PosterTemplate[];
  lang?: 'hi' | 'en';
}

export const PosterMakerScreen: React.FC<PosterMakerProps> = ({ user, templates }) => {
  const [selectedTemplateIndex, setSelectedTemplateIndex] = useState(0);
  const [selectedCategory, setSelectedCategory] = useState<string>('सभी');
  const [aspectRatio, setAspectRatio] = useState<'4:5' | '1:1' | '9:16'>('4:5');
  const [zoomLevel, setZoomLevel] = useState<number>(100);

  const [headline, setHeadline] = useState("आपका विश्वास • सदैव आपके साथ");
  const [subHeadline, setSubHeadline] = useState("जनता का साथ, हर समय आपके साथ");
  const [showPhoto, setShowPhoto] = useState(true);
  const [showMobile, setShowMobile] = useState(true);
  const [showLeaderBadge, setShowLeaderBadge] = useState(true);
  const [userName, setUserName] = useState(user.fullName || "रितुराज तोमर");
  const [designation, setDesignation] = useState(user.designation || "सक्रिय कार्यकर्ता • ग्वालियर");
  const [mobile, setMobile] = useState(user.mobile || "+91 98765 43210");
  const [userPhoto, setUserPhoto] = useState<string | null>(user.customPhotoUri || null);
  const [leaderImg, setLeaderImg] = useState<HTMLImageElement | null>(null);

  useEffect(() => {
    const img = new Image();
    img.src = '/aapka_vishwas_leader.png';
    img.onload = () => setLeaderImg(img);
  }, []);

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const template = templates[selectedTemplateIndex] || templates[0];

  // Categories list for template lineup
  const categories = ['सभी', 'विकास', 'अभियान', 'राष्ट्रीय पर्व', 'किसान', 'युवा', 'शुभकामनाएँ', 'स्मृति'];

  const filteredTemplates = selectedCategory === 'सभी'
    ? templates
    : templates.filter(t => t.category === selectedCategory || (selectedCategory === 'अभियान' && t.category === 'अभियान'));

  // Draw Poster on HTML5 Canvas
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let width = 1080;
    let height = 1350; // 4:5 ratio default
    if (aspectRatio === '1:1') {
      height = 1080;
    } else if (aspectRatio === '9:16') {
      height = 1920;
    }

    canvas.width = width;
    canvas.height = height;

    // 1. Background Gradient
    const bgGrad = ctx.createLinearGradient(0, 0, width, height);
    if (template.backgroundGradient && template.backgroundGradient.length >= 2) {
      bgGrad.addColorStop(0, template.backgroundGradient[0]);
      bgGrad.addColorStop(0.5, template.backgroundGradient[1] || template.backgroundGradient[0]);
      bgGrad.addColorStop(1, template.backgroundGradient[2] || template.backgroundGradient[1]);
    } else {
      bgGrad.addColorStop(0, '#ea580c');
      bgGrad.addColorStop(1, '#1e3a8a');
    }
    ctx.fillStyle = bgGrad;
    ctx.fillRect(0, 0, width, height);

    // Decorative Sunburst / Radial glow in the center
    const radial = ctx.createRadialGradient(width / 2, height / 3, 50, width / 2, height / 3, 550);
    radial.addColorStop(0, 'rgba(255, 255, 255, 0.28)');
    radial.addColorStop(1, 'rgba(0, 0, 0, 0.45)');
    ctx.fillStyle = radial;
    ctx.fillRect(0, 0, width, height);

    // 2. Top Header Ribbon (Aapka Vishwas Banner)
    ctx.fillStyle = 'rgba(15, 23, 42, 0.90)';
    ctx.fillRect(40, 40, width - 80, 110);
    ctx.strokeStyle = template.accentColor || '#f97316';
    ctx.lineWidth = 4;
    ctx.strokeRect(40, 40, width - 80, 110);

    // Top Header Text
    ctx.fillStyle = '#ffffff';
    ctx.font = 'bold 36px "Noto Sans Devanagari", sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText('आपका विश्वास • सदैव आपके साथ', width / 2, 92);

    ctx.fillStyle = template.accentColor || '#f97316';
    ctx.font = '600 22px "Noto Sans Devanagari", sans-serif';
    ctx.fillText(template.headerText || 'राष्ट्र सर्वोपरि • विधानसभा क्षेत्र जनसंपर्क प्रकोष्ठ', width / 2, 128);

    // 3. Middle Slogan Card / Headline
    const cardY = 200;
    const cardHeight = aspectRatio === '9:16' ? 500 : aspectRatio === '1:1' ? 360 : 420;
    ctx.fillStyle = 'rgba(255, 255, 255, 0.96)';
    ctx.beginPath();
    ctx.roundRect(80, cardY, width - 160, cardHeight, 28);
    ctx.fill();

    // Slogan Text inside card
    ctx.fillStyle = '#991b1b';
    ctx.font = '900 50px "Noto Sans Devanagari", sans-serif';
    ctx.textAlign = 'center';
    
    const words = headline.split(' ');
    const line1 = words.slice(0, Math.ceil(words.length / 2)).join(' ');
    const line2 = words.slice(Math.ceil(words.length / 2)).join(' ');
    ctx.fillText(line1, width / 2, cardY + 100);
    if (line2) {
      ctx.fillText(line2, width / 2, cardY + 170);
    }

    ctx.fillStyle = '#1e3a8a';
    ctx.font = 'bold 30px "Noto Sans Devanagari", sans-serif';
    ctx.fillText(subHeadline, width / 2, cardY + 250);

    // Leader Emblem / Badge
    ctx.fillStyle = template.accentColor || '#ea580c';
    ctx.font = 'bold 24px "Noto Sans Devanagari", sans-serif';
    ctx.fillText('★ जननायक हेमंत राठौर के नेतृत्व में अटूट विश्वास ★', width / 2, cardY + 320);

    // 4. Central Visual Art / Leader Official Emblem or Quotes Box
    const quoteY = cardY + cardHeight + 40;
    const quoteHeight = aspectRatio === '9:16' ? 440 : aspectRatio === '1:1' ? 220 : 340;

    if (showLeaderBadge && leaderImg) {
      const emblemSize = aspectRatio === '9:16' ? 440 : aspectRatio === '1:1' ? 240 : 360;
      const emblemX = (width - emblemSize) / 2;
      const emblemY = quoteY + (quoteHeight - emblemSize) / 2;

      ctx.save();
      ctx.shadowColor = 'rgba(234, 88, 12, 0.4)';
      ctx.shadowBlur = 35;
      ctx.drawImage(leaderImg, emblemX, emblemY, emblemSize, emblemSize);
      ctx.restore();
    } else {
      ctx.fillStyle = 'rgba(15, 23, 42, 0.75)';
      ctx.beginPath();
      ctx.roundRect(80, quoteY, width - 160, quoteHeight, 24);
      ctx.fill();

      ctx.fillStyle = '#f8fafc';
      ctx.font = 'bold 40px "Noto Sans Devanagari", sans-serif';
      ctx.fillText('॥ राष्ट्रहित सर्वोपरि ॥', width / 2, quoteY + 110);

      ctx.fillStyle = '#fde047';
      ctx.font = '600 26px "Noto Sans Devanagari", sans-serif';
      ctx.fillText('“सत्य, सेवा और समर्पण ही हमारी राजनीति का मूल आधार है।”', width / 2, quoteY + 180);

      ctx.fillStyle = '#94a3b8';
      ctx.font = '500 22px "Noto Sans Devanagari", sans-serif';
      ctx.fillText('— हेमंत राठौर (जनसेवक)', width / 2, quoteY + 235);
    }

    // 5. Lower Footer Banner: User Identity Strip
    const footerHeight = 220;
    const footerY = height - footerHeight;
    ctx.fillStyle = '#0f172a';
    ctx.fillRect(0, footerY, width, footerHeight);

    // Gold / Tricolor border line at top of footer
    const lineGrad = ctx.createLinearGradient(0, 0, width, 0);
    lineGrad.addColorStop(0, '#ea580c');
    lineGrad.addColorStop(0.5, '#ffffff');
    lineGrad.addColorStop(1, '#059669');
    ctx.fillStyle = lineGrad;
    ctx.fillRect(0, footerY, width, 8);

    // Draw user circular avatar or placeholder
    const photoCenterX = 170;
    const photoCenterY = footerY + (footerHeight / 2);
    const photoRadius = 75;

    ctx.save();
    ctx.beginPath();
    ctx.arc(photoCenterX, photoCenterY, photoRadius, 0, Math.PI * 2);
    ctx.strokeStyle = template.accentColor || '#f97316';
    ctx.lineWidth = 6;
    ctx.stroke();
    ctx.clip();

    if (showPhoto && userPhoto) {
      const img = new Image();
      img.src = userPhoto;
      img.onload = () => {
        ctx.drawImage(img, photoCenterX - photoRadius, photoCenterY - photoRadius, photoRadius * 2, photoRadius * 2);
      };
      if (img.complete) {
        ctx.drawImage(img, photoCenterX - photoRadius, photoCenterY - photoRadius, photoRadius * 2, photoRadius * 2);
      }
    } else {
      ctx.fillStyle = template.accentColor || '#ea580c';
      ctx.fillRect(photoCenterX - photoRadius, photoCenterY - photoRadius, photoRadius * 2, photoRadius * 2);
      ctx.fillStyle = '#ffffff';
      ctx.font = 'bold 50px "Noto Sans Devanagari", sans-serif';
      ctx.textAlign = 'center';
      ctx.fillText(userName ? userName[0] : 'र', photoCenterX, photoCenterY + 18);
    }
    ctx.restore();

    // User Text Details in Footer
    ctx.textAlign = 'left';
    ctx.fillStyle = '#ffffff';
    ctx.font = 'bold 44px "Noto Sans Devanagari", sans-serif';
    ctx.fillText(userName, 280, footerY + 80);

    ctx.fillStyle = '#fbbf24';
    ctx.font = 'bold 28px "Noto Sans Devanagari", sans-serif';
    ctx.fillText(designation, 280, footerY + 128);

    if (showMobile) {
      ctx.fillStyle = '#94a3b8';
      ctx.font = '500 24px "Poppins", sans-serif';
      ctx.fillText(`📱 मो. ${mobile}`, 280, footerY + 172);
    }

  }, [selectedTemplateIndex, headline, subHeadline, showPhoto, showMobile, showLeaderBadge, leaderImg, userName, designation, mobile, userPhoto, template, aspectRatio]);

  const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        setUserPhoto(event.target?.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const downloadPoster = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const link = document.createElement('a');
    link.download = `AapkaVishwas_Poster_${Date.now()}.png`;
    link.href = canvas.toDataURL('image/png');
    link.click();

    confetti({
      particleCount: 80,
      spread: 70,
      origin: { y: 0.6 }
    });
  };

  const shareToWhatsApp = () => {
    const text = encodeURIComponent(
      `🚩 *आपका विश्वास • सदैव आपके साथ*\n\n"${headline}"\n\n— *${userName}* (${designation})\n\n📲 आधिकारिक वेब पोर्टल से अपना पोस्टर बनाएं: https://github.com/RiturajTomarOrchha/AAPKA-VISHWAS`
    );
    window.open(`https://api.whatsapp.com/send?text=${text}`, '_blank');
  };

  const sloganPresets = [
    "आपका विश्वास • सदैव आपके साथ",
    "विकास हर गाँव, हर घर • हमारी प्रतिबद्धता",
    "युवा शक्ति, देश की शक्ति • न्याय और रोजगार",
    "किसान का सम्मान • खुशहाल हिंदुस्तान",
    "समस्त क्षेत्रवासियों को हार्दिक बधाई एवं मंगलकामनाएँ",
    "जन-जन का सम्मान, क्षेत्र का स्वाभिमान",
    "सत्य, सेवा और समर्पण ही हमारा धर्म",
    "अन्नदाता किसान खुशहाल, तभी देश खुशहाल",
    "हर बूथ मजबूत, हर कार्यकर्ता मुस्तैद"
  ];

  return (
    <div className="space-y-6">
      
      {/* Title Bar with Quick Action Buttons */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h2 className="text-xl sm:text-2xl font-black text-white flex items-center space-x-2">
            <span>🎨 डिजिटल पोस्टर स्टूडियो</span>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-orange-600/30 text-orange-400 border border-orange-500/30 font-semibold">
              HD Banner
            </span>
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            सभी {templates.length} थीम्स लाइन अप हैं • नाम, पद व फोटो के साथ उच्च गुणवत्ता का राजनीतिक पोस्टर बनाएं
          </p>
        </div>

        <div className="flex items-center space-x-2">
          <button
            onClick={downloadPoster}
            className="flex-1 sm:flex-none px-4 py-2.5 rounded-xl bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-500 hover:to-amber-500 text-white font-bold text-xs shadow-lg shadow-orange-600/25 flex items-center justify-center space-x-2 transition-all active:scale-95"
          >
            <Download className="w-4 h-4" />
            <span>डाउनलोड (HD PNG)</span>
          </button>
          
          <button
            onClick={shareToWhatsApp}
            className="px-4 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs shadow-lg shadow-emerald-600/20 flex items-center justify-center space-x-2 transition-all active:scale-95"
          >
            <Share2 className="w-4 h-4" />
            <span>व्हाट्सएप</span>
          </button>
        </div>
      </div>

      {/* Templates Line-up Bar with Category Filters (Requested by User) */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-4 sm:p-5 shadow-xl space-y-3">
        <div className="flex items-center justify-between">
          <label className="text-xs font-bold text-slate-300 uppercase tracking-wider flex items-center space-x-2">
            <Filter className="w-3.5 h-3.5 text-orange-400" />
            <span>1. सभी टेम्पलेट्स लाइन अप ({templates.length} डिजाइन्स)</span>
          </label>
          <span className="text-[11px] text-orange-400 font-semibold">
            चयनित: {template.title}
          </span>
        </div>

        {/* Category Pills */}
        <div className="flex items-center space-x-2 overflow-x-auto pb-1 scrollbar-none">
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-3 py-1 rounded-xl text-xs font-semibold whitespace-nowrap transition-colors ${
                selectedCategory === cat
                  ? 'bg-orange-600 text-white'
                  : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        {/* All Lined Up Templates Horizontal Carousel / Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-8 gap-2.5 pt-1">
          {filteredTemplates.map((tpl) => {
            const originalIndex = templates.findIndex(t => t.id === tpl.id);
            const isSelected = selectedTemplateIndex === originalIndex;
            return (
              <button
                key={tpl.id}
                onClick={() => setSelectedTemplateIndex(originalIndex >= 0 ? originalIndex : 0)}
                className={`p-2.5 rounded-2xl border text-left transition-all relative ${
                  isSelected
                    ? 'border-orange-500 bg-orange-600/15 ring-2 ring-orange-500/40 shadow-lg'
                    : 'border-slate-800 bg-slate-950/60 hover:border-slate-700'
                }`}
              >
                <div
                  className="w-full h-10 rounded-xl mb-1.5 shadow"
                  style={{
                    background: `linear-gradient(135deg, ${tpl.backgroundGradient.join(', ')})`
                  }}
                />
                <div className="text-[11px] font-bold text-white truncate">{tpl.title}</div>
                <div className="text-[9px] text-slate-400 truncate">{tpl.category}</div>
                {isSelected && (
                  <span className="absolute top-1.5 right-1.5 w-4 h-4 rounded-full bg-orange-600 text-white flex items-center justify-center text-[10px]">
                    ✓
                  </span>
                )}
              </button>
            );
          })}
        </div>
      </div>

      {/* Main Studio Dual Grid (Optimized for PC Desktop & Tablet) */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        
        {/* Left Side: Sticky Live Canvas Preview on PC */}
        <div className="lg:col-span-5 flex flex-col items-center lg:sticky lg:top-24 z-10 space-y-3">
          
          <div className="w-full max-w-md bg-slate-900 border border-slate-800 rounded-3xl p-3.5 shadow-2xl">
            
            {/* Aspect Ratio Selector Bar */}
            <div className="flex items-center justify-between pb-2 mb-2 border-b border-slate-800 text-xs">
              <span className="text-[11px] text-slate-400 font-medium">अनुपात (Ratio):</span>
              <div className="flex items-center space-x-1.5">
                {(['4:5', '1:1', '9:16'] as const).map((r) => (
                  <button
                    key={r}
                    onClick={() => setAspectRatio(r)}
                    className={`px-2 py-0.5 rounded-lg text-[10px] font-bold transition-colors ${
                      aspectRatio === r ? 'bg-orange-600 text-white' : 'bg-slate-800 text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    {r}
                  </button>
                ))}
              </div>
            </div>

            {/* Canvas Rendering Box */}
            <div
              className={`relative w-full rounded-2xl overflow-hidden bg-slate-950 flex items-center justify-center shadow-inner ${
                aspectRatio === '4:5' ? 'aspect-[4/5]' : aspectRatio === '1:1' ? 'aspect-square' : 'aspect-[9/16]'
              }`}
            >
              <canvas
                ref={canvasRef}
                className="w-full h-full object-contain pointer-events-none transition-transform"
                style={{ transform: `scale(${zoomLevel / 100})` }}
              />
            </div>

            {/* Canvas Controls Bar */}
            <div className="mt-3 flex items-center justify-between px-2 text-xs text-slate-400">
              <span className="flex items-center space-x-1 text-[11px]">
                <Sparkles className="w-3.5 h-3.5 text-amber-400" />
                <span>1080p Ultra HD</span>
              </span>

              <div className="flex items-center space-x-3">
                <button
                  onClick={() => setHeadline(sloganPresets[Math.floor(Math.random() * sloganPresets.length)])}
                  className="text-orange-400 hover:text-orange-300 flex items-center space-x-1 text-[11px]"
                >
                  <RefreshCw className="w-3 h-3" />
                  <span>रैंडम स्लोगन</span>
                </button>
              </div>
            </div>

          </div>

          {/* Quick Action under canvas */}
          <div className="w-full max-w-md grid grid-cols-2 gap-2">
            <button
              onClick={downloadPoster}
              className="py-2.5 rounded-xl bg-orange-600 hover:bg-orange-500 text-white font-bold text-xs flex items-center justify-center space-x-1.5 shadow"
            >
              <Download className="w-3.5 h-3.5" />
              <span>PNG डाउनलोड करें</span>
            </button>
            <button
              onClick={shareToWhatsApp}
              className="py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs flex items-center justify-center space-x-1.5 shadow"
            >
              <Share2 className="w-3.5 h-3.5" />
              <span>सीधा व्हाट्सएप शेयर</span>
            </button>
          </div>

        </div>

        {/* Right Side: Customization Controls (Tabs & Inputs) */}
        <div className="lg:col-span-7 space-y-5">
          
          {/* Slogan Customizer */}
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-5 space-y-3.5 shadow-xl">
            <div className="flex items-center justify-between">
              <label className="text-xs font-bold text-slate-300 uppercase tracking-wider">
                2. मुख्य नारा व सुविचार (Headline)
              </label>
              <span className="text-[10px] text-slate-400">अपना मनपसंद स्लोगन लिखें</span>
            </div>
            
            <input
              type="text"
              value={headline}
              onChange={(e) => setHeadline(e.target.value)}
              className="w-full px-4 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs sm:text-sm text-white focus:outline-none focus:border-orange-500"
              placeholder="मुख्य नारा दर्ज करें"
            />

            <div>
              <label className="block text-[11px] font-semibold text-slate-400 mb-1.5">
                उप-शीर्षक (Sub-headline)
              </label>
              <input
                type="text"
                value={subHeadline}
                onChange={(e) => setSubHeadline(e.target.value)}
                className="w-full px-4 py-2 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                placeholder="उप-शीर्षक दर्ज करें"
              />
            </div>

            {/* Quick Slogan Presets */}
            <div className="pt-1">
              <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block mb-2">
                त्वरित स्लोगन चुनें:
              </span>
              <div className="flex flex-wrap gap-1.5">
                {sloganPresets.map((slogan, i) => (
                  <button
                    key={i}
                    onClick={() => setHeadline(slogan)}
                    className="px-2.5 py-1 rounded-xl bg-slate-800 hover:bg-slate-700 text-[11px] text-slate-300 transition-colors"
                  >
                    {slogan}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* User Profile Details Controls */}
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-5 space-y-4 shadow-xl">
            <div className="flex items-center justify-between">
              <label className="text-xs font-bold text-slate-300 uppercase tracking-wider">
                3. कार्यकर्ता पहचान पट्टी (Footer Strip)
              </label>
              <div className="flex items-center space-x-3 text-xs">
                <label className="flex items-center space-x-1.5 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={showLeaderBadge}
                    onChange={(e) => setShowLeaderBadge(e.target.checked)}
                    className="accent-orange-500"
                  />
                  <span className="text-amber-400 font-semibold">नेताजी प्रतीक</span>
                </label>
                <label className="flex items-center space-x-1.5 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={showPhoto}
                    onChange={(e) => setShowPhoto(e.target.checked)}
                    className="accent-orange-500"
                  />
                  <span className="text-slate-300">फोटो</span>
                </label>
                <label className="flex items-center space-x-1.5 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={showMobile}
                    onChange={(e) => setShowMobile(e.target.checked)}
                    className="accent-orange-500"
                  />
                  <span className="text-slate-300">मोबाइल</span>
                </label>
              </div>
            </div>

            {/* Photo Upload Section */}
            <div className="flex items-center space-x-4 p-3 rounded-2xl bg-slate-950/60 border border-slate-800">
              <div className="w-16 h-16 rounded-full bg-slate-800 border-2 border-orange-500 overflow-hidden flex items-center justify-center shrink-0">
                {userPhoto ? (
                  <img src={userPhoto} alt="User" className="w-full h-full object-cover" />
                ) : (
                  <User className="w-8 h-8 text-slate-400" />
                )}
              </div>
              <div className="flex-1">
                <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handlePhotoUpload}
                  accept="image/*"
                  className="hidden"
                />
                <button
                  onClick={() => fileInputRef.current?.click()}
                  className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-slate-200 border border-slate-700 flex items-center space-x-2 transition-colors"
                >
                  <Upload className="w-4 h-4 text-orange-400" />
                  <span>अपनी फोटो बदलें / अपलोड करें</span>
                </button>
                <p className="text-[10px] text-slate-400 mt-1">PNG या JPG • गोल फ्रेम में स्वतः सेट होगी</p>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
              <div>
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">कार्यकर्ता का नाम</label>
                <input
                  type="text"
                  value={userName}
                  onChange={(e) => setUserName(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                />
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">पद / दायित्व</label>
                <input
                  type="text"
                  value={designation}
                  onChange={(e) => setDesignation(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                />
              </div>

              <div className="sm:col-span-2">
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">मोबाइल नंबर (WhatsApp)</label>
                <input
                  type="text"
                  value={mobile}
                  onChange={(e) => setMobile(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                />
              </div>
            </div>

          </div>

        </div>

      </div>

    </div>
  );
};
