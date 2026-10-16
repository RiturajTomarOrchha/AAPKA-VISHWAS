package com.example.data

import com.example.model.*

object SampleData {

    val feedItems = listOf(
        FeedItem(
            id = "post_1",
            titleHi = "हमारा विशेष पोस्ट",
            titleEn = "Featured Campaign Post",
            categoryHi = "हेमंत राठौर",
            categoryEn = "Hemant Rathore",
            captionHi = "जनता की आवाज, हमारा संकल्प। क्षेत्र के सर्वांगीण विकास के लिए हम सदैव समर्पित हैं।",
            captionEn = "Voice of the people, our pledge. Dedicated always to holistic regional development.",
            type = FeedContentType.ADMIN_GRAPHIC,
            timestampHi = "2 घंटे पहले",
            timestampEn = "2 hours ago",
            likesCount = "1.2K",
            commentsCount = "86",
            sharesCount = "340",
            isFeatured = true,
            posterHeadlineHi = "जनता की आवाज, हमारा संकल्प",
            posterSubHeadlineHi = "हर हाथ को काम, हर किसान को सम्मान",
            bannerThemeIndex = 0
        ),
        FeedItem(
            id = "post_2",
            titleHi = "Facebook पोस्ट • राहुल गांधी",
            titleEn = "Facebook Post • Rahul Gandhi",
            categoryHi = "राहुल गांधी",
            categoryEn = "Rahul Gandhi",
            captionHi = "Admin द्वारा चुना गया Facebook post preview",
            captionEn = "Selected Facebook post preview",
            type = FeedContentType.FACEBOOK_POST,
            timestampHi = "3 घंटे पहले",
            timestampEn = "3 hours ago",
            likesCount = "4.8K",
            commentsCount = "520",
            sharesCount = "980",
            facebookAuthor = "Rahul Gandhi",
            facebookTimeHi = "3 घंटे पहले",
            facebookContentHi = "भारत का भविष्य उसके युवाओं के सपनों में है। न्याय, शिक्षा और सम्मानजनक रोजगार हर युवा का संवैधानिक अधिकार है।",
            bannerThemeIndex = 1
        ),
        FeedItem(
            id = "post_3",
            titleHi = "दैनिक सुविचार",
            titleEn = "Daily Quote",
            categoryHi = "सुविचार",
            categoryEn = "Suvichar",
            captionHi = "सत्य और अहिंसा ही वो हथियार हैं जिनसे हर अन्याय को हराया जा सकता है।",
            captionEn = "Truth and non-violence are the weapons that conquer all injustice.",
            type = FeedContentType.ADMIN_GRAPHIC,
            timestampHi = "5 घंटे पहले",
            timestampEn = "5 hours ago",
            likesCount = "890",
            commentsCount = "34",
            sharesCount = "215",
            posterHeadlineHi = "सत्यमेव जयते • दैनिक सुविचार",
            posterSubHeadlineHi = "सेवा और समर्पण ही राष्ट्र निर्माण की नींव है",
            bannerThemeIndex = 0
        ),
        FeedItem(
            id = "post_4",
            titleHi = "मध्य प्रदेश कांग्रेस संदेश",
            titleEn = "MP Congress Message",
            categoryHi = "मध्य प्रदेश कांग्रेस",
            categoryEn = "MP Congress",
            captionHi = "मध्य प्रदेश के हर किसान, युवा और महिला के अधिकारों के लिए कांग्रेस का संघर्ष जारी रहेगा।",
            captionEn = "Congress will relentlessly fight for the rights of every farmer and youth in MP.",
            type = FeedContentType.ADMIN_GRAPHIC,
            timestampHi = "6 घंटे पहले",
            timestampEn = "6 hours ago",
            likesCount = "2.1K",
            commentsCount = "112",
            sharesCount = "440",
            posterHeadlineHi = "न्याय का संकल्प • मध्य प्रदेश कांग्रेस",
            posterSubHeadlineHi = "किसानों को कर्जमुक्ति और युवाओं को रोजगार",
            bannerThemeIndex = 2
        ),
        FeedItem(
            id = "post_5",
            titleHi = "प्रियंका गांधी जी का संदेश",
            titleEn = "Priyanka Gandhi Message",
            categoryHi = "प्रियंका गांधी",
            categoryEn = "Priyanka Gandhi",
            captionHi = "नारी शक्ति के उत्थान से ही देश का सच्चा विकास संभव है। हर बहन को आत्मसम्मान व सुरक्षा मिले।",
            captionEn = "True national progress only happens with women empowerment.",
            type = FeedContentType.ADMIN_GRAPHIC,
            timestampHi = "8 घंटे पहले",
            timestampEn = "8 hours ago",
            likesCount = "3.4K",
            commentsCount = "198",
            sharesCount = "720",
            posterHeadlineHi = "लड़की हूँ, लड़ सकती हूँ",
            posterSubHeadlineHi = "महिलाओं का स्वावलंबन ही सशक्त भारत की नींव",
            bannerThemeIndex = 1
        ),
        FeedItem(
            id = "post_6",
            titleHi = "जीतू पटवारी जी का बयान",
            titleEn = "Jitu Patwari Statement",
            categoryHi = "जीतू पटवारी",
            categoryEn = "Jitu Patwari",
            captionHi = "प्रदेश में भ्रष्टाचार और जनविरोधी नीतियों के खिलाफ कार्यकर्ता हर ब्लॉक में आंदोलन तेज करेंगे।",
            captionEn = "Workers will intensify movements against corruption across all blocks.",
            type = FeedContentType.ADMIN_GRAPHIC,
            timestampHi = "10 घंटे पहले",
            timestampEn = "10 hours ago",
            likesCount = "1.9K",
            commentsCount = "88",
            sharesCount = "390",
            posterHeadlineHi = "जन आक्रोश • जनआंदोलन",
            posterSubHeadlineHi = "हर कार्यकर्ता मैदान में — जनता के हक की लड़ाई",
            bannerThemeIndex = 0
        ),
        FeedItem(
            id = "post_7",
            titleHi = "उमंग सिंगार जी का वक्तव्य",
            titleEn = "Umang Singhar Statement",
            categoryHi = "उमंग सिंगार",
            categoryEn = "Umang Singhar",
            captionHi = "आदिवासी समाज, वंचितों और युवाओं के संवैधानिक अधिकारों की रक्षा हमारा प्राथमिक दायित्व है।",
            captionEn = "Protecting tribal and underprivileged constitutional rights is our primary duty.",
            type = FeedContentType.ADMIN_GRAPHIC,
            timestampHi = "12 घंटे पहले",
            timestampEn = "12 hours ago",
            likesCount = "1.5K",
            commentsCount = "64",
            sharesCount = "280",
            posterHeadlineHi = "हक और अधिकार की लड़ाई",
            posterSubHeadlineHi = "संविधान की रक्षा ही देश की रक्षा है",
            bannerThemeIndex = 2
        ),
        FeedItem(
            id = "post_8",
            titleHi = "कांग्रेस विचारधारा",
            titleEn = "Congress Ideology",
            categoryHi = "कांग्रेस विचारधारा",
            categoryEn = "Congress Ideology",
            captionHi = "सर्वधर्म समभाव, सामाजिक न्याय और भाईचारे का संदेश ही हमारी ऐतिहासिक धरोहर है।",
            captionEn = "Communal harmony, social justice and fraternity are our historic heritage.",
            type = FeedContentType.ADMIN_GRAPHIC,
            timestampHi = "1 दिन पहले",
            timestampEn = "1 day ago",
            likesCount = "2.8K",
            commentsCount = "140",
            sharesCount = "610",
            posterHeadlineHi = "सद्भाव और सामाजिक न्याय",
            posterSubHeadlineHi = "एक भारत, श्रेष्ठ भारत • सर्वधर्म समभाव",
            bannerThemeIndex = 1
        )
    )

