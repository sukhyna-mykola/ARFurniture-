package com.sms.arfurniture;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.squareup.picasso.Picasso;

public class FurnitureNode extends TransformableNode {
    public static final String TAG = FurnitureNode.class.getClass().getSimpleName();

    public AnchorNode getParentNode() {
        return (AnchorNode) getParent();
    }

    public FurnitureNode(TransformationSystem transformationSystem, long itemId, Context context) {
        super(transformationSystem);

        FurnitureItem item = MainActivity.getFurnitireItemById(itemId);

        TransformableNode node = new TransformableNode(transformationSystem);
        node.setLocalPosition(new Vector3(0f, 1f, 0f));

        ViewRenderable.builder()
                .setView(context, R.layout.controll_renderable)
                .build()
                .thenAccept(
                        renderable -> {
                            node.setRenderable(renderable);
                            View controllView = renderable.getView();

                            TextView title = controllView.findViewById(R.id.title);
                            title.setText(item.getTitle());

                            TextView description = controllView.findViewById(R.id.description);
                            description.setText(item.getDescription());

                            ImageView imageView = controllView.findViewById(R.id.model_preview);
                            Picasso.get().load(item.getIcon()).into(imageView);

                            controllView.findViewById(R.id.removeSelected).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ((MainActivity) context).removeSelected();
                                }
                            });

                            controllView.findViewById(R.id.bay).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                                    context.startActivity(browserIntent);
                                }
                            });

                            SeekBar rotation = controllView.findViewById(R.id.rotationSeek);
                            rotation.setMax(360);
                            rotation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    if (fromUser) {
                                        Quaternion rotationz = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), progress);
                                        setLocalRotation(rotationz);
                                        node.setLocalRotation(Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), -progress));
                                    }
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });

                        });

        addChild(node);

    }

    public void updateNode() {
        if (isSelected()) {
            showControll();
        } else {
            hideControll();
        }
    }

    public void hideControll() {
        for (Node n : getChildren()) {
            n.setEnabled(false);
        }
    }

    public void showControll() {
        for (Node n : getChildren()) {
            n.setEnabled(true);
        }
    }

}
