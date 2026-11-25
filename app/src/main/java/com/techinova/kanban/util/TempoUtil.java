package com.techinova.kanban.util;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.techinova.kanban.R;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * C6: formata há quanto tempo algo aconteceu ("há 12 min", "há 1 h 5 min").
 * Os textos vêm de strings.xml.
 *
 * Observação: logo depois de criar um cartão, o abertoEm ainda pode estar null
 * (o horário do servidor chega em seguida). Nesse caso mostramos "enviando…".
 */
public final class TempoUtil {

    private TempoUtil() {
    }

    /** Minutos inteiros desde o horário informado (0 se for null ou no futuro). */
    public static long minutosDesde(@Nullable Timestamp inicio) {
        if (inicio == null) return 0;
        long diferencaMs = System.currentTimeMillis() - inicio.toDate().getTime();
        if (diferencaMs < 0) return 0; // relógio do aparelho um pouco atrasado em relação ao servidor
        return TimeUnit.MILLISECONDS.toMinutes(diferencaMs);
    }

    /** "agora", "há 12 min", "há 1 h", "há 1 h 5 min" ou "enviando…" (se null). */
    public static String formatarDecorrido(Context contexto, @Nullable Timestamp inicio) {
        if (inicio == null) {
            return contexto.getString(R.string.tempo_sem_horario);
        }
        long minutos = minutosDesde(inicio);
        if (minutos < 1) {
            return contexto.getString(R.string.tempo_agora);
        }
        if (minutos < 60) {
            return contexto.getString(R.string.tempo_minutos, (int) minutos);
        }
        int horas = (int) (minutos / 60);
        int resto = (int) (minutos % 60);
        if (resto == 0) {
            return contexto.getString(R.string.tempo_horas, horas);
        }
        return contexto.getString(R.string.tempo_horas_minutos, horas, resto);
    }

    /** Hora no formato 14:32 (útil para mostrar os horários de cada etapa no histórico). */
    public static String formatarHora(@Nullable Timestamp momento) {
        if (momento == null) return "--:--";
        return new SimpleDateFormat("HH:mm", Locale.forLanguageTag("pt-BR")).format(momento.toDate());
    }

    /** Data e hora no formato 23/11/2026 14:32. */
    public static String formatarDataHora(@Nullable Timestamp momento) {
        if (momento == null) return "--";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR")).format(momento.toDate());
    }
}
