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


  /*  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    protected TransformationSystem makeTransformationSystem() {
        MyFootprintSelectionVisualizer selectionVisualizer = new MyFootprintSelectionVisualizer();

        TransformationSystem transformationSystem =
                new TransformationSystem(getResources().getDisplayMetrics(), selectionVisualizer);

        ViewRenderable.builder()
                .setView(getActivity(), R.layout.footprint_renderable)
                .build()
                .thenAccept(
                        renderable -> {
                            // If the selection visualizer already has a footprint renderable, then it was set to
                            // something custom. Don't override the custom visual.
                            if (selectionVisualizer.getFootprintRenderable() == null) {
                                selectionVisualizer.setFootprintRenderable(renderable);
                            }
                        })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(
                                            getContext(), "Unable to load footprint renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        return transformationSystem;
    }*/

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
