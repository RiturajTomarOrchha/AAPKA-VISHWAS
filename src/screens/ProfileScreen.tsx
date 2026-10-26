import React, { useState } from 'react';
import { UserProfile, UserRole } from '../types';
import { User, Shield, Phone, MapPin, CheckCircle, Save, Award, LogOut, Upload, Sparkles, Building, Hash } from 'lucide-react';
import confetti from 'canvas-confetti';

interface ProfileProps {
  user: UserProfile;
  onUpdateUser: (updated: UserProfile) => void;
  onLogout?: () => void;
}

export const ProfileScreen: React.FC<ProfileProps> = ({ user: initialUser, onUpdateUser, onLogout }) => {
  const [formData, setFormData] = useState<UserProfile>(initialUser);
  const [savedSuccess, setSavedSuccess] = useState(false);
  const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    onUpdateUser({
      ...formData,
      isLoggedIn: true,
      profileCompletion: Math.min(100, (formData.customPhotoUri ? 30 : 15) + (formData.fullName ? 20 : 0) + (formData.mobile ? 20 : 0) + (formData.ward ? 15 : 0) + (formData.designation ? 15 : 0))
    });
    setSavedSuccess(true);
    confetti({ particleCount: 50, spread: 60 });
    setTimeout(() => setSavedSuccess(false), 3000);
  };

  const roles: { id: UserRole; title: string; desc: string; badgeColor: string }[] = [
    {
      id: 'supporter',
      title: 'समर्थक (Supporter)',
      desc: 'दैनिक पोस्टर, सुविचार, वीडियो एवं विधायक जी को सीधा संवाद',
      badgeColor: 'border-emerald-500/40 text-emerald-400 bg-emerald-500/10'
    },
    {
      id: 'worker',
      title: 'सक्रिय कार्यकर्ता (Worker)',
      desc: 'प्रचार सामग्री, डिजिटल I-Card, बूथ कार्य चेकलिस्ट व संगठनात्मक परिपत्र',
      badgeColor: 'border-orange-500/40 text-orange-400 bg-orange-500/10'
    },
    {
      id: 'office_bearer',
      title: 'पदाधिकारी (Office Bearer)',
      desc: 'केंद्रीय चुनाव वॉर रूम, 284 बूथों की समीक्षा, टास्क निगरानी व लाइव मीटिंग',
      badgeColor: 'border-purple-500/40 text-purple-400 bg-purple-500/10'
    },
    {
      id: 'admin',
      title: 'व्यवस्थापक (Admin)',
      desc: 'केंद्रीय व्यवस्थापक एवं संपूर्ण मंच व डेटाबेस प्रबंधन',
      badgeColor: 'border-amber-500/40 text-amber-400 bg-amber-500/10'
    },
  ];

  const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (ev) => {
        setFormData(prev => ({ ...prev, customPhotoUri: ev.target?.result as string }));
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <div className="space-y-6 pb-20 md:pb-6 max-w-4xl mx-auto">
      
      {/* Title with Logout Button */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h2 className="text-xl sm:text-2xl font-black text-white flex items-center space-x-2">
            <span>👤 मेरी प्रोफाइल व सदस्यता विवरण</span>
          </h2>
          <p className="text-xs text-slate-400 mt-1">
            नरेला विधानसभा (भोपाल) • संगठनात्मक दायित्व, संपर्क जानकारी एवं फोटो प्रबंधन
          </p>
        </div>

        {/* Prominent Logout Button requested by User */}
        {onLogout && (
          <button
            onClick={() => setShowLogoutConfirm(true)}
            className="self-start sm:self-auto px-4 py-2 rounded-xl bg-red-600/20 hover:bg-red-600/30 text-red-300 hover:text-red-200 border border-red-500/40 font-bold text-xs flex items-center space-x-2 transition-all active:scale-95 shadow-md shadow-red-950/40"
          >
            <LogOut className="w-4 h-4 text-red-400" />
            <span>लॉगआउट करें (Logout)</span>
          </button>
        )}
      </div>

      {/* Logout Confirmation Dialog */}
      {showLogoutConfirm && (
        <div className="p-4 rounded-2xl bg-red-950/40 border border-red-500/40 text-white space-y-3 animate-in fade-in">
          <div className="flex items-center space-x-2.5 text-sm font-bold text-red-300">
            <LogOut className="w-5 h-5 text-red-400" />
            <span>क्या आप वाकई लॉगआउट करना चाहते हैं?</span>
          </div>
          <p className="text-xs text-slate-300">
            लॉगआउट करने के बाद आप नए सदस्य के रूप में साइन अप पेज देख सकते हैं और नया पंजीकरण फॉर्म टेस्ट कर सकते हैं।
          </p>
          <div className="flex items-center space-x-3 pt-1">
            <button
              onClick={() => {
                setShowLogoutConfirm(false);
                onLogout?.();
              }}
              className="px-4 py-2 rounded-xl bg-red-600 hover:bg-red-500 text-white font-bold text-xs shadow-lg transition-all"
            >
              हाँ, लॉगआउट करें
            </button>
            <button
              onClick={() => setShowLogoutConfirm(false)}
              className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold text-xs transition-all"
            >
              रद्द करें
            </button>
          </div>
        </div>
      )}

      {/* Completion Meter & Identity Card */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-5 shadow-xl flex flex-col sm:flex-row sm:items-center justify-between gap-5">
        <div className="flex items-center space-x-4">
          <div className="relative group shrink-0">
            <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-orange-600 to-emerald-600 flex items-center justify-center text-white text-2xl font-black shadow-lg overflow-hidden border-2 border-orange-500/50">
              {formData.customPhotoUri ? (
                <img src={formData.customPhotoUri} alt={formData.fullName} className="w-full h-full object-cover" />
              ) : (
                <span>{formData.fullName ? formData.fullName[0] : 'र'}</span>
              )}
            </div>
            <label className="absolute -bottom-1 -right-1 p-1 rounded-full bg-orange-600 text-white cursor-pointer hover:bg-orange-500 transition-colors shadow-md">
              <Upload className="w-3.5 h-3.5" />
              <input type="file" accept="image/*" onChange={handlePhotoUpload} className="hidden" />
            </label>
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <h3 className="text-base font-bold text-white">{formData.fullName || 'रितुराज तोमर'}</h3>
              <span className={`text-[10px] px-2 py-0.5 rounded-full font-bold border ${roles.find(r => r.id === formData.role)?.badgeColor}`}>
                {roles.find(r => r.id === formData.role)?.title.split(' ')[0]}
              </span>
            </div>
            <p className="text-xs text-orange-400 font-semibold mt-0.5">{formData.designation}</p>
            <p className="text-[11px] text-slate-400 mt-0.5">नरेला विधानसभा (151), भोपाल • {formData.state}</p>
          </div>
        </div>

        <div className="sm:text-right">
          <div className="flex items-center sm:justify-end space-x-2">
            <span className="text-xs text-slate-400">प्रोफाइल पूर्णता:</span>
            <span className="text-sm font-black text-emerald-400">{formData.profileCompletion}%</span>
          </div>
          <div className="w-48 h-2 bg-slate-800 rounded-full mt-2 overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-orange-500 via-amber-400 to-emerald-500 rounded-full"
              style={{ width: `${formData.profileCompletion}%` }}
            />
          </div>
          <p className="text-[10px] text-slate-500 mt-1">फोटो व संपूर्ण विवरण पोस्टर पर स्वतः प्रदर्शित होंगे</p>
        </div>
      </div>

      {/* Edit Form */}
      <form onSubmit={handleSave} className="bg-slate-900 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
        
        {/* Role Selector (Adapts the user interface) */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <label className="block text-xs font-bold text-slate-300 uppercase tracking-wider">
              संगठनात्मक भूमिका (User Role)
            </label>
            <span className="text-[11px] text-orange-400 font-semibold">
              * रोल बदलने पर डैशबोर्ड के बॉक्स व सुविधाएं स्वतः बदलेंगी
            </span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {roles.map(r => (
              <div
                key={r.id}
                onClick={() => setFormData(prev => ({ ...prev, role: r.id }))}
                className={`p-3.5 rounded-2xl border cursor-pointer transition-all ${
                  formData.role === r.id
                    ? 'border-orange-500 bg-orange-600/15 ring-2 ring-orange-500/20'
                    : 'border-slate-800 bg-slate-950/60 hover:border-slate-700'
                }`}
              >
                <div className="text-xs font-bold text-white flex items-center justify-between">
                  <span>{r.title}</span>
                  {formData.role === r.id && <CheckCircle className="w-4 h-4 text-orange-400" />}
                </div>
                <div className="text-[10px] text-slate-400 mt-1">{r.desc}</div>
              </div>
            ))}
          </div>
        </div>

        {/* Personal Details */}
        <div className="space-y-4">
          <h4 className="text-xs font-bold text-slate-300 uppercase tracking-wider border-b border-slate-800 pb-2">
            व्यक्तिगत एवं संपर्क विवरण
          </h4>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">पूरा नाम</label>
              <input
                type="text"
                value={formData.fullName}
                onChange={e => setFormData({ ...formData, fullName: e.target.value })}
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">पिता का नाम</label>
              <input
                type="text"
                value={formData.fatherName}
                onChange={e => setFormData({ ...formData, fatherName: e.target.value })}
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">मोबाइल नंबर (WhatsApp)</label>
              <input
                type="text"
                value={formData.mobile}
                onChange={e => setFormData({ ...formData, mobile: e.target.value, whatsapp: e.target.value })}
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">ईमेल पता</label>
              <input
                type="email"
                value={formData.email}
                onChange={e => setFormData({ ...formData, email: e.target.value })}
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
              />
            </div>
          </div>
        </div>

        {/* Organizational / Area Details */}
        <div className="space-y-4">
          <h4 className="text-xs font-bold text-slate-300 uppercase tracking-wider border-b border-slate-800 pb-2">
            विधानसभा व संगठनात्मक दायित्व
          </h4>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">विधानसभा क्षेत्र</label>
              <input
                type="text"
                value="नरेला विधानसभा (151), भोपाल"
                disabled
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950/60 border border-slate-800 text-xs text-slate-300 cursor-not-allowed font-medium"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">संगठनात्मक पद / दायित्व</label>
              <input
                type="text"
                value={formData.designation}
                onChange={e => setFormData({ ...formData, designation: e.target.value })}
                placeholder="उदा. सक्रिय कार्यकर्ता / मंडल पदाधिकारी / बूथ प्रभारी"
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">मंडल का नाम</label>
              <input
                type="text"
                value={formData.mandal || 'नरेला मंडल'}
                onChange={e => setFormData({ ...formData, mandal: e.target.value })}
                placeholder="उदा. नरेला मंडल / सेमरा / करोंद मंडल"
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">वार्ड / क्षेत्र</label>
              <input
                type="text"
                value={formData.ward || 'वार्ड क्र. 38, अशोका गार्डन'}
                onChange={e => setFormData({ ...formData, ward: e.target.value })}
                placeholder="उदा. वार्ड क्र. 36-41, सुभाष नगर, करोंद"
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
              />
            </div>
          </div>
        </div>

        {/* Submit & Status Bar */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pt-4 border-t border-slate-800">
          {savedSuccess ? (
            <span className="text-xs text-emerald-400 font-bold flex items-center space-x-1.5">
              <CheckCircle className="w-4 h-4" />
              <span>प्रोफाइल व सदस्यता विवरण सुरक्षित हो गए!</span>
            </span>
          ) : (
            <span className="text-[11px] text-slate-500">
              यह जानकारी आपके सभी डिजिटल पोस्टरों और जैकेट फ्रेम पर स्वतः दिखेगी
            </span>
          )}

          <div className="flex items-center space-x-3">
            <button
              type="submit"
              className="px-6 py-2.5 rounded-xl bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-500 hover:to-amber-500 text-white font-bold text-xs shadow-lg shadow-orange-600/30 flex items-center space-x-2 transition-all active:scale-95"
            >
              <Save className="w-4 h-4" />
              <span>प्रोफाइल सेव करें</span>
            </button>
          </div>
        </div>

      </form>

    </div>
  );
};
