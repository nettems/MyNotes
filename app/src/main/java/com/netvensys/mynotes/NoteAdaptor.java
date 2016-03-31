package com.netvensys.mynotes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Srinivasa.Nettem on 2/11/2016.
 */
public class NoteAdaptor extends ArrayAdapter<Note> {

    public static class ViewHolder{
        TextView title;
        TextView note;
        ImageView noteIcon;
        TextView alertDate;
    }

    View.OnTouchListener mTouchListener;

    public NoteAdaptor(Context context, ArrayList<Note> notes, View.OnTouchListener listener){
        super(context, 0, notes);
        mTouchListener = listener;
    }

    public NoteAdaptor(Context context, ArrayList<Note> notes){
        super(context, 0, notes);
        mTouchListener = null;
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // return super.onCreateView(inflater, container, savedInstanceState);

        View view = (ViewGroup) inflater.inflate(R.layout.product_frame, null);
        getFragmentManager().beginTransaction().replace(R.id.sub_header, new Sub_Header()).commit();
        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_left,
                R.animator.slide_out_right, 0, 0).replace(R.id.product_frame, new Sub_Catagory_Grid()).commit();

        view.getWidth();
        return view;

    }
    */

    public static class NoteHolder {
        public RelativeLayout mainView;
        public RelativeLayout deleteView;
        public RelativeLayout editView;
        /* other views here */
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the data item for this position
        Note note = getItem(position);

        //Create new ViewHolder
        ViewHolder viewHolder;

        NoteHolder noteHolder =  new NoteHolder();

        //Check if existing view is being re-used, otherwise inflate a new view from custom row layout
        if (convertView == null)
        {
            //if we don't have a view that is being used to create one, and make sure you create a ViewHolder along with it to save our data to
            viewHolder = new ViewHolder();

            //noteHolder = new NoteHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);

            noteHolder.mainView = (RelativeLayout)convertView.findViewById(R.id.mainView);
            noteHolder.deleteView = (RelativeLayout)convertView.findViewById(R.id.deleteView);
            noteHolder.editView = (RelativeLayout)convertView.findViewById(R.id.editView);

            //Set our views to view holder so that we no longer have to go back and find view by id every time we have a new row
            viewHolder.title = (TextView) noteHolder.mainView.findViewById(R.id.listItemNoteTitle);
            viewHolder.note = (TextView) noteHolder.mainView.findViewById(R.id.listItemNoteBody);
            viewHolder.noteIcon = (ImageView) noteHolder.mainView.findViewById(R.id.listItemNoteImg);
            viewHolder.alertDate = (TextView) noteHolder.mainView.findViewById(R.id.listItemAlarm);

            //Use set tag to remember our view holder which is holding our reference to widget
            noteHolder.mainView.setTag(viewHolder);
        }
        else
        {
            //We already have a view so just go our viewHolder and grab the widgets from it
            noteHolder.mainView = (RelativeLayout)convertView.findViewById(R.id.mainView);
            viewHolder = (ViewHolder) noteHolder.mainView.getTag();
        }

        /*
        //Grab reference of views so we can populate them with  specific note row date
        TextView noteTitle = (TextView) convertView.findViewById(R.id.listItemNoteTitle);
        TextView noteText = (TextView) convertView.findViewById(R.id.listItemNoteBody);
        ImageView noteIcon = (ImageView) convertView.findViewById(R.id.listItemNoteImg);
*/
        //Fill each referenced view with data associated with note it's referencing
        viewHolder.title.setText(note.getTitle());
        viewHolder.note.setText(note.getMessage());
        //viewHolder.noteIcon.setImageResource(note.getAssociateDrawable());

        final Resources res = getContext().getResources();
        final int tileSize = res.getDimensionPixelSize(R.dimen.letter_tile_size);

        final LetterTileProvider tileProvider = new LetterTileProvider(getContext());
        final Bitmap letterTile = tileProvider.getLetterTile(note.getCategory().name(), "key", tileSize, tileSize);

        viewHolder.noteIcon.setImageBitmap((new BitmapDrawable(res, letterTile).getBitmap()));

        viewHolder.alertDate.setText(DateFormat.format("dd/MM/yyyy hh:mm:ss", note.getAlertStartDate()).toString());

        //viewHolder.noteIcon.setImageIcon();

        /*
        if(noteHolder.deleteView != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                    noteHolder.deleteView .getLayoutParams();
            params.height = noteHolder.mainView.getLayoutParams().height;
            noteHolder.deleteView .setLayoutParams(params);
            //Toast.makeText(getActivity().getApplicationContext(), mainView.getLayoutParams().height, Toast.LENGTH_SHORT).show();
        }
        if(noteHolder.editView != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                    noteHolder.editView .getLayoutParams();
            params.height = noteHolder.mainView.getLayoutParams().height;
            noteHolder.editView .setLayoutParams(params);
            //Toast.makeText(getActivity().getApplicationContext(), mainView.getLayoutParams().height, Toast.LENGTH_SHORT).show();
        }
        */

        if(mTouchListener != null){
            // Add touch listener to every new view to track swipe motion
            noteHolder.mainView.setOnTouchListener(mTouchListener);
        }

        //now that we modified the view to display the appropriate data, return it so it will be displayed.
        return convertView;
        }
    }

