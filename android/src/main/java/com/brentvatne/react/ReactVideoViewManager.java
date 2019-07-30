package com.brentvatne.react;

import com.brentvatne.react.ReactVideoView.Events;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.yqritc.scalablevideoview.ScalableType;

import javax.annotation.Nullable;
import java.util.Map;

public class ReactVideoViewManager extends SimpleViewManager<ReactVideoView> {

    public static final String REACT_CLASS = "RCTVideo";

    public static final String PROP_SRC = "src";
    public static final String PROP_SRC_URI = "uri";
    public static final String PROP_SRC_TYPE = "type";
    public static final String PROP_SRC_HEADERS = "requestHeaders";
    public static final String PROP_SRC_IS_NETWORK = "isNetwork";
    public static final String PROP_SRC_MAINVER = "mainVer";
    public static final String PROP_SRC_PATCHVER = "patchVer";
    public static final String PROP_SRC_IS_ASSET = "isAsset";
    public static final String PROP_DRM = "drm";
    public static final String PROP_DRM_TYPE = "type";
    public static final String PROP_DRM_LICENSESERVER = "licenseServer";
    public static final String PROP_DRM_HEADERS = "headers";
    public static final String PROP_RESIZE_MODE = "resizeMode";
    public static final String PROP_REPEAT = "repeat";
    public static final String PROP_PAUSED = "paused";
    public static final String PROP_MUTED = "muted";
    public static final String PROP_VOLUME = "volume";
    public static final String PROP_STEREO_PAN = "stereoPan";
    public static final String PROP_PROGRESS_UPDATE_INTERVAL = "progressUpdateInterval";
    public static final String PROP_SEEK = "seek";
    public static final String PROP_RATE = "rate";
    public static final String PROP_FULLSCREEN = "fullscreen";
    public static final String PROP_PLAY_IN_BACKGROUND = "playInBackground";
    public static final String PROP_CONTROLS = "controls";

    public static final int COMMAND_SAVE = 1;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ReactVideoView createViewInstance(ThemedReactContext themedReactContext) {
        return new ReactVideoView(themedReactContext);
    }

    @Override
    public Map<String,Integer> getCommandsMap() {
        Log.d("React"," View manager getCommandsMap:");
        return MapBuilder.of("save", COMMAND_SAVE);
    }

    @Override
    public void receiveCommand(ReactVideoView view, int commandType, @Nullable ReadableArray args) {
        Assertions.assertNotNull(view);
        Assertions.assertNotNull(args);
        switch (commandType) {
            case COMMAND_SAVE: {
                view.save(args);
                return;
            }
            default:
                throw new IllegalArgumentException(String.format("Unsupported command %d received by %s.", commandType, getClass().getSimpleName()));
        }
    }

    @Override
    public void onDropViewInstance(ReactVideoView view) {
        super.onDropViewInstance(view);
        view.cleanupMediaPlayerResources();
    }

    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder builder = MapBuilder.builder();
        for (Events event : Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }

    @Override
    @Nullable
    public Map getExportedViewConstants() {
        return MapBuilder.of(
                "ScaleNone", Integer.toString(ScalableType.LEFT_TOP.ordinal()),
                "ScaleToFill", Integer.toString(ScalableType.FIT_XY.ordinal()),
                "ScaleAspectFit", Integer.toString(ScalableType.FIT_CENTER.ordinal()),
                "ScaleAspectFill", Integer.toString(ScalableType.CENTER_CROP.ordinal())
        );
    }

    @ReactProp(name = PROP_DRM)
    public void setDRM(final ReactExoplayerView videoView, @Nullable ReadableMap drm) {
        if (drm != null && drm.hasKey(PROP_DRM_TYPE)) {
            String drmType = drm.hasKey(PROP_DRM_TYPE) ? drm.getString(PROP_DRM_TYPE) : null;
            String drmLicenseServer = drm.hasKey(PROP_DRM_LICENSESERVER) ? drm.getString(PROP_DRM_LICENSESERVER) : null;
            ReadableMap drmHeaders = drm.hasKey(PROP_DRM_HEADERS) ? drm.getMap(PROP_DRM_HEADERS) : null;
            if (drmType != null && drmLicenseServer != null && Util.getDrmUuid(drmType) != null) {
                UUID drmUUID = Util.getDrmUuid(drmType);
                videoView.setDrmType(drmUUID);
                videoView.setDrmLicenseUrl(drmLicenseServer);
                if (drmHeaders != null) {
                    ArrayList<String> drmKeyRequestPropertiesList = new ArrayList<>();
                    ReadableMapKeySetIterator itr = drmHeaders.keySetIterator();
                    while (itr.hasNextKey()) {
                        String key = itr.nextKey();
                        drmKeyRequestPropertiesList.add(key);
                        drmKeyRequestPropertiesList.add(drmHeaders.getString(key));
                    }
                    videoView.setDrmLicenseHeader(drmKeyRequestPropertiesList.toArray(new String[0]));
                }
                videoView.setUseTextureView(false);
            }
        }
    }

