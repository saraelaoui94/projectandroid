package com.example.filters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;

import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements ThumbnailCallback {
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private Activity activity;
    private RecyclerView thumbListView;
    private ImageView placeHolderImageView;
    Bitmap bitmap;
    SeekBar seekBar;
    // int drawable;
    private int PICK_IMAGE = 1;
    private int v = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        int drawable = R.drawable.dog;
        initUIWidgets(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getApplicationContext().getResources(), drawable), 640, 640, false));

        seekBar = (SeekBar) findViewById(R.id.seek1);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                placeHolderImageView.setColorFilter(setBrightness(progress));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public static PorterDuffColorFilter setBrightness(int progress) {
        if (progress >=	100)
        {
            int value = (int) (progress-100) * 255 / 100;

            return new PorterDuffColorFilter(Color.argb(value, 255, 255, 255), Mode.SRC_OVER);

        }
        else
        {
            int value = (int) (100-progress) * 255 / 100;
            return new PorterDuffColorFilter(Color.argb(value, 0, 0, 0), Mode.SRC_ATOP);


        }
    }


    public void getImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void initUIWidgets(Bitmap bitmap) {
        thumbListView = (RecyclerView) findViewById(R.id.thumbnails);
        placeHolderImageView = (ImageView) findViewById(R.id.place_holder_imageview);
//        placeHolderImageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getApplicationContext().getResources(), drawable), 640, 640, false));
        placeHolderImageView.setImageBitmap(bitmap);

        initHorizontalList(bitmap);
    }

    private void initHorizontalList(Bitmap bitmap) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        thumbListView.setLayoutManager(layoutManager);
        thumbListView.setHasFixedSize(true);
        bindDataToAdapter(bitmap);
    }

    private void bindDataToAdapter(Bitmap bitmap) {
        final Bitmap bitmap1 = bitmap;
        final Context context = this.getApplication();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                //Bitmap thumbImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), drawable), 640, 640, false);
                Bitmap thumbImage = bitmap1;
                ThumbnailsManager.clearThumbs();
                List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

                for (Filter filter : filters) {
                    ThumbnailItem thumbnailItem = new ThumbnailItem();
                    thumbnailItem.image = thumbImage;
                    thumbnailItem.filter = filter;
                    ThumbnailsManager.addThumb(thumbnailItem);
                }

                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, (ThumbnailCallback) activity);
                thumbListView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                if (v == 1) {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    // Log.d(TAG, String.valueOf(bitmap));


                    // ImageView imageView = (ImageView) findViewById(R.id.imageView0);
                    //imageView.setImageBitmap(bitmap);
                    initUIWidgets(bitmap);

                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);


                    //             bitmap=createContrast(bitmap,100);
                    //           ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                    // imageView.setImageBitmap(bitmap);
                    initUIWidgets(bitmap);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       /* else if (requestCode == PICK_IMAGE_2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
/*
            Uri uri = data.getData();

            try {
                if(v==1){
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    // Log.d(TAG, String.valueOf(bitmap));


                   // ImageView imageView = (ImageView) findViewById(R.id.imageView0);
                    //imageView.setImageBitmap(bitmap);
                    initUIWidgets(bitmap);
                }
                else{
                    //********************************************_______________________________________________________
                   /* Bitmap textBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                    Bitmap textBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_with_text_2);
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
                    if (!textRecognizer.isOperational()) {
                        new AlertDialog.Builder(this).setMessage("Text recognizer could not be set up on your device :(").show();
                        return;
                    }
                    else{
                        String detectedText="this is the text : ";

                        TextView detectedTextView=findViewById(R.id.results);
                        Frame frame = new Frame.Builder().setBitmap(textBitmap).build();
                        SparseArray<TextBlock> text = textRecognizer.detect(frame);
                        for (int i = 0; i < text.size(); i++) {
                            TextBlock textBlock = text.valueAt(i);
                            if (textBlock != null && textBlock.getValue() != null) {
                                detectedText += textBlock.getValue();
                            }
                        }
                        detectedTextView.setText(detectedText);
                        textRecognizer.release();
                    }

                    //****__________________________________________________________________________________________________
                }

            } catch (IOException e) {
               e.printStackTrace();
            }
        }*/

    }


    @Override
    public void onThumbnailClick(Filter filter) {
//        placeHolderImageView.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getApplicationContext().getResources(), drawable), 640, 640, false)));
        placeHolderImageView.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(bitmap, 640, 640, false)));

    }

}