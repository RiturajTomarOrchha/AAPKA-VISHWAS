export type Language = 'hi' | 'en';

export type UserRole = 'supporter' | 'worker' | 'office_bearer' | 'admin';

export interface UserProfile {
  fullName: string;
  fatherName: string;
  motherName: string;
  gender: string;
  dob: string;
  mobile: string;
  whatsapp: string;
  email: string;
  state: string;
  district: string;
  assembly: string;
  mandal?: string;
  ward?: string;
  village: string;
  role: UserRole;
  designation: string;
  isApproved: boolean;
  isPendingReview: boolean;
  reviewReason: string;
  customPhotoUri: string | null;
  customLogoUri?: string | null;
  isLoggedIn: boolean;
  isBlocked: boolean;
  hasFacebook: boolean;
  hasInstagram: boolean;
  hasYoutube: boolean;
  hasX: boolean;
  profileCompletion: number;
}

export type FeedContentType = 'ADMIN_GRAPHIC' | 'FACEBOOK_POST' | 'VIDEO';

export interface FeedItem {
  id: string;
  titleHi: string;
  titleEn: string;
  categoryHi: string;
  categoryEn: string;
  captionHi: string;
  captionEn: string;
  type: FeedContentType;
  timestampHi: string;
  timestampEn: string;
  likesCount: string;
  commentsCount: string;
  sharesCount: string;
  isFeatured?: boolean;
  posterHeadlineHi?: string;
  posterSubHeadlineHi?: string;
  facebookAuthor?: string;
  facebookTimeHi?: string;
  facebookContentHi?: string;
  facebookUrl?: string;
  imageUrl?: string;
  downloadableGraphicUrl?: string;
  graphicTheme?: string;
}

export interface VideoFeedItem {
  id: string;
  titleHi: string;
  titleEn: string;
  captionHi: string;
  captionEn: string;
  duration: string;
  timestampHi: string;
  timestampEn: string;
  likesCount: string;
  commentsCount: string;
  sharesCount: string;
  themeColorHex?: string;
  requiresJacket?: boolean;
  aspectRatio: string;
  jacketTemplateName?: string;
  categories: string[];
  videoUri?: string;
  videoThumbnailUri?: string;
}

export interface PosterTemplate {
  id: string;
  title: string;
  category: string;
  aspectRatio: '4:5' | '1:1' | '9:16';
  headerText: string;
  footerText: string;
  slogan: string;
  backgroundGradient: string[];
  bannerTheme: string;
  accentColor: string;
}

export interface ProgramItem {
  id: string;
  titleHi: string;
  titleEn: string;
  dateHi: string;
  dateEn: string;
  timeHi: string;
  timeEn: string;
  venueHi: string;
  venueEn: string;
  instructionsHi: string;
  rsvpStatus: 'NONE' | 'ATTENDING' | 'MAYBE' | 'NOT_ATTENDING';
  attendingCount: number;
  maybeCount: number;
  cantCount: number;
  isHighlighted?: boolean;
}

export interface ImportantNotice {
  id: string;
  titleHi: string;
  titleEn: string;
  departmentHi?: string;
  departmentEn?: string;
  dateHi: string;
  dateEn: string;
  bodyHi: string;
  isUrgent: boolean;
  viewsCount: number;
  likesCount: number;
  agreeCount: number;
  photoAttached?: boolean;
}

export interface WarRoomStats {
  totalBooths: number;
  activeWorkers: number;
  verifiedVoters: number;
  reportedIssues: number;
  resolvedIssues: number;
  liveMeetingActive: boolean;
  meetingTitle: string;
  meetingHost: string;
}
