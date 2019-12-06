package com.example.ckgns.playerproject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.CustomViewHolder> {

    ArrayList<MainData> dataList = new ArrayList<MainData>();
    Activity activity;

    public MainAdapter(ArrayList<MainData> dataList, Activity activity) {
        this.dataList = dataList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mp3_layout, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder customViewHolder, final int position) {

        customViewHolder.tvTitle.setSelected(true);
        customViewHolder.tvSinger.setSelected(true);
        Bitmap albumImage = getAlbumImage(activity, Integer.parseInt((dataList.get(position)).getAlbumId()), 170);
        customViewHolder.imageView.setImageBitmap(albumImage);
        customViewHolder.tvTitle.setText(dataList.get(position).getTitle());
        customViewHolder.tvSinger.setText(dataList.get(position).getArtist());
        customViewHolder.itemView.setTag(position);

        customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.check=true;
                MainActivity.now = position;
                MainActivity.btnPlay.callOnClick();

            }
        });
    }
    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvTitle, tvSinger;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSinger = itemView.findViewById(R.id.tvSinger);
        }
    }

    private static final BitmapFactory.Options options = new BitmapFactory.Options();

    private static Bitmap getAlbumImage(Context context, int album_id, int MAX_IMAGE_SIZE) {
        // NOTE: There is in fact a 1 pixel frame in the ImageView used to
        // display this drawable. Take it into account now, so we don't have to
        // scale later.
        ContentResolver res = context.getContentResolver();
        Uri uri = Uri.parse("content://media/external/audio/albumart/" + album_id);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;

                //크기를 얻어오기 위한옵션 ,
                //inJustDecodeBounds값이 true로 설정되면 decoder가 bitmap object에 대해 메모리를 할당하지 않고, 따라서 bitmap을 반환하지도 않는다.
                // 다만 options fields는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있다.
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, options);
                int scale = 0;
                if (options.outHeight > MAX_IMAGE_SIZE || options.outWidth > MAX_IMAGE_SIZE) {
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_IMAGE_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }
                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;

                // 크기를 샘플링한 Bitmap을 생성합니다
                Log.e("....", sampleSize + "");
                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, options);
                if (b != null) {
                    // finally rescale to exactly the size we need
                    if (options.outWidth != MAX_IMAGE_SIZE || options.outHeight != MAX_IMAGE_SIZE) {
                        Bitmap tmp = Bitmap.createScaledBitmap(b, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, true);
                        b.recycle();
                        b = tmp;
                    }
                }
                return b;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }


}
