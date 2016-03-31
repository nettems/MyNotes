package com.netvensys.mynotes;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.app.ListFragment;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends ListFragment {

    private ArrayList<Note> notes;
    private NoteAdaptor noteAdaptor;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    static final int SWIPE_MAX_OFF_PATH = 250;

    // Viewport extremes. See mCurrentViewport for a discussion of the viewport.
    private static final float AXIS_X_MIN = -1f;
    private static final float AXIS_X_MAX = 1f;
    private static final float AXIS_Y_MIN = -1f;
    private static final float AXIS_Y_MAX = 1f;

    Animation slide_in_left, slide_out_right;
    Animation slide_in_right, slide_out_left;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private String alarmDate;
    private String alarmTime;
    //private ImageButton noteShareButton;

    private int notePosition;
    private GestureDetectorCompat mDetector;

    private Rect mContentRect;

    private OverScroller mScroller;
    private RectF mScrollerStartViewport;

    private RectF mCurrentViewport = new RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX);

    BackgroundContainer mBackgroundContainer;
    boolean mSwiping = false;
    boolean mItemPressed = false;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();
    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;

    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        /*
        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new SideIndexGestureListener());
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        getListView().setOnTouchListener(gestureListener);
        */

        NotesDBAdaptor notesDBAdaptor = new NotesDBAdaptor(getActivity().getBaseContext());
        notesDBAdaptor.open();
        notes = notesDBAdaptor.getAllNotes();
        notesDBAdaptor.close();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isSwipeAction = sharedPreferences.getBoolean("swipe", false);

        if(isSwipeAction) {
            noteAdaptor = new NoteAdaptor(getActivity(), notes, mTouchListener);
        }
        else {
            noteAdaptor = new NoteAdaptor(getActivity(), notes);
        }
        setListAdapter(noteAdaptor);

        //Context context = getActivity().getBaseContext();

        //slide_in_left = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
        //slide_out_right = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right);
        //slide_in_right = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
        //slide_out_left = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);

        //mDetector = new GestureDetectorCompat(getContext(),this);

        getListView().setDivider(ContextCompat.getDrawable(getActivity(), android.R.color.transparent));
        getListView().setDividerHeight(10);
        FrameLayout.LayoutParams Params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Params.setMargins(10, 10, 10, 10);
        getListView().setLayoutParams(Params);

        registerForContextMenu(getListView());

        /*
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                ImageButton noteShareButton = (ImageButton) view.findViewById(R.id.noteShareButton);
                Toast.makeText(getActivity().getApplicationContext(), noteShareButton.getVisibility() + "", Toast.LENGTH_SHORT).show();

            }
        });
        */
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);

        onClick(v,position);
    }

    private void onClick(View v, int position){
        ImageButton noteShareButton = (ImageButton)v.findViewById(R.id.noteShareButton);
        ImageButton noteEditButton = (ImageButton)v.findViewById(R.id.noteEditButton);
        ImageButton noteDeleteButton = (ImageButton)v.findViewById(R.id.noteDeleteButton);
        ImageButton noteAlarmButton = (ImageButton)v.findViewById(R.id.noteAlarmButton);
        ImageButton noteMapButton = (ImageButton)v.findViewById(R.id.noteMapButton);//noteAlarmToggle
        ToggleButton noteAlarmToggle = (ToggleButton)v.findViewById(R.id.noteAlarmToggle);

        TextView noteAlarm = (TextView) v.findViewById(R.id.listItemAlarm);
        TextView noteAgo = (TextView) v.findViewById(R.id.listItemAgo);

        //DateToTimeSpan timeSpan = new DateToTimeSpan();
        notePosition = position;
        Note note = (Note) getListAdapter().getItem(notePosition);
        noteAgo.setText(DateToTimeSpan.DateToTimeSpan(note.getDate()));

        if(noteShareButton.getVisibility()==View.VISIBLE){
            noteShareButton.setVisibility(View.GONE);
            noteEditButton.setVisibility(View.GONE);
            noteDeleteButton.setVisibility(View.GONE);
            noteAlarmButton.setVisibility(View.GONE);
            noteMapButton.setVisibility(View.GONE);

            noteAlarm.setVisibility(View.GONE);
            noteAgo.setVisibility(View.GONE);

            noteAlarmToggle.setVisibility(View.GONE);
        }else{
            noteShareButton.setVisibility(View.VISIBLE);
            noteEditButton.setVisibility(View.VISIBLE);
            noteDeleteButton.setVisibility(View.VISIBLE);
            noteAlarmButton.setVisibility(View.VISIBLE);
            noteMapButton.setVisibility(View.VISIBLE);

            noteAlarm.setVisibility(View.VISIBLE);
            noteAgo.setVisibility(View.VISIBLE);

            noteAlarmToggle.setVisibility(View.VISIBLE);
        }

        noteShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Grab the note information associated with whatever note item we clicked on
                Note note = (Note) getListAdapter().getItem(notePosition);
                //TextView noteBody = (TextView) v.findViewById(R.id.viewNoteMessage);
                shareMessage(note.getMessage());
                //Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
            }
        });

        noteEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchNoteDetailActivity(MainActivity.fragmentToLaunch.EDIT, notePosition);
            }
        });

        noteMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMyLocationActivity(notePosition);
            }
        });

        noteAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Grab the note information associated with whatever note item we clicked on

                View multiPickerLayout = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_alarm, null);
                final TabHost tabHost=(TabHost)multiPickerLayout.findViewById(R.id.tabhost);
                tabHost.setup();
                TabHost.TabSpec spec=tabHost.newTabSpec("tag1");
                spec.setContent(R.id.tab1);
                spec.setIndicator("START");
                tabHost.addTab(spec);

                spec=tabHost.newTabSpec("tag2");
                spec.setContent(R.id.tab2);
                spec.setIndicator("END");
                tabHost.addTab(spec);

                tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                    @Override
                    public void onTabChanged(String tabId) {
                        int tab = tabHost.getCurrentTab();
                        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                            tabHost.getTabWidget().getChildAt(i)
                                    .setBackgroundColor(Color.TRANSPARENT); // unselected
                        }
                        tabHost.getTabWidget().getChildAt(tab).setBackgroundColor(Color.CYAN);
                    }
                });

                final DatePicker multiPickerFromDate = (DatePicker) multiPickerLayout.findViewById(R.id.multiPicker_from_date);
                final TimePicker multiPickerFromTime = (TimePicker) multiPickerLayout.findViewById(R.id.multiPicker_from_time);
                final DatePicker multiPickerToDate = (DatePicker) multiPickerLayout.findViewById(R.id.multiPicker_to_date);
                final TimePicker multiPickerToTime = (TimePicker) multiPickerLayout.findViewById(R.id.multiPicker_to_time);

                //Get today's date
                Calendar todayCal = Calendar.getInstance();
                todayCal.add(Calendar.DATE, 0);

                multiPickerFromDate.setMinDate(todayCal.getTimeInMillis());
                multiPickerToDate.setMinDate(todayCal.getTimeInMillis());

                DialogInterface.OnClickListener dialogButtonListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case DialogInterface.BUTTON_NEGATIVE: {
                                // user tapped "cancel"
                                dialog.dismiss();
                                break;
                            }
                            case DialogInterface.BUTTON_POSITIVE: {
                                Note note = (Note) getListAdapter().getItem(notePosition);
                                // user tapped "set"
                                // here, use the "multiPickerDate" and "multiPickerTime" objects to retreive the date/time the user selected
                                Calendar fromCalendar = Calendar.getInstance();
                                Calendar toCalendar = Calendar.getInstance();
                                fromCalendar.set(multiPickerFromDate.getYear(), multiPickerFromDate.getMonth(), multiPickerFromDate.getDayOfMonth(),
                                        multiPickerFromTime.getCurrentHour(), multiPickerFromTime.getCurrentMinute(), 0);
                                long startTime = fromCalendar.getTimeInMillis();
                                toCalendar.set(multiPickerToDate.getYear(), multiPickerToDate.getMonth(), multiPickerToDate.getDayOfMonth(),
                                        multiPickerToTime.getCurrentHour(), multiPickerToTime.getCurrentMinute(), 0);
                                long endTime = toCalendar.getTimeInMillis();
                                editSelectedNoteAlarm(note, startTime + "", endTime + "");
                                //Toast.makeText(getActivity().getApplicationContext(), endTime + "", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default: {
                                dialog.dismiss();
                                break;
                            }
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(multiPickerLayout);
                builder.setPositiveButton("Set", dialogButtonListener);
                builder.setNegativeButton("Cancel", dialogButtonListener);
                Spannable wordToSpan = new SpannableString("REMINDER");
                wordToSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#339966")), 0, wordToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setTitle(wordToSpan);
                AlertDialog alt= builder.show();
                alt.getWindow().setBackgroundDrawableResource(R.drawable.rounded_corner);
            }
        });

        noteDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Grab the note information associated with whatever note item we clicked on
                Note note = (Note) getListAdapter().getItem(notePosition);
                deleteSelectedNote(note);
            }
        });
        //l.setItemsCanFocus(true);
        //launchNoteDetailActivity(MainActivity.fragmentToLaunch.VIEW, position);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.long_press_menu, menu);

        Spannable wordToSpan = new SpannableString("Choose Options");
        wordToSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, wordToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        menu.setHeaderTitle(wordToSpan);
        //menu.setHeaderTitle(Html.fromHtml("<font color='#ff3824'>Choose Options</font>"));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        //Row position of the log pressed
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int rowPosition = info.position;

        Note note = (Note) getListAdapter().getItem(rowPosition);
        //Returns whatever menu item we selected
        switch (item.getItemId())
        {
            case R.id.edit: {
                launchNoteDetailActivity(MainActivity.fragmentToLaunch.EDIT, rowPosition);
                return true;
            }
            case R.id.delete: {
                deleteSelectedNote(note);
                return true;
            }
            case R.id.share: {
                shareMessage(note.getMessage());
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    private void shareMessage(String message) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        //intent.putExtra(Intent.EXTRA_TEXT, note.getMessage());
        //intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share via...");
        //startActivity(Intent.createChooser(intent, "Share"));
        startActivity(
                Intent.createChooser(
                        intent,
                        "Share via..."
                )
        );
    }

    private void deleteSelectedNote(Note note){
        NotesDBAdaptor dbAdaptor = new NotesDBAdaptor(getActivity().getBaseContext());
        dbAdaptor.open();
        dbAdaptor.deleteNote(note.getId());

        notes.clear();
        notes.addAll(dbAdaptor.getAllNotes());
        noteAdaptor.notifyDataSetChanged();

        dbAdaptor.close();
    }

    private void editSelectedNoteAlarm(Note note, String alarmStartDateTime, String alarmEndDateTime){

        NotesDBAdaptor dbAdaptor = new NotesDBAdaptor(getActivity().getBaseContext());
        dbAdaptor.open();
        long noteId = dbAdaptor.updateNote(note.getId(), alarmStartDateTime, alarmEndDateTime);

        notes.clear();
        notes.addAll(dbAdaptor.getAllNotes());
        noteAdaptor.notifyDataSetChanged();

        dbAdaptor.close();
    }

    private void launchNoteDetailActivity(MainActivity.fragmentToLaunch ftl, int position){
        //Grab the note information associated with whatever note item we clicked on
        Note note = (Note) getListAdapter().getItem(position);

        if(note != null) {
            //Create a new intent launches our note detail activity
            Intent intent = new Intent(getActivity(), NoteDetailActivity.class);

            //Pass along the information of the note we clicked on to note detail activity
            intent.putExtra(MainActivity.NOTE_ID_EXTRA, note.getId());
            intent.putExtra(MainActivity.NOTE_TITLE_EXTRA, note.getTitle());
            intent.putExtra(MainActivity.NOTE_MESSAGE_EXTRA, note.getMessage());
            intent.putExtra(MainActivity.NOTE_CATEGORY_EXTRA, note.getCategory());

            switch (ftl) {
                case EDIT:
                    intent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD_EXTRA, MainActivity.fragmentToLaunch.EDIT);
                    break;
                case VIEW:
                    intent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD_EXTRA, MainActivity.fragmentToLaunch.VIEW);
                    break;
            }
            startActivity(intent);
        }
    }

    private void launchMyLocationActivity(int position){
        //Grab the note information associated with whatever note item we clicked on
        Note note = (Note) getListAdapter().getItem(position);

        if(note != null) {
            //Create a new intent launches our map activity
            Intent intent = new Intent(getActivity(), MyLocation.class);

            //Pass along the information of the note we clicked on to map activity
            intent.putExtra(MainActivity.NOTE_ID_EXTRA, note.getId());
            intent.putExtra(MainActivity.NOTE_TITLE_EXTRA, note.getTitle());
            intent.putExtra(MainActivity.NOTE_MESSAGE_EXTRA, note.getMessage());
            intent.putExtra(MainActivity.NOTE_LATITUDE_EXTRA, note.getLatitude());
            intent.putExtra(MainActivity.NOTE_LONGITUDE_EXTRA, note.getLongitude());

            startActivity(intent);
        }
    }

    /**
     * Handle touch events to fade/move dragged items as they are swiped out
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        float mDownX;
        private int mSwipeSlop = -1;
        float TheImpactPoint;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(getActivity()).
                        getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    TheImpactPoint=event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            getListView().requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX((x - mDownX));
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                {
                    float distance =TheImpactPoint-event.getX();

                    //click event
                    if(distance == 0.0 && !mSwiping){
                        int position = getListView().getPositionForView(v);
                        onClick(v, position);
                    }
                    //Toast.makeText(getActivity().getApplicationContext(), distance + ":" + mSwiping + ":" + v.getTranslationX(), Toast.LENGTH_SHORT).show();
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;
                        final boolean edit;
                        if (deltaXAbs > v.getWidth() / 3) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            endAlpha = 0;
                            if(v.getTranslationX() > 0){
                                remove = true;
                                edit = false;
                            }
                            else
                            {
                                edit = true;
                                remove = false;
                            }
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                            edit = false;
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        getListView().setEnabled(false);
                        v.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        if (remove) {
                                            animateRemoval(getListView(), v);
                                        } else if(edit)
                                        {
                                            int position = getListView().getPositionForView(v);
                                            launchNoteDetailActivity(MainActivity.fragmentToLaunch.EDIT, position);
                                        }
                                        else{
                                            //mBackgroundContainer.hideBackground();
                                            mSwiping = false;
                                            getListView().setEnabled(true);
                                        }
                                    }
                                });
                    }
                }
                mItemPressed = false;
                break;
                default:
                    return false;
            }
            return true;
        }
    };
    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
    private void animateRemoval(final ListView listview, View viewToRemove) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = noteAdaptor.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int position = getListView().getPositionForView(viewToRemove);
        Note note = (Note) getListAdapter().getItem(position);

        noteAdaptor.remove(noteAdaptor.getItem(position));

        final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = noteAdaptor.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        //mBackgroundContainer.hideBackground();
                                        mSwiping = false;
                                        getListView().setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    //mBackgroundContainer.hideBackground();
                                    mSwiping = false;
                                    getListView().setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
        deleteSelectedNote(note);
    }

    class SideIndexGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            //String swipe = "";
            float sensitivity = 50;

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean isSwipeAction = sharedPreferences.getBoolean("swipe", false);

            if(isSwipeAction) {
                ListView lv = getListView();
                int pos = lv.pointToPosition((int) e1.getX(), (int) e1.getY());

                final View v = getListView().getAdapter().getView(pos, null, getListView());

                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
                            || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
                        return false;
                    }
                } else {
                    if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                        return false;
                    }
                    if (pos >= 0) {
                        Note note = (Note) getListAdapter().getItem(pos);

                        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            //swipe += "Swipe Left\n";
                            //Swipe left to open edit mode
                            launchNoteDetailActivity(MainActivity.fragmentToLaunch.EDIT, pos);
                        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            //swipe += "Swipe Right\n";
                            //Swipre right to delete
                            deleteSelectedNote(note);
                        }
                    }
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
