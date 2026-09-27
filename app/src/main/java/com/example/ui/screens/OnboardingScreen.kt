package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.GenderOption
import com.example.ui.theme.AmberCrown
import com.example.ui.theme.CrimsonPanic
import com.example.ui.theme.DeepSlateSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElevatedCardSurface
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.GlassBorderColor
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.TextSecondaryMuted

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onCompleteOnboarding: (
        displayName: String,
        birthYear: Int,
        myGender: GenderOption,
        preferredGender: GenderOption,
        interestsCsv: String,
        isIdVerified: Boolean
    ) -> Unit
) {
    var displayName by remember { mutableStateOf("NovaRider") }
    var selectedBirthYear by remember { mutableIntStateOf(2001) }
    var idVerificationCompleted by remember { mutableStateOf(true) }
    var myGender by remember { mutableStateOf(GenderOption.MALE) }
    var preferredMatchGender by remember { mutableStateOf(GenderOption.ANY) }
    var acceptedTermsAnd18Plus by remember { mutableStateOf(true) }
    var permissionsRequested by remember { mutableStateOf(false) }

    val availableInterests = listOf(
        "Music", "Late Night Talks", "Gaming", "Cyberpunk",
        "Travel", "Cinema", "Photography", "Languages"
    )
    val selectedInterests = remember {
        mutableStateListOf("Music", "Late Night Talks", "Gaming", "Cyberpunk")
    }

    val currentYear = 2026
    val calculatedAge = currentYear - selectedBirthYear
    val isAdult18Plus = calculatedAge >= 18

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionsRequested = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianNight),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Hero Header Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_globe),
                        contentDescription = "Roll Call Global Matchmaking Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        ObsidianNight.copy(alpha = 0.25f),
                                        ObsidianNight.copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Surface(
                            color = NeonCoral.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, NeonCoral)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Strict 18+ & Ephemeral Protection",
                                    tint = NeonCoral,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "18+ ONLY • 1:1 EPHEMERAL • ANTI-RECORDING",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Welcome to Roll Call",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Global 1-on-1 video & voice chat matched by gender preference with 60s self-destructing media.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }

            // Step 1: 18+ Age Gate & Biometric ID Verification
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElevatedCardSurface),
                border = BorderStroke(
                    1.dp,
                    if (isAdult18Plus) GlassBorderColor else CrimsonPanic
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Age Gate Verification",
                            tint = if (isAdult18Plus) EmeraldShield else CrimsonPanic
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "1. Mandatory 18+ Age & ID Gate",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = "Minors (<18) are strictly prohibited under Roll Call & Play Store UGC policy.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }
                    }

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Callsign / Display Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = GlassBorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_name_input")
                    )

                    Text(
                        text = "Select Birth Year (Calculated Age: $calculatedAge yrs)",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )

                    val birthYearPresets = listOf(1995, 1998, 2001, 2004, 2007, 2010)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        birthYearPresets.forEach { year ->
                            val selected = selectedBirthYear == year
                            FilterChip(
                                selected = selected,
                                onClick = { selectedBirthYear = year },
                                label = {
                                    Text(
                                        text = "$year (${currentYear - year}y)",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (currentYear - year >= 18) {
                                        ElectricCyan.copy(alpha = 0.22f)
                                    } else {
                                        CrimsonPanic.copy(alpha = 0.28f)
                                    },
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("birth_year_$year")
                            )
                        }
                    }

                    if (!isAdult18Plus) {
                        Surface(
                            color = CrimsonPanic.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CrimsonPanic)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Under 18 Blocked",
                                    tint = CrimsonPanic
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ACCESS BLOCKED: You must be 18 or older to use Roll Call.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Biometric ID Verification Badge toggle
                    Surface(
                        color = DeepSlateSurface,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.dp,
                            if (idVerificationCompleted) EmeraldShield else GlassBorderColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { idVerificationCompleted = !idVerificationCompleted }
                            .testTag("biometric_id_verify_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = "ID Verification",
                                    tint = if (idVerificationCompleted) EmeraldShield else TextSecondaryMuted
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "3rd-Party Age & ID Verification Shield",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (idVerificationCompleted) {
                                            "Verified 18+ Badge Active (Yoti/Veriff Token Validated)"
                                        } else {
                                            "Tap to run instant 18+ ID & Liveness check"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (idVerificationCompleted) EmeraldShield else TextSecondaryMuted
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified Status",
                                tint = if (idVerificationCompleted) EmeraldShield else TextSecondaryMuted
                            )
                        }
                    }
                }
            }

            // Step 2: Gender & Preferred Match Gender Selection
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElevatedCardSurface),
                border = BorderStroke(1.dp, GlassBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "2. Gender & Matchmaking Filter",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )

                    Text(
                        text = "Your Gender",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondaryMuted
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(GenderOption.MALE, GenderOption.FEMALE).forEach { option ->
                            val selected = myGender == option
                            FilterChip(
                                selected = selected,
                                onClick = { myGender = option },
                                label = { Text(option.label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan.copy(alpha = 0.25f),
                                    selectedLabelColor = ElectricCyan
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("my_gender_${option.name.lowercase()}")
                            )
                        }
                    }

                    Text(
                        text = "Preferred Match Gender (Queue Filter)",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondaryMuted
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        GenderOption.entries.forEach { option ->
                            val selected = preferredMatchGender == option
                            FilterChip(
                                selected = selected,
                                onClick = { preferredMatchGender = option },
                                label = { Text(option.label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonCoral.copy(alpha = 0.25f),
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("pref_gender_${option.name.lowercase()}")
                            )
                        }
                    }

                    Text(
                        text = "Match Interest Tags",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondaryMuted
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableInterests.forEach { tag ->
                            val isSelected = selectedInterests.contains(tag)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected && selectedInterests.size > 1) {
                                        selectedInterests.remove(tag)
                                    } else if (!isSelected) {
                                        selectedInterests.add(tag)
                                    }
                                },
                                label = { Text(tag) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AmberCrown.copy(alpha = 0.2f),
                                    selectedLabelColor = AmberCrown
                                )
                            )
                        }
                    }
                }
            }

            // Step 3: Camera & Mic Hardware Permissions + Strict Platform Rules
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElevatedCardSurface),
                border = BorderStroke(1.dp, GlassBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "3. Camera, Mic & Anti-Recording Rules",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = "• FLAG_SECURE blocks screenshots & screen recording during all calls.\n" +
                            "• Voice recording outside WebRTC is blocked during active calls.\n" +
                            "• All chat media expires 60s after viewing (no saving to gallery, no forwarding).\n" +
                            "• Strictly 1-on-1 conversations — no group chats.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryMuted
                    )

                    OutlinedButton(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.RECORD_AUDIO
                                )
                            )
                        },
                        border = BorderStroke(1.dp, ElectricCyan),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("grant_permissions_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera Permission",
                            tint = ElectricCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mic Permission",
                            tint = ElectricCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (permissionsRequested) {
                                "Camera & Mic Configured"
                            } else {
                                "Grant Camera & Mic Permissions"
                            },
                            color = ElectricCyan
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { acceptedTermsAnd18Plus = !acceptedTermsAnd18Plus }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = acceptedTermsAnd18Plus,
                            onCheckedChange = { acceptedTermsAnd18Plus = it },
                            colors = CheckboxDefaults.colors(checkedColor = ElectricCyan),
                            modifier = Modifier.testTag("accept_tos_checkbox")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I confirm I am 18+ years old and agree to the Roll Call Terms of Service, Privacy Policy, and Zero-Tolerance CSAM/Abuse Moderation Policy.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (isAdult18Plus && acceptedTermsAnd18Plus) {
                        onCompleteOnboarding(
                            displayName,
                            selectedBirthYear,
                            myGender,
                            preferredMatchGender,
                            selectedInterests.joinToString(","),
                            idVerificationCompleted
                        )
                    }
                },
                enabled = isAdult18Plus && acceptedTermsAnd18Plus,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricCyan,
                    contentColor = ObsidianNight,
                    disabledContainerColor = GlassBorderColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("complete_onboarding_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Enter Roll Call"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isAdult18Plus) {
                        "Enter Roll Call Radar (18+ Verified)"
                    } else {
                        "Must Be 18+ to Continue"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
