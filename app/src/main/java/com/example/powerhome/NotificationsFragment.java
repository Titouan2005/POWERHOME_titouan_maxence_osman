package com.example.powerhome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * NotificationsFragment — Vue "Mes notifications".
 *
 * Affiche les alertes éco-citoyennes : créneaux saturés, bonus/malus
 * éco-coins obtenus, rappels d'engagement.
 *
 * Pour ce projet académique, les notifications sont affichées statiquement.
 * En production, elles viendraient d'un endpoint /api/notifications.php.
 */
public class NotificationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }
}
