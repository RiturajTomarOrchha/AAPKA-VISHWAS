import React, { useState } from 'react';
import { UserProfile, UserRole } from '../types';
import { User, Shield, Phone, MapPin, CheckCircle, Save, Award, ExternalLink } from 'lucide-react';

interface ProfileProps {
  user: UserProfile;
  onUpdateUser: (updated: UserProfile) => void;
}

export const ProfileScreen: React.FC<ProfileProps> = ({ user: initialUser, onUpdateUser }) => {
  const [formData, setFormData] = useState<UserProfile>(initialUser);
  const [savedSuccess, setSavedSuccess] = useState(false);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    onUpdateUser(formData);
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 3000);
  };

  const roles: { id: UserRole; title: string; desc: string }[] = [
    { id: 'supporter', title: 'समर्थक (Supporter)', desc: 'दैनिक पोस्टर, सुविचार, वीडियो एवं सीधा संवाद' },
    { id: 'worker', title: 'कार्यकर्ता (Worker)', desc: 'प्रचार सामग्री, वॉर रूम एवं संगठनात्मक कार्यक्रम' },
    { id: 'office_bearer', title: 'पदाधिकारी (Office Bearer)', desc: 'संगठनात्मक जिम्मेदारियाँ, बूथ समन्वय व समीक्षा' },
    { id: 'admin', title: 'व्यवस्थापक (Admin)', desc: 'केंद्रीय व्यवस्थापक एवं संपूर्ण मंच प्रबंधन' },
  ];

  return (
    <div className="space-y-6 pb-20 md:pb-6 max-w-4xl mx-auto">
      
      {/* Title */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl sm:text-2xl font-black text-white flex items-center space-x-2">
            <span>👤 मेरी प्रोफाइल व सदस्यता विवरण</span>
          </h2>
          <p className="text-xs text-slate-400 mt-1">
            अपने संगठनात्मक दायित्व, विधानसभा क्षेत्र एवं संपर्क जानकारी को अपडेट करें
          </p>
        </div>
      </div>

      {/* Completion Meter Card */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-5 shadow-xl flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="flex items-center space-x-4">
          <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-orange-600 to-amber-500 flex items-center justify-center text-white text-2xl font-black shadow-lg">
            {formData.fullName ? formData.fullName[0] : 'र'}
          </div>
          <div>
            <h3 className="text-base font-bold text-white">{formData.fullName}</h3>
            <p className="text-xs text-orange-400 font-semibold">{formData.designation}</p>
            <p className="text-[11px] text-slate-400 mt-0.5">{formData.assembly} • {formData.state}</p>
          </div>
        </div>

        <div className="sm:text-right">
          <div className="flex items-center sm:justify-end space-x-2">
            <span className="text-xs text-slate-400">प्रोफाइल पूर्णता:</span>
            <span className="text-sm font-black text-emerald-400">{formData.profileCompletion}%</span>
          </div>
          <div className="w-48 h-2 bg-slate-800 rounded-full mt-2 overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-orange-500 to-emerald-500 rounded-full"
              style={{ width: `${formData.profileCompletion}%` }}
            />
          </div>
        </div>
      </div>

      {/* Edit Form */}
      <form onSubmit={handleSave} className="bg-slate-900 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
        
        {/* Role Selector */}
        <div>
          <label className="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-3">
            संगठनात्मक भूमिका (Role)
          </label>
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
              <label className="block text-xs font-semibold text-slate-400 mb-1">मोबाइल नंबर</label>
              <input
                type="text"
                value={formData.mobile}
                onChange={e => setFormData({ ...formData, mobile: e.target.value })}
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">व्हाट्सएप नंबर</label>
              <input
                type="text"
                value={formData.whatsapp}
                onChange={e => setFormData({ ...formData, whatsapp: e.target.value })}
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">राज्य</label>
              <input
                type="text"
                value={formData.state}
                disabled
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950/60 border border-slate-800 text-xs text-slate-400 cursor-not-allowed"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">विधानसभा क्षेत्र</label>
              <input
                type="text"
                value={formData.assembly}
                onChange={e => setFormData({ ...formData, assembly: e.target.value })}
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
              />
            </div>
          </div>
        </div>

        {/* Submit Button */}
        <div className="flex items-center justify-between pt-4 border-t border-slate-800">
          {savedSuccess ? (
            <span className="text-xs text-emerald-400 font-bold flex items-center space-x-1">
              <CheckCircle className="w-4 h-4" />
              <span>प्रोफाइल सफलतापूर्वक सुरक्षित हो गई!</span>
            </span>
          ) : (
            <span className="text-[11px] text-slate-500">सभी बदलाव तुरंत प्रभावी होंगे</span>
          )}

          <button
            type="submit"
            className="px-6 py-2.5 rounded-xl bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-500 hover:to-amber-500 text-white font-bold text-xs shadow-lg flex items-center space-x-2 transition-all active:scale-95"
          >
            <Save className="w-4 h-4" />
            <span>प्रोफाइल सेव करें</span>
          </button>
        </div>

      </form>

    </div>
  );
};