    val videoItems = listOf(
        VideoFeedItem(
            id = "vid_1",
            titleHi = "युवा शक्ति ही बदलाव की ताकत है",
            titleEn = "Youth Power is Catalyst for Change",
            captionHi = "ग्वालियर संभाग युवा महासम्मेलन में कार्यकर्ताओं को संबोधित करते हुए।",
            captionEn = "Addressing workers at the Gwalior Division Youth Mega Convention.",
            duration = "03:24",
            timestampHi = "2 दिन पहले",
            timestampEn = "2 days ago",
            likesCount = "1.8K",
            commentsCount = "124",
            sharesCount = "512",
            themeColorIndex = 0,
            requiresJacket = false
        ),
        VideoFeedItem(
            id = "vid_2",
            titleHi = "जनसंवाद कार्यक्रम की एक झलक",
            titleEn = "Glimpses of Jan Samwad Tour",
            captionHi = "ग्रामीण भाइयों और माताओं के साथ सीधा संवाद और समस्याओं का मौके पर निराकरण।",
            captionEn = "Direct interaction with rural brothers and mothers solving grievances on spot.",
            duration = "01:56",
            timestampHi = "5 दिन पहले",
            timestampEn = "5 days ago",
            likesCount = "892",
            commentsCount = "56",
            sharesCount = "274",
            themeColorIndex = 1,
            requiresJacket = true
        ),
        VideoFeedItem(
            id = "vid_3",
            titleHi = "किसान सम्मान एवं चौपाल संवाद",
            titleEn = "Farmer Honor & Village Gathering",
            captionHi = "अन्नदाता किसान भाइयों के साथ खाद, बीज और सिंचाई व्यवस्था पर सकारात्मक चर्चा।",
            captionEn = "Positive discussion on seeds, fertilizers, and irrigation facilities with farmers.",
            duration = "02:45",
            timestampHi = "1 सप्ताह पहले",
            timestampEn = "1 week ago",
            likesCount = "1.4K",
            commentsCount = "98",
            sharesCount = "410",
            themeColorIndex = 2,
            requiresJacket = false
        )
    )

