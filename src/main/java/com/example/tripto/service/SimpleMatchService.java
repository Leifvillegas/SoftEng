package com.example.tripto.service;

import com.example.tripto.model.User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SimpleMatchService {

    private static final double W_PERSONALITY = 0.70;
    private static final double W_INTERESTS = 0.20;
    private static final double W_LANGUAGES = 0.10;

    public double compatibility(User a, User b) {

        double p = personalitySimilarity(a.getPersonalityType(), b.getPersonalityType());
        double i = interestsSimilarity(a.getInterests(), b.getInterests());
        double l = languagesSimilarity(a.getLanguages(), b.getLanguages());

        double score = W_PERSONALITY * p +  W_INTERESTS * i + W_LANGUAGES * l;
        return Math.max(0.0, Math.min(1.0, score));

    }

    public int compatibilityPercent(User a, User b) {

        return (int)Math.round(compatibility(a, b) * 100.0);

    }

    private double personalitySimilarity(String a, String b) {

        if (isBlank(a) || isBlank(b)) return 0.0;
        return a.trim().equalsIgnoreCase(b.trim()) ? 1.0 : 0.0;

    }

    private double interestsSimilarity(String a, String b) {

        Set<String> sa = toSet(a);
        Set<String> sb = toSet(b);
        if (sa.isEmpty() || sb.isEmpty()) return 0.0;

        int overlap = 0;
        for(String x : sa) if (sb.contains(x)) overlap++;

        int maxCount = Math.max(sa.size(), sb.size());
        return maxCount == 0 ? 0.0 : (overlap * 1.0) / maxCount;

    }

    private double languagesSimilarity(String a, String b) {

        Set<String> sa = toSet(a);
        Set<String> sb = toSet(b);
        for (String x : sa) if (sb.contains(x)) return 1.0;
        return 0.0;

    }

    private boolean isBlank(String s) {

        return s == null || s.trim().isEmpty();

    }

    private Set<String> toSet(String csv) {

        Set<String> out = new HashSet<>();
        if (isBlank(csv)) return out;
        String[] parts = csv.split("[,;]");
        for (String p : parts) {
            String s = p.trim().toLowerCase();
            if (!s.isEmpty()) out.add(s);

        }
        return out;
    }
}
