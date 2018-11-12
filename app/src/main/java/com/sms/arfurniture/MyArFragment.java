package com.sms.arfurniture;

import android.view.Gravity;
import android.widget.Toast;

import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformationSystem;

public class MyArFragment extends ArFragment {


    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
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
    }
}
