package com.example.util

import com.example.model.Language

object AppStrings {
    fun get(key: String, lang: Language): String {
        val entry = strings[key] ?: return key
        return if (lang == Language.HINDI) entry.first else entry.second
    }

    private val strings = mapOf(
        // App Identity & Taglines
        "app_title" to Pair("TEAM RATHORE", "TEAM RATHORE"),
        "app_tagline" to Pair("साथ • सेवा • समर्पण • संगठन", "Together • Service • Dedication • Organization"),
        "app_subtagline" to Pair("जनसेवा ही हमारा संकल्प • भारतीय राष्ट्रीय कांग्रेस", "Public Service is Our Resolve • INC"),
        "app_slogan" to Pair("हाथ से हाथ जोड़ो, संगठन को मजबूत करो", "Join Hands, Strengthen the Organization"),

        // Navigation
        "nav_home" to Pair("होम", "Home"),
        "nav_videos" to Pair("वीडियो", "Videos"),
        "nav_create" to Pair("बनाएं", "Create"),
        "nav_war_room" to Pair("वॉर रूम", "War Room"),
        "nav_profile" to Pair("प्रोफाइल", "Profile"),
        "nav_tasks" to Pair("जिम्मेदारियाँ", "Tasks"),

        // Language & Splash
        "choose_language" to Pair("अपनी भाषा चुनें", "Choose Your Language"),
        "hindi" to Pair("हिंदी", "Hindi"),
        "english" to Pair("English", "English"),
        "continue_btn" to Pair("आगे बढ़ें →", "Continue →"),
        "login_title" to Pair("Login", "Login"),
        "login_sub" to Pair("मोबाइल नंबर से सुरक्षित प्रवेश", "Secure login via mobile number"),
        "welcome_back" to Pair("साथ मिलकर, बेहतर कल के लिए", "Together for a better tomorrow"),
        "mobile_number" to Pair("मोबाइल नंबर दर्ज करें", "Enter Mobile Number"),
        "send_otp" to Pair("OTP भेजें", "Send OTP"),
        "login_with_password" to Pair("पासवर्ड से लॉगिन", "Login with Password"),
        "new_user_register" to Pair("नया उपयोगकर्ता? अभी जुड़ें", "New User? Register Now"),

        // OTP
        "otp_title" to Pair("मोबाइल नंबर सत्यापन", "OTP Verification"),
        "otp_subtitle" to Pair("OTP आपके दर्ज मोबाइल नंबर पर भेजा गया है।", "OTP has been sent to your registered mobile number."),
        "enter_otp" to Pair("6-अंकीय OTP दर्ज करें", "Enter 6-digit OTP"),
        "verify_otp" to Pair("सत्यापित करें", "Verify"),
        "resend_otp" to Pair("OTP पुनः भेजें", "Resend OTP"),

        // Role Selection
        "role_question" to Pair("आपकी भूमिका क्या है?", "What is your role?"),
        "role_subtitle" to Pair("सही भूमिका चुनें ताकि सही फीचर्स दिखें", "Select your role to access personalized tools"),
        "role_supporter" to Pair("समर्थक", "Supporter"),
        "role_supporter_desc" to Pair("विचारधारा से जुड़ें, रेडीमेड पोस्टर व वीडियो शेयर करें", "Connect with vision, share posters & videos"),
        "role_worker" to Pair("कार्यकर्ता", "Worker"),
        "role_worker_desc" to Pair("सक्रिय रूप से टीम के साथ काम करें, वॉर रूम व कार्यक्रम", "Active ground mobilization, War Room & events"),
        "role_office_bearer" to Pair("पदाधिकारी", "Office Bearer"),
        "role_office_bearer_desc" to Pair("नेतृत्व करें, सौंपी गई जिम्मेदारियाँ और कार्य संभालें", "Leadership, task management & responsibilities"),

        // Registration Steps
        "reg_step1_title" to Pair("व्यक्तिगत जानकारी (1/3)", "Personal Information (1/3)"),
        "reg_step2_title" to Pair("क्षेत्र की जानकारी (2/3)", "Location Information (2/3)"),
        "reg_step3_title" to Pair("संपर्क एवं सोशल मीडिया (3/3)", "Contact & Social Media (3/3)"),
        "upload_photos" to Pair("अपनी फोटो अपलोड करें", "Upload Your Photos"),
        "upload_photos_hint" to Pair("2 से 5 फोटो • Square Crop • न्यूनतम 2 जरूरी", "2 to 5 photos • Square Crop • Min 2 mandatory"),
        "full_name" to Pair("पूरा नाम *", "Full Name *"),
        "father_name" to Pair("पिता का नाम *", "Father's Name *"),
        "mother_name" to Pair("माता का नाम *", "Mother's Name *"),
        "gender" to Pair("लिंग *", "Gender *"),
        "gender_male" to Pair("पुरुष", "Male"),
        "gender_female" to Pair("महिला", "Female"),
        "gender_other" to Pair("अन्य", "Other"),
        "dob" to Pair("जन्म तिथि *", "Date of Birth *"),
        "state_fixed" to Pair("राज्य (मध्य प्रदेश - फिक्स)", "State (Madhya Pradesh - Fixed)"),
        "district" to Pair("जिला *", "District *"),
        "assembly" to Pair("विधानसभा *", "Assembly Constituency *"),
        "village" to Pair("ग्राम / वार्ड *", "Village / Ward *"),
        "location_privacy_note" to Pair("यह जानकारी केवल संगठन रिकॉर्ड व सूचनाओं के लिए है। आपके बनाए गए ग्राफिक में स्थान कभी नहीं दिखाया जाएगा।", "This location is for database records only. It is NEVER rendered on social media graphics."),
        "whatsapp_mandatory" to Pair("WhatsApp नंबर (अनिवार्य) *", "WhatsApp Number (Mandatory) *"),
        "email_optional" to Pair("ईमेल (वैकल्पिक)", "Email (Optional)"),
        "social_handles" to Pair("आप कौन-कौन से सोशल मीडिया चलाते हैं?", "Which social platforms do you use?"),
        "declaration" to Pair("मैं घोषणा करता/करती हूँ कि दी गई जानकारी सत्य है।", "I certify that the information provided is correct."),
        "create_account" to Pair("खाता बनाएँ ✓", "Create Account ✓"),
        "prev_btn" to Pair("← पिछला", "← Previous"),

        // Home Feed
        "feed_tab_all" to Pair("सभी", "All"),
        "feed_tab_our_posts" to Pair("हमारे पोस्ट", "Official Posts"),
        "feed_tab_social" to Pair("सोशल मीडिया", "Social Media"),
        "feed_tab_videos" to Pair("वीडियो", "Videos"),
        "our_special_post" to Pair("हमारा विशेष पोस्ट", "Featured Campaign Post"),
        "share" to Pair("Share", "Share"),
        "create_from_post" to Pair("इस पोस्ट से बनाएं", "Personalize This"),
        "facebook_post" to Pair("Facebook पोस्ट", "Facebook Post"),
        "view_on_fb" to Pair("Facebook पर देखें", "View on Facebook"),
        "create_graphic" to Pair("इस पोस्ट को रीशेयर करें", "Reshare This Post"),
        "reshare_post" to Pair("इस पोस्ट को रीशेयर करें", "Reshare This Post"),

        // Editing Table
        "editing_table_title" to Pair("अपना Graphic बनाएं", "Graphic Editing Studio"),
        "editing_table_sub" to Pair("अपनी फोटो और नाम जोड़ें", "Add your photo & name (4:5 Social Media Size)"),
        "identity_options" to Pair("पहचान विकल्प", "Identity Options"),
        "option_photo" to Pair("फोटो दिखाएं", "Show Photo"),
        "option_name" to Pair("नाम दिखाएं", "Show Name"),
        "option_designation" to Pair("पद / उपनाम दिखाएं", "Show Designation"),
        "edit_name" to Pair("नाम संपादित करें", "Edit Display Name"),
        "edit_designation" to Pair("पद / उपनाम (वैकल्पिक)", "Designation / Title (Optional)"),
        "download_graphic" to Pair("डाउनलोड करें", "Download Graphic"),
        "share_graphic" to Pair("शेयर करें", "Share Graphic"),
        "save_to_gallery" to Pair("गैलरी में सेव करें", "Save to Gallery"),
        "graphic_ready" to Pair("Graphic तैयार है!", "Graphic is Ready!"),
        "graphic_ready_sub" to Pair("गैलरी में सेव हो गया व सोशल मीडिया पर शेयर करने के लिए तैयार है।", "Saved to gallery and ready to share across WhatsApp and social media."),

        // Daily Suvichar
        "daily_suvichar" to Pair("आज का सुविचार", "Daily Suvichar"),
        "suvichar_subtitle" to Pair("सीमित, अनुमोदित प्रेरणादायक विचार से रेडी-टू-शेयर ग्राफिक बनाएं", "Create verified inspiring thoughts with 1-click personal branding"),
        "new_suvichar" to Pair("नया सुविचार बनाएं", "Generate New Thought"),
        "select_category" to Pair("श्रेणी चुनें", "Select Category"),
        "write_custom_thought" to Pair("अपना विचार लिखें (वैकल्पिक)", "Write Custom Thought (Optional)"),

        // Videos
        "videos_title" to Pair("वीडियो फीड", "Video Feed"),
        "play_video" to Pair("Play Video", "Play Video"),
        "reuse_video" to Pair("इस वीडियो से बनाएं", "Reuse with My Brand"),
        "download_video" to Pair("डाउनलोड", "Download"),
        "video_processing" to Pair("वीडियो तैयार हो रहा है", "Processing Video"),
        "video_processing_sub" to Pair("मोबाइल फ्रेंडली कम्प्रेशन व ब्रांडिंग लागू हो रही है...", "Applying mobile compression & branding..."),

        // War Room
        "war_room_title" to Pair("वॉर रूम", "War Room"),
        "war_room_sub" to Pair("कार्यकर्ता समन्वय व संचार केंद्र", "Worker Communication Hub"),
        "tile_important_info" to Pair("जरूरी सूचना", "Important Notice"),
        "tile_upcoming_programs" to Pair("आगामी कार्यक्रम", "Upcoming Events"),
        "tile_live_meeting" to Pair("लाइव मीटिंग", "Live Meeting"),
        "tile_send_suggestion" to Pair("सुझाव दें", "Send Suggestion"),
        "tile_share_info" to Pair("जानकारी दें", "Share Info"),
        "tile_documents" to Pair("दस्तावेज़", "Documents"),
        "next_program" to Pair("अगला कार्यक्रम", "Next Program"),
        "join_meeting" to Pair("मीटिंग में जुड़ें", "Join Meeting"),
        "live_meeting_title" to Pair("लाइव मीटिंग", "Live Meeting"),
        "meeting_waiting_room" to Pair("मीटिंग वेटिंग रूम", "Meeting Waiting Room"),
        "meeting_waiting_sub" to Pair("होस्ट (एडमिन) द्वारा मीटिंग शुरू करने की प्रतीक्षा करें...", "Waiting for the host to admit you to the room..."),
        "meeting_ended" to Pair("मीटिंग समाप्त", "Meeting Ended"),
        "leave_meeting" to Pair("मीटिंग छोड़ें", "Leave Meeting"),
        "mic" to Pair("माइक", "Mic"),
        "camera" to Pair("कैमरा", "Camera"),
        "rsvp_attending" to Pair("शामिल होंगे", "Attending"),
        "rsvp_maybe" to Pair("शायद", "Maybe"),
        "rsvp_cant" to Pair("नहीं आ पाएँगे", "Can't Attend"),

        // Office Bearer Tasks
        "tasks_title" to Pair("मेरी जिम्मेदारियाँ व कार्य", "My Responsibilities & Tasks"),
        "assigned_responsibility" to Pair("सौंपी गई मुख्य जिम्मेदारी", "Assigned Core Responsibility"),
        "accept_task" to Pair("टास्क स्वीकार करें", "Accept Task"),
        "task_accept" to Pair("टास्क स्वीकार करें", "Accept Task"),
        "task_complete_action" to Pair("पूर्ण चिह्नित करें", "Mark Complete"),
        "update_task" to Pair("कार्य अपडेट व रिपोर्ट", "Submit Task Update"),
        "evidence_hint" to Pair("फोटो / दस्तावेज / प्रगति विवरण संलग्न करें", "Attach photo, progress note or proof"),
        "task_overdue" to Pair("Due Date निकल चुकी है", "Task is Overdue"),
        "task_submitted_msg" to Pair("आपकी प्रगति रिपोर्ट एडमिन को भेज दी गई है।", "Progress report submitted for admin review."),

        // Direct Connect (Aapka Vidhayak Aapke Paas)
        "direct_connect_title" to Pair("सीधा संवाद", "Direct Connect"),
        "vidhayak_title" to Pair("विधायक आपके पास", "Aapka Vidhayak"),
        "vidhayak_sub" to Pair("सीधी बात, सीधे समाधान की ओर", "Direct Connect for Citizen Grievance & Ideas"),
        "submit_request" to Pair("अनुरोध / समस्या दर्ज करें", "Submit Request"),
        "submit_request_btn" to Pair("अनुरोध प्रेषित करें", "Submit Request"),
        "request_category" to Pair("विषय / समस्या की श्रेणी चुनें *", "Select Category *"),
        "request_desc" to Pair("समस्या अथवा सुझाव का विवरण *", "Description of Grievance / Suggestion *"),
        "attach_photo_doc" to Pair("फोटो या दस्तावेज जोड़ें", "Attach Photo or Document"),
        "track_requests" to Pair("मेरी पूर्व शिकायतें एवं स्थिति ट्रैकिंग", "My Requests & Tracking"),
        "select_subject" to Pair("विषय / श्रेणी चुनें *", "Select Category *"),
        "write_message" to Pair("अपनी बात / समस्या लिखें *", "Write your problem or suggestion *"),
        "attach_photo" to Pair("फोटो जोड़ें", "Add Photo"),
        "attach_video" to Pair("दस्तावेज जोड़ें", "Add Document"),
        "send_request" to Pair("अनुरोध भेजें", "Submit Request"),
        "my_requests" to Pair("मेरी शिकायतें / अनुरोध", "My Requests & Grievances"),
        "request_id" to Pair("अनुरोध संख्या", "Request ID"),
        "status_submitted" to Pair("सफलतापूर्वक दर्ज", "Submitted"),
        "status_in_review" to Pair("समीक्षाधीन", "In Review"),
        "status_action_taken" to Pair("कार्यवाही जारी", "Action Taken"),
        "status_resolved" to Pair("निराकरण पूर्ण", "Resolved"),

        // Notifications
        "notifications_title" to Pair("सूचनाएं व अलर्ट", "Notifications & Alerts"),
        "notifications" to Pair("सूचनाएं", "Notifications"),

        // Referral
        "referral_title" to Pair("टीम में जोड़ें", "Invite to Team"),
        "referral_sub" to Pair("अपने रेफरल कोड से नए कार्यकर्ताओं व समर्थकों को जोड़ें", "Share your invite link to grow our team"),
        "your_code" to Pair("आपका रेफरल कोड", "Your Referral Code"),
        "share_invite" to Pair("WhatsApp पर शेयर करें", "Share on WhatsApp"),
        "total_invited" to Pair("कुल आमंत्रित", "Total Invited"),
        "total_registered" to Pair("पंजीकृत सदस्य", "Registered"),
        "total_active" to Pair("सक्रिय साथी", "Active Members"),

        // My Activity
        "my_activity_title" to Pair("मेरी गतिविधियाँ", "My Activity Dashboard"),
        "act_graphics" to Pair("ग्राफिक्स बनाए व शेयर किए", "Graphics Created & Shared"),
        "act_videos" to Pair("वीडियो शेयर किए", "Videos Shared"),
        "act_suvichar" to Pair("सुविचार बनाए", "Suvichar Created"),
        "act_requests" to Pair("जनसमस्या अनुरोध भेजे", "Citizen Requests Filed"),
        "act_meetings" to Pair("मीटिंग में शामिल हुए", "Meetings Attended"),
        "act_tasks" to Pair("कार्य पूर्ण किए", "Tasks Completed"),

        // Profile & Settings
        "edit_profile" to Pair("प्रोफाइल संपादित करें", "Edit Profile"),
        "switch_role_preview" to Pair("भूमिका बदलें", "Switch Role (Preview)"),
        "admin_panel_btn" to Pair("एडमिन डैशबोर्ड", "Admin Dashboard"),
        "ux_states_btn" to Pair("सभी 10 UX States देखें", "View 10 UX Edge States"),
        "notifications" to Pair("सूचनाएं", "Notifications"),
        "settings" to Pair("सेटिंग्स", "Settings"),
        "privacy_security" to Pair("गोपनीयता एवं सुरक्षा", "Privacy & Security"),
        "help_support" to Pair("सहायता एवं संपर्क", "Help & Support"),
        "about_app" to Pair("ऐप के बारे में", "About App"),
        "logout" to Pair("लॉगआउट", "Logout"),
        "deactivate_acc" to Pair("अकाउंट अस्थायी निष्क्रिय करें", "Deactivate Account"),
        "delete_acc" to Pair("अकाउंट स्थायी डिलीट करें", "Delete Account")
    )
}
