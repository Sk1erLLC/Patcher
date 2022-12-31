package gg.essential.api;

import gg.essential.api.config.EssentialConfig;
import gg.essential.api.data.OnboardingData;
import gg.essential.api.utils.TrustedHostsUtil;

import java.util.Set;

public interface EssentialAPI {
    static EssentialConfig getConfig() {
        return new EssentialConfig() {
            private boolean essentialScreenshots = true;
            private boolean openToFriends = true;
            private boolean essentialFull = true;
            @Override
            public boolean getEssentialScreenshots() {
                return essentialScreenshots;
            }

            @Override
            public boolean getOpenToFriends() {
                return openToFriends;
            }

            @Override
            public boolean getEssentialFull() {
                return essentialFull;
            }
        };
    }

    static TrustedHostsUtil getTrustedHostsUtil() {
        return new TrustedHostsUtil() {
            private Set<TrustedHost> dummy;
            @Override
            public Set<TrustedHost> getTrustedHosts() {
                return dummy;
            }
        };
    }

    static OnboardingData getOnboardingData() {
        return new OnboardingData() {
            private boolean dummy;
            @Override
            public boolean hasAcceptedEssentialTOS() {
                return dummy;
            }
        };
    }
}
