package com.ewarranty.warranty.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import com.ewarranty.warranty.R;
import com.ewarranty.warranty.models.Theme;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class ThemeSelectDialogFragment extends DialogFragment {

    private OnItemSelectListener onItemSelectListener;

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.black2);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_theme_select_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView lite = (TextView) view.findViewById(R.id.lite);
        TextView dark = (TextView) view.findViewById(R.id.dark);
        TextView auto = (TextView) view.findViewById(R.id.auto);
        lite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemSelectListener!=null){
                    onItemSelectListener.onItemSelectListener(Theme.LITE);
                }
            }
        });

        dark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemSelectListener!=null){
                    onItemSelectListener.onItemSelectListener(Theme.DARK);
                }
            }
        });

        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemSelectListener!=null){
                    onItemSelectListener.onItemSelectListener(Theme.AUTO);
                }
            }
        });

    }


    public ThemeSelectDialogFragment setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
        return this;
    }

    public interface OnItemSelectListener {
        // TODO: Update argument type and name
        void onItemSelectListener(Theme theme);
    }
}
