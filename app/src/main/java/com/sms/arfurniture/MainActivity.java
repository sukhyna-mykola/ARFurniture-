package com.sms.arfurniture;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.PixelCopy;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.PlaneRenderer;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity implements FurnitureListAdapter.OnItemClickListener, RemoweSelectedNodeListener, View.OnClickListener {
    private ArFragment fragment;

    private RecyclerView furnitureList;

    private FurnitureItemHelper furnitureItemHelper;
    private NodesHelper nodesHelper;

    private boolean hideControll, hideGrid, hidePointer;

    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        setupPlainTexture();

        findViewById(R.id.action_hide_controll).setOnClickListener(this);
        findViewById(R.id.action_hide_grid).setOnClickListener(this);
        findViewById(R.id.action_hide_pointer).setOnClickListener(this);
        findViewById(R.id.action_take_photo).setOnClickListener(this);
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

        if (hideGrid) {
            fragment.getArSceneView()
                    .getPlaneRenderer().setEnabled(false);
        } else {
            fragment.getArSceneView()
                    .getPlaneRenderer().setEnabled(true);
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
                    placeObject(fragment, hit.createAnchor(), item);
                    break;

                }
            }
        }
    }

    private void placeObject(ArFragment fragment, Anchor anchor, FurnitureItem item) {
        CompletableFuture<Void> renderableFuture =
                ModelRenderable.builder()
                        .setSource(fragment.getContext(), Uri.parse(item.getModel()))
                        .build()
                        .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable, item.getId()))
                        .exceptionally((throwable -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage(throwable.getMessage())
                                    .setTitle("Codelab error!");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            return null;
                        }));
    }

    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable, long itemId) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        FurnitureNode node = new FurnitureNode(fragment.getTransformationSystem(), itemId, this, this);
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
                    ((ImageButton) v).setImageResource(R.drawable.ic_border_clear_black_24dp);
                } else {
                    ((ImageButton) v).setImageResource(R.drawable.grid_24dp);
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
        }
    }
}
