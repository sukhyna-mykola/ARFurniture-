package com.sms.arfurniture;

import android.Manifest;
import android.view.Gravity;
import android.widget.Toast;

import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformationSystem;

public class MyArFragment extends ArFragment {

    MyFootprintSelectionVisualizer selectionVisualizer;

    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    protected TransformationSystem makeTransformationSystem() {
        selectionVisualizer = new MyFootprintSelectionVisualizer();
        TransformationSystem transformationSystem =
                new TransformationSystem(getResources().getDisplayMetrics(), selectionVisualizer);

        showSelectionVisualizerRenderable();

        return transformationSystem;
    }


    public void hideSelectionVisualizerRenderable() {
        selectionVisualizer.setFootprintRenderable(null);
    }

    public void showSelectionVisualizerRenderable() {
        (ModelRenderable.builder().setSource(this.getActivity(), com.google.ar.sceneform.ux.R.raw.sceneform_footprint)).build().thenAccept((renderable) -> {
            if (selectionVisualizer.getFootprintRenderable() == null) {
                selectionVisualizer.setFootprintRenderable(renderable);
            }

        }).exceptionally((throwable) -> {
            Toast toast = Toast.makeText(this.getContext(), "Unable to load footprint renderable", 1);
            toast.setGravity(17, 0, 0);
            toast.show();
            return null;
        });
    }

    @Override
    public String[] getAdditionalPermissions() {
        String[] additionalPermissions = super.getAdditionalPermissions();
        int permissionLength = additionalPermissions != null ? additionalPermissions.length : 0;
        String[] permissions = new String[permissionLength + 1];
        permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (permissionLength > 0) {
            System.arraycopy(additionalPermissions, 0, permissions, 1, additionalPermissions.length);
        }
        return permissions;
    }
}