    val suvicharQuotes = listOf(
        SuvicharItem(
            id = "suv_1",
            category = SuvicharCategory.TODAY_THOUGHT,
            quoteHi = "छोटे कदम भी बड़े बदलाव की शुरुआत हो सकते हैं। सेवा ही हमारी सबसे बड़ी शक्ति है।",
            quoteEn = "Even small steps ignite major change. Selfless service is our greatest strength."
        ),
        SuvicharItem(
            id = "suv_2",
            category = SuvicharCategory.INSPIRATION,
            quoteHi = "सत्य और संकल्प के मार्ग पर चलने वालों को कोई बाधा रोक नहीं सकती।",
            quoteEn = "No obstacle can halt those walking the path of truth and resolute commitment."
        ),
        SuvicharItem(
            id = "suv_3",
            category = SuvicharCategory.YOUTH,
            quoteHi = "युवाओं की ऊर्जा और सकारात्मक सोच से ही सशक्त राष्ट्र का निर्माण संभव है।",
            quoteEn = "A strong nation is shaped by the vibrant energy and constructive vision of our youth."
        ),
        SuvicharItem(
            id = "suv_4",
            category = SuvicharCategory.SEVA,
            quoteHi = "जनता की सेवा राजनीति नहीं, हमारा जीवन-धर्म है।",
            quoteEn = "Public service is not mere politics; it is our supreme life duty."
        ),
        SuvicharItem(
            id = "suv_5",
            category = SuvicharCategory.VIKAS,
            quoteHi = "गाँव खुशहाल तो देश समृद्ध, अंतिम पंक्ति के व्यक्ति तक विकास पहुँचे।",
            quoteEn = "When villages prosper, the country thrives. Development must reach the last person."
        ),
        SuvicharItem(
            id = "suv_6",
            category = SuvicharCategory.POSITIVE,
            quoteHi = "एकता और भाईचारे से हर कठिनाई को आसान बनाया जा सकता है।",
            quoteEn = "With unity and brotherhood, every arduous challenge becomes effortless."
        )
    )

    val upcomingPrograms = listOf(
        ProgramItem(
            id = "prog_1",
            titleHi = "जनसंवाद सभा - ओरछा",
            titleEn = "Public Meeting - Orchha",
            dateHi = "12 सितंबर 2026",
            dateEn = "12 Sep 2026",
            timeHi = "शाम 5:00 बजे",
            timeEn = "5:00 PM",
            venueHi = "ओरछा, निवाड़ी (म.प्र.)",
            venueEn = "Orchha, Niwari (M.P.)",
            instructionsHi = "सभी मंडल व सेक्टर पदाधिकारी समय पर उपस्थित हों।",
            rsvpStatus = RsvpStatus.ATTENDING
        ),
        ProgramItem(
            id = "prog_2",
            titleHi = "युवा रोजगार संगोष्ठी",
            titleEn = "Youth Employment Symposium",
            dateHi = "15 सितंबर 2026",
            dateEn = "15 Sep 2026",
            timeHi = "दोपहर 2:00 बजे",
            timeEn = "2:00 PM",
            venueHi = "ग्वालियर टाउन हॉल",
            venueEn = "Gwalior Town Hall",
            instructionsHi = "प्रत्येक बूथ से 5 युवा साथियों की सहभागिता सुनिश्चित करें।",
            rsvpStatus = RsvpStatus.NONE
        )
    )

