import React, { useState } from 'react';
import { Header } from './components/Header';
import { Navigation, TabKey } from './components/Navigation';
import { HomeScreen } from './screens/HomeScreen';
import { PosterMakerScreen } from './screens/PosterMakerScreen';
import { WarRoomScreen } from './screens/WarRoomScreen';
import { VideoFeedScreen } from './screens/VideoFeedScreen';
import { VolunteerScreen } from './screens/VolunteerScreen';
import { ProfileScreen } from './screens/ProfileScreen';
import {
  initialUserProfile,
  sampleFeedItems,
  sampleVideos,
  samplePosterTemplates,
  samplePrograms,
  sampleNotices,
  sampleWarRoomStats
} from './services/data';
import { UserProfile, Language } from './types';

export const App: React.FC = () => {
  const [currentTab, setCurrentTab] = useState<TabKey>('home');
  const [lang, setLang] = useState<Language>('hi');
  const [user, setUser] = useState<UserProfile>(initialUserProfile);
  const [feedItems] = useState(sampleFeedItems);
  const [videos] = useState(sampleVideos);
  const [templates] = useState(samplePosterTemplates);
  const [programs] = useState(samplePrograms);
  const [notices] = useState(sampleNotices);
  const [stats] = useState(sampleWarRoomStats);

  const toggleLanguage = () => {
    setLang(prev => (prev === 'hi' ? 'en' : 'hi'));
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-devanagari">
      
      {/* Top Header */}
      <Header
        user={user}
        lang={lang}
        onToggleLang={toggleLanguage}
        onOpenProfile={() => setCurrentTab('profile')}
      />

      {/* Main Body with Sidebar + Screen Content */}
      <div className="flex-1 flex max-w-[1600px] w-full mx-auto">
        
        {/* Navigation (Sidebar on Desktop, Bottom bar on Mobile) */}
        <Navigation
          currentTab={currentTab}
          onSelectTab={setCurrentTab}
          lang={lang}
        />

        {/* Dynamic Screen View (Responsive for PC Chrome & Mobile Chrome) */}
        <main className="flex-1 min-w-0 p-3.5 sm:p-6 lg:p-8 pb-24 md:pb-10 overflow-y-auto">
          {currentTab === 'home' && (
            <HomeScreen
              feedItems={feedItems}
              notices={notices}
              user={user}
              lang={lang}
              stats={stats}
              programs={programs}
              templates={templates}
              onNavigateToPosters={() => setCurrentTab('posters')}
              onNavigateToWarRoom={() => setCurrentTab('warroom')}
            />
          )}

          {currentTab === 'posters' && (
            <PosterMakerScreen
              user={user}
              templates={templates}
              lang={lang}
            />
          )}

          {currentTab === 'warroom' && (
            <WarRoomScreen
              stats={stats}
              notices={notices}
              programs={programs}
              user={user}
            />
          )}

          {currentTab === 'videos' && (
            <VideoFeedScreen
              videos={videos}
            />
          )}

          {currentTab === 'volunteer' && (
            <VolunteerScreen
              user={user}
            />
          )}

          {currentTab === 'profile' && (
            <ProfileScreen
              user={user}
              onUpdateUser={setUser}
            />
          )}
        </main>

      </div>

    </div>
  );
};

export default App;
