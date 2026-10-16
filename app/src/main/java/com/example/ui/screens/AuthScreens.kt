package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MadhyaPradeshData
import com.example.model.Language
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.CongressAtmosphereBackground
import com.example.ui.components.TeamRathoreEmblem
import com.example.ui.components.TeamRathoreLogoIcon
import com.example.ui.components.TricolorRibbon
import com.example.ui.theme.*
import com.example.util.AppStrings
import com.example.viewmodel.AppScreen
import com.example.viewmodel.AppViewModel

@Composable
fun SplashScreen(
    viewModel: AppViewModel,
    language: Language
) {
    CongressAtmosphereBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Official Team Rathore Congress Emblem
            TeamRathoreEmblem(size = 120.dp, showSubtext = true)

            Spacer(modifier = Modifier.height(28.dp))

            // Card for Language Selection
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, BorderStroke)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = AppStrings.get("choose_language", language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setLanguage(Language.HINDI) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("btn_lang_hindi"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (language == Language.HINDI) RathoreGreen else Color(0xFFF1F3F5),
                                contentColor = if (language == Language.HINDI) Color.White else TextPrimary
                            )
                        ) {
                            Text("हिंदी", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Button(
                            onClick = { viewModel.setLanguage(Language.ENGLISH) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("btn_lang_english"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (language == Language.ENGLISH) RathoreGreen else Color(0xFFF1F3F5),
                                contentColor = if (language == Language.ENGLISH) Color.White else TextPrimary
                            )
                        ) {
                            Text("English", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.navigateTo(AppScreen.Login) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_splash_continue"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
            ) {
                Text(
                    text = AppStrings.get("continue_btn", language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = AppStrings.get("app_slogan", language),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = TextTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    language: Language,
    mobileInput: String
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var mobile by remember { mutableStateOf(mobileInput.filter { it.isDigit() }.takeLast(10)) }
    var mobileError by remember { mutableStateOf<String?>(null) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var showAdminDialog by remember { mutableStateOf(false) }
    var adminId by remember { mutableStateOf("breakingnewswala.com@gmail.com") }
    var adminPassword by remember { mutableStateOf("RathoreAdmin@2026") }
    var adminError by remember { mutableStateOf(false) }

    CongressAtmosphereBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TricolorRibbon()
            Spacer(modifier = Modifier.height(16.dp))

            // Official Team Rathore Congress Emblem
            TeamRathoreLogoIcon(size = 90.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(28.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "टीम राठौर",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = RathoreGreenDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "भारतीय राष्ट्रीय कांग्रेस",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = RathoreOrangeDark
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "साथ • सेवा • समर्पण • संगठन",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    )
                }
                // Discreet small Admin Icon as requested by user
                IconButton(
                    onClick = { showAdminDialog = true },
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("btn_admin_login_icon")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Login",
                        tint = TextTertiary.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = AppStrings.get("welcome_back", language),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, BorderStroke)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("login_title", language),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = RathoreGreen
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("सुरक्षित OTP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                            }
                        }
                    }

                    Text(
                        text = AppStrings.get("login_sub", language),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 18.dp)
                    )

                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }.take(10)
                            mobile = digits
                            mobileError = null
                            viewModel.setMobileInput(digits)
                        },
                        label = { Text(AppStrings.get("mobile_number", language)) },
                        prefix = { Text("+91 ", fontWeight = FontWeight.Bold, color = RathoreGreen) },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = RathoreGreen)
                        },
                        isError = mobileError != null,
                        supportingText = {
                            if (mobileError != null) {
                                Text(mobileError ?: "", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                            } else {
                                Text("10 अंकों का सक्रिय मोबाइल नंबर दर्ज करें", color = TextSecondary, fontSize = 11.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_mobile"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val digits = mobile.filter { it.isDigit() }
                            if (digits.length != 10) {
                                mobileError = "कृपया 10 अंकों का मान्य मोबाइल नंबर दर्ज करें!"
                            } else {
                                mobileError = null
                                isSendingOtp = true
                                val success = viewModel.requestOtpForMobile(digits, activity)
                                isSendingOtp = false
                                if (success) {
                                    viewModel.navigateTo(AppScreen.OtpVerify)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_send_otp"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                    ) {
                        if (isSendingOtp) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("OTP भेजा जा रहा है...", color = Color.White, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = "सत्यापन OTP प्राप्त करें →",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { showAdminDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_admin_login_portal"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.2.dp, Color(0xFFDC2626))
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("व्यवस्थापक / Admin लॉगिन पोर्टल", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // Admin Login Dialog
    if (showAdminDialog) {
        AlertDialog(
            onDismissRequest = { showAdminDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("प्रशासक लॉगिन (Admin Login)", fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "केंद्रीय टीम राठौर Admin डैशबोर्ड में प्रवेश के लिए अधिकृत क्रेडेंशियल दर्ज करें:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.dp, Color(0xFFFECACA))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("अधिकृत व्यवस्थापक (Admin) क्रेडेंशियल:", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF991B1B))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• आईडी / ईमेल: breakingnewswala.com@gmail.com", fontSize = 11.sp, color = Color(0xFF7F1D1D), fontWeight = FontWeight.SemiBold)
                            Text("• पासवर्ड: RathoreAdmin@2026", fontSize = 11.sp, color = Color(0xFF7F1D1D), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        adminId = "breakingnewswala.com@gmail.com"
                                        adminPassword = "RathoreAdmin@2026"
                                        adminError = false
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("ऑटो-भरें (Fill Credentials)", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = adminId,
                        onValueChange = { adminId = it; adminError = false },
                        label = { Text("एडमिन आईडी / ईमेल") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_admin_id"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it; adminError = false },
                        label = { Text("पासवर्ड") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_admin_pwd"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (adminError) {
                        Text(
                            "❌ आईडी या पासवर्ड सही नहीं है! कृपया सही क्रेडेंशियल दर्ज करें।",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = viewModel.loginWithAdminCredentials(adminId, adminPassword)
                        if (success) {
                            showAdminDialog = false
                        } else {
                            adminError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.testTag("btn_confirm_admin_login")
                ) {
                    Text("एडमिन लॉगिन करें ✓", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminDialog = false }) {
                    Text("रद्द करें", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun OtpVerifyScreen(
    viewModel: AppViewModel,
    language: Language,
    mobile: String,
    otpInput: String
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var otp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }
    val displayMobile = uiState.mobileInput.ifBlank { mobile.filter { it.isDigit() }.takeLast(10) }

    CongressAtmosphereBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TricolorRibbon()
            Spacer(modifier = Modifier.height(16.dp))

            TeamRathoreLogoIcon(size = 72.dp)
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, BorderStroke)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("otp_title", language),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = RathoreGreen
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF0FDF4),
                            border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                        ) {
                            Text(
                                text = "सुरक्षित सत्यापन",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RathoreGreenDark
                            )
                        }
                    }

                    Text(
                        text = "मोबाइल नंबर +91 $displayMobile पर 6 अंकों का OTP सत्यापन कोड भेजा गया है।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    // Real-time SMS notification simulation banner
                    if (uiState.sentOtpCode.isNotBlank()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDCFCE7),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Sms, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("सुरक्षा सत्यापन SMS अलर्ट", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "आपके नंबर पर भेजा गया OTP: ${uiState.sentOtpCode}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF14532D)
                                )
                                Text(
                                    text = "लॉगिन करने के लिए यही 6 अंकों का कोड नीचे दर्ज करें।",
                                    fontSize = 11.sp,
                                    color = Color(0xFF166534)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    OutlinedTextField(
                        value = otp,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }.take(6)
                            otp = clean
                            otpError = null
                            viewModel.setOtpInput(clean)
                        },
                        label = { Text("6 अंकों का OTP दर्ज करें *") },
                        placeholder = { Text("उदा. 123456") },
                        isError = otpError != null,
                        supportingText = {
                            if (otpError != null) {
                                Text(otpError ?: "", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                            } else {
                                Text("SMS में प्राप्त 6 अंकों का कोड लिखें", color = TextSecondary, fontSize = 11.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_otp"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val cleanOtp = otp.trim()
                            if (cleanOtp.length != 6) {
                                otpError = "कृपया 6 अंकों का OTP कोड दर्ज करें!"
                            } else if (cleanOtp != uiState.sentOtpCode && uiState.sentOtpCode.isNotBlank()) {
                                otpError = "❌ गलत OTP दर्ज किया गया है! सही SMS कोड दर्ज करें।"
                            } else {
                                otpError = null
                                isVerifying = true
                                val verified = viewModel.verifyEnteredOtp(cleanOtp)
                                isVerifying = false
                                if (!verified) {
                                    otpError = "❌ अमान्य OTP! कृपया सही कोड दर्ज करें।"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_verify_otp"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                    ) {
                        if (isVerifying) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("सत्यापन जारी...", color = Color.White, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = "सत्यापित करें और आगे बढ़ें ✓",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { viewModel.navigateTo(AppScreen.Login) }
                        ) {
                            Text("← नंबर बदलें", color = TextSecondary, fontSize = 12.sp)
                        }

                        TextButton(
                            onClick = {
                                viewModel.resendOtp()
                                otpError = null
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "OTP पुनः भेजें",
                                color = RathoreOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSelectionScreen(
    viewModel: AppViewModel,
    language: Language,
    selectedRole: UserRole
) {
    var showInfoDialog by remember { mutableStateOf(false) }
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var adminId by remember { mutableStateOf("breakingnewswala.com@gmail.com") }
    var adminPassword by remember { mutableStateOf("RathoreAdmin@2026") }
    var adminError by remember { mutableStateOf<String?>(null) }

    val citizenRoles = remember {
        listOf(UserRole.SUPPORTER, UserRole.WORKER, UserRole.OFFICE_BEARER)
    }

    CongressAtmosphereBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
        TricolorRibbon()
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = AppStrings.get("role_question", language),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
            color = RathoreGreen
        )
        Text(
            text = if (language == Language.HINDI)
                "कृपया संगठन एवं जनसेवा में अपनी प्राथमिक भूमिका का चयन करें:"
            else
                "Please select your primary role in the organization:",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        // The 3 Citizen / Field Roles on Top
        citizenRoles.forEach { role ->
            val isSelected = selectedRole == role
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { viewModel.setUserRole(role) }
                    .testTag("role_item_${role.id}"),
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) Color(0xFFF0FDF4) else Color.White,
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) RathoreGreen else BorderStroke
                ),
                shadowElevation = if (isSelected) 3.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when (role) {
                                    UserRole.SUPPORTER -> RathoreOrange
                                    UserRole.WORKER -> RathoreGreen
                                    UserRole.OFFICE_BEARER -> Color(0xFF5E35B1)
                                    UserRole.ADMIN -> Color(0xFFC62828)
                                }
                            )
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == Language.HINDI) role.titleHi else role.titleEn,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = if (language == Language.HINDI) role.descHi else role.descEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { viewModel.setUserRole(role) },
                        colors = RadioButtonDefaults.colors(selectedColor = RathoreGreen)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Continue Button for Citizen Roles
        Button(
            onClick = { viewModel.navigateTo(AppScreen.RegistrationStep1) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_role_continue"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
        ) {
            Text(
                text = AppStrings.get("continue_btn", language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Info Card: "टीम राठौर ऐप क्या है? (उद्देश्य एवं विस्तृत जानकारी)"
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showInfoDialog = true }
                .testTag("box_team_rathore_info"),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF1F5F9),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(RathoreGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = RathoreGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == Language.HINDI)
                            "टीम राठौर ऐप क्या है? (उद्देश्य व जानकारी)"
                        else
                            "What is Team Rathore App? (Purpose & Info)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = if (language == Language.HINDI)
                            "टच करके जानें यह मंच आमजन व कार्यकर्ताओं के लिए कैसे सहायक है"
                        else
                            "Tap to understand benefits for citizens & workers",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = RathoreGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Admin Secure Login Option at Bottom (Separate flow, no district/assembly required)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showAdminLoginDialog = true }
                .testTag("btn_admin_login_box"),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFFFF1F2),
            border = BorderStroke(1.dp, Color(0xFFFECDD3))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE11D48).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Color(0xFFE11D48),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == Language.HINDI)
                            "प्रशासक / एडमिन सुरक्षित लॉगिन"
                        else
                            "Administrator / Admin Secure Login",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF9F1239)
                    )
                    Text(
                        text = if (language == Language.HINDI)
                            "केंद्रीकृत नियंत्रण एवं कंटेंट प्रबंधन (केवल अधिकृत एडमिन)"
                        else
                            "Central content & platform control (Authorized only)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFFBE123C)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFE11D48),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Team Rathore Info Popup Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = RathoreGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == Language.HINDI) "टीम राठौर डिजिटल मंच — परिचय" else "About Team Rathore App",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoSectionItem(
                        title = "1. टीम राठौर बेसिकली क्या है?",
                        content = "यह माननीय विधायक हेमंत राठौर जी के मार्गदर्शन में संचालित विधानसभा क्षेत्र निवाड़ी एवं मध्य प्रदेश का आधिकारिक संगठनात्मक व नागरिक संवाद डिजिटल मंच है।"
                    )
                    InfoSectionItem(
                        title = "2. किनके द्वारा बनाई गई है?",
                        content = "इस ऐप का निर्माण एवं तकनीकी संचालन 'टीम राठौर केंद्रीय आईटी एवं मीडिया समन्वय सेल' द्वारा क्षेत्र के विकास और पारदर्शी संपर्क हेतु किया गया है।"
                    )
                    InfoSectionItem(
                        title = "3. इसका मुख्य उद्देश्य क्या है?",
                        content = "• 'सीधी बात, सीधे समाधान': बिना किसी बिचौलिये के नागरिक अपनी समस्या व सुझाव सीधे विधायक कार्यालय तक पहुंचा सकें।\n• कार्यकर्ताओं को प्रतिदिन आधिकारिक प्रचार सामग्री (पोस्टर, सुविचार, वीडियो) उपलब्ध कराना।"
                    )
                    InfoSectionItem(
                        title = "4. आमजन एवं कार्यकर्ताओं के लिए उपयोगिता:",
                        content = "• 1-क्लिक में अपनी फोटो और नाम के साथ व्यक्तिगत सोशल मीडिया पोस्टर व वीडियो तैयार कर सकते हैं।\n• अपने गांव, वार्ड और विधानसभा के विकास कार्यों व आगामी कार्यक्रमों से निरंतर जुड़े रह सकते हैं।"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Text(if (language == Language.HINDI) "समझ गया (Got It)" else "Close", color = Color.White)
                }
            }
        )
    }

    // Admin Login Dialog (Direct ID & Password, NO State/District/Assembly required)
    if (showAdminLoginDialog) {
        AlertDialog(
            onDismissRequest = {
                showAdminLoginDialog = false
                adminError = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFFC62828))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == Language.HINDI) "एडमिन सुरक्षित प्रवेश" else "Admin Secure Login",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (language == Language.HINDI)
                            "कृपया अपना व्यवस्थापक आईडी और पासवर्ड दर्ज करें:"
                        else
                            "Enter your administrator credentials:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = adminId,
                        onValueChange = { adminId = it },
                        label = { Text("Admin ID / Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = RathoreGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it },
                        label = { Text("Password (पासवर्ड)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = RathoreOrange) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        placeholder = { Text("••••••••") }
                    )

                    adminError?.let { err ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(err, color = Color(0xFFC62828), style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = viewModel.loginWithAdminCredentials(adminId, adminPassword)
                        if (success) {
                            showAdminLoginDialog = false
                        } else {
                            adminError = "❌ अमान्य आईडी या पासवर्ड! सही व्यवस्थापक क्रेडेंशियल दर्ज करें।"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text(if (language == Language.HINDI) "लॉगिन करें" else "Login", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAdminLoginDialog = false
                    adminError = null
                }) {
                    Text(if (language == Language.HINDI) "रद्द करें" else "Cancel", color = TextSecondary)
                }
            }
        )
    }
}
}

@Composable
private fun InfoSectionItem(title: String, content: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = RathoreGreen
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp, lineHeight = 18.sp),
            color = Color(0xFF334155)
        )
    }
}

@Composable
fun RegistrationStep1Screen(
    viewModel: AppViewModel,
    language: Language,
    profile: UserProfile
) {
    var name by remember { mutableStateOf(profile.fullName) }
    var father by remember { mutableStateOf(profile.fatherName) }
    var mother by remember { mutableStateOf(profile.motherName) }
    var dob by remember { mutableStateOf(profile.dob) }
    var gender by remember { mutableStateOf(profile.gender) }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.addProfilePhoto(it.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TricolorRibbon()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = AppStrings.get("reg_step1_title", language),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = RathoreGreen
        )

        // Stepper indicator (1 active, 2, 3)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            StepCircle(number = "1", isActive = true)
            StepDivider()
            StepCircle(number = "2", isActive = false)
            StepDivider()
            StepCircle(number = "3", isActive = false)
        }

        // Photo upload card with active photo picker & preview
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderStroke)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "प्रोफ़ाइल फोटो अपलोड करें (अनिवार्य)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "पोस्टर, वीडियो जैकेट एवं कार्यकर्ता पहचान-पत्र के लिए स्पष्ट फोटो अपलोड करें",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Uploaded user photos
                    profile.userPhotos.forEachIndexed { idx, photoUri ->
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9))
                                .border(2.dp, RathoreGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Uploaded Photo",
                                tint = RathoreGreen,
                                modifier = Modifier.size(44.dp)
                            )
                            // Remove photo badge if more than 1
                            if (profile.userPhotos.size > 1) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                        .clickable { viewModel.removeProfilePhoto(idx) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Add Photo button via gallery picker
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3E0))
                            .border(1.5.dp, RathoreOrange, CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("btn_upload_photo"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = "Upload Photo",
                            tint = RathoreOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick photo adder button (convenient for emulator or device testing)
                TextButton(
                    onClick = {
                        viewModel.addProfilePhoto("sample_photo_${profile.userPhotos.size + 1}")
                    },
                    modifier = Modifier.testTag("btn_add_sample_photo")
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("गैलरी / नमूना फोटो जोड़ें (${profile.userPhotos.size} अपलोड)", color = RathoreGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var step1Error by remember { mutableStateOf<String?>(null) }
        var nameError by remember { mutableStateOf(false) }
        var fatherError by remember { mutableStateOf(false) }
        var dobError by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = false
                step1Error = null
            },
            label = { Text("${AppStrings.get("full_name", language)} *") },
            isError = nameError,
            supportingText = if (nameError) {
                { Text("पूरा नाम लिखना अनिवार्य है", color = MaterialTheme.colorScheme.error) }
            } else null,
            modifier = Modifier.fillMaxWidth().testTag("input_reg_name"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = father,
            onValueChange = {
                father = it
                fatherError = false
                step1Error = null
            },
            label = { Text("${AppStrings.get("father_name", language)} *") },
            isError = fatherError,
            supportingText = if (fatherError) {
                { Text("पिता का नाम लिखना अनिवार्य है", color = MaterialTheme.colorScheme.error) }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = mother,
            onValueChange = { mother = it },
            label = { Text(AppStrings.get("mother_name", language)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Gender selector
        Text(
            text = "${AppStrings.get("gender", language)} *",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("पुरुष", "महिला", "अन्य").forEach { g ->
                val selected = gender == g
                FilterChip(
                    selected = selected,
                    onClick = { gender = g },
                    label = { Text(g) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RathoreGreenLight,
                        selectedLabelColor = RathoreGreen
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = dob,
            onValueChange = {
                dob = it
                dobError = false
                step1Error = null
            },
            label = { Text("${AppStrings.get("dob", language)} *") },
            placeholder = { Text("DD/MM/YYYY (उदा. 15/08/1990)") },
            isError = dobError,
            supportingText = if (dobError) {
                { Text("जन्मतिथि लिखना अनिवार्य है", color = MaterialTheme.colorScheme.error) }
            } else null,
            trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        step1Error?.let { err ->
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFEE2E2),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(err, color = Color(0xFFB91C1C), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (name.trim().isBlank()) {
                    nameError = true
                    step1Error = "कृपया आगे बढ़ने के लिए अपना पूरा नाम लिखें!"
                } else if (father.trim().isBlank()) {
                    fatherError = true
                    step1Error = "कृपया आगे बढ़ने के लिए पिता का नाम लिखें!"
                } else if (dob.trim().isBlank()) {
                    dobError = true
                    step1Error = "कृपया आगे बढ़ने के लिए जन्मतिथि दर्ज करें!"
                } else {
                    step1Error = null
                    viewModel.updateProfile(profile.copy(fullName = name.trim(), fatherName = father.trim(), motherName = mother.trim(), dob = dob.trim(), gender = gender))
                    viewModel.navigateTo(AppScreen.RegistrationStep2)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_step1_next"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
        ) {
            Text(AppStrings.get("continue_btn", language), fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun RegistrationStep2Screen(
    viewModel: AppViewModel,
    language: Language,
    profile: UserProfile
) {
    var district by remember { mutableStateOf(profile.district.ifBlank { "निवाड़ी" }) }
    var assembly by remember { mutableStateOf(profile.assembly.ifBlank { "निवाड़ी" }) }
    var village by remember { mutableStateOf(profile.village) }

    var showDistrictDialog by remember { mutableStateOf(false) }
    var showAssemblyDialog by remember { mutableStateOf(false) }
    var districtSearchQuery by remember { mutableStateOf("") }
    var villageError by remember { mutableStateOf(false) }
    var step2Error by remember { mutableStateOf<String?>(null) }

    val filteredDistricts = remember(districtSearchQuery) {
        if (districtSearchQuery.isBlank()) MadhyaPradeshData.districts
        else MadhyaPradeshData.districts.filter { it.contains(districtSearchQuery, ignoreCase = true) }
    }

    val availableAssemblies = remember(district) {
        MadhyaPradeshData.getAssembliesForDistrict(district)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TricolorRibbon()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = AppStrings.get("reg_step2_title", language),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = RathoreGreen
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            StepCircle(number = "1", isActive = true)
            StepDivider()
            StepCircle(number = "2", isActive = true)
            StepDivider()
            StepCircle(number = "3", isActive = false)
        }

        // Fixed State
        OutlinedTextField(
            value = "मध्य प्रदेश (स्वतः चयनित / Fixed)",
            onValueChange = {},
            enabled = false,
            label = { Text("राज्य *") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = RathoreGreen) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 55 Districts Selector with Checkmark
        Text(
            text = "जिला चुनें (मध्य प्रदेश के 55 जिले) *",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDistrictDialog = true }
                .testTag("select_district_trigger"),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.5.dp, RathoreGreen),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (district.isNotBlank()) district else "जिला चुनें",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "मध्य प्रदेश के 55 जिलों में से एक",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = RathoreGreen)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vidhan Sabha Selector based on chosen District
        Text(
            text = "विधानसभा क्षेत्र चुनें (जिले के अनुसार) *",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showAssemblyDialog = true }
                .testTag("select_assembly_trigger"),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.5.dp, RathoreGreen),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HowToVote, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (assembly.isNotBlank()) assembly else "विधानसभा चुनें",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "$district जिले की विधानसभा",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = RathoreGreen)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Village / Ward / Mohalla Input with Explicit Notice & Placeholder as requested
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF1F8E9),
            border = BorderStroke(1.dp, Color(0xFFC8E6C9))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "सूचना: कृपया अपने गाँव, वार्ड या मोहल्ले का नाम लिखें",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = RathoreGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = village,
            onValueChange = {
                village = it
                villageError = false
                step2Error = null
            },
            label = { Text("ग्राम / वार्ड / मोहल्ला *") },
            placeholder = { Text("यहाँ अपने गाँव, वार्ड या मोहल्ले का नाम लिखें") },
            isError = villageError,
            supportingText = if (villageError) {
                { Text("गाँव / वार्ड / मोहल्ला का नाम लिखना अनिवार्य है", color = MaterialTheme.colorScheme.error) }
            } else null,
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = RathoreGreen) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_village_ward"),
            shape = RoundedCornerShape(10.dp)
        )

        step2Error?.let { err ->
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFEE2E2),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(err, color = Color(0xFFB91C1C), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy note (Highlight from prompt: "Graphic में location कभी नहीं दिखाई जाएगी")
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFFF8E1),
            border = BorderStroke(1.dp, Color(0xFFFFD54F))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.get("location_privacy_note", language),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color(0xFF5D4037)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.navigateTo(AppScreen.RegistrationStep1) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(AppStrings.get("prev_btn", language), color = TextPrimary)
            }

            Button(
                onClick = {
                    if (district.isBlank()) {
                        step2Error = "कृपया अपना जिला चुनें!"
                    } else if (assembly.isBlank()) {
                        step2Error = "कृपया अपनी विधानसभा चुनें!"
                    } else if (village.trim().isBlank()) {
                        villageError = true
                        step2Error = "कृपया आगे बढ़ने के लिए अपने गाँव / वार्ड / मोहल्ले का नाम लिखें!"
                    } else {
                        step2Error = null
                        viewModel.updateProfile(profile.copy(district = district, assembly = assembly, village = village.trim()))
                        viewModel.navigateTo(AppScreen.RegistrationStep3)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("btn_step2_next"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
            ) {
                Text(AppStrings.get("continue_btn", language), fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }

    // 55 Districts Selection Dialog
    if (showDistrictDialog) {
        AlertDialog(
            onDismissRequest = { showDistrictDialog = false },
            title = {
                Text(
                    "मध्य प्रदेश के 55 जिले (चुनें)",
                    fontWeight = FontWeight.Bold,
                    color = RathoreGreen
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = districtSearchQuery,
                        onValueChange = { districtSearchQuery = it },
                        placeholder = { Text("जिला खोजें...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        filteredDistricts.forEach { distItem ->
                            val isSelected = district == distItem
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        district = distItem
                                        val assemblies = MadhyaPradeshData.getAssembliesForDistrict(distItem)
                                        assembly = assemblies.firstOrNull() ?: ""
                                        showDistrictDialog = false
                                        districtSearchQuery = ""
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFFF0FDF4) else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = distItem,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) RathoreGreen else TextPrimary
                                        )
                                    )
                                    // Tick selection option
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = if (isSelected) "चयनित (Ticked)" else "चुनें",
                                        tint = if (isSelected) RathoreGreen else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = BorderStroke)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDistrictDialog = false }) {
                    Text("बंद करें", color = RathoreGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Vidhan Sabha Selection Dialog
    if (showAssemblyDialog) {
        AlertDialog(
            onDismissRequest = { showAssemblyDialog = false },
            title = {
                Column {
                    Text(
                        "$district जिले की विधानसभाएं",
                        fontWeight = FontWeight.Bold,
                        color = RathoreGreen
                    )
                    Text(
                        "अपनी विधानसभा पर टिक लगाएं",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    availableAssemblies.forEach { assemItem ->
                        val isSelected = assembly == assemItem
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable {
                                    assembly = assemItem
                                    showAssemblyDialog = false
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFF0FDF4) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = assemItem,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) RathoreGreen else TextPrimary
                                    )
                                )
                                // Tick selection option
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = if (isSelected) "चयनित (Ticked)" else "चुनें",
                                    tint = if (isSelected) RathoreGreen else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = BorderStroke)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAssemblyDialog = false }) {
                    Text("बंद करें", color = RathoreGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun RegistrationStep3Screen(
    viewModel: AppViewModel,
    language: Language,
    profile: UserProfile
) {
    var mobile by remember { mutableStateOf(profile.mobile.filter { it.isDigit() }.takeLast(10)) }
    var whatsapp by remember { mutableStateOf(profile.whatsapp.filter { it.isDigit() }.takeLast(10).ifBlank { profile.mobile.filter { it.isDigit() }.takeLast(10) }) }
    var email by remember { mutableStateOf(profile.email) }
    var fb by remember { mutableStateOf(profile.hasFacebook) }
    var insta by remember { mutableStateOf(profile.hasInstagram) }
    var yt by remember { mutableStateOf(profile.hasYoutube) }
    var x by remember { mutableStateOf(profile.hasX) }
    var agreed by remember { mutableStateOf(true) }

    var mobileError by remember { mutableStateOf(false) }
    var whatsappError by remember { mutableStateOf(false) }
    var step3Error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TricolorRibbon()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = AppStrings.get("reg_step3_title", language),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = RathoreGreen
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            StepCircle(number = "1", isActive = true)
            StepDivider()
            StepCircle(number = "2", isActive = true)
            StepDivider()
            StepCircle(number = "3", isActive = true)
        }

        OutlinedTextField(
            value = mobile,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }.take(10)
                mobile = digits
                mobileError = false
                step3Error = null
            },
            label = { Text("${AppStrings.get("mobile_number", language)} *") },
            prefix = { Text("+91 ", fontWeight = FontWeight.Bold, color = RathoreGreen) },
            isError = mobileError,
            supportingText = if (mobileError) {
                { Text("10 अंकों का वैध मोबाइल नंबर आवश्यक है", color = MaterialTheme.colorScheme.error) }
            } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = whatsapp,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }.take(10)
                whatsapp = digits
                whatsappError = false
                step3Error = null
            },
            label = { Text("${AppStrings.get("whatsapp_mandatory", language)} *") },
            prefix = { Text("+91 ", fontWeight = FontWeight.Bold, color = RathoreGreen) },
            isError = whatsappError,
            supportingText = if (whatsappError) {
                { Text("10 अंकों का व्हाट्सऐप नंबर आवश्यक है", color = MaterialTheme.colorScheme.error) }
            } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(AppStrings.get("email_optional", language)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = AppStrings.get("social_handles", language),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SocialCheckbox("Facebook", fb) { fb = it }
            SocialCheckbox("Instagram", insta) { insta = it }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SocialCheckbox("YouTube", yt) { yt = it }
            SocialCheckbox("X (Twitter)", x) { x = it }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = agreed, onCheckedChange = { agreed = it; step3Error = null })
            Text(
                text = AppStrings.get("declaration", language),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        step3Error?.let { err ->
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFEE2E2),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(err, color = Color(0xFFB91C1C), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val cleanMobile = mobile.filter { it.isDigit() }
                val cleanWhatsapp = whatsapp.filter { it.isDigit() }
                if (cleanMobile.length != 10) {
                    mobileError = true
                    step3Error = "कृपया 10 अंकों का मान्य मोबाइल नंबर दर्ज करें!"
                } else if (cleanWhatsapp.length != 10) {
                    whatsappError = true
                    step3Error = "कृपया 10 अंकों का मान्य व्हाट्सऐप नंबर दर्ज करें!"
                } else if (!agreed) {
                    step3Error = "कृपया जारी रखने के लिए घोषणा (घोषणा पत्र) स्वीकार करें!"
                } else {
                    step3Error = null
                    viewModel.updateProfile(
                        profile.copy(
                            mobile = cleanMobile,
                            whatsapp = cleanWhatsapp,
                            email = email.trim(),
                            hasFacebook = fb,
                            hasInstagram = insta,
                            hasYoutube = yt,
                            hasX = x
                        )
                    )
                    viewModel.navigateTo(AppScreen.RegistrationSuccess)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_create_account"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
        ) {
            Text(
                text = AppStrings.get("create_account", language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
    }
}

@Composable
fun RegistrationSuccessScreen(
    viewModel: AppViewModel,
    language: Language,
    profile: UserProfile
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = RathoreGreen,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Registration सफल",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                color = RathoreGreen
            )
            Text(
                text = "आपका profile तैयार है (Profile 90% Complete)",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Admin Approval status badge (as in PDF mockups)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE8F5E9),
                border = BorderStroke(1.dp, RathoreGreen)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = RathoreGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Admin Approval: स्वीकृत (Approved)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = RathoreGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = { viewModel.navigateTo(AppScreen.Main) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_go_home"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
            ) {
                Text(
                    text = if (language == Language.HINDI) "होम पेज पर जाएं →" else "Go to Home Feed →",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun StepCircle(number: String, isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (isActive) RathoreGreen else Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            color = if (isActive) Color.White else TextSecondary,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun StepDivider() {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(2.dp)
            .background(Color(0xFFBDBDBD))
            .padding(horizontal = 4.dp)
    )
}

@Composable
private fun SocialCheckbox(name: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onChecked(!checked) }
            .padding(vertical = 6.dp)
    ) {
        Checkbox(checked = checked, onCheckedChange = onChecked)
        Text(name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}
