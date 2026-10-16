import React, { useState } from 'react';
import { UserProfile } from '../types';
import { Shield, Award, CheckCircle, QrCode, Download, UserCheck, Star, Trophy, Users } from 'lucide-react';
import confetti from 'canvas-confetti';

interface VolunteerProps {
  user: UserProfile;
}

export const VolunteerScreen: React.FC<VolunteerProps> = ({ user }) => {
  const [tasks, setTasks] = useState([
    { id: 1, title: 'दैनिक सुविचार व पोस्टर व्हाट्सएप स्टेटस पर लगाएं', points: 20, done: true },
    { id: 2, title: 'अपने बूथ के 10 नए मतदाताओं का सत्यापन करें', points: 50, done: false },
    { id: 3, title: 'विधानसभा संकल्प सम्मेलन के लिए 5 युवाओं को आमंत्रित करें', points: 40, done: false },
    { id: 4, title: 'वॉर रूम द्वारा जारी अति आवश्यक सूचना को ग्रुप्स में शेयर करें', points: 30, done: true },
  ]);

  const toggleTask = (id: number) => {
    setTasks(prev =>
      prev.map(t => {
        if (t.id === id) {
          const newDone = !t.done;
          if (newDone) {
            confetti({ particleCount: 50, spread: 60, origin: { y: 0.7 } });
          }
          return { ...t, done: newDone };
        }
        return t;
      })
    );
  };

  const totalPoints = tasks.filter(t => t.done).reduce((acc, t) => acc + t.points, 0) + 420;

  return (
    <div className="space-y-6 pb-20 md:pb-6">
      
      {/* Title */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h2 className="text-xl sm:text-2xl font-black text-white flex items-center space-x-2">
            <span>🛡️ योद्धा मंच व डिजिटल पहचान पत्र</span>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-emerald-600/30 text-emerald-400 border border-emerald-500/30 font-semibold">
              सत्यापित सदस्य
            </span>
          </h2>
          <p className="text-xs text-slate-400 mt-1">
            टीम राठौर का आधिकारिक डिजिटल पहचान पत्र (I-Card) एवं फील्ड टास्क बोर्ड
          </p>
        </div>

        <div className="flex items-center space-x-2">
          <div className="px-4 py-2 rounded-2xl bg-amber-500/10 border border-amber-500/30 text-amber-300 font-bold text-xs flex items-center space-x-2">
            <Trophy className="w-4 h-4 text-amber-400" />
            <span>कुल सेवा अंक: {totalPoints} Pts</span>
          </div>
        </div>
      </div>

      {/* Grid: Left I-Card, Right Tasks & Leaderboard */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        
        {/* Left: Official Digital I-Card (Sticky on PC Chrome) */}
        <div className="lg:col-span-5 flex flex-col items-center lg:sticky lg:top-24 z-10">
          <div className="w-full max-w-sm bg-gradient-to-br from-slate-900 via-slate-850 to-slate-900 border-2 border-orange-500/60 rounded-3xl p-5 shadow-2xl relative overflow-hidden">
            
            {/* Top Tricolor Accent Bar */}
            <div className="absolute top-0 inset-x-0 h-2 bg-gradient-to-r from-orange-500 via-white to-emerald-500" />

            {/* Header with Logo */}
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <div className="flex items-center space-x-2.5">
                <img
                  src="/aapka_vishwas_leader.png"
                  alt="आपका विश्वास"
                  className="w-10 h-10 rounded-xl object-cover ring-1 ring-orange-500/60 shadow shrink-0"
                />
                <div>
                  <h4 className="text-xs font-bold text-white tracking-wide">आपका विश्वास • टीम राठौर</h4>
                  <p className="text-[9px] text-amber-400 font-semibold">सदैव आपके साथ • आधिकारिक पहचान पत्र</p>
                </div>
              </div>
              <span className="text-[10px] px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/30 shrink-0">
                वैध 2026-27
              </span>
            </div>

            {/* Photo & Member Core Info */}
            <div className="mt-5 flex items-center space-x-4">
              <div className="relative">
                <div className="w-20 h-20 rounded-2xl bg-gradient-to-tr from-orange-600 to-amber-500 p-0.5 shadow-lg">
                  <div className="w-full h-full rounded-2xl bg-slate-900 flex items-center justify-center text-white text-2xl font-black">
                    {user.fullName ? user.fullName[0] : 'र'}
                  </div>
                </div>
                <div className="absolute -bottom-1 -right-1 w-6 h-6 rounded-full bg-emerald-500 border-2 border-slate-900 flex items-center justify-center text-white text-xs">
                  ✓
                </div>
              </div>

              <div className="space-y-1">
                <div className="text-base font-extrabold text-white">{user.fullName || 'रितुराज तोमर'}</div>
                <div className="text-xs font-bold text-orange-400">{user.designation || 'सक्रिय कार्यकर्ता'}</div>
                <div className="text-[10px] text-slate-300">सदस्यता क्र: <strong>TR-MP-2026-0842</strong></div>
                <div className="text-[10px] text-slate-400">विधानसभा: <strong>{user.assembly || 'ग्वालियर'}</strong></div>
              </div>
            </div>

            {/* Verification Metadata Box */}
            <div className="mt-5 p-3 rounded-2xl bg-slate-950/70 border border-slate-800/80 grid grid-cols-2 gap-2 text-[10px]">
              <div>
                <span className="text-slate-500 block">बूथ संख्या:</span>
                <span className="text-white font-bold">बूथ क्र. 42 (प्राथमिक शाला)</span>
              </div>
              <div>
                <span className="text-slate-500 block">मोबाइल नंबर:</span>
                <span className="text-white font-bold">{user.mobile || '+91 98765 43210'}</span>
              </div>
              <div>
                <span className="text-slate-500 block">सत्यापन स्थिति:</span>
                <span className="text-emerald-400 font-bold">✓ जिला कोर कमेटी सत्यापित</span>
              </div>
              <div>
                <span className="text-slate-500 block">राज्य:</span>
                <span className="text-white font-bold">{user.state || 'मध्य प्रदेश'}</span>
              </div>
            </div>

            {/* QR Code and Slogan Footer */}
            <div className="mt-5 pt-4 border-t border-slate-800 flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <div className="p-1.5 rounded-lg bg-white text-slate-900">
                  <QrCode className="w-8 h-8" />
                </div>
                <div className="text-[9px] text-slate-400 leading-tight">
                  स्कैन कर सदस्यता<br />सत्यापित करें
                </div>
              </div>

              <div className="text-right">
                <div className="text-[11px] font-bold text-white">हेमंत राठौर</div>
                <div className="text-[9px] text-orange-400 font-medium">अध्यक्ष, टीम राठौर</div>
              </div>
            </div>

          </div>

          <button
            onClick={() => {
              confetti({ particleCount: 70, spread: 60 });
              alert('डिजिटल I-Card डाउनलोड प्रारंभ हुआ!');
            }}
            className="mt-4 px-5 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-white font-bold text-xs border border-slate-700 flex items-center space-x-2 shadow-lg"
          >
            <Download className="w-4 h-4 text-orange-400" />
            <span>I-Card डाउनलोड करें (PDF/PNG)</span>
          </button>
        </div>

        {/* Right: Field Task Board & Leaderboard */}
        <div className="lg:col-span-7 space-y-5">
          
          {/* Daily Field Tasks Checklist */}
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-5 space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-bold text-white flex items-center space-x-2">
                <CheckCircle className="w-4 h-4 text-orange-400" />
                <span>आज का फील्ड टास्क बोर्ड (Daily Tasks)</span>
              </h3>
              <span className="text-xs text-emerald-400 font-bold">
                {tasks.filter(t => t.done).length}/{tasks.length} पूर्ण
              </span>
            </div>

            <div className="space-y-2.5">
              {tasks.map((task) => (
                <div
                  key={task.id}
                  onClick={() => toggleTask(task.id)}
                  className={`p-3.5 rounded-2xl border cursor-pointer transition-all flex items-center justify-between ${
                    task.done
                      ? 'bg-emerald-950/20 border-emerald-500/40 text-slate-300'
                      : 'bg-slate-950/60 border-slate-800 hover:border-slate-700 text-white'
                  }`}
                >
                  <div className="flex items-center space-x-3">
                    <div
                      className={`w-5 h-5 rounded-lg border flex items-center justify-center ${
                        task.done
                          ? 'bg-emerald-600 border-emerald-500 text-white font-bold text-xs'
                          : 'border-slate-600'
                      }`}
                    >
                      {task.done && '✓'}
                    </div>
                    <span className={`text-xs font-medium ${task.done ? 'line-through text-slate-400' : ''}`}>
                      {task.title}
                    </span>
                  </div>

                  <span className="text-[10px] px-2 py-0.5 rounded-full bg-orange-500/20 text-orange-400 font-bold shrink-0">
                    +{task.points} अंक
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Assembly Leaderboard Preview */}
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-5 space-y-4">
            <h3 className="text-sm font-bold text-white flex items-center space-x-2">
              <Trophy className="w-4 h-4 text-amber-400" />
              <span>विधानसभा कार्यकर्ता लीडरबोर्ड (शीर्ष 5)</span>
            </h3>

            <div className="space-y-2">
              {[
                { rank: '1', name: 'विकास सिंह तोमर', booth: 'बूथ 12', pts: '890 Pts', badge: '🥇 स्वर्ण' },
                { rank: '2', name: 'दिलीप शर्मा', booth: 'बूथ 45', pts: '760 Pts', badge: '🥈 रजत' },
                { rank: '3', name: 'रितुराज तोमर (आप)', booth: 'बूथ 42', pts: `${totalPoints} Pts`, badge: '🥉 कांस्य' },
                { rank: '4', name: 'अजय गुर्जर', booth: 'बूथ 88', pts: '620 Pts', badge: '⭐ योद्धा' },
                { rank: '5', name: 'संदीप कुशवाह', booth: 'बूथ 03', pts: '580 Pts', badge: '⭐ योद्धा' },
              ].map((w, idx) => (
                <div
                  key={idx}
                  className={`p-3 rounded-xl border flex items-center justify-between text-xs ${
                    w.rank === '3'
                      ? 'bg-orange-950/30 border-orange-500/50 text-orange-200'
                      : 'bg-slate-950/40 border-slate-800/80 text-slate-300'
                  }`}
                >
                  <div className="flex items-center space-x-3">
                    <span className="w-6 h-6 rounded-full bg-slate-800 flex items-center justify-center font-bold text-white text-[11px]">
                      {w.rank}
                    </span>
                    <div>
                      <div className="font-bold text-white">{w.name}</div>
                      <div className="text-[10px] text-slate-400">{w.booth}</div>
                    </div>
                  </div>

                  <div className="text-right">
                    <div className="font-bold text-amber-400">{w.pts}</div>
                    <div className="text-[10px] text-slate-400">{w.badge}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>

        </div>

      </div>

    </div>
  );
};
