package com.astrom.dinnerihno.setting;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;

    public boolean isUserRegistrationAllowed() {
        return settingRepository.findTopByOrderByIdAsc()
                .map(Setting::isAllowUserRegistration)
                .orElse(true); // default till false om ingen inställning finns
    }

    public void updateAllowUserRegistration(boolean allow) {
        Setting setting = settingRepository.findFirst().orElseGet(() -> new Setting());
        setting.setAllowUserRegistration(allow);
        settingRepository.save(setting);
    }

}

