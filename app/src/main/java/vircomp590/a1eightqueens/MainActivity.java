package vircomp590.a1eightqueens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String virText, quitText, clearText;
    TextView message, errors;
    GridView gridView;
    BoardAdapter board;
    int psize, dsize;
    NumberPicker np;
    ArrayList<Integer[]> solutions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        solutions = new ArrayList<Integer[]>();
        virText = getResources().getString(R.string.virText);
        clearText = getResources().getString(R.string.clearText);
        quitText = getResources().getString(R.string.quitText);

        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.grid);
        message = (TextView) findViewById(R.id.message);
        errors = (TextView) findViewById(R.id.errors);
        Button virButton = (Button) findViewById(R.id.vir);
        Button clearButton = (Button) findViewById(R.id.clear);
        Button quitButton = (Button) findViewById(R.id.quit);
        virButton.setOnClickListener(click());
        clearButton.setOnClickListener(click());
        quitButton.setOnClickListener(click());

        psize = dm.widthPixels < dm.heightPixels ? dm.widthPixels/8 : dm.heightPixels/10; //divide by 10 for each square psize. 1 at top, 1 at bottom, 8 for squares
        dsize = Math.round(psize / (dm.densityDpi / 160f));
        message.setPadding(0, psize * 7, 0, 0);
        message.setTextSize(((float) psize) / 7);
        errors.setPadding(0, psize * 6, 0, 0);
        errors.setTextSize(((float) psize) / 7);
        board = new BoardAdapter(this, psize);
        gridView.setAdapter(board);
        board.msg("", 3);
    }

    private View.OnClickListener click(){
        return new View.OnClickListener(){ //when clear button is pressed the following activates
            @Override
            public void onClick(View v) {
                AlertDialog box = Click(v.getId());
                box.show();
            }
        };
    }

    private AlertDialog Click(int id) {
        switch(id){
            case R.id.clear:
                return new AlertDialog.Builder(this)
                        .setTitle("Clear?")
                        .setMessage(clearText)
                        .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //code to clear all the queens off the board
                                for(int i = 0; i < gridView.getChildCount(); i++) {
                                    ((ViewGroup) gridView.getChildAt(i)).removeAllViews();
                                }
                                board.clear();
                                if(findViewById(R.id.np) != null)
                                    ((ViewGroup)((NumberPicker)findViewById(R.id.np)).getParent()).removeView(findViewById(R.id.np));
                                solutions.clear();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
            case R.id.vir:
                return new AlertDialog.Builder(this)
                        .setTitle("Info")
                        .setMessage(virText)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
            case R.id.quit:
                return new AlertDialog.Builder(this)
                        .setTitle("Giving Up?")
                        .setMessage(quitText)
                        .setPositiveButton("Give Up", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                solve();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
            default:
                return null;
        }
    }

    public void removePrev(int message){
        int index = 0;
        for(int i = board.locations.length-1; i >= 0; i--){
            if(board.locations[i] != -1){
                index = i + 8*board.locations[i];
                board.locations[i] = -1;
                break;
            }
        }
        ((FrameLayout)((GridView)findViewById(R.id.grid)).getChildAt(index)).removeViewAt(0);
        if(message == 0)
            board.msg("", 3);
    }

    public void addAt(int index, int message){
        ImageView q = new ImageView(this);
        q.setImageResource(R.mipmap.queen);
        q.setScaleType(ImageView.ScaleType.FIT_CENTER);
        q.setLayoutParams(new FrameLayout.LayoutParams(psize - 5, psize - 5));
        q.setPadding(0, 0, 1, 0);
        ((FrameLayout)((GridView)findViewById(R.id.grid)).getChildAt(index)).addView(q);
        board.locations[index%8] = index/8;
        if(message == 0)
            board.msg("", 3);
    }

    public void solve(){
        int col = 0;
        Integer[] loc = new Integer[8];
        for(int i = 0; i < board.locations.length; i++){
            if(board.locations[i] != -1)
                col = i+1;
            loc[i] = board.locations[i];
        }
        int tot = total(0, col);
        final int column = col;
        message.setText("Solutions when quit: " + String.valueOf(tot));
        if(tot == 0){
            errors.setText(getResources().getString(R.string.noSol));
        }else {
            errors.setText(getResources().getString(R.string.empty));
            np = new NumberPicker(this);
            np.setMinValue(0);
            np.setValue(0);
            np.setId(R.id.np);
            np.setMaxValue(tot - 1);
            for (int i = 0; i < np.getChildCount(); i++)
                ((EditText) np.getChildAt(i)).setFocusable(false);
            np.setWrapSelectorWheel(false);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    for(int i = column; i < board.locations.length; i++)
                        removePrev(1);
                    boardChange(newVal, column);
                }
            });
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dsize * 5, dsize * 4);
            params.gravity = (Gravity.BOTTOM | Gravity.END);
            params.setMargins(0, 0, psize, dsize * 5);
            np.setLayoutParams(params);
            addContentView(np, params);
            System.arraycopy(loc, 0, board.locations, 0, 8);
            boardChange(0, column);
        }
    }

    public int total(int tot, int colStart){
        if(board.count() == 0) {
            Integer[] temp = new Integer[8];
            System.arraycopy(board.locations,0,temp,0,8);
            solutions.add(temp);
            return tot + 1;
        }
        for(int i = colStart; i < board.locations.length; i++){
            for(int j = 0; j < board.locations.length; j++){
                int index = i + j*8;
                if(board.valid(index)) {
                    addAt(index,1);
                    tot = total(tot, i+1);
                    removePrev(1);
                }
            }
        }
        return tot;
    }

    public boolean solve(int col){
        if(board.count()==0)
            return true;
        for(int i = 0; i < board.locations.length; i++){
            int index = col + 8*i;
            if(board.valid(index)){
                addAt(index,0);
                if(solve(col+1))
                    return true;
                removePrev(0);
            }
        }
        return false;
    }

    public void boardChange(int index, int col){
        Integer[] temp = solutions.get(index);
        for(int i = col; i < temp.length; i++)
            addAt((temp.length-board.count())+8*temp[i],1);
    }
}