    val importantNotices = listOf(
        ImportantNotice(
            id = "not_1",
            titleHi = "कल की बैठक स्थगित",
            titleEn = "Tomorrow's Meeting Postponed",
            dateHi = "10 सितंबर 2026",
            dateEn = "10 Sep 2026",
            bodyHi = "सभी कार्यकर्ताओं से निवेदन है कि भारी बारिश के कारण कल की सेक्टर बैठक अब 13 सितंबर को होगी।",
            isUrgent = true
        ),
        ImportantNotice(
            id = "not_2",
            titleHi = "मतदाता सूची सत्यापन अभियान",
            titleEn = "Voter List Verification Drive",
            dateHi = "08 सितंबर 2026",
            dateEn = "08 Sep 2026",
            bodyHi = "घर-घर संपर्क कर नए युवाओं के नाम मतदाता सूची में जुड़वाने का विशेष अभियान 15 तारीख तक जारी रहेगा।",
            isUrgent = false
        )
    )

    val tourSchedules = listOf(
        TourSchedule(
            id = "tour_1",
            leaderNameHi = "टीम राठौर मुख्य नेतृत्व",
            dateHi = "11-14 सितंबर 2026",
            locationHi = "ग्वालियर ग्रामीण • भितरवार • डबरा",
            detailsHi = "गाँव-गाँव चौपाल, किसान संवाद एवं विकास कार्यों का स्थलीय निरीक्षण।"
        )
    )

    val defaultMeeting = MeetingInfo(
        id = "meet_101",
        titleHi = "विधानसभा स्तरीय कार्यकर्ता डिजिटल मीटिंग",
        titleEn = "Assembly Level Digital Worker Meeting",
        dateHi = "आज, 10 सितंबर 2026",
        timeHi = "शाम 7:30 बजे",
        hostHi = "एडमिन / मुख्य प्रवक्ता",
        agendaHi = "आगामी जनसंवाद यात्रा की कार्ययोजना व सोशल मीडिया रणनीति",
        participantCount = 68,
        status = MeetingStatus.UPCOMING,
        meetingUrl = "https://meet.google.com/team-rathore-assembly",
        isScheduled = false
    )

    val officeBearerTasks = listOf(
        OfficeBearerTask(
            id = "task_1",
            titleHi = "क्षेत्रीय कार्यक्रम समन्वय",
            titleEn = "Regional Event Coordination",
            descriptionHi = "ओरछा जनसंवाद सभा हेतु सेक्टर 1 से 4 के कार्यकर्ताओं से संपर्क कर बैठक व्यवस्था सुनिश्चित करें।",
            descriptionEn = "Coordinate seating and transport for Sectors 1-4 for the Orchha public meeting.",
            assignedBy = "जिला अध्यक्ष / केंद्रीय टीम",
            dueDateHi = "11 सितंबर 2026",
            priority = TaskPriority.HIGH,
            status = TaskStatus.IN_PROGRESS,
            evidenceSubmitted = false
        ),
        OfficeBearerTask(
            id = "task_2",
            titleHi = "बूथ सत्यापन एवं व्हाट्सएप ग्रुप निर्माण",
            titleEn = "Booth Verification & WhatsApp Groups",
            descriptionHi = "वार्ड 12 और 14 के प्रत्येक मतदान केंद्र पर 10 समर्पित समर्थकों की सूची तैयार कर ग्रुप में जोड़ें।",
            descriptionEn = "Prepare list of 10 active supporters per polling booth for Wards 12 & 14.",
            assignedBy = "संगठन महासचिव",
            dueDateHi = "16 सितंबर 2026",
            priority = TaskPriority.MEDIUM,
            status = TaskStatus.PENDING
        ),
        OfficeBearerTask(
            id = "task_3",
            titleHi = "सड़क मरम्मत जनसमस्या का सत्यापन",
            titleEn = "Road Repair Grievance Inspection",
            descriptionHi = "पिपरोली मुख्य मार्ग के गड्ढों का निरीक्षण कर मौके की फोटो व रिपोर्ट ऐप पर सबमिट करें।",
            descriptionEn = "Inspect potholes on Piproli main road and submit photo evidence.",
            assignedBy = "विधायक कार्यालय",
            dueDateHi = "08 सितंबर 2026",
            priority = TaskPriority.HIGH,
            status = TaskStatus.COMPLETED,
            evidenceSubmitted = true,
            evidenceNote = "निरीक्षण पूर्ण। PWD अधिकारियों को पत्र प्रेषित किया जा चुका है।"
        )
    )

