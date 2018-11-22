package com.sms.arfurniture;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoFragment extends DialogFragment {
    private static final String ARG_BITMAP = "bitmap";
    private static final String ARG_FILE_NAME = "file_name";

    private Bitmap photo;
    private String fileName;


    public PhotoFragment() {
        // Required empty public constructor
    }

    public static PhotoFragment newInstance(Bitmap bitmap, String fileName) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BITMAP, bitmap);
        args.putString(ARG_FILE_NAME, fileName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photo = getArguments().getParcelable(ARG_BITMAP);
            fileName = getArguments().getString(ARG_FILE_NAME);
        }

        setHasOptionsMenu(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_photo, null, false);
        ImageView imageView = v.findViewById(R.id.photo_preview);
        imageView.setImageBitmap(photo);

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(fileName);

        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.action_save_to_disk:
                                save(photo, fileName);
                                return true;
                            case R.id.action_share:
                                share(photo, fileName);
                                return true;

                            default:
                                break;
                        }

                        return false;
                    }
                });

        toolbar.inflateMenu(R.menu.menu_photo_dialog);
        return new AlertDialog.Builder(getContext()).setView(v).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
    }

    private void save(Bitmap bitmap, String filename) {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
            Snackbar snackbar = Snackbar.make(getDialog().findViewById(android.R.id.content),
                    "Photo saved", Snackbar.LENGTH_LONG);
            snackbar.setAction("Open in Photos", v -> {
                File photoFile = new File(filename);

                Uri photoURI = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".ar.sms.provider", photoFile);
                Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                intent.setDataAndType(photoURI, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);

            });
            snackbar.show();
        } catch (IOException ex) {
            Toast.makeText(getContext(), "Failed to save bitmap to disk", Toast.LENGTH_SHORT).show();
        }


    }

    private void share(Bitmap photo, String fileName) {
        File file = new File(fileName);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);

            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String shareBody ="Screenshot from ARCore based app";
            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            Uri uri  = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".ar.sms.provider", file);

            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share via"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }}
}
