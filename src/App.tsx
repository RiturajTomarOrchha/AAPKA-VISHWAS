import React, { useState, useEffect } from 'react';
import { Header } from './components/Header';
import { Navigation, TabKey } from './components/Navigation';
import { InstallAppModal } from './components/InstallAppModal';
import { SignUpModal } from './components/SignUpModal';
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

  // PWA / Add to Home Screen states
  const [deferredPrompt, setDeferredPrompt] = useState<any>(null);
  const [isInstallModalOpen, setIsInstallModalOpen] = useState(false);
  const [isSignUpModalOpen, setIsSignUpModalOpen] = useState(false);
  const [isAfterSignUp, setIsAfterSignUp] = useState(false);

  useEffect(() => {
    const handleBeforeInstall = (e: Event) => {
      e.preventDefault();
      setDeferredPrompt(e);
    };

    window.addEventListener('beforeinstallprompt', handleBeforeInstall);

    return () => {
      window.removeEventListener('beforeinstallprompt', handleBeforeInstall);
    };
  }, []);

  const toggleLanguage = () => {
    setLang(prev => (prev === 'hi' ? 'en' : 'hi'));
  };

  const handleOpenInstallApp = () => {
    setIsAfterSignUp(false);
    setIsInstallModalOpen(true);
  };

  const handleCompleteSignUp = (updatedUser: UserProfile) => {
    setUser(updatedUser);
    setIsSignUpModalOpen(false);
    // As requested: After completing sign up, automatically show the "Add to Home Screen / Download Web App APK" prompt!
    setIsAfterSignUp(true);
    setCurrentTab('home');
    setIsInstallModalOpen(true);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-devanagari">
      
      {/* Top Header (Web Application Layout) */}
      <Header
        user={user}
        lang={lang}
        onToggleLang={toggleLanguage}
        onOpenProfile={() => setCurrentTab('profile')}
        onOpenInstallApp={handleOpenInstallApp}
        onOpenSignUp={() => setIsSignUpModalOpen(true)}
      />

      {/* Main Body with Sidebar + Screen Content */}
      <div className="flex-1 flex max-w-[1600px] w-full mx-auto">
        
        {/* Navigation (Sidebar on Desktop/Tablet, Bottom bar on Mobile Chrome) */}
        <Navigation
          currentTab={currentTab}
          onSelectTab={setCurrentTab}
          lang={lang}
          onOpenInstallApp={handleOpenInstallApp}
        />

        {/* Dynamic Screen View (Optimized for PC Chrome, Tablet & Mobile Chrome) */}
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
              onOpenInstallApp={handleOpenInstallApp}
              onOpenSignUp={() => setIsSignUpModalOpen(true)}
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

      {/* Install App / Add to Home Screen Modal (Web Application PWA & APK Download) */}
      <InstallAppModal
        isOpen={isInstallModalOpen}
        onClose={() => setIsInstallModalOpen(false)}
        deferredPrompt={deferredPrompt}
        isAfterSignUp={isAfterSignUp}
      />

      {/* Sign Up / Member Registration Modal */}
      <SignUpModal
        isOpen={isSignUpModalOpen}
        onClose={() => setIsSignUpModalOpen(false)}
        currentUser={user}
        onCompleteSignUp={handleCompleteSignUp}
      />

    </div>
  );
};

export default App;
