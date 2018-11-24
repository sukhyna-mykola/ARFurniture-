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

    private RemoweSelectedNodeListener removeSelected;

    private TransformableNode controllNode;

    public AnchorNode getParentNode() {
        return (AnchorNode) getParent();
    }

    public FurnitureNode(TransformationSystem transformationSystem, long itemId, Context context, RemoweSelectedNodeListener remoweSelectedNodeListener) {
        super(transformationSystem);
        this.removeSelected = remoweSelectedNodeListener;

        FurnitureItem item = FurnitureItemHelper.getInstance().getFurnitireItemById(itemId);

        controllNode = new TransformableNode(transformationSystem);
        controllNode.setLocalPosition(new Vector3(0f, 1f, 0f));

        ViewRenderable.builder()
                .setView(context, R.layout.controll_renderable)
                .build()
                .thenAccept(
                        renderable -> {
                            controllNode.setRenderable(renderable);
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
                                    removeSelected.onRemove();
                                }
                            });

                            controllView.findViewById(R.id.bay).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                                    context.startActivity(browserIntent);
                                }
                            });

                            SeekBar rotationXY = controllView.findViewById(R.id.rotationXYSeek);
                            //SeekBar rotationXZ = controllView.findViewById(R.id.rotationXZSeek);
                            // SeekBar rotationYZ = controllView.findViewById(R.id.rotationYZSeek);

                            rotationXY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    if (fromUser) {
                                        Quaternion rotationz = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), progress);
                                        setLocalRotation(rotationz);
                                        controllNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), -progress));
                                    }
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });
                            /*
                            rotationXZ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    if (fromUser) {
                                        Quaternion rotationz = Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), progress);
                                        setLocalRotation(rotationz);
                                    }
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });

                            rotationYZ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    if (fromUser) {
                                        Quaternion rotationz = Quaternion.axisAngle(new Vector3(0.0f, 0.0f, 1.0f), progress);
                                        setLocalRotation(rotationz);
                                    }
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });

*/
                            SeekBar scale = controllView.findViewById(R.id.scale);
                            scale.setMax((int) ((getScaleController().getMaxScale() - getScaleController().getMinScale()) * 100));
                            scale.setProgress(25);
                            scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    if (fromUser) {
                                        float scaleF = (float) (getScaleController().getMinScale() + progress / 100.00);
                                        Vector3 scale = new Vector3(scaleF, scaleF, scaleF);
                                        Node parent = getParent();
                                        setParent(null);
                                        setLocalScale(scale);
                                        setParent(parent);
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

        addChild(controllNode);
    }


    public void addControllNode() {
        getParentNode().addChild(controllNode);
    }

    public void updateNode() {
        if (isSelected()) {
            showControll();
        } else {
            hideControll();
        }
    }

    public void hideControll() {
        controllNode.setEnabled(false);
    }

    public void showControll() {
        controllNode.setEnabled(true);
    }

    public void remove() {
        getParentNode().getAnchor().detach();
        setParent(null);
        setRenderable(null);
    }
}
