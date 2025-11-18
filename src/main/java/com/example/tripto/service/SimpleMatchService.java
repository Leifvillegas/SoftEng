package com.example.tripto.service;

import com.example.tripto.model.User;
import org.springframework.stereotype.Service;

@Service
public class SimpleMatchService {

    public int compatibilityPercent(User a, User b) {

        if (a == null || b == null)
            return 0;

        int score = 0;

        if (isSame(a.getPersonalityType(), b.getPersonalityType())) {

            score += 50;

        }

        if (isSame(a.getTravelStyle(), b.getTravelStyle())) {

            score += 20;

        }

        if (listSharesAtLeastOnce(a.getInterests(), b.getInterests())) {

            score += 20;

        }

        if (listSharesAtLeastOnce(a.getLanguages(), b.getLanguages())) {

            score += 10;

        }

        return Math.min(score, 100);

    }

    private boolean isSame(String s1, String s2) {

        if (s1 == null || s2 == null) return false;
        return s1.trim().equalsIgnoreCase(s2.trim());

    }

    private boolean listSharesAtLeastOnce(String list1, String list2) {

        if (list1 == null || list2 == null) return false;

        String l1 = list1.toLowerCase();
        String l2 = list2.toLowerCase();

        String[] items = l1.split(",");

        for (String i :  items) {

            String item = i.trim();
            if (!item.isEmpty() && l2.contains(item)) {

                return true;

            }

        }

        return false;

    }

}