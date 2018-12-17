package com.sms.arfurniture;

import android.support.annotation.Nullable;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.NodeParent;
import com.google.ar.sceneform.collision.CollisionShape;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.SelectionVisualizer;

public class MyFootprintSelectionVisualizer implements SelectionVisualizer {
    private final Node footprintNode = new Node();
    @Nullable
    private ModelRenderable footprintRenderable;

    public MyFootprintSelectionVisualizer() {
    }

    public void setFootprintRenderable(ModelRenderable renderable) {
        if (renderable != null) {
            ModelRenderable copyRenderable = renderable.makeCopy();
            this.footprintNode.setRenderable(copyRenderable);
            copyRenderable.setCollisionShape((CollisionShape) null);
            this.footprintRenderable = copyRenderable;
        } else {
            this.footprintNode.setRenderable(null);
            this.footprintRenderable = null;
        }
    }



    @Nullable
    public ModelRenderable getFootprintRenderable() {
        return this.footprintRenderable;
    }

    public void applySelectionVisual(BaseTransformableNode node) {
        this.footprintNode.setParent(node);
    }

    public void removeSelectionVisual(BaseTransformableNode node) {
        this.footprintNode.setParent((NodeParent) null);
    }
}
