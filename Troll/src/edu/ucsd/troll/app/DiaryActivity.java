package edu.ucsd.troll.app;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;

public class DiaryActivity extends ListActivity {
	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private NotesDbAdapter mDbHelper;

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes_list);
		
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		fillData();
		registerForContextMenu(getListView());

		final Button diaryInsert = (Button) findViewById(R.id.diary_addBtn);
//		final Button diaryDelete = (Button) findViewById(R.id.diary_deleteBtn);

   		Typeface btn_font = Typeface.createFromAsset(getAssets(), "KaushanScript-Regular.ttf");
   		diaryInsert.setTypeface(btn_font);
//   		diaryInsert.setTypeface(btn_font);
   		Drawable round_btn = getResources().getDrawable(R.drawable.round_btn);
   		
   		int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
        	diaryInsert.setBackgroundDrawable(round_btn); 
//        	diaryDelete.setBackgroundDrawable(round_btn);
        } else {
        	diaryInsert.setBackground(round_btn); 
//        	diaryDelete.setBackground(round_btn); 
        }

		
		diaryInsert.setOnClickListener(new View.OnClickListener() {
			@Override

			public void onClick(View view) {
				if(mDbHelper != null){
					createNote();
				}
			}
		});

//		diaryDelete.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(mDbHelper != null){
//					mDbHelper.deleteNote(R.id.diary_deleteBtn);
//					fillData();
//				}
//			}
//		});
		
	
	}

	@SuppressWarnings("deprecation")
	private void fillData() {
		Cursor diaryCursor = mDbHelper.fetchAllNotes();
		startManagingCursor(diaryCursor);

		// Create an array to specify the fields we want to display in the list (only TITLE)
		String[] from = new String[]{NotesDbAdapter.KEY_TITLE, NotesDbAdapter.KEY_DATE};

		// and an array of the fields we want to bind those fields to (in this case just text1)
		int[] to = new int[]{R.id.text1, R.id.diary_date};

		// Now create a simple cursor adapter and set it to display

		SimpleCursorAdapter notes = 
		new SimpleCursorAdapter(this, R.layout.notes_row, diaryCursor, from, to);
		setListAdapter(notes);
	}

	private void createNote() {
		Intent i = new Intent(this, DiaryEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, DiaryEdit.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.diary_menu_insert);
		return true;
	}
	
	

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case INSERT_ID:
			createNote();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.diary_menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			mDbHelper.deleteNote(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}


}
