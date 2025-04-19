package com.example.prosecommandementv2;

import android.widget.TextView;

/**
 * Classe utilitaire pour les opérations sur TextView
 */
public class TextViewUtils {

    /**
     * Définir le style de texte d'un TextView
     * @param textView Le TextView à modifier
     * @param style Le style à appliquer (ex: Typeface.BOLD)
     */
    public static void setTextStyle(TextView textView, int style) {
        textView.setTypeface(textView.getTypeface(), style);
    }
}