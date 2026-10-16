import React, { useState } from 'react';
import { VideoFeedItem } from '../types';
import { Play, Heart, Share2, MessageCircle, Shield, Award, Clock } from 'lucide-react';

interface VideoFeedProps {
  videos: VideoFeedItem[];
}

export const VideoFeedScreen: React.FC<VideoFeedProps> = ({ videos }) => {
  const [activeVideo, setActiveVideo] = useState<VideoFeedItem | null>(null);
  const [likes, setLikes] = useState<Record<string, boolean>>({});

  const toggleLike = (id: string) => {
    setLikes(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const handleShare = (v: VideoFeedItem) => {
    const text = `🎬 ${v.titleHi}\n\n${v.captionHi}\n\nटीम राठौर वीडियो गैलरी: https://ais-pre-4e6xqjiicgx6hix4eeszuh-496088405107.asia-southeast1.run.app`;
    if (navigator.share) {
      navigator.share({ title: v.titleHi, text }).catch(() => {});
    } else {
      navigator.clipboard.writeText(text);
      alert('वीडियो लिंक कॉपी हो गया!');
    }
  };

  return (
    <div className="space-y-6 pb-20 md:pb-6">
      
      {/* Title */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl sm:text-2xl font-black text-white flex items-center space-x-2">
            <span>🎬 वीडियो व रील्स गैलरी</span>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-purple-600/30 text-purple-400 border border-purple-500/30 font-semibold">
              HD Shorts
            </span>
          </h2>
          <p className="text-xs text-slate-400 mt-1">
            हेमंत राठौर जी के प्रमुख संबोधन, जनसुनवाई और चुनावी रैलियों की विशेष वीडियो क्लिप्स
          </p>
        </div>
      </div>

      {/* Videos Grid (Responsive: 1 col mobile, 2 col tablet/laptop, 3 col wide PC) */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
        {videos.map((vid) => {
          const isLiked = likes[vid.id];
          return (
            <div
              key={vid.id}
              className="bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-xl flex flex-col justify-between hover:border-purple-500/40 transition-all"
            >
              {/* Video Thumbnail with Political Jacket Overlay */}
              <div
                onClick={() => setActiveVideo(vid)}
                className="relative aspect-video bg-slate-950 overflow-hidden cursor-pointer group"
              >
                {/* Jacket Top Header Ribbon */}
                <div className="absolute top-0 inset-x-0 z-20 bg-gradient-to-r from-purple-900 via-indigo-900 to-slate-900 px-3 py-1.5 flex items-center justify-between text-[11px] text-white font-bold shadow-md">
                  <div className="flex items-center space-x-1.5">
                    <span className="w-2 h-2 rounded-full bg-purple-400 animate-pulse" />
                    <span>हेमंत राठौर • जनसेवा ही संकल्प</span>
                  </div>
                  <span className="text-[10px] text-purple-300 font-medium">आधिकारिक जैकेट</span>
                </div>

                {/* Thumbnail Image */}
                <img
                  src={vid.videoThumbnailUri || "https://images.unsplash.com/photo-1540910419892-4a36d2c3266c?auto=format&fit=crop&w=800&q=80"}
                  alt={vid.titleHi}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                />

                {/* Play Button Overlay */}
                <div className="absolute inset-0 z-10 flex items-center justify-center bg-black/40 group-hover:bg-black/20 transition-colors">
                  <div className="w-14 h-14 rounded-full bg-purple-600/90 group-hover:bg-purple-500 text-white flex items-center justify-center shadow-xl shadow-purple-600/50 group-hover:scale-110 transition-transform">
                    <Play className="w-6 h-6 fill-white ml-0.5" />
                  </div>
                </div>

                {/* Duration Badge */}
                <div className="absolute bottom-3 right-3 z-20 px-2 py-0.5 rounded-md bg-black/75 backdrop-blur text-[11px] font-bold text-white flex items-center space-x-1">
                  <Clock className="w-3 h-3 text-purple-400" />
                  <span>{vid.duration}</span>
                </div>

                {/* Jacket Bottom Footer Strip */}
                <div className="absolute bottom-0 inset-x-0 z-20 bg-gradient-to-t from-slate-950 via-slate-950/80 to-transparent p-3 pt-6 flex items-center justify-between text-xs text-slate-300">
                  <span className="font-semibold text-white">#TeamRathore • जन-जन की आवाज</span>
                </div>
              </div>

              {/* Video Content Details */}
              <div className="p-4 sm:p-5 flex-1 flex flex-col justify-between">
                <div>
                  <div className="flex flex-wrap gap-1.5 mb-2">
                    {vid.categories.map((cat, i) => (
                      <span key={i} className="text-[10px] px-2 py-0.5 rounded bg-purple-500/10 text-purple-300 border border-purple-500/20 font-medium">
                        {cat}
                      </span>
                    ))}
                  </div>
                  <h3 className="font-bold text-base text-white leading-snug line-clamp-2">
                    {vid.titleHi}
                  </h3>
                  <p className="mt-2 text-xs text-slate-300 line-clamp-2 leading-relaxed">
                    {vid.captionHi}
                  </p>
                </div>

                {/* Bottom Engagement Bar */}
                <div className="mt-4 pt-3 border-t border-slate-800 flex items-center justify-between text-xs text-slate-400">
                  <div className="flex items-center space-x-4">
                    <button
                      onClick={() => toggleLike(vid.id)}
                      className={`flex items-center space-x-1.5 transition-colors ${
                        isLiked ? 'text-red-500 font-bold' : 'hover:text-white'
                      }`}
                    >
                      <Heart className={`w-4 h-4 ${isLiked ? 'fill-red-500' : ''}`} />
                      <span>{isLiked ? 'पसंद' : vid.likesCount}</span>
                    </button>
                    <span className="flex items-center space-x-1.5">
                      <MessageCircle className="w-4 h-4" />
                      <span>{vid.commentsCount}</span>
                    </span>
                  </div>

                  <button
                    onClick={() => handleShare(vid)}
                    className="flex items-center space-x-1 px-3 py-1.5 rounded-lg bg-purple-600/20 text-purple-300 hover:bg-purple-600/30 transition-colors font-medium border border-purple-500/30"
                  >
                    <Share2 className="w-3.5 h-3.5" />
                    <span>साझा करें</span>
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Video Modal Player */}
      {activeVideo && (
        <div className="fixed inset-0 z-50 bg-black/90 backdrop-blur-md flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-700 rounded-3xl max-w-2xl w-full overflow-hidden shadow-2xl">
            <div className="p-4 bg-slate-950 flex items-center justify-between border-b border-slate-800">
              <h4 className="text-sm font-bold text-white truncate max-w-md">{activeVideo.titleHi}</h4>
              <button
                onClick={() => setActiveVideo(null)}
                className="w-8 h-8 rounded-full bg-slate-800 hover:bg-slate-700 text-slate-200 flex items-center justify-center font-bold text-sm"
              >
                ✕
              </button>
            </div>

            <div className="relative aspect-video bg-black flex items-center justify-center">
              <img
                src={activeVideo.videoThumbnailUri || "https://images.unsplash.com/photo-1540910419892-4a36d2c3266c?auto=format&fit=crop&w=800&q=80"}
                alt={activeVideo.titleHi}
                className="w-full h-full object-cover opacity-60"
              />
              <div className="absolute inset-0 flex flex-col items-center justify-center text-center p-6 space-y-3">
                <div className="w-16 h-16 rounded-full bg-purple-600 flex items-center justify-center text-white shadow-2xl animate-pulse">
                  <Play className="w-8 h-8 fill-white ml-1" />
                </div>
                <div className="text-white font-bold text-sm">
                  वीडियो लाइव स्ट्रीम • टीम राठौर मीडिया सर्वर
                </div>
                <p className="text-xs text-slate-300 max-w-md">
                  {activeVideo.captionHi}
                </p>
              </div>
            </div>

            <div className="p-4 bg-slate-900 flex items-center justify-between text-xs text-slate-300">
              <span>समय अवधि: {activeVideo.duration}</span>
              <button
                onClick={() => {
                  handleShare(activeVideo);
                  setActiveVideo(null);
                }}
                className="px-4 py-2 rounded-xl bg-orange-600 hover:bg-orange-500 text-white font-bold flex items-center space-x-1.5"
              >
                <Share2 className="w-3.5 h-3.5" />
                <span>व्हाट्सएप पर भेजें</span>
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
};
