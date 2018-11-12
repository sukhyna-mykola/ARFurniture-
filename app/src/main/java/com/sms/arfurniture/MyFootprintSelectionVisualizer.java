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
    private ViewRenderable footprintRenderable;


    MyFootprintSelectionVisualizer() {
    }

    public void setFootprintRenderable(ViewRenderable renderable) {
        ViewRenderable copyRenderable = renderable.makeCopy();
        this.footprintNode.setRenderable(copyRenderable);
        Quaternion quaternion = Quaternion.axisAngle(new Vector3(1f, 0, 0), 90);
        //this.footprintNode.setLocalRotation(quaternion);
        this.footprintNode.setLocalPosition(new Vector3(0f, 1f, 0f));

        copyRenderable.setCollisionShape(null);
        this.footprintRenderable = copyRenderable;

    }

    @Nullable
    public ViewRenderable getFootprintRenderable() {
        return this.footprintRenderable;
    }

    public void applySelectionVisual(BaseTransformableNode node) {
        this.footprintNode.setParent(node);
    }

    public void removeSelectionVisual(BaseTransformableNode node) {
        this.footprintNode.setParent((NodeParent) null);
    }
}
