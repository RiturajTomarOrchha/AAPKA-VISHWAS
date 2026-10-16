import React, { useState, useEffect } from 'react';
import { Download, Smartphone, Monitor, CheckCircle, X, Sparkles, ExternalLink, ShieldCheck } from 'lucide-react';
import confetti from 'canvas-confetti';

interface InstallAppModalProps {
  isOpen: boolean;
  onClose: () => void;
  deferredPrompt: any;
  onInstallSuccess?: () => void;
  isAfterSignUp?: boolean;
}

export const InstallAppModal: React.FC<InstallAppModalProps> = ({
  isOpen,
  onClose,
  deferredPrompt,
  onInstallSuccess,
  isAfterSignUp = false
}) => {
  const [installed, setInstalled] = useState(false);
  const [downloadingApk, setDownloadingApk] = useState(false);
  const [infoTip, setInfoTip] = useState<string | null>(null);

  useEffect(() => {
    if (isOpen && isAfterSignUp) {
      confetti({
        particleCount: 70,
        spread: 80,
        origin: { y: 0.5 }
      });
    }
  }, [isOpen, isAfterSignUp]);

  if (!isOpen) return null;

  const handleInstallPwa = async () => {
    if (deferredPrompt) {
      try {
        deferredPrompt.prompt();
        const { outcome } = await deferredPrompt.userChoice;
        if (outcome === 'accepted') {
          setInstalled(true);
          onInstallSuccess?.();
          confetti({ particleCount: 90, spread: 90 });
        }
      } catch (err) {
        console.error('Install prompt error:', err);
      }
    } else {
      // If beforeinstallprompt is not available (e.g. already installed or iOS)
      setInfoTip(
        'ब्राउज़र मेनू (⋮) पर क्लिक करें और "Add to Home screen" या "Install app" का विकल्प चुनें।'
      );
    }
  };

  const handleDownloadApk = () => {
    setDownloadingApk(true);
    const link = document.createElement('a');
    link.href = '/AapkaVishwas.apk';
    link.download = 'AapkaVishwas_Official.apk';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    setTimeout(() => {
      setDownloadingApk(false);
      confetti({ particleCount: 60, spread: 70 });
    }, 1500);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-in fade-in duration-200">
      <div className="relative w-full max-w-lg rounded-3xl bg-gradient-to-b from-slate-900 to-slate-950 border border-orange-500/40 p-6 sm:p-8 shadow-2xl shadow-orange-950/30 text-white overflow-hidden">
        
        {/* Glow background accent */}
        <div className="absolute -top-24 -right-24 w-48 h-48 rounded-full bg-orange-600/20 blur-3xl pointer-events-none" />
        <div className="absolute -bottom-24 -left-24 w-48 h-48 rounded-full bg-blue-600/20 blur-3xl pointer-events-none" />

        {/* Close button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-2 rounded-full bg-slate-800/80 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Header Icon & Tag */}
        <div className="flex items-center space-x-3 mb-4">
          <img
            src="/aapka_vishwas_leader.png"
            alt="आपका विश्वास"
            className="w-13 h-13 rounded-2xl object-cover ring-2 ring-orange-500/60 shadow-lg shadow-orange-500/25 shrink-0"
            style={{ width: '52px', height: '52px' }}
          />
          <div>
            <div className="flex items-center space-x-2">
              <span className="text-[11px] font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full bg-orange-500/20 text-orange-400 border border-orange-500/30">
                {isAfterSignUp ? '🎉 साइन अप पूर्ण' : '📲 आपका विश्वास • आधिकारिक ऐप'}
              </span>
              <span className="text-xs text-slate-400">PWA & Android</span>
            </div>
            <h3 className="text-base sm:text-xl font-black text-white mt-0.5">
              {isAfterSignUp
                ? 'स्वागतम्! अब ऐप को होम स्क्रीन पर जोड़ें'
                : 'आपका विश्वास • सदैव आपके साथ'}
            </h3>
          </div>
        </div>

        <p className="text-xs sm:text-sm text-slate-300 leading-relaxed mb-6">
          बिना किसी रुकावट 1-क्लिक में डिजिटल पोस्टर बनाने, लाइव वॉर रूम संवाद में जुड़ने और दैनिक प्रेस विज्ञप्ति प्राप्त करने के लिए ऐप को तुरंत इंस्टॉल करें।
        </p>

        {infoTip && (
          <div className="mb-4 p-3 rounded-xl bg-amber-500/20 border border-amber-500/40 text-amber-200 text-xs font-semibold flex items-center space-x-2">
            <span>ℹ️</span>
            <span>{infoTip}</span>
          </div>
        )}

        {/* Action Options */}
        <div className="space-y-3.5">
          
          {/* 1. Add to Home Screen (PWA - PC & Mobile Chrome) */}
          <button
            onClick={handleInstallPwa}
            className="w-full p-4 rounded-2xl bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-500 hover:to-amber-500 text-white font-bold text-sm shadow-xl shadow-orange-600/25 flex items-center justify-between transition-all active:scale-[0.98] border border-orange-400/40"
          >
            <div className="flex items-center space-x-3.5 text-left">
              <div className="p-2.5 rounded-xl bg-white/20 text-white">
                <Smartphone className="w-5 h-5" />
              </div>
              <div>
                <div className="text-sm font-extrabold flex items-center space-x-1.5">
                  <span>होम स्क्रीन पर जोड़ें (Add to Home Screen)</span>
                  <Sparkles className="w-3.5 h-3.5 text-amber-200" />
                </div>
                <div className="text-[11px] text-orange-100 font-normal">
                  PC व Mobile Chrome के लिए स्वतः इंस्टॉल (बिना मेमोरी भरे)
                </div>
              </div>
            </div>
            <CheckCircle className="w-5 h-5 text-white/80 shrink-0 ml-2" />
          </button>

          {/* 2. Direct Android APK Download */}
          <button
            onClick={handleDownloadApk}
            disabled={downloadingApk}
            className="w-full p-4 rounded-2xl bg-slate-800/90 hover:bg-slate-800 text-white font-bold text-sm border border-slate-700 flex items-center justify-between transition-all active:scale-[0.98]"
          >
            <div className="flex items-center space-x-3.5 text-left">
              <div className="p-2.5 rounded-xl bg-emerald-500/20 text-emerald-400">
                <Download className="w-5 h-5" />
              </div>
              <div>
                <div className="text-sm font-extrabold text-emerald-300">
                  {downloadingApk ? 'APK डाउनलोड हो रही है...' : 'Android APK डाउनलोड करें (Direct APK)'}
                </div>
                <div className="text-[11px] text-slate-400 font-normal">
                  मूल Android पैकेज फ़ाइल • आकार: ~30 MB
                </div>
              </div>
            </div>
            <span className="text-xs px-2.5 py-1 rounded-lg bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 font-semibold shrink-0">
              {downloadingApk ? 'प्रतीक्षा करें...' : '30 MB'}
            </span>
          </button>

        </div>

        {/* Quick Instructions Guide */}
        <div className="mt-5 pt-4 border-t border-slate-800 grid grid-cols-1 sm:grid-cols-2 gap-3 text-[11px] text-slate-400">
          <div className="flex items-start space-x-2">
            <Monitor className="w-4 h-4 text-orange-400 shrink-0 mt-0.5" />
            <span>
              <strong className="text-slate-200">PC Chrome:</strong> ऊपर एड्रेस बार में दाईं ओर दिए गए Install (+) आइकन पर क्लिक करें।
            </span>
          </div>
          <div className="flex items-start space-x-2">
            <Smartphone className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
            <span>
              <strong className="text-slate-200">Mobile Chrome:</strong> 3-डॉट्स मेनू पर टैप करके "Add to Home screen" चुनें।
            </span>
          </div>
        </div>

        {/* Security badge */}
        <div className="mt-4 flex items-center justify-center space-x-1.5 text-[11px] text-slate-400">
          <ShieldCheck className="w-4 h-4 text-emerald-400" />
          <span>100% सुरक्षित • आधिकारिक 'आपका विश्वास' सर्वर द्वारा सत्यापित</span>
        </div>

      </div>
    </div>
  );
};
