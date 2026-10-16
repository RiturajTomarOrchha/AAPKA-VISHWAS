import { FeedItem, VideoFeedItem, PosterTemplate, ProgramItem, ImportantNotice, WarRoomStats, UserProfile } from '../types';

export const initialUserProfile: UserProfile = {
  fullName: "रितुराज तोमर",
  fatherName: "श्री महेन्द्र सिंह तोमर",
  motherName: "श्रीमती सरोज देवी",
  gender: "पुरुष",
  dob: "15/08/1996",
  mobile: "+91 98765 43210",
  whatsapp: "+91 98765 43210",
  email: "rituraj.aapkavishwas@gmail.com",
  state: "मध्य प्रदेश",
  district: "ग्वालियर",
  assembly: "ग्वालियर दक्षिण (17)",
  village: "वार्ड क्र. 28, लश्कर",
  role: "worker",
  designation: "सक्रिय कार्यकर्ता • आपका विश्वास",
  isApproved: true,
  isPendingReview: false,
  reviewReason: "",
  customPhotoUri: null,
  isBlocked: false,
  hasFacebook: true,
  hasInstagram: true,
  hasYoutube: false,
  hasX: true,
  profileCompletion: 92
};

export const sampleFeedItems: FeedItem[] = [
  {
    id: "feed-1",
    titleHi: "आपका विश्वास • सदैव आपके साथ: क्षेत्र के समग्र विकास और जन-जन की सेवा का महा-संकल्प",
    titleEn: "Aapka Vishwas - Sadaiv Aapke Saath: Great pledge for overall development and public service",
    categoryHi: "आधिकारिक घोषणा",
    categoryEn: "Official Announcement",
    captionHi: "हर गाँव, हर वार्ड और हर घर तक सम्मान, विकास और न्याय पहुँचाना ही हमारा परम ध्येय है। आपका अटूट विश्वास ही हमारी सबसे बड़ी शक्ति है।",
    captionEn: "Ensuring respect, development, and justice for every household. Your trust is our strength.",
    type: "ADMIN_GRAPHIC",
    timestampHi: "अभी-अभी लाइव",
    timestampEn: "Just now",
    likesCount: "4.9K",
    commentsCount: "682",
    sharesCount: "1.5K",
    isFeatured: true,
    posterHeadlineHi: "आपका विश्वास • सदैव आपके साथ",
    posterSubHeadlineHi: "सत्य, सेवा और समर्पण ही राजनीति का मूल आधार है",
    imageUrl: "/aapka_vishwas_leader.png"
  },
  {
    id: "feed-2",
    titleHi: "किसान कल्याण एवं सिंचाई परियोजना की समीक्षा बैठक",
    titleEn: "Review meeting on Farmer Welfare and Irrigation Project",
    categoryHi: "किसान कल्याण",
    categoryEn: "Farmer Welfare",
    captionHi: "अन्नदाता किसान भाइयों को समय पर नहर का पानी, पर्याप्त खाद और निर्बाध बिजली उपलब्ध कराने हेतु अधिकारियों के साथ विस्तृत बैठक संपन्न हुई।",
    captionEn: "Detailed review meeting held with officials to ensure timely canal water and fertilizer for farmers.",
    type: "FACEBOOK_POST",
    timestampHi: "5 घंटे पहले",
    timestampEn: "5 hours ago",
    likesCount: "940",
    commentsCount: "82",
    sharesCount: "180",
    facebookAuthor: "हेमंत राठौर आधिकारिक",
    facebookTimeHi: "आज दोपहर 12:30",
    facebookContentHi: "किसान का पसीना व्यर्थ नहीं जाएगा। सिंचाई की हर समस्या का त्वरित समाधान हमारा दायित्व है।"
  },
  {
    id: "feed-3",
    titleHi: "विशाल युवा संवाद एवं रोजगार मार्गदर्शन शिविर",
    titleEn: "Youth Dialogue & Career Guidance Camp",
    categoryHi: "युवा शक्ति",
    categoryEn: "Youth Power",
    captionHi: "विधानसभा क्षेत्र के 500 से अधिक होनहार युवाओं के साथ सीधा संवाद। करियर, स्टार्टअप और कौशल विकास पर गहन चर्चा।",
    captionEn: "Direct dialogue with over 500 youth on career, startups, and skill development.",
    type: "VIDEO",
    timestampHi: "1 दिन पहले",
    timestampEn: "1 day ago",
    likesCount: "3.2K",
    commentsCount: "410",
    sharesCount: "1.2K",
    imageUrl: "https://images.unsplash.com/photo-1511578314322-379afb476865?auto=format&fit=crop&w=1200&q=80"
  }
];

