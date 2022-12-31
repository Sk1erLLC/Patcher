package gg.essential.api.utils;

import java.util.Set;

public interface TrustedHostsUtil {
    Set<TrustedHost> getTrustedHosts();

    final class TrustedHost {
        private Set<String> domains;
        public Set<String> getDomains() {
            return domains;
        }
    }
}
