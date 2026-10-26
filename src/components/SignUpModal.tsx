import React, { useState } from 'react';
import { UserProfile, UserRole } from '../types';
import { User, Phone, MapPin, Award, X, Sparkles, CheckCircle2, Upload, Building, Check } from 'lucide-react';
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
  const [fatherName, setFatherName] = useState(currentUser.fatherName || '');
  const [mobile, setMobile] = useState(currentUser.mobile || '');
  const [role, setRole] = useState<UserRole>(currentUser.role || 'worker');
  const [mandal, setMandal] = useState(currentUser.mandal || 'नरेला मंडल');
  const [ward, setWard] = useState(currentUser.ward || 'वार्ड क्र. 38, अशोका गार्डन');
  const [assembly] = useState('नरेला विधानसभा (151), भोपाल');
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
      setErrorMessage('कृपया अपना पूरा नाम और मोबाइल नंबर अनिवार्य रूप से दर्ज करें।');
      return;
    }
    setErrorMessage(null);

    const updated: UserProfile = {
      ...currentUser,
      fullName: fullName.trim(),
      fatherName: fatherName.trim(),
      mobile: mobile.trim(),
      whatsapp: mobile.trim(),
      role: role,
      assembly: 'नरेला विधानसभा (151)',
      district: 'भोपाल',
      state: 'मध्य प्रदेश',
      mandal: mandal.trim(),
      ward: ward.trim(),
      designation: designation.trim() || (role === 'supporter' ? 'समर्थक' : role === 'office_bearer' ? 'पदाधिकारी' : 'सक्रिय कार्यकर्ता'),
      customPhotoUri: photoPreview,
      isLoggedIn: true,
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

  const roleOptions: { id: UserRole; title: string; subtitle: string }[] = [
    { id: 'supporter', title: 'समर्थक', subtitle: 'दैनिक पोस्टर, सुविचार, सीधा संवाद' },
    { id: 'worker', title: 'सक्रिय कार्यकर्ता', subtitle: 'प्रचार, डिजिटल I-Card, बूथ कार्य' },
    { id: 'office_bearer', title: 'पदाधिकारी', subtitle: 'वॉर रूम, बूथ समन्वय, समीक्षा' },
  ];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-slate-950/85 backdrop-blur-md animate-in fade-in duration-200 overflow-y-auto">
      <div className="relative w-full max-w-xl rounded-3xl bg-slate-900 border border-slate-800 p-5 sm:p-7 shadow-2xl text-white my-8 max-h-[92vh] overflow-y-auto">
        
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
        <div className="flex items-center space-x-3.5 mb-4">
          <img
            src="/aapka_vishwas_leader.png"
            alt="आपका विश्वास"
            className="w-12 h-12 rounded-2xl object-cover ring-2 ring-orange-500/60 shadow-lg shadow-orange-500/25 shrink-0"
          />
          <div>
            <span className="text-[10px] font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full bg-orange-500/20 text-orange-400 border border-orange-500/30">
              नरेला विधानसभा (भोपाल) • सदस्यता
            </span>
            <h3 className="text-lg sm:text-xl font-black text-white mt-0.5">
              कार्यकर्ता व समर्थक पंजीकरण (Sign Up)
            </h3>
          </div>
        </div>

        <p className="text-xs text-slate-300 mb-4 leading-relaxed">
          'आपका विश्वास • सदैव आपके साथ' डिजिटल मंच से जुड़ें। साइन अप पूर्ण होते ही आपके नाम व फोटो के साथ आधिकारिक हेडर-फुटर जैकेट युक्त HD पोस्टर और डिजिटल सदस्यता I-Card तुरंत तैयार हो जाएगा।
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
            <div className="w-14 h-14 rounded-2xl bg-slate-800 border-2 border-orange-500/50 overflow-hidden flex items-center justify-center shrink-0">
              {photoPreview ? (
                <img src={photoPreview} alt="Preview" className="w-full h-full object-cover" />
              ) : (
                <User className="w-6 h-6 text-slate-400" />
              )}
            </div>
            <div className="flex-1">
              <label className="inline-flex items-center space-x-2 px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-slate-200 border border-slate-700 cursor-pointer transition-colors">
                <Upload className="w-3.5 h-3.5 text-orange-400" />
                <span>कार्यकर्ता फोटो अपलोड करें (वैकल्पिक)</span>
                <input type="file" accept="image/*" onChange={handlePhotoUpload} className="hidden" />
              </label>
              <p className="text-[10px] text-slate-400 mt-1">आई-कार्ड और सभी पोस्टरों पर आपका फोटो स्वतः सेट होगा</p>
            </div>
          </div>

          {/* Role Selection */}
          <div>
            <label className="block text-xs font-bold text-slate-300 mb-1.5">
              आपकी भूमिका चुनें (Select Role) <span className="text-red-400">*</span>
            </label>
            <div className="grid grid-cols-3 gap-2">
              {roleOptions.map((opt) => (
                <div
                  key={opt.id}
                  onClick={() => {
                    setRole(opt.id);
                    if (opt.id === 'supporter' && (!designation || designation === 'सक्रिय कार्यकर्ता')) setDesignation('समर्थक');
                    if (opt.id === 'worker' && (!designation || designation === 'समर्थक')) setDesignation('सक्रिय कार्यकर्ता');
                    if (opt.id === 'office_bearer' && (!designation || designation === 'समर्थक')) setDesignation('वार्ड / मंडल पदाधिकारी');
                  }}
                  className={`p-2.5 rounded-xl border text-center cursor-pointer transition-all ${
                    role === opt.id
                      ? 'border-orange-500 bg-orange-600/20 text-white ring-1 ring-orange-500'
                      : 'border-slate-800 bg-slate-950/50 text-slate-400 hover:text-white'
                  }`}
                >
                  <div className="text-xs font-bold">{opt.title}</div>
                  <div className="text-[9px] mt-0.5 line-clamp-1">{opt.subtitle}</div>
                </div>
              ))}
            </div>
          </div>

          {/* Name & Father's Name */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                पूरा नाम <span className="text-red-400">*</span>
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

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                पिता का नाम
              </label>
              <input
                type="text"
                value={fatherName}
                onChange={(e) => setFatherName(e.target.value)}
                placeholder="श्री महेन्द्र सिंह"
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
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

          {/* Assembly & Designation */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                विधानसभा क्षेत्र
              </label>
              <div className="relative">
                <MapPin className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
                <input
                  type="text"
                  disabled
                  value={assembly}
                  className="w-full pl-10 pr-3 py-2.5 rounded-xl bg-slate-950/60 border border-slate-800 text-xs text-slate-300 cursor-not-allowed font-medium"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                संगठनात्मक पद / दायित्व
              </label>
              <div className="relative">
                <Award className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
                <input
                  type="text"
                  value={designation}
                  onChange={(e) => setDesignation(e.target.value)}
                  placeholder="उदा. सक्रिय कार्यकर्ता / बूथ प्रभारी"
                  className="w-full pl-10 pr-3 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                />
              </div>
            </div>
          </div>

          {/* Mandal & Ward */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                मंडल
              </label>
              <input
                type="text"
                value={mandal}
                onChange={(e) => setMandal(e.target.value)}
                placeholder="नरेला मंडल / करोंद / सेमरा"
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">
                वार्ड / क्षेत्र
              </label>
              <input
                type="text"
                value={ward}
                onChange={(e) => setWard(e.target.value)}
                placeholder="वार्ड क्र. 38, अशोका गार्डन"
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
              />
            </div>
          </div>

          <div className="pt-2">
            <button
              type="submit"
              className="w-full py-3 rounded-2xl bg-gradient-to-r from-orange-600 via-amber-600 to-emerald-600 hover:from-orange-500 hover:to-emerald-500 text-white font-bold text-sm shadow-xl shadow-orange-600/30 flex items-center justify-center space-x-2 transition-all active:scale-[0.98]"
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>पंजीकरण पूर्ण करें एवं मंच में प्रवेश करें</span>
            </button>
          </div>

        </form>

      </div>
    </div>
  );
};
