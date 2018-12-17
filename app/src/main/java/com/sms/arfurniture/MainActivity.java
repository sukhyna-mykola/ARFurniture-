package com.sms.arfurniture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.PixelCopy;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.PlaneRenderer;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity implements FurnitureListAdapter.OnItemClickListener, RemoveSelectedNodeListener, View.OnClickListener {
    private ArFragment fragment;

    private RecyclerView furnitureList;
    private FloatingActionButton actionButtonVideoRecord;
    private ImageView recordingView;
    Animation myFadeInAnimation;

    private FurnitureItemHelper furnitureItemHelper;
    private NodesHelper nodesHelper;

    private VideoRecorder videoRecorder;

    private boolean hideControll, hideGrid, hidePointer, hideSelection;

    private PointerDrawable pointer;
    private boolean isTracking;
    private boolean isHitting;

    private OnTouchController onTouchController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create a new video recorder instance.
        videoRecorder = new VideoRecorder();
        pointer = new PointerDrawable(this);

        furnitureItemHelper = FurnitureItemHelper.getInstance();
        nodesHelper = NodesHelper.getInstance();

        furnitureList = findViewById(R.id.furniture_list_view);
        furnitureList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        furnitureList.setAdapter(new FurnitureListAdapter(this, furnitureItemHelper.getFurnitureItems()));

        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            fragment.onUpdate(frameTime);
            onUpdate();

        });

        // Specify the AR scene view to be recorded.
        videoRecorder.setSceneView(fragment.getArSceneView());

        // Set video quality and recording orientation to match that of the device.
        int orientation = getResources().getConfiguration().orientation;
        videoRecorder.setVideoQuality(CamcorderProfile.QUALITY_2160P, orientation);


        setupPlainTexture();

        actionButtonVideoRecord = findViewById(R.id.action_video);
        recordingView = findViewById(R.id.recording_video);
        myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash);


        findViewById(R.id.action_hide_controll).setOnClickListener(this);
        findViewById(R.id.action_hide_grid).setOnClickListener(this);
        findViewById(R.id.action_hide_pointer).setOnClickListener(this);
        findViewById(R.id.action_take_photo).setOnClickListener(this);
        findViewById(R.id.action_settings).setOnClickListener(this);
        findViewById(R.id.action_video).setOnClickListener(this);
        findViewById(R.id.action_hide_selection).setOnClickListener(this);

        onTouchController = new OnTouchController();
    }


    private void onUpdate() {


        boolean trackingChanged = updateTracking();
        View contentView = findViewById(android.R.id.content);
        if (trackingChanged) {
            if (isTracking) {
                contentView.getOverlay().add(pointer);
            } else {
                contentView.getOverlay().remove(pointer);
            }
            contentView.invalidate();
        }

        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (hitTestChanged) {
                pointer.setEnabled(isHitting);
                contentView.invalidate();
            }
        }

        if (hidePointer) {
            pointer.setVisible(false);
        } else {
            pointer.setVisible(true);
        }
        contentView.invalidate();

        if (hideControll) {
            nodesHelper.hideNodesControll();
        } else {
            nodesHelper.update();
        }

    }

    private boolean updateTracking() {
        Frame frame = fragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        isTracking = frame != null &&
                frame.getCamera().getTrackingState() == TrackingState.TRACKING;
        return isTracking != wasTracking;
    }

    private boolean updateHitTest() {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        boolean wasHitting = isHitting;
        isHitting = false;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    isHitting = true;
                    break;
                }
            }
        }
        return wasHitting != isHitting;
    }

    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }

    private void addObject(FurnitureItem item) {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    placeObject(fragment, hit.createAnchor(), item, ((Plane) trackable).getType() == Plane.Type.VERTICAL);
                    break;

                }
            }
        }
    }

    private void placeObject(ArFragment fragment, Anchor anchor, FurnitureItem item, boolean vertical) {
        if (item.getId() <= 0) {
            /* When you build a Renderable, Sceneform loads model and related resources
             * in the background while returning a CompletableFuture.
             * Call thenAccept(), handle(), or check isDone() before calling get().
             */
            ModelRenderable.builder()
                    .setSource(this, RenderableSource.builder().setSource(
                            this,
                            Uri.parse(item.getModel()),
                            RenderableSource.SourceType.GLB)
                            .setScale(0.025f)  // Scale the original model to 25%.
                            .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                            .build())
                    .setRegistryId(item.getModel())
                    .build()
                    .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable, item.getId(), vertical))
                    .exceptionally(
                            throwable -> {
                                Toast toast =
                                        Toast.makeText(this, "Unable to load renderable " +
                                                item.getModel(), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return null;
                            });
        } else {
            CompletableFuture<Void> renderableFuture =
                    ModelRenderable.builder()
                            .setSource(fragment.getContext(), Uri.parse(item.getModel()))
                            .build()
                            .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable, item.getId(), vertical))
                            .exceptionally((throwable -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage(throwable.getMessage())
                                        .setTitle("Codelab error!");
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                return null;
                            }));
        }
    }

    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable, long itemId, boolean vertical) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        FurnitureNode node = new FurnitureNode(fragment.getTransformationSystem(), itemId, this, this, vertical);
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        //node.addControllNode();

        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();

        nodesHelper.add(node);
    }


    private void takePhoto() {
        ArSceneView view = fragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                PhotoFragment.newInstance(bitmap).show(getSupportFragmentManager(), "photo_fragment");
            } else {
                Toast toast = Toast.makeText(MainActivity.this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();

        }, new Handler(handlerThread.getLooper()));
    }

    private void setupPlainTexture() {
        // Build texture sampler
        Texture.Sampler sampler = Texture.Sampler.builder()
                .setMinFilter(Texture.Sampler.MinFilter.LINEAR)
                .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
                .setWrapMode(Texture.Sampler.WrapMode.REPEAT).build();

        // Build texture with sampler
        CompletableFuture<Texture> trigrid = Texture.builder()
                .setSource(this, R.drawable.plain)
                .setSampler(sampler).build();

        // Set plane texture
        fragment.getArSceneView()
                .getPlaneRenderer()
                .getMaterial()
                .thenAcceptBoth(trigrid, (material, texture) -> {
                    material.setTexture(PlaneRenderer.MATERIAL_TEXTURE, texture);
                });

    }

    private void hidePlainTexture() {
        // Build texture sampler
        Texture.Sampler sampler = Texture.Sampler.builder()
                .setMinFilter(Texture.Sampler.MinFilter.LINEAR)
                .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
                .setWrapMode(Texture.Sampler.WrapMode.REPEAT).build();

        // Build texture with sampler
        CompletableFuture<Texture> trigrid = Texture.builder()
                .setSource(this, R.drawable.plain_empty)
                .setSampler(sampler).build();

        // Set plane texture
        fragment.getArSceneView()
                .getPlaneRenderer()
                .getMaterial()
                .thenAcceptBoth(trigrid, (material, texture) -> {
                    material.setTexture(PlaneRenderer.MATERIAL_TEXTURE, texture);
                });

    }

    @Override
    public void OnItemClick(FurnitureItem item) {
        addObject(item);
    }


    @Override
    public void onRemove() {
        nodesHelper.removeSelectedNode();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_hide_controll:
                hideControll = !hideControll;

                if (hideControll) {
                    ((ImageButton) v).setImageResource(R.drawable.ic_visibility_off_black_24dp);
                } else {
                    ((ImageButton) v).setImageResource(R.drawable.ic_visibility_black_24dp);
                }
                break;
            case R.id.action_hide_grid:
                hideGrid = !hideGrid;

                if (hideGrid) {
                    hidePlainTexture();
                    ((ImageButton) v).setImageResource(R.drawable.ic_border_clear_black_24dp);
                } else {
                    ((ImageButton) v).setImageResource(R.drawable.grid_24dp);
                    setupPlainTexture();
                }
                break;

            case R.id.action_hide_pointer:
                hidePointer = !hidePointer;

                if (hidePointer) {
                    ((ImageButton) v).setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                } else {
                    ((ImageButton) v).setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                }
                break;
            case R.id.action_take_photo:
                takePhoto();
                break;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.action_video:
                boolean recording = videoRecorder.onToggleRecord();
                if (recording) {
                    recordingView.setVisibility(View.VISIBLE);
                    actionButtonVideoRecord.setImageResource(android.R.drawable.presence_video_online);
                    recordingView.startAnimation(myFadeInAnimation);
                } else {
                    actionButtonVideoRecord.setImageResource(R.drawable.ic_videocam_black_24dp);
                    recordingView.setVisibility(View.GONE);
                    recordingView.clearAnimation();
                    String videoPath = videoRecorder.getVideoPath().getAbsolutePath();
                    Toast.makeText(this, "File " + videoPath + " saved", Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Video saved", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Open in Videos", view -> {
                        File photoFile = new File(videoPath);

                        Uri photoURI = FileProvider.getUriForFile(this, getPackageName() + ".ar.sms.provider", photoFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                        intent.setDataAndType(photoURI, "video/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);

                    });
                    snackbar.show();
                }
                break;

            case R.id.action_hide_selection:
                hideSelection = !hideSelection;
                if (hideSelection) {
                    ((ImageButton) v).setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                    ((MyArFragment) fragment).hideSelectionVisualizerRenderable();
                } else {
                    ((MyArFragment) fragment).showSelectionVisualizerRenderable();
                    ((ImageButton) v).setImageResource(R.drawable.ic_golf_course_black_24dp);

                }

                break;

        }
    }

}
