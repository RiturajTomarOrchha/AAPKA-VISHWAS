import React, { useState } from 'react';
import { ImportantNotice, ProgramItem, WarRoomStats, UserProfile } from '../types';
import { Flame, Video, Users, CheckCircle, Radio, AlertCircle, Calendar, MapPin, Send, ThumbsUp, ShieldCheck } from 'lucide-react';

interface WarRoomProps {
  stats: WarRoomStats;
  notices: ImportantNotice[];
  programs: ProgramItem[];
  user: UserProfile;
}

export const WarRoomScreen: React.FC<WarRoomProps> = ({
  stats,
  notices,
  programs: initialPrograms,
  user
}) => {
  const [programs, setPrograms] = useState(initialPrograms);
  const [meetingJoined, setMeetingJoined] = useState(false);
  const [suggestionSubmitted, setSuggestionSubmitted] = useState(false);
  const [suggestion, setSuggestion] = useState({ subject: '', category: 'बूथ समन्वय', message: '' });

  const handleRsvp = (programId: string, status: 'ATTENDING' | 'MAYBE' | 'NOT_ATTENDING') => {
    setPrograms(prev =>
      prev.map(p => {
        if (p.id === programId) {
          const wasAttending = p.rsvpStatus === 'ATTENDING';
          const isNowAttending = status === 'ATTENDING';
          return {
            ...p,
            rsvpStatus: status,
            attendingCount: wasAttending && !isNowAttending ? p.attendingCount - 1 : !wasAttending && isNowAttending ? p.attendingCount + 1 : p.attendingCount
          };
        }
        return p;
      })
    );
  };

  const handleSuggestionSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!suggestion.message.trim()) return;
    setSuggestionSubmitted(true);
    setTimeout(() => {
      setSuggestion({ subject: '', category: 'बूथ समन्वय', message: '' });
      setSuggestionSubmitted(false);
    }, 4000);
  };

  return (
    <div className="space-y-6 pb-20 md:pb-6">
      
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <div className="flex items-center space-x-2">
            <span className="w-3 h-3 rounded-full bg-red-500 animate-ping" />
            <h2 className="text-xl sm:text-2xl font-black text-white">
              केंद्रीय चुनाव वॉर रूम (Election War Room)
            </h2>
          </div>
          <p className="text-xs text-slate-400 mt-1">
            284 बूथों का लाइव समन्वय, चुनावी कार्यक्रम एवं प्रत्यक्ष कार्यकर्ता संवाद
          </p>
        </div>

        <div className="flex items-center space-x-2">
          <span className="px-3 py-1 rounded-full bg-red-500/20 text-red-400 border border-red-500/30 text-xs font-bold flex items-center space-x-1.5">
            <Radio className="w-3.5 h-3.5 animate-pulse" />
            <span>24x7 सक्रिय सर्वर</span>
          </span>
        </div>
      </div>

      {/* Live Meeting Broadcast Card */}
      <div className="bg-gradient-to-br from-slate-900 via-indigo-950/60 to-slate-900 border border-indigo-500/30 rounded-3xl p-6 shadow-2xl relative overflow-hidden">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="flex items-center space-x-4">
            <img
              src="/aapka_vishwas_leader.png"
              alt="हेमंत राठौर • आपका विश्वास"
              className="w-16 h-16 sm:w-20 sm:h-20 rounded-2xl object-cover ring-2 ring-orange-500/60 shadow-xl shrink-0"
            />
            <div className="space-y-1.5">
              <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-red-600 text-white text-[11px] font-bold uppercase tracking-wider shadow">
                <span className="w-2 h-2 rounded-full bg-white animate-pulse" />
                <span>लाइव वीडियो संवाद जारी</span>
              </div>
              <h3 className="text-lg sm:text-2xl font-black text-white">{stats.meetingTitle}</h3>
              <p className="text-xs sm:text-sm text-slate-300">
                संबोधन: <strong className="text-amber-300">{stats.meetingHost}</strong> • आपका विश्वास, सदैव आपके साथ
              </p>
            </div>
          </div>

          <div className="flex items-center space-x-3 shrink-0">
            {meetingJoined ? (
              <div className="flex items-center space-x-3 bg-slate-950/80 px-4 py-3 rounded-2xl border border-emerald-500/40 text-emerald-400 text-xs font-bold">
                <CheckCircle className="w-5 h-5 text-emerald-400" />
                <span>आप वॉर रूम मीटिंग में कनेक्टेड हैं</span>
              </div>
            ) : (
              <button
                onClick={() => setMeetingJoined(true)}
                className="px-6 py-3 rounded-2xl bg-gradient-to-r from-red-600 to-orange-600 hover:from-red-500 hover:to-orange-500 text-white font-bold text-sm shadow-xl shadow-red-600/30 flex items-center space-x-2 transition-all active:scale-95"
              >
                <Video className="w-4 h-4" />
                <span>मीटिंग में शामिल हों (Join)</span>
              </button>
            )}
          </div>
        </div>

        {/* Live Audio / Video Visualizer if joined */}
        {meetingJoined && (
          <div className="mt-6 pt-6 border-t border-indigo-500/20 grid grid-cols-2 sm:grid-cols-4 gap-3">
            {['हेमंत राठौर (अध्यक्ष)', 'मंडल अध्यक्ष - पश्चिम', 'युवा मोर्चा प्रभारी', 'रितुराज तोमर (आप)'].map((name, i) => (
              <div key={i} className="bg-slate-950/70 border border-slate-800 rounded-xl p-3 flex items-center space-x-3">
                <div className="w-8 h-8 rounded-full bg-indigo-600/30 border border-indigo-500/50 flex items-center justify-center text-xs font-bold text-white">
                  {name[0]}
                </div>
                <div className="overflow-hidden">
                  <div className="text-xs font-semibold text-white truncate">{name}</div>
                  <div className="text-[10px] text-emerald-400 flex items-center space-x-1">
                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
                    <span>ऑडियो कनेक्टेड</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* War Room Metric Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          { label: 'कुल बूथ (विधानसभा)', val: stats.totalBooths, sub: 'शत-प्रतिशत डिजिटल मैप्ड', color: 'text-orange-400' },
          { label: 'सक्रिय कार्यकर्ता', val: stats.activeWorkers, sub: 'मैदान में मुस्तैद', color: 'text-blue-400' },
          { label: 'सत्यापित मतदाता', val: stats.verifiedVoters.toLocaleString('en-IN'), sub: 'डिजिटल डेटाबेस', color: 'text-emerald-400' },
          { label: 'निस्तारित समस्याएं', val: `${stats.resolvedIssues}/${stats.reportedIssues}`, sub: 'त्वरित समाधान दर', color: 'text-amber-400' },
        ].map((m, i) => (
          <div key={i} className="bg-slate-900 border border-slate-800 rounded-2xl p-4">
            <div className={`text-2xl font-black ${m.color}`}>{m.val}</div>
            <div className="text-xs font-bold text-white mt-1">{m.label}</div>
            <div className="text-[10px] text-slate-400 mt-0.5">{m.sub}</div>
          </div>
        ))}
      </div>

      {/* Two Column Layout: Programs & Urgent Notices */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* Left: Programs & RSVP */}
        <div className="lg:col-span-7 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-base font-bold text-white flex items-center space-x-2">
              <Calendar className="w-4 h-4 text-orange-400" />
              <span>आगामी संगठनात्मक कार्यक्रम व RSVP</span>
            </h3>
            <span className="text-xs text-slate-400">अपनी उपस्थिति दर्ज करें</span>
          </div>

          <div className="space-y-4">
            {programs.map((prog) => (
              <div
                key={prog.id}
                className="bg-slate-900 border border-slate-800 rounded-2xl p-5 shadow-lg space-y-3"
              >
                <div className="flex items-start justify-between gap-3">
                  <h4 className="text-sm sm:text-base font-bold text-white">{prog.titleHi}</h4>
                  <span className="text-[10px] px-2 py-0.5 rounded bg-blue-500/20 text-blue-300 font-semibold shrink-0">
                    {prog.attendingCount} कार्यकर्ता शामिल
                  </span>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs text-slate-300">
                  <div className="flex items-center space-x-2">
                    <Calendar className="w-3.5 h-3.5 text-orange-400 shrink-0" />
                    <span>{prog.dateHi} • {prog.timeHi}</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <MapPin className="w-3.5 h-3.5 text-red-400 shrink-0" />
                    <span className="truncate">{prog.venueHi}</span>
                  </div>
                </div>

                <p className="text-xs text-slate-400 bg-slate-950/60 p-2.5 rounded-xl border border-slate-800/80">
                  💡 <strong>निर्देश:</strong> {prog.instructionsHi}
                </p>

                {/* RSVP Action Buttons */}
                <div className="pt-2 flex flex-wrap items-center gap-2">
                  <span className="text-[11px] font-semibold text-slate-400 mr-2">आपकी स्थिति:</span>
                  <button
                    onClick={() => handleRsvp(prog.id, 'ATTENDING')}
                    className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                      prog.rsvpStatus === 'ATTENDING'
                        ? 'bg-emerald-600 text-white shadow'
                        : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                    }`}
                  >
                    ✓ अवश्य आऊँगा
                  </button>
                  <button
                    onClick={() => handleRsvp(prog.id, 'MAYBE')}
                    className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
                      prog.rsvpStatus === 'MAYBE'
                        ? 'bg-amber-600 text-white'
                        : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                    }`}
                  >
                    शायद
                  </button>
                  <button
                    onClick={() => handleRsvp(prog.id, 'NOT_ATTENDING')}
                    className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
                      prog.rsvpStatus === 'NOT_ATTENDING'
                        ? 'bg-red-600 text-white'
                        : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                    }`}
                  >
                    उपस्थित नहीं हो पाऊँगा
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Right: Urgent Notice & Suggestions Form */}
        <div className="lg:col-span-5 space-y-5">
          
          {/* Important Notices */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4">
            <h3 className="text-sm font-bold text-white flex items-center space-x-2">
              <AlertCircle className="w-4 h-4 text-amber-400" />
              <span>कार्यालय महत्वपूर्ण आदेश व परिपत्र</span>
            </h3>

            <div className="space-y-3">
              {notices.map((n) => (
                <div key={n.id} className="p-3.5 rounded-xl bg-slate-950/60 border border-slate-800 space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] text-orange-400 font-bold">{n.dateHi}</span>
                    {n.isUrgent && (
                      <span className="text-[9px] px-2 py-0.5 rounded bg-red-600/30 text-red-400 font-extrabold uppercase">
                        Urgent
                      </span>
                    )}
                  </div>
                  <h5 className="text-xs font-bold text-white leading-snug">{n.titleHi}</h5>
                  <p className="text-[11px] text-slate-300 leading-relaxed">{n.bodyHi}</p>
                  <div className="flex items-center space-x-4 pt-1 text-[10px] text-slate-400">
                    <span className="flex items-center space-x-1">
                      <ThumbsUp className="w-3 h-3 text-blue-400" />
                      <span>{n.agreeCount} सहमत</span>
                    </span>
                    <span>👁️ {n.viewsCount} देखा गया</span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Quick Problem / Suggestion Reporting Box */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4">
            <h3 className="text-sm font-bold text-white flex items-center space-x-2">
              <ShieldCheck className="w-4 h-4 text-emerald-400" />
              <span>वॉर रूम में सीधी समस्या / सुझाव भेजें</span>
            </h3>

            {suggestionSubmitted ? (
              <div className="p-4 rounded-xl bg-emerald-950/50 border border-emerald-500/40 text-center space-y-2">
                <CheckCircle className="w-8 h-8 text-emerald-400 mx-auto" />
                <h5 className="text-xs font-bold text-white">सुझाव सफलता पूर्वक दर्ज हुआ!</h5>
                <p className="text-[10px] text-slate-300">
                  वॉर रूम की समन्वय टीम द्वारा इसका तुरंत परीक्षण कर समाधान किया जाएगा।
                </p>
              </div>
            ) : (
              <form onSubmit={handleSuggestionSubmit} className="space-y-3">
                <div>
                  <label className="block text-[11px] font-semibold text-slate-400 mb-1">श्रेणी</label>
                  <select
                    value={suggestion.category}
                    onChange={(e) => setSuggestion(prev => ({ ...prev, category: e.target.value }))}
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500"
                  >
                    <option value="बूथ समन्वय">बूथ समन्वय व बीएलए सहायता</option>
                    <option value="मतदाता सूची">मतदाता सूची संशोधन</option>
                    <option value="प्रचार सामग्री">प्रचार सामग्री व बैनर मांग</option>
                    <option value="स्थानीय समस्या">क्षेत्रीय जनसमस्या</option>
                  </select>
                </div>

                <div>
                  <label className="block text-[11px] font-semibold text-slate-400 mb-1">विवरण</label>
                  <textarea
                    rows={3}
                    value={suggestion.message}
                    onChange={(e) => setSuggestion(prev => ({ ...prev, message: e.target.value }))}
                    placeholder="बूथ संख्या या समस्या का संक्षिप्त विवरण लिखें..."
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-700 text-xs text-white focus:outline-none focus:border-orange-500 resize-none"
                    required
                  />
                </div>

                <button
                  type="submit"
                  className="w-full py-2.5 rounded-xl bg-gradient-to-r from-orange-600 to-amber-600 hover:from-orange-500 hover:to-amber-500 text-white font-bold text-xs shadow-lg flex items-center justify-center space-x-2 transition-all active:scale-95"
                >
                  <Send className="w-3.5 h-3.5" />
                  <span>वॉर रूम को सबमिट करें</span>
                </button>
              </form>
            )}
          </div>

        </div>

      </div>

    </div>
  );
};