export const sampleVideos: VideoFeedItem[] = [
  {
    id: "vid-1",
    titleHi: "विधानसभा में जनसमस्याओं पर ऐतिहासिक संबोधन • हेमंत राठौर",
    titleEn: "Historic address in Assembly on public issues • Hemant Rathore",
    captionHi: "क्षेत्र की सड़कों और पेयजल संकट पर सरकार से दो टूक सवाल। जनता के हक की लड़ाई हर मंच पर लड़ी जाएगी।",
    captionEn: "Firm questions raised regarding roads and water crisis.",
    duration: "02:45",
    timestampHi: "आज सुबह",
    timestampEn: "Today morning",
    likesCount: "4.5K",
    commentsCount: "580",
    sharesCount: "1.9K",
    requiresJacket: true,
    aspectRatio: "9:16",
    jacketTemplateName: "हेमंत राठौर आधिकारिक जैकेट",
    categories: ["संबोधन", "विधानसभा", "जनआवाज"],
    videoThumbnailUri: "https://images.unsplash.com/photo-1540910419892-4a36d2c3266c?auto=format&fit=crop&w=800&q=80"
  },
  {
    id: "vid-2",
    titleHi: "गाँव-गाँव जनसुनवाई: हर नागरिक की समस्या का मौके पर निस्तारण",
    titleEn: "Village connect: On-the-spot redressal of grievances",
    captionHi: "प्रशासनिक अधिकारियों की मौजूदगी में पेंशन, पट्टा और सड़क निर्माण के मामलों में त्वरित निर्देश दिए।",
    captionEn: "Immediate instructions issued to administrative officials.",
    duration: "01:30",
    timestampHi: "कल",
    timestampEn: "Yesterday",
    likesCount: "2.8K",
    commentsCount: "312",
    sharesCount: "890",
    requiresJacket: true,
    aspectRatio: "9:16",
    categories: ["जनसुनवाई", "विकास"],
    videoThumbnailUri: "https://images.unsplash.com/photo-1475721027785-f74eccf877e2?auto=format&fit=crop&w=800&q=80"
  }
];

