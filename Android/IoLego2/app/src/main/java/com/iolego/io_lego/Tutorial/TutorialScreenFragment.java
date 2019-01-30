package com.iolego.io_lego.Tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iolego.io_lego.R;

public class TutorialScreenFragment extends Fragment {
    private static String 
        IMG_ID = "imgId", 
        TIT_ID = "titId",
        POS = "pos";


    private ImageView image;
    private int imageResId, titleResId, position;

    private TextView title;
    private Button exitBTN;


    public static TutorialScreenFragment newInstance(int imageResId, int titleResId, int position) {
        final TutorialScreenFragment f = new TutorialScreenFragment();
        final Bundle args = new Bundle();
        args.putInt(IMG_ID, imageResId);
        args.putInt(TIT_ID, titleResId);
        args.putInt(POS, position);
        f.setArguments(args);

        return f;
    }

    // Empty constructor, required as per Fragment docs
    public TutorialScreenFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.imageResId = arguments.getInt(IMG_ID);
            this.titleResId = arguments.getInt(TIT_ID);
            this.position = arguments.getInt(POS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial_screen, container, false);
        image = rootView.findViewById(R.id.tutorial_screen_image);
        title = rootView.findViewById(R.id.tutorial_screen_title);
        exitBTN = rootView.findViewById(R.id.exit_tutorial);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Tutorial.class.isInstance(getActivity())) {
            title.setText(titleResId);
            Glide.with(this)
                    .load(imageResId)
                    .centerCrop()
                    .into(image);

            if(position == 3) {
                exitBTN.setVisibility(View.VISIBLE);
                exitBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();
                    }
                });
            }else{
                exitBTN.setVisibility(View.GONE);
            }

        }

    }
}