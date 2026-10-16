import React, { useState } from 'react';
import { UserProfile } from '../types';
import { User, Phone, MapPin, Award, X, Sparkles, CheckCircle2, Upload } from 'lucide-react';
import confetti from 'canvas-confetti';

interface SignUpModalProps {
  isOpen: boolean;
  onClose: () => void;
  currentUser: UserProfile;
  onCompleteSignUp: (updatedUser: UserProfile) => void;
}

export const SignUpModal: React.FC<SignUpModalProps> = ({
  isOpen,
  onClose,
  currentUser,
  onCompleteSignUp
}) => {
  const [fullName, setFullName] = useState(currentUser.fullName || '');
  const [mobile, setMobile] = useState(currentUser.mobile || '');
  const [assembly, setAssembly] = useState(currentUser.assembly || 'ग्वालियर दक्षिण (17)');
  const [designation, setDesignation] = useState(currentUser.designation || 'सक्रिय कार्यकर्ता');
  const [photoPreview, setPhotoPreview] = useState<string | null>(currentUser.customPhotoUri || null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  if (!isOpen) return null;

  const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (ev) => {
        setPhotoPreview(ev.target?.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim() || !mobile.trim()) {
      setErrorMessage('कृपया अपना नाम और मोबाइल नंबर दर्ज करें।');
      return;
    }
    setErrorMessage(null);

    const updated: UserProfile = {
      ...currentUser,
      fullName: fullName.trim(),
      mobile: mobile.trim(),
      whatsapp: mobile.trim(),
      assembly: assembly.trim(),
      designation: designation.trim(),
      customPhotoUri: photoPreview,
      isApproved: true,
      profileCompletion: 100
    };

    confetti({
      particleCount: 100,
      spread: 80,
      origin: { y: 0.6 }
    });

    onCompleteSignUp(updated);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/85 backdrop-blur-md animate-in fade-in duration-200">
      <div className="relative w-full max-w-lg rounded-3xl bg-slate-900 border border-slate-800 p-6 sm:p-8 shadow-2xl text-white overflow-hidden">
        
        {/* Glow */}
        <div className="absolute top-0 right-0 w-48 h-48 bg-orange-600/20 blur-3xl pointer-events-none" />

        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-2 rounded-full bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Header */}
        <div className="flex items-center space-x-3.5 mb-5">
          <img
            src="/aapka_vishwas_leader.png"
            alt="आपका विश्वास"
            className="w-12 h-12 rounded-2xl object-cover ring-2 ring-orange-500/60 shadow-lg shadow-orange-500/25 shrink-0"
          />
          <div>
            <span className="text-[10px] font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full bg-orange-500/20 text-orange-400 border border-orange-500/30">
              आपका विश्वास • डिजिटल सदस्यता
            </span>
            <h3 className="text-lg sm:text-xl font-black text-white mt-0.5">
              कार्यकर्ता पंजीकरण व साइन अप
            </h3>
          </div>
        </div>

        <p className="text-xs text-slate-300 mb-5 leading-relaxed">
          'आपका विश्वास • सदैव आपके साथ' डिजिटल मंच से जुड़ें। साइन अप पूर्ण होते ही आपको आपका आधिकारिक पहचान पत्र (I-Card) व ऐप इंस्टॉल विकल्प प्राप्त होगा।
        </p>

        {errorMessage && (
          <div className="mb-4 p-3 rounded-xl bg-red-500/20 border border-red-500/40 text-red-300 text-xs font-semibold flex items-center space-x-2">
            <span>⚠️</span>
            <span>{errorMessage}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          
          {/* Photo upload */}
          <div className="flex items-center space-x-4 p-3 rounded-2xl bg-slate-950/60 border border-slate-800">
            <div className="w-14 h-14 rounded-2xl bg-slate-800 border border-orange-500/40 overflow-hidden flex items-center justify-center shrink-0">
              {photoPreview ? (
                <img src={photoPreview} alt="Preview" className="w-full h-full object-cover" />
              ) : (
                <User className="w-6 h-6 text-slate-400" />
              )}
            </div>
            <div className="flex-1">
              <label className="inline-flex items-center space-x-2 px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-slate-200 border border-slate-700 cursor-pointer transition-colors">
                <Upload className="w-3.5 h-3.5 text-orange-400" />
                <span>फोटो अपलोड करें (वैकल्पिक)</span>
                <input type="file" accept="image/*" onChange={handlePhotoUpload} className="hidden" />
              </label>
              <p className="text-[10px] text-slate-400 mt-1">आई-कार्ड और पोस्टर पर स्वतः सेट होगी</p>
            </div>
          </div>

          {/* Name */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              कार्यकर्ता का पूरा नाम <span className="text-red-400">*</span>
            </label>
            <div className="relative">
              <User className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
              <input
                type="text"
                required
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="उदा. रितुराज तोमर"
                className="w-full pl-10 pr-3 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
              />
            </div>
          </div>

          {/* Mobile */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              मोबाइल नंबर (WhatsApp) <span className="text-red-400">*</span>
            </label>
            <div className="relative">
              <Phone className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
              <input
                type="tel"
                required
                value={mobile}
                onChange={(e) => setMobile(e.target.value)}
                placeholder="+91 98765 43210"
                className="w-full pl-10 pr-3 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
              />
            </div>
          </div>

          {/* Assembly */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                विधानसभा क्षेत्र
              </label>
              <div className="relative">
                <MapPin className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
                <input
                  type="text"
                  value={assembly}
                  onChange={(e) => setAssembly(e.target.value)}
                  placeholder="ग्वालियर दक्षिण (17)"
                  className="w-full pl-10 pr-3 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                संगठनात्मक दायित्व / पद
              </label>
              <div className="relative">
                <Award className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
                <input
                  type="text"
                  value={designation}
                  onChange={(e) => setDesignation(e.target.value)}
                  placeholder="सक्रिय कार्यकर्ता"
                  className="w-full pl-10 pr-3 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                />
              </div>
            </div>
          </div>

          <div className="pt-2">
            <button
              type="submit"
              className="w-full py-3 rounded-2xl bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-500 hover:to-amber-500 text-white font-bold text-sm shadow-xl shadow-orange-600/30 flex items-center justify-center space-x-2 transition-all active:scale-[0.98]"
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>पंजीकरण पूर्ण करें एवं ऐप प्राप्त करें</span>
            </button>
          </div>

        </form>

      </div>
    </div>
  );
};
