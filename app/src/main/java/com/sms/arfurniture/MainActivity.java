package com.sms.arfurniture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Sun;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.PlaneRenderer;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.SelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity implements FurnitureListAdapter.OnItemClickListener {
    private ArFragment fragment;

    private RecyclerView furnitureList;
    public static List<FurnitureItem> furnitureItems;

    private List<FurnitureNode> nodes;

    private boolean hideControll, hideGrid, hidePointer;


    static {
        furnitureItems = new ArrayList<>();
        furnitureItems.add(new FurnitureItem(1, FurnitureItem.FurnitureType.CHAIR, "Chair", "Chair description", "file:///android_asset/chair_model.jpg", "chair_model.sfb"));
        furnitureItems.add(new FurnitureItem(2, FurnitureItem.FurnitureType.SOFA, "Sofa", "Sofa description", "file:///android_asset/Craft+Sofa.jpg", "Craft+Sofa.sfb"));
        furnitureItems.add(new FurnitureItem(3, FurnitureItem.FurnitureType.CHAIR, "Chair", "Chair description", "file:///android_asset/chair_model.jpg", "chair_model.sfb"));
        furnitureItems.add(new FurnitureItem(4, FurnitureItem.FurnitureType.SOFA, "Sofa", "Sofa description", "file:///android_asset/Craft+Sofa.jpg", "Craft+Sofa.sfb"));
        furnitureItems.add(new FurnitureItem(5, FurnitureItem.FurnitureType.CHAIR, "Chair", "Chair description", "file:///android_asset/chair_model.jpg", "chair_model.sfb"));
        furnitureItems.add(new FurnitureItem(6, FurnitureItem.FurnitureType.SOFA, "Sofa", "Sofa description", "file:///android_asset/Craft+Sofa.jpg", "Craft+Sofa.sfb"));

    }


    public static FurnitureItem getFurnitireItemById(long id) {
        for (FurnitureItem item : furnitureItems) {
            if (item.getId() == id)
                return item;
        }

        return null;
    }

    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      /*  Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        findViewById(R.id.action_hide_controll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideControll = !hideControll;

                if (hideControll) {
                    ((ImageButton) v).setImageResource(R.drawable.ic_visibility_off_black_24dp);
                } else {
                    ((ImageButton) v).setImageResource(R.drawable.ic_visibility_black_24dp);
                }
            }
        });

        findViewById(R.id.action_hide_grid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideGrid = !hideGrid;

                if (hideGrid) {
                    ((ImageButton) v).setImageResource(R.drawable.ic_border_clear_black_24dp);
                } else {
                    ((ImageButton) v).setImageResource(R.drawable.grid_24dp);
                }
            }
        });

        findViewById(R.id.action_hide_pointer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePointer = !hidePointer;

                if (hidePointer) {
                    ((ImageButton) v).setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                } else {
                    ((ImageButton) v).setImageResource(R.drawable.ic_wb_sunny_black_24dp);
                }
            }
        });


        nodes = new ArrayList<>();

        furnitureList = findViewById(R.id.furniture_list_view);
        furnitureList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        furnitureList.setAdapter(new FurnitureListAdapter(this, furnitureItems));

        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            fragment.onUpdate(frameTime);
            onUpdate();

        });


        setupPlainTexture();
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_hide:

                return true;
        }
        return false;
    }*/

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
            hide();
        } else {
            update();
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

    @Override
    public void OnItemClick(FurnitureItem item) {
        addObject(item);
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
        FurnitureNode node = new FurnitureNode(fragment.getTransformationSystem(), itemId, this);
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
        nodes.add(node);
    }

    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/" + date + "_screenshot.jpg";
    }


    private void takePhoto() {

        final String filename = generateFilename();
        ArSceneView view = fragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                PhotoFragment.newInstance(bitmap, filename).show(getSupportFragmentManager(), "photo_fragment");
            } else {
                Toast toast = Toast.makeText(MainActivity.this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();

        }, new Handler(handlerThread.getLooper()));
    }


    public void removeSelected() {
        boolean removed = false;
        Iterator<FurnitureNode> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            FurnitureNode n = iterator.next();
            if (n.isSelected()) {
                n.getParentNode().getAnchor().detach();
                n.setParent(null);
                n.setRenderable(null);
                iterator.remove();
                removed = true;
            }
        }
        if (removed) {
            if (!nodes.isEmpty()) {
                nodes.get(nodes.size() - 1).select();
            }
        }
    }


    public void hide() {
        for (FurnitureNode n : nodes) {
            n.hideControll();
        }

        // hide plane texture
        fragment.getArSceneView()
                .getPlaneRenderer().setEnabled(false);
    }

    private void update() {
        for (FurnitureNode n : nodes) {
            n.updateNode();
        }

    }


}
