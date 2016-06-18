package vircomp590.a1eightqueens;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by Vir on 1/18/2016.
 */
public class BoardAdapter extends BaseAdapter {

    private Context ctxt;
    public int size;
    public Integer[] locations = {-1,-1,-1,-1,-1,-1,-1,-1};
    public String message;
    Activity activity;

    public BoardAdapter(Context c, int size){
        ctxt = c;
        this.size = size;
        message = "Queens left to place: ";
        activity = (Activity) ctxt;
    }

    public View getView(final int position, View view, final ViewGroup viewGroup){
        final FrameLayout fl = new FrameLayout(ctxt);
        fl.setLayoutParams(new GridView.LayoutParams(size, size));
        fl.setId(position);
        if(((position / 8) & 1)== 0) {
            if((position & 1) == 0)
                fl.setBackgroundColor(ContextCompat.getColor(ctxt, R.color.colorPrimary));
            else
                fl.setBackgroundColor(ContextCompat.getColor(ctxt, R.color.colorPrimaryDark));
        }else{
            if((position & 1) == 0)
                fl.setBackgroundColor(ContextCompat.getColor(ctxt, R.color.colorPrimaryDark));
            else
                fl.setBackgroundColor(ContextCompat.getColor(ctxt, R.color.colorPrimary));
        }
        fl.setPadding(0, 0, 0, 0);
        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valid(position)) {
                    if (((ViewGroup) v).getChildCount() == 1) {
                        locations[position % 8] = -1;
                        ((ViewGroup) v).removeViewAt(0);
                        if(activity.findViewById(R.id.np) != null)
                            ((ViewGroup) ((NumberPicker) activity.findViewById(R.id.np)).getParent()).removeView(activity.findViewById(R.id.np));
                        msg("", 3);
                    } else {
                        ImageView q = new ImageView(ctxt);
                        q.setImageResource(R.mipmap.queen);
                        q.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        q.setLayoutParams(new FrameLayout.LayoutParams(size - 5, size - 5));
                        q.setPadding(0, 0, 5, 0);
                        fl.addView(q);
                        locations[position % 8] = position / 8;
                        msg("", 3);
                    }
                }
            }
        });
        return fl;
    }

    @Override
    public int getCount(){
        return 64;
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    //checks if all previous cols are filled or if in the first col and then if the new queen location is in attacking position
    public boolean valid(int index){
        int row = index/8;
        int col = index%8;
        if(!allPrev(col)){
            msg("", 1);
            return false;
        }
        if(!allPost(col)){
            msg("", 2);
            return false;
        }
        for(int i = 0; i < locations.length; i++){
            if(locations[i] != -1){
                if(locations[i] == row && i == col){
                    return true;
                }
                if(locations[i] == row){
                    msg("in row " + String.valueOf(row), 0);
                    return false;
                }
                if(Math.abs(row-locations[i]) == Math.abs(col-i)){
                    msg("diagonally (" + String.valueOf(locations[i]) + ", " + String.valueOf(i) + ")", 0);
                    return false;
                }
                if(i == col){
                    msg("in column " + String.valueOf(col), 0);
                    return false;
                }
            }
        }
        return true;
    }

    //simple method for changing my error message
    public void msg(String loc, int choice){
        if(choice == 0)
            ((TextView) activity.findViewById(R.id.errors)).setText("Error: Queen conflict " + loc);
        else if(choice == 1)
            ((TextView) activity.findViewById(R.id.errors)).setText("Error: Unfilled previous columns" + loc);
        else if(choice == 2)
            ((TextView) activity.findViewById(R.id.errors)).setText("Error: Future columns filled" + loc);
        else{
            ((TextView) activity.findViewById(R.id.errors)).setText(loc);
            ((TextView) activity.findViewById(R.id.message)).setText(message + String.valueOf(count()));
        }
    }

    //helper method for boolean valid() call
    public boolean allPrev(int col){
        if(col == 0 && allPost(col))
            return true;
        for(int i = col-1; i >= 0; i--){
            if(locations[i] == -1)
                return false;
        }
        return true;
    }

    public boolean allPost(int col){
        if(col == 7 && allPrev(col))
            return true;
        for(int i = col+1; i<locations.length; i++){
            if(locations[i] != -1)
                return false;
        }
        return true;
    }

    //checks the remaining number of queens needing to be placed
    public int count(){
        int count = 0;
        for(int i = 0; i < locations.length; i++)
            if (locations[i] == -1)
                count++;
        return count;
    }

    public void clear(){
        for(int i = 0; i < locations.length; i++)
            locations[i] = -1;
        msg("", 3);
    }
}
