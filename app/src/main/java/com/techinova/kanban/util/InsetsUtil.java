package com.techinova.kanban.util;

import android.graphics.Color;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Ajuste de barras do sistema (já pronto, não precisa mexer).
 *
 * A partir do Android 15 os apps desenham por baixo da status bar e da barra de navegação
 * (edge-to-edge). Sem este ajuste, a toolbar ficaria escondida embaixo do relógio.
 * Uso em toda Activity:
 *
 *   InsetsUtil.ativarEdgeToEdge(this);          // antes do setContentView
 *   setContentView(binding.getRoot());
 *   InsetsUtil.aplicar(binding.getRoot(), binding.appBar);
 */
public final class InsetsUtil {

    private InsetsUtil() {
    }

    /** Status bar transparente com ícones claros (ficam sobre a toolbar azul). */
    public static void ativarEdgeToEdge(ComponentActivity activity) {
        EdgeToEdge.enable(activity,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT));
    }

    /**
     * Empurra o conteúdo para não ficar embaixo das barras do sistema e do teclado.
     *
     * @param raiz   view raiz do layout
     * @param appBar AppBarLayout (recebe o espaço da status bar, assim o azul vai até o topo).
     *               Se for null, a raiz recebe o espaço do topo.
     */
    public static void aplicar(View raiz, @Nullable View appBar) {
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            int topo = appBar == null ? barras.top : 0;
            v.setPadding(barras.left, topo, barras.right, Math.max(barras.bottom, teclado.bottom));
            if (appBar != null) {
                appBar.setPadding(0, barras.top, 0, 0);
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