    val citizenRequests = listOf(
        CitizenRequest(
            id = "TR-2026-001245",
            categoryHi = "सड़क एवं नाली मरम्मत",
            categoryEn = "Road & Drainage Repair",
            message = "ग्राम पिपरोली से मुख्य सड़क तक का रास्ता काफी जर्जर है। बारिश में जलभराव हो जाता है। कृपया पक्की सड़क बनवाने की कृपा करें।",
            dateHi = "08 सितंबर 2026",
            status = RequestStatus.ACTION_TAKEN,
            adminResponseHi = "आपकी मांग पर त्वरित संज्ञान लेकर PWD टीम मौके पर पहुंच चुकी है। सड़क समतलीकरण व गिट्टी डलवाने का कार्य युद्धस्तर पर जारी है।",
            citizenName = "रामस्वरूप यादव",
            citizenPhone = "+91 98261 44556",
            location = "ग्राम पिपरोली, सेक्टर-3",
            adminResolutionPhotoUri = "https://picsum.photos/seed/road_repair/600/400",
            resolutionDateHi = "09 सितं 2026",
            adminOfficerName = "विधायक जनसेवा प्रकोष्ठ • हेमंत राठौर कार्यालय"
        ),
        CitizenRequest(
            id = "TR-2026-000997",
            categoryHi = "बिजली व ट्रांसफार्मर",
            categoryEn = "Electricity & Transformer",
            message = "खेती वाले क्षेत्र में 100 KVA का ट्रांसफार्मर 4 दिन से जला हुआ है। सिंचाई प्रभावित हो रही है।",
            dateHi = "28 अगस्त 2026",
            status = RequestStatus.RESOLVED,
            adminResponseHi = "विद्युत मंडल द्वारा नया 100 KVA ट्रांसफार्मर स्थापित कर विद्युत आपूर्ति बहाल कर दी गई है। समस्या का पूर्ण निवारण हुआ।",
            citizenName = "दिनेश कुशवाहा",
            citizenPhone = "+91 94250 88991",
            location = "कृषि फीडर, ओरछा तिगैला",
            adminResolutionPhotoUri = "https://picsum.photos/seed/transformer_fixed/600/400",
            resolutionDateHi = "30 अगस्त 2026",
            adminOfficerName = "विद्युत समीक्षा सेल • हेमंत राठौर कार्यालय"
        ),
        CitizenRequest(
            id = "TR-2026-000812",
            categoryHi = "पेयजल व हैंडपंप",
            categoryEn = "Drinking Water & Handpump",
            message = "वार्ड क्रमांक 7 की बस्ती में हैंडपंप पिछले एक हफ्ते से खराब है, महिलाओं को दूर से पानी लाना पड़ रहा है।",
            dateHi = "15 अगस्त 2026",
            status = RequestStatus.RESOLVED,
            adminResponseHi = "पीएचई (PHE) मैकेनिक दल द्वारा नया पाइप एवं सिलेंडर डालकर हैंडपंप चालू कर दिया गया है। पानी उपलब्ध है।",
            citizenName = "शांति देवी",
            citizenPhone = "+91 91112 33445",
            location = "वार्ड 7, पृथ्वीपुर",
            adminResolutionPhotoUri = "https://picsum.photos/seed/water_pump/600/400",
            resolutionDateHi = "17 अगस्त 2026",
            adminOfficerName = "जनस्वास्थ्य सेल • हेमंत राठौर कार्यालय"
        )
    )

