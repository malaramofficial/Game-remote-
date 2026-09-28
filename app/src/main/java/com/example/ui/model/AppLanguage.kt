package com.example.ui.model

enum class AppLanguage {
    HINDI,
    ENGLISH
}

object AppStrings {
    fun get(key: String, language: AppLanguage): String {
        return when (language) {
            AppLanguage.HINDI -> hindiMap[key] ?: englishMap[key] ?: key
            AppLanguage.ENGLISH -> englishMap[key] ?: key
        }
    }

    private val englishMap = mapOf(
        "app_title" to "DriveRemote",
        "tab_car" to "Vehicle Key",
        "tab_controller" to "Cockpit Drive",
        "tab_media" to "Remote Deck",
        "tab_settings" to "Settings",
        
        "status_connected" to "CONNECTED",
        "status_disconnected" to "DISCONNECTED",
        "status_locked" to "LOCKED",
        "status_unlocked" to "UNLOCKED",
        "engine_running" to "ENGINE ON",
        "engine_stopped" to "ENGINE OFF",
        
        "btn_start" to "START",
        "btn_stop" to "STOP",
        "hold_to_start" to "HOLD TO START",
        "hold_to_stop" to "HOLD TO STOP",
        "lock" to "Lock",
        "unlock" to "Unlock",
        "trunk" to "Trunk",
        "frunk" to "Frunk",
        "horn" to "Horn",
        "hazard" to "Hazards",
        "windows" to "Windows",
        "sentry" to "Sentry",
        "summon_fwd" to "Summon FWD",
        "summon_rev" to "Summon REV",
        
        "battery" to "Battery",
        "range" to "Range",
        "odometer" to "Odometer",
        "tire_pressures" to "Tire Pressures (TPMS)",
        "front_left" to "FL",
        "front_right" to "FR",
        "rear_left" to "RL",
        "rear_right" to "RR",
        "optimal" to "Optimal",
        
        "climate_control" to "Climate Control",
        "interior_temp" to "Interior",
        "exterior_temp" to "Exterior",
        "ac" to "A/C",
        "defrost" to "Defrost",
        "seat_heater" to "Seat Heater",
        
        "steering" to "Steering",
        "throttle" to "Throttle",
        "brake" to "Brake",
        "speed" to "Speed",
        "rpm" to "RPM",
        "gear" to "Gear",
        "nitro" to "BOOST",
        "headlights" to "Lights",
        "cruise" to "Cruise",
        
        "presentation_deck" to "Presentation & PC Remote",
        "prev_slide" to "Prev Slide",
        "next_slide" to "Next Slide",
        "laser_pointer" to "Laser Pointer",
        "black_screen" to "Black Screen",
        
        "media_controls" to "Media Player Deck",
        "play_pause" to "Play/Pause",
        "previous" to "Previous",
        "next" to "Next",
        "volume" to "Volume",
        
        "cloud_drive" to "Cloud Drive Sync",
        "storage_used" to "Storage Used",
        "sync_now" to "Quick Sync",
        "last_synced" to "Last Synced",
        
        "vehicles_devices" to "Paired Vehicles & Devices",
        "add_device" to "Add New Device",
        "diagnostics" to "OBD-II Diagnostics",
        "run_diagnostics" to "Run System Scan",
        "activity_log" to "Command Activity Log",
        "clear_logs" to "Clear Logs",
        "app_settings" to "App Preferences",
        "language" to "Language (भाषा)",
        "haptics" to "Haptic Feedback",
        "units" to "Units",
        "creator" to "Malaram Official Edition"
    )

    private val hindiMap = mapOf(
        "app_title" to "ड्राइव रिमोट",
        "tab_car" to "वाहन की",
        "tab_controller" to "कॉकपिट ड्राइव",
        "tab_media" to "रिमोट डेक",
        "tab_settings" to "सेटिंग्स",
        
        "status_connected" to "कनेक्टेड",
        "status_disconnected" to "डिस्कनेक्टेड",
        "status_locked" to "लॉक",
        "status_unlocked" to "अनलॉक",
        "engine_running" to "इंजन चालू",
        "engine_stopped" to "इंजन बंद",
        
        "btn_start" to "स्टार्ट",
        "btn_stop" to "बंद करें",
        "hold_to_start" to "दबाकर स्टार्ट करें",
        "hold_to_stop" to "दबाकर बंद करें",
        "lock" to "लॉक",
        "unlock" to "अनलॉक",
        "trunk" to "ट्रंक",
        "frunk" to "फ्रंक",
        "horn" to "हॉर्न",
        "hazard" to "हैज़र्ड लाइट्स",
        "windows" to "खिड़कियां",
        "sentry" to "सेंट्री मोड",
        "summon_fwd" to "समन आगे",
        "summon_rev" to "समन पीछे",
        
        "battery" to "बैटरी",
        "range" to "रेंज",
        "odometer" to "ओडोमीटर",
        "tire_pressures" to "टायर प्रेशर (TPMS)",
        "front_left" to "आगे बायां",
        "front_right" to "आगे दायां",
        "rear_left" to "पीछे बायां",
        "rear_right" to "पीछे दायां",
        "optimal" to "सही",
        
        "climate_control" to "क्लाइमेट कंट्रोल",
        "interior_temp" to "अंदर का तापमान",
        "exterior_temp" to "बाहर का तापमान",
        "ac" to "ए/सी",
        "defrost" to "डिफ्रॉस्ट",
        "seat_heater" to "सीट हीटर",
        
        "steering" to "स्टीयरिंग",
        "throttle" to "एक्सेलरेटर",
        "brake" to "ब्रेक",
        "speed" to "स्पीड",
        "rpm" to "आरपीएम",
        "gear" to "गियर",
        "nitro" to "बूस्ट",
        "headlights" to "हेडलाइट्स",
        "cruise" to "क्रूज़",
        
        "presentation_deck" to "प्रेजेंटेशन और पीसी रिमोट",
        "prev_slide" to "पिछली स्लाइड",
        "next_slide" to "अगली स्लाइड",
        "laser_pointer" to "लेजर पॉइंटर",
        "black_screen" to "ब्लैक स्क्रीन",
        
        "media_controls" to "मीडिया प्लेयर डेक",
        "play_pause" to "चलाएं/रोकें",
        "previous" to "पिछला",
        "next" to "अगला",
        "volume" to "आवाज़",
        
        "cloud_drive" to "क्लाउड ड्राइव सिंक",
        "storage_used" to "उपयोग स्टोरेज",
        "sync_now" to "त्वरित सिंक",
        "last_synced" to "अंतिम सिंक",
        
        "vehicles_devices" to "कनेक्टेड वाहन और डिवाइस",
        "add_device" to "नया डिवाइस जोड़ें",
        "diagnostics" to "वाहन डायग्नोस्टिक्स (OBD)",
        "run_diagnostics" to "सिस्टम स्कैन करें",
        "activity_log" to "कमांड एक्टिविटी लॉग",
        "clear_logs" to "लॉग साफ़ करें",
        "app_settings" to "ऐप प्राथमिकताएं",
        "language" to "भाषा (Language)",
        "haptics" to "वाइब्रेशन फीडबैक",
        "units" to "माप इकाइयां",
        "creator" to "Malaram Official एडिशन"
    )
}
