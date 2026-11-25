package com.techinova.kanban.util;

import android.content.Context;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.techinova.kanban.BuildConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração do Cloudinary (upload de fotos). O CORE não usa fotos, mas a dependência
 * e a configuração ficam prontas para uma evolução (ex.: foto do insumo ou do posto).
 *
 * As chaves NÃO ficam no código: são lidas do local.properties (fora do Git) e
 * expostas pelo BuildConfig. Exemplo de local.properties:
 *
 *   cloudinary.cloud_name=meu-cloud
 *   cloudinary.upload_preset=kanban_unsigned
 *
 * Use upload "unsigned" (com upload preset). NUNCA coloque o api_secret no app.
 */
public final class CloudinaryConfig {

    private static final String TAG = "CloudinaryConfig";
    private static boolean inicializado = false;

    private CloudinaryConfig() {
    }

    /** Está configurado se o cloud_name foi preenchido no local.properties. */
    public static boolean estaConfigurado() {
        return !BuildConfig.CLOUDINARY_CLOUD_NAME.isEmpty();
    }

    /** Nome do upload preset unsigned, para usar em MediaManager.get().upload(...).unsigned(preset). */
    public static String getUploadPreset() {
        return BuildConfig.CLOUDINARY_UPLOAD_PRESET;
    }

    /**
     * Inicializa o MediaManager uma única vez. Pode ser chamado na SplashActivity.
     * Se as chaves não estiverem configuradas, apenas registra um aviso no log.
     */
    public static synchronized void inicializar(Context contexto) {
        if (inicializado) return;
        if (!estaConfigurado()) {
            Log.w(TAG, "Cloudinary não configurado (cloudinary.cloud_name vazio no local.properties).");
            return;
        }
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", BuildConfig.CLOUDINARY_CLOUD_NAME);
        config.put("secure", true);
        MediaManager.init(contexto.getApplicationContext(), config);
        inicializado = true;
    }
}