    @ReactProp(name = PROP_SRC)
    public void setSrc(final ReactVideoView videoView, @Nullable ReadableMap src) {
        int mainVer = src.getInt(PROP_SRC_MAINVER);
        int patchVer = src.getInt(PROP_SRC_PATCHVER);
        if(mainVer<0) { mainVer = 0; }
        if(patchVer<0) { patchVer = 0; }
        if(mainVer>0) {
            videoView.setSrc(
                    src.getString(PROP_SRC_URI),
                    src.getString(PROP_SRC_TYPE),
                    src.getBoolean(PROP_SRC_IS_NETWORK),
                    src.getBoolean(PROP_SRC_IS_ASSET),
                    src.getMap(PROP_SRC_HEADERS),
                    mainVer,
                    patchVer
            );
        }
        else {
            videoView.setSrc(
                    src.getString(PROP_SRC_URI),
                    src.getString(PROP_SRC_TYPE),
                    src.getBoolean(PROP_SRC_IS_NETWORK),
                    src.getBoolean(PROP_SRC_IS_ASSET),
                    src.getMap(PROP_SRC_HEADERS)
                    );
        }
    }

    @ReactProp(name = PROP_RESIZE_MODE)
    public void setResizeMode(final ReactVideoView videoView, final String resizeModeOrdinalString) {
        videoView.setResizeModeModifier(ScalableType.values()[Integer.parseInt(resizeModeOrdinalString)]);
    }

    @ReactProp(name = PROP_REPEAT, defaultBoolean = false)
    public void setRepeat(final ReactVideoView videoView, final boolean repeat) {
        videoView.setRepeatModifier(repeat);
    }

    @ReactProp(name = PROP_PAUSED, defaultBoolean = false)
    public void setPaused(final ReactVideoView videoView, final boolean paused) {
        videoView.setPausedModifier(paused);
    }

    @ReactProp(name = PROP_MUTED, defaultBoolean = false)
    public void setMuted(final ReactVideoView videoView, final boolean muted) {
        videoView.setMutedModifier(muted);
    }

    @ReactProp(name = PROP_VOLUME, defaultFloat = 1.0f)
    public void setVolume(final ReactVideoView videoView, final float volume) {
        videoView.setVolumeModifier(volume);
    }

    @ReactProp(name = PROP_STEREO_PAN)
    public void setStereoPan(final ReactVideoView videoView, final float stereoPan) {
        videoView.setStereoPan(stereoPan);
    }

    @ReactProp(name = PROP_PROGRESS_UPDATE_INTERVAL, defaultFloat = 250.0f)
    public void setProgressUpdateInterval(final ReactVideoView videoView, final float progressUpdateInterval) {
        videoView.setProgressUpdateInterval(progressUpdateInterval);
    }

    @ReactProp(name = PROP_SEEK)
    public void setSeek(final ReactVideoView videoView, final float seek) {
        videoView.seekTo(Math.round(seek * 1000.0f));
    }

    @ReactProp(name = PROP_RATE)
    public void setRate(final ReactVideoView videoView, final float rate) {
        videoView.setRateModifier(rate);
    }

    @ReactProp(name = PROP_FULLSCREEN, defaultBoolean = false)
    public void setFullscreen(final ReactVideoView videoView, final boolean fullscreen) {
        videoView.setFullscreen(fullscreen);
    }

    @ReactProp(name = PROP_PLAY_IN_BACKGROUND, defaultBoolean = false)
    public void setPlayInBackground(final ReactVideoView videoView, final boolean playInBackground) {
        videoView.setPlayInBackground(playInBackground);
    }

    @ReactProp(name = PROP_CONTROLS, defaultBoolean = false)
    public void setControls(final ReactVideoView videoView, final boolean controls) {
        videoView.setControls(controls);
    }
}
