import React, { useState, useRef, useEffect } from 'react';
import { UserProfile, PosterTemplate } from '../types';
import { Download, Share2, Upload, RefreshCw, Check, Sparkles, Image as ImageIcon, Phone, User, Award } from 'lucide-react';
import confetti from 'canvas-confetti';

interface PosterMakerProps {
  user: UserProfile;
  templates: PosterTemplate[];
  lang: 'hi' | 'en';
}

export const PosterMakerScreen: React.FC<PosterMakerProps> = ({ user, templates }) => {
  const [selectedTemplateIndex, setSelectedTemplateIndex] = useState(0);
  const [headline, setHeadline] = useState("विकास हर गाँव, हर घर • हमारी प्रतिबद्धता");
  const [subHeadline, setSubHeadline] = useState("जनता का साथ, हर समय आपके साथ • टीम राठौर");
  const [showPhoto, setShowPhoto] = useState(true);
  const [showMobile, setShowMobile] = useState(true);
  const [userName, setUserName] = useState(user.fullName || "रितुराज तोमर");
  const [designation, setDesignation] = useState(user.designation || "सक्रिय कार्यकर्ता • ग्वालियर");
  const [mobile, setMobile] = useState(user.mobile || "+91 98765 43210");
  const [userPhoto, setUserPhoto] = useState<string | null>(null);

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const template = templates[selectedTemplateIndex] || templates[0];

  // Draw Poster on HTML5 Canvas
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const width = 1080;
    const height = 1350; // 4:5 ratio standard political banner
    canvas.width = width;
    canvas.height = height;

    // 1. Background Gradient
    const bgGrad = ctx.createLinearGradient(0, 0, width, height);
    if (template.backgroundGradient.length >= 2) {
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
    const radial = ctx.createRadialGradient(width / 2, height / 3, 50, width / 2, height / 3, 500);
    radial.addColorStop(0, 'rgba(255, 255, 255, 0.25)');
    radial.addColorStop(1, 'rgba(0, 0, 0, 0.4)');
    ctx.fillStyle = radial;
    ctx.fillRect(0, 0, width, height);

    // 2. Top Header Ribbon (Team Rathore Banner)
    ctx.fillStyle = 'rgba(15, 23, 42, 0.85)';
    ctx.fillRect(40, 40, width - 80, 110);
    ctx.strokeStyle = '#f97316';
    ctx.lineWidth = 4;
    ctx.strokeRect(40, 40, width - 80, 110);

    // Top Header Text
    ctx.fillStyle = '#ffffff';
    ctx.font = 'bold 38px "Noto Sans Devanagari", sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText('टीम राठौर • जनसेवा ही संकल्प', width / 2, 95);

    ctx.fillStyle = '#f97316';
    ctx.font = '600 22px "Noto Sans Devanagari", sans-serif';
    ctx.fillText('राष्ट्र सर्वोपरि • विधानसभा क्षेत्र जनसंपर्क प्रकोष्ठ', width / 2, 130);

    // 3. Middle Slogan Card / Headline
    ctx.fillStyle = 'rgba(255, 255, 255, 0.96)';
    ctx.beginPath();
    ctx.roundRect(80, 220, width - 160, 420, 30);
    ctx.fill();
    ctx.shadowColor = 'rgba(0,0,0,0.3)';
    ctx.shadowBlur = 30;

    // Slogan Text
    ctx.shadowBlur = 0;
    ctx.fillStyle = '#991b1b';
    ctx.font = '900 52px "Noto Sans Devanagari", sans-serif';
    ctx.textAlign = 'center';
    
    // Word wrap headline
    const words = headline.split(' ');
    let line1 = words.slice(0, Math.ceil(words.length / 2)).join(' ');
    let line2 = words.slice(Math.ceil(words.length / 2)).join(' ');
    ctx.fillText(line1, width / 2, 330);
    if (line2) {
      ctx.fillText(line2, width / 2, 400);
    }

    ctx.fillStyle = '#1e3a8a';
    ctx.font = 'bold 32px "Noto Sans Devanagari", sans-serif';
    ctx.fillText(subHeadline, width / 2, 490);

    // Leader Emblem / Badge
    ctx.fillStyle = '#ea580c';
    ctx.font = 'bold 24px "Noto Sans Devanagari", sans-serif';
    ctx.fillText('★ जननायक हेमंत राठौर के नेतृत्व में अटूट विश्वास ★', width / 2, 570);

    // 4. Central Visual Art / Graphic Placeholder
    ctx.fillStyle = 'rgba(15, 23, 42, 0.7)';
    ctx.beginPath();
    ctx.roundRect(80, 680, width - 160, 360, 24);
    ctx.fill();

    ctx.fillStyle = '#f8fafc';
    ctx.font = 'bold 44px "Noto Sans Devanagari", sans-serif';
    ctx.fillText('॥ राष्ट्रहित सर्वोपरि ॥', width / 2, 820);

    ctx.fillStyle = '#fde047';
    ctx.font = '600 28px "Noto Sans Devanagari", sans-serif';
    ctx.fillText('“सत्य, सेवा और समर्पण ही हमारी राजनीति का मूल आधार है।”', width / 2, 890);

    ctx.fillStyle = '#94a3b8';
    ctx.font = '500 22px "Noto Sans Devanagari", sans-serif';
    ctx.fillText('— हेमंत राठौर (अध्यक्ष, टीम राठौर)', width / 2, 940);

    // 5. Lower Footer Banner: User Identity Strip
    const footerY = 1080;
    const footerHeight = 220;
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
    const photoCenterX = 180;
    const photoCenterY = footerY + (footerHeight / 2);
    const photoRadius = 75;

    ctx.save();
    ctx.beginPath();
    ctx.arc(photoCenterX, photoCenterY, photoRadius, 0, Math.PI * 2);
    ctx.strokeStyle = '#f97316';
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
      ctx.fillStyle = '#ea580c';
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
    ctx.fillText(userName, 290, footerY + 80);

    ctx.fillStyle = '#fbbf24';
    ctx.font = 'bold 28px "Noto Sans Devanagari", sans-serif';
    ctx.fillText(designation, 290, footerY + 128);

    if (showMobile) {
      ctx.fillStyle = '#94a3b8';
      ctx.font = '500 24px "Poppins", sans-serif';
      ctx.fillText(`📱 मो. ${mobile}`, 290, footerY + 172);
    }

  }, [selectedTemplateIndex, headline, subHeadline, showPhoto, showMobile, userName, designation, mobile, userPhoto, template]);

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
    link.download = `TeamRathore_Poster_${Date.now()}.png`;
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
      `🚩 *टीम राठौर - आधिकारिक डिजिटल पोस्टर*\n\n"${headline}"\n\n— *${userName}* (${designation})\n\n📲 टीम राठौर वेब पोर्टल से अपना पोस्टर बनाएं: https://ais-pre-4e6xqjiicgx6hix4eeszuh-496088405107.asia-southeast1.run.app`
    );
    window.open(`https://api.whatsapp.com/send?text=${text}`, '_blank');
  };

  const sloganPresets = [
    "विकास हर गाँव, हर घर • हमारी प्रतिबद्धता",
    "युवा शक्ति, देश की शक्ति • न्याय और रोजगार",
    "किसान का सम्मान • खुशहाल हिंदुस्तान",
    "समस्त क्षेत्रवासियों को हार्दिक बधाई एवं मंगलकामनाएँ",
    "जन-जन का सम्मान, क्षेत्र का स्वाभिमान"
  ];

  return (
    <div className="space-y-6 pb-20 md:pb-6">
      
      {/* Title Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h2 className="text-xl sm:text-2xl font-black text-white flex items-center space-x-2">
            <span>🎨 डिजिटल पोस्टर स्टूडियो</span>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-orange-600/30 text-orange-400 border border-orange-500/30">
              HD 4:5
            </span>
          </h2>
          <p className="text-xs text-slate-400 mt-1">
            अपने नाम, पद व फोटो के साथ उच्च गुणवत्ता का राजनीतिक पोस्टर बनाएं एवं डाउनलोड करें
          </p>
        </div>

        <div className="flex items-center space-x-2">
          <button
            onClick={downloadPoster}
            className="flex-1 sm:flex-none px-4 py-2.5 rounded-xl bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-500 hover:to-amber-500 text-white font-bold text-xs shadow-lg shadow-orange-600/20 flex items-center justify-center space-x-2 transition-all active:scale-95"
          >
            <Download className="w-4 h-4" />
            <span>डाउनलोड (PNG)</span>
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

      {/* Main Studio Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        
        {/* Left Side: Live Canvas Preview (Sticky on PC Chrome for effortless side-by-side editing) */}
        <div className="lg:col-span-6 flex flex-col items-center lg:sticky lg:top-24 z-10">
          <div className="w-full max-w-md bg-slate-900 border border-slate-800 rounded-3xl p-3 shadow-2xl">
            <div className="relative aspect-[4/5] w-full rounded-2xl overflow-hidden bg-slate-950 flex items-center justify-center shadow-inner">
              <canvas
                ref={canvasRef}
                className="w-full h-full object-contain pointer-events-none"
              />
            </div>
            <div className="mt-3 flex items-center justify-between px-2 text-xs text-slate-400">
              <span className="flex items-center space-x-1">
                <Sparkles className="w-3.5 h-3.5 text-amber-400" />
                <span>1080 x 1350 HD रिज़ॉल्यूशन</span>
              </span>
              <button
                onClick={() => setHeadline(sloganPresets[Math.floor(Math.random() * sloganPresets.length)])}
                className="text-orange-400 hover:text-orange-300 flex items-center space-x-1"
              >
                <RefreshCw className="w-3 h-3" />
                <span>स्लोगन बदलें</span>
              </button>
            </div>
          </div>
        </div>

        {/* Right Side: Customization Controls */}
        <div className="lg:col-span-6 space-y-5">
          
          {/* Template Theme Selector */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4 sm:p-5">
            <label className="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-3">
              1. पोस्टर थीम चुनें ({templates.length} थीम्स)
            </label>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-2.5">
              {templates.map((tpl, idx) => (
                <button
                  key={tpl.id}
                  onClick={() => setSelectedTemplateIndex(idx)}
                  className={`p-3 rounded-xl border text-left transition-all ${
                    selectedTemplateIndex === idx
                      ? 'border-orange-500 bg-orange-600/10 ring-2 ring-orange-500/20'
                      : 'border-slate-800 bg-slate-950/50 hover:border-slate-700'
                  }`}
                >
                  <div
                    className="w-full h-8 rounded-lg mb-2 shadow"
                    style={{
                      background: `linear-gradient(135deg, ${tpl.backgroundGradient.join(', ')})`
                    }}
                  />
                  <div className="text-xs font-bold text-white truncate">{tpl.title}</div>
                  <div className="text-[10px] text-slate-400">{tpl.category}</div>
                </button>
              ))}
            </div>
          </div>

          {/* Slogan Presets & Input */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4 sm:p-5 space-y-3">
            <label className="block text-xs font-bold text-slate-300 uppercase tracking-wider">
              2. मुख्य नारा व सुविचार (Headline)
            </label>
            
            <input
              type="text"
              value={headline}
              onChange={(e) => setHeadline(e.target.value)}
              className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-sm text-white focus:outline-none focus:border-orange-500"
              placeholder="मुख्य नारा दर्ज करें"
            />

            <div className="flex flex-wrap gap-1.5 pt-1">
              {sloganPresets.map((slogan, i) => (
                <button
                  key={i}
                  onClick={() => setHeadline(slogan)}
                  className="px-2.5 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-[11px] text-slate-300 transition-colors"
                >
                  {slogan}
                </button>
              ))}
            </div>
          </div>

          {/* User Profile Details Controls */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4 sm:p-5 space-y-4">
            <div className="flex items-center justify-between">
              <label className="text-xs font-bold text-slate-300 uppercase tracking-wider">
                3. कार्यकर्ता पहचान पट्टी (Footer Strip)
              </label>
              <div className="flex items-center space-x-3 text-xs">
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

            {/* Photo Upload Button */}
            <div className="flex items-center space-x-4">
              <div className="w-14 h-14 rounded-full bg-slate-800 border-2 border-orange-500 overflow-hidden flex items-center justify-center shrink-0">
                {userPhoto ? (
                  <img src={userPhoto} alt="User" className="w-full h-full object-cover" />
                ) : (
                  <User className="w-7 h-7 text-slate-400" />
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
                  <span>अपनी फोटो अपलोड करें</span>
                </button>
                <p className="text-[10px] text-slate-400 mt-1">PNG या JPG • गोल फ्रेम में स्वतः सेट होगी</p>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
              <div>
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">कार्यकर्ता का नाम</label>
                <input
                  type="text"
                  value={userName}
                  onChange={(e) => setUserName(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                />
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">पद / दायित्व</label>
                <input
                  type="text"
                  value={designation}
                  onChange={(e) => setDesignation(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                />
              </div>

              <div className="sm:col-span-2">
                <label className="block text-[11px] font-semibold text-slate-400 mb-1">मोबाइल नंबर</label>
                <input
                  type="text"
                  value={mobile}
                  onChange={(e) => setMobile(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                />
              </div>
            </div>

          </div>

        </div>

      </div>

    </div>
  );
};