    val officialDocuments = listOf(
        OfficialDocument(
            id = "doc_1",
            title = "विधानसभा स्तरीय जनसंवाद यात्रा दिशानिर्देश परिपत्र",
            letterNumber = "TR/HQ/2026/CIR-042",
            category = "संगठनात्मक परिपत्र",
            date = "08 सितंबर 2026",
            summary = "सभी मंडल व सेक्टर प्रभारियों को जनसंवाद यात्रा के दौरान बूथ कमेटियों के साथ समन्वय स्थापित करने संबंधी आवश्यक निर्देश।",
            issuedBy = "केंद्रीय संगठन कार्यालय • टीम राठौर",
            documentUrl = "https://example.com/docs/cir_042.pdf"
        ),
        OfficialDocument(
            id = "doc_2",
            title = "अतिवृष्टि एवं फसल क्षति मुआवजा हेतु ज्ञापन प्रति",
            letterNumber = "TR/MEMO/2026/019",
            category = "प्रेस व ज्ञापन",
            date = "05 सितंबर 2026",
            summary = "जिला कलेक्टर को किसानों की फसल बीमा व त्वरित आर्थिक सहायता प्रदान करने हेतु सौंपा गया आधिकारिक पत्र।",
            issuedBy = "जनसेवा प्रभाग • टीम राठौर",
            documentUrl = "https://example.com/docs/memo_019.pdf"
        ),
        OfficialDocument(
            id = "doc_3",
            title = "मंडल व बूथ प्रभारियों की संशोधित नियुक्ति सूची",
            letterNumber = "TR/APPT/2026/108",
            category = "नियुक्ति पत्र",
            date = "01 सितंबर 2026",
            summary = "निवाड़ी विधानसभा के विभिन्न सेक्टरों हेतु नवनियुक्त पदाधिकारियों का आधिकारिक विवरण पत्र।",
            issuedBy = "प्रदेश कांग्रेस समन्वय समिति",
            documentUrl = "https://example.com/docs/appt_108.pdf"
        )
    )

    val fieldReports = listOf(
        FieldReport(
            id = "fr_1",
            authorName = "दिनेश कुशवाहा",
            area = "वार्ड 07, निवाड़ी",
            subject = "नल-जल योजना पाइपलाइन लीकेज",
            message = "मुख्य चौराहे के पास पिछले 3 दिनों से पेयजल लाइन टूटी हुई है जिससे घरों में गंदा पानी आ रहा है। तत्काल नगर परिषद से समन्वय आवश्यक है।",
            photoUri = "sample_leakage_photo",
            videoUri = null,
            date = "आज सुबह 10:15",
            status = "प्राप्त"
        ),
        FieldReport(
            id = "fr_2",
            authorName = "राकेश तोमर",
            area = "ग्राम तरीचर कलां",
            subject = "किसानों की खाद वितरण समस्या",
            message = "सोसायटी पर डीएपी और यूरिया खाद वितरण में किसानों की लंबी कतार लग रही है। अतिरिक्त काउंटर खोलने की आवश्यकता है।",
            photoUri = "sample_kisan_photo",
            videoUri = "sample_video_clip",
            date = "कल शाम 04:30",
            status = "प्रगति पर"
        )
    )

    val notifications = listOf(
        NotificationItem(
            id = "notif_1",
            titleHi = "जरूरी सूचना: कल की बैठक",
            titleEn = "Urgent: Tomorrow's Meeting",
            messageHi = "भारी बारिश के कारण कल की बैठक 13 सितंबर तक स्थगित की गई है।",
            messageEn = "Tomorrow's meeting is rescheduled to Sep 13 due to heavy rainfall.",
            timeAgoHi = "15 मिनट पहले",
            isUnread = true,
            type = "urgent"
        ),
        NotificationItem(
            id = "notif_2",
            titleHi = "नया ग्राफिक उपलब्ध है",
            titleEn = "New Graphic Available",
            messageHi = "आज का नया अभियान पोस्टर अपलोड हो चुका है। अपनी फोटो के साथ कस्टमाइज करें।",
            messageEn = "Today's campaign poster is uploaded. Personalize it with your photo.",
            timeAgoHi = "2 घंटे पहले",
            isUnread = true,
            type = "graphic"
        )
    )
}