export const samplePosterTemplates: PosterTemplate[] = [
  {
    id: "tpl-aapka-vishwas-flagship",
    title: "आपका विश्वास • सदैव आपके साथ",
    category: "अभियान",
    aspectRatio: "4:5",
    headerText: "जय जवान • जय किसान • जन-जन की आवाज",
    footerText: "समर्पित कार्यकर्ता • आपका विश्वास",
    slogan: "आपका विश्वास • सदैव आपके साथ",
    backgroundGradient: ["#ea580c", "#1e1b4b", "#020617"],
    bannerTheme: "flagship_leader",
    accentColor: "#f97316"
  },
  {
    id: "tpl-saffron-glory",
    title: "राष्ट्र सेवा व विकास पर्व",
    category: "विकास",
    aspectRatio: "4:5",
    headerText: "जय जवान • जय किसान • जय युवा",
    footerText: "समर्पित कार्यकर्ता • आपका विश्वास",
    slogan: "जन-जन की सेवा, क्षेत्र का स्वाभिमान",
    backgroundGradient: ["#ea580c", "#c2410c", "#7c2d12"],
    bannerTheme: "royal_saffron",
    accentColor: "#f97316"
  },
  {
    id: "tpl-royal-blue",
    title: "सत्यमेव जयते • जननायक",
    category: "अभियान",
    aspectRatio: "4:5",
    headerText: "हक और न्याय की मजबूत आवाज",
    footerText: "विधानसभा क्षेत्र कार्यकर्ता मंच",
    slogan: "आपका विश्वास, हमारा संकल्प",
    backgroundGradient: ["#1e3a8a", "#172554", "#090d16"],
    bannerTheme: "deep_navy",
    accentColor: "#3b82f6"
  },
  {
    id: "tpl-tricolor-seva",
    title: "तिरंगा संकल्प • एकता व प्रगति",
    category: "राष्ट्रीय पर्व",
    aspectRatio: "4:5",
    headerText: "राष्ट्र सर्वोपरि • सेवा ही धर्म",
    footerText: "जनसंपर्क प्रकोष्ठ • आपका विश्वास",
    slogan: "हर हाथ को काम, हर खेत को पानी",
    backgroundGradient: ["#ea580c", "#1e293b", "#059669"],
    bannerTheme: "tricolor_gold",
    accentColor: "#10b981"
  },
  {
    id: "tpl-festive-shubh",
    title: "शुभकामना संदेश • त्यौहार पर्व",
    category: "शुभकामनाएँ",
    aspectRatio: "4:5",
    headerText: "हार्दिक बधाई एवं मंगलकामनाएँ",
    footerText: "समस्त क्षेत्रवासियों को सप्रेम भेंट",
    slogan: "सुख, समृद्धि और खुशहाली का पर्व",
    backgroundGradient: ["#b45309", "#78350f", "#451a03"],
    bannerTheme: "festive_gold",
    accentColor: "#fbbf24"
  },
  {
    id: "tpl-kisan-nyay",
    title: "किसान न्याय एवं खुशहाली",
    category: "किसान",
    aspectRatio: "4:5",
    headerText: "जय जवान • जय किसान • खुशहाल हिंदुस्तान",
    footerText: "किसान कल्याण मोर्चा",
    slogan: "अन्नदाता का मान, देश की शान",
    backgroundGradient: ["#14532d", "#064e3b", "#022c22"],
    bannerTheme: "kisan_green",
    accentColor: "#22c55e"
  },
  {
    id: "tpl-youth-power",
    title: "युवा शक्ति • रोजगार अधिकार",
    category: "युवा",
    aspectRatio: "4:5",
    headerText: "युवा शक्ति ही देश की सबसे बड़ी ताकत है",
    footerText: "युवा संवाद प्रकोष्ठ",
    slogan: "शिक्षा, सम्मान और सुनिश्चित रोजगार",
    backgroundGradient: ["#991b1b", "#7f1d1d", "#450a0a"],
    bannerTheme: "youth_red",
    accentColor: "#ef4444"
  },
  {
    id: "tpl-samvidhan-raksha",
    title: "संविधान व लोकतंत्र रक्षा",
    category: "अभियान",
    aspectRatio: "4:5",
    headerText: "सत्य, अहिंसा और सामाजिक न्याय",
    footerText: "जनसंपर्क एवं विचार प्रकोष्ठ",
    slogan: "लोकतंत्र बचाओ, देश आगे बढ़ाओ",
    backgroundGradient: ["#581c87", "#3b0764", "#1e1b4b"],
    bannerTheme: "royal_purple",
    accentColor: "#a855f7"
  },
  {
    id: "tpl-shradhanjali",
    title: "सादर नमन • विनम्र श्रद्धांजलि",
    category: "स्मृति",
    aspectRatio: "4:5",
    headerText: "॥ विनम्र श्रद्धांजलि ॥",
    footerText: "शोक संतप्त: समस्त परिवार एवं शुभचिंतक",
    slogan: "ईश्वर दिवंगत आत्मा को शांति प्रदान करें",
    backgroundGradient: ["#334155", "#1e293b", "#0f172a"],
    bannerTheme: "solemn_dark",
    accentColor: "#94a3b8"
  }
];

export const samplePrograms: ProgramItem[] = [
  {
    id: "prog-1",
    titleHi: "विशाल कार्यकर्ता सम्मेलन एवं बूथ विजय संकल्प",
    titleEn: "Mega Worker Convention & Booth Victory Pledge",
    dateHi: "रविवार, 20 अक्टूबर 2026",
    dateEn: "Sunday, Oct 20, 2026",
    timeHi: "प्रातः 10:00 बजे",
    timeEn: "10:00 AM",
    venueHi: "केंद्रीय कार्यालय प्रांगण, मुख्य मार्ग, ग्वालियर",
    venueEn: "Central Office Ground, Main Road, Gwalior",
    instructionsHi: "सभी मंडल अध्यक्ष, सेक्टर प्रभारी व बीएलए साथी समय पूर्व पहुँचकर स्थान ग्रहण करें।",
    rsvpStatus: "ATTENDING",
    attendingCount: 385,
    maybeCount: 42,
    cantCount: 8,
    isHighlighted: true
  },
  {
    id: "prog-2",
    titleHi: "पदयात्रा व जनसुनवाई: वार्ड क्र. 12 से 18",
    titleEn: "Padyatra & Public Hearing: Ward 12 to 18",
    dateHi: "मंगलवार, 22 अक्टूबर 2026",
    dateEn: "Tuesday, Oct 22, 2026",
    timeHi: "सायं 04:30 बजे",
    timeEn: "04:30 PM",
    venueHi: "कमल तिराहा से प्रारंभ होकर सामुदायिक भवन तक",
    venueEn: "Starts from Kamal Tiraha to Community Hall",
    instructionsHi: "स्थानीय समस्याओं के आवेदन पत्र एकत्रित करने के लिए वॉलिंटियर्स उपस्थित रहें।",
    rsvpStatus: "NONE",
    attendingCount: 162,
    maybeCount: 18,
    cantCount: 3
  }
];

export const sampleNotices: ImportantNotice[] = [
  {
    id: "not-1",
    titleHi: "मतदाता सूची पुनरीक्षण अभियान (SIR) में प्रत्येक पात्र युवा का नाम जुड़वाएं",
    titleEn: "Ensure enrollment of eligible youth in voter list revision drive",
    departmentHi: "संगठन प्रकोष्ठ",
    departmentEn: "Organization Cell",
    dateHi: "15 अक्टूबर 2026",
    dateEn: "15 Oct 2026",
    bodyHi: "सभी बूथ प्रभारियों को निर्देशित किया जाता है कि 18 वर्ष पूर्ण कर चुके प्रत्येक नवयुवक एवं नवयुवती का फॉर्म-6 भरवाकर मतदाता सूची में नाम अनिवार्यतः जुड़वाएं।",
    isUrgent: true,
    viewsCount: 1420,
    likesCount: 680,
    agreeCount: 412
  },
  {
    id: "not-2",
    titleHi: "सोशल मीडिया योद्धा प्रकोष्ठ: दैनिक प्रचार दिशा-निर्देश",
    titleEn: "Social Media Warriors Cell: Daily campaign guidelines",
    departmentHi: "सोशल मीडिया सेल",
    departmentEn: "Social Media Cell",
    dateHi: "14 अक्टूबर 2026",
    dateEn: "14 Oct 2026",
    bodyHi: "सभी कार्यकर्ता प्रतिदिन जारी आधिकारिक पोस्टर को अपने व्हाट्सएप स्टेटस, फेसबुक एवं इंस्टाग्राम पर #AapkaVishwas हैशटैग के साथ अनिवार्य रूप से साझा करें।",
    isUrgent: false,
    viewsCount: 980,
    likesCount: 520,
    agreeCount: 310
  }
];

export const sampleWarRoomStats: WarRoomStats = {
  totalBooths: 284,
  activeWorkers: 1240,
  verifiedVoters: 248600,
  reportedIssues: 38,
  resolvedIssues: 34,
  liveMeetingActive: true,
  meetingTitle: "साप्ताहिक चुनावी समीक्षा व बूथ संवाद",
  meetingHost: "हेमंत राठौर (अध्यक्ष) एवं कोर कमेटी"
};
